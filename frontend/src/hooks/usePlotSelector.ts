import { Map, View } from "ol";

import GeoJSON from "ol/format/GeoJSON.js";
import TileLayer from "ol/layer/Tile";
import VectorLayer from "ol/layer/Vector";
import { OSM, TileWMS, Vector as VectorSource } from "ol/source";
import { Point, LineString, Polygon } from "ol/geom";
import _ from "lodash";
import { defaults as defaultControls } from "ol/control.js";
import { Extent } from "ol/extent";
import { Fill, Stroke, Style } from "ol/style";
import { StyleFunction } from "ol/style/Style";
import queryString from "query-string";
import { useCallback, useContext, useEffect, useState } from "react";
import { Plot, PlotGeoJSON, getProjectPlots, updateProjectPlots, updateProject } from "../api/projectsServices";
import ConfigContext from "../context/ConfigContext";
import ProjectContext from "../context/ProjectContext";
import { extentToCenter, mapBoundsToExtent } from "../utils/map";

const baseUrlKadasterWms = "https://service.pdok.nl/kadaster/kadastralekaart/wms/v5_0";

const usePlotSelector = (id: string) => {
    const { selectedProject } = useContext(ProjectContext);
    const { mapBounds } = useContext(ConfigContext);

    const [map, setMap] = useState<Map>();
    const [selectedPlotLayerSource, setSelectedPlotLayerSource] = useState<VectorSource>();

    const [originalSelectedPlots, setOriginalSelectedPlots] = useState<Plot[] | null>(null);
    const [selectedPlots, setSelectedPlots] = useState<Plot[]>([]);

    const [plotsChanged, setPlotsChanged] = useState(false);
    const [extent, setExtent] = useState<Extent | null>(null);

    const selectedFeatureStyle = useCallback((): Style => {
        const fillOpacityHex = "99";
        const borderOpacityHex = "DD";
        const red = "#ff4122";
        return new Style({
            fill: new Fill({ color: (selectedProject?.projectColor ? selectedProject.projectColor : red) + fillOpacityHex }),
            stroke: new Stroke({
                color: (selectedProject?.projectColor ?? red) + borderOpacityHex,
                width: 5,
            }),
        });
    }, [selectedProject]);

    useEffect(
        function fetchPlots() {
            if (!selectedProject) return;

            getProjectPlots(selectedProject.projectId).then((plots) => {
                setOriginalSelectedPlots(plots);
                setSelectedPlots(plots);
            });
        },
        [selectedProject],
    );

    const handleCancelChange = () => {
        setSelectedPlots(originalSelectedPlots || []);
    };

    const handleSaveChange = async () => {
        if (selectedProject) {
            await updateProjectPlots(selectedProject.projectId, selectedPlots);
            setOriginalSelectedPlots(selectedPlots);

            const extent = selectedPlotLayerSource?.getExtent();
            if (extent) {
                const center = extentToCenter(extent);
                selectedProject.location = { lat: center[0], lng: center[1] };
            }
            await updateProject(selectedProject);
        }
    };

    const areBoundingBoxesEqual = (bbox1: [number, number, number, number] | Extent, bbox2: [number, number, number, number] | Extent): boolean => {
        return bbox1.every((val, idx) => val === bbox2[idx]);
    };

    const handleClick = useCallback(
        (e: any) => {
            const map: Map = e.map;
            if (!map) return;

            const features = map.getFeaturesAtPixel(e.pixel);

            let alreadySelected = false;
            let alreadySelectedIndex = -1;

            if (features && features.length > 0) {
                const topFeature = features[0];
                const geometry = topFeature.getGeometry();

                if (geometry instanceof Point || geometry instanceof LineString || geometry instanceof Polygon) {
                    const featureBoundingBox = geometry.getExtent();

                    alreadySelectedIndex = selectedPlots.findIndex((plot) => {
                        const plotGeoJSON = new GeoJSON().readFeature(plot.plotFeature.features[0]);
                        const plotGeometry = plotGeoJSON.getGeometry();

                        if (plotGeometry instanceof Point || plotGeometry instanceof LineString || plotGeometry instanceof Polygon) {
                            const plotBoundingBox = plotGeometry.getExtent();
                            return areBoundingBoxesEqual(plotBoundingBox, featureBoundingBox);
                        }

                        return false;
                    });

                    alreadySelected = alreadySelectedIndex !== -1;
                }
            }

            if (alreadySelected) {
                const newSelectedPlots = [...selectedPlots.slice(0, alreadySelectedIndex), ...selectedPlots.slice(alreadySelectedIndex + 1)];
                setSelectedPlots(newSelectedPlots);
            } else {
                const bboxSize = 1;
                const loc = e.coordinate;
                const bbox = `${loc[0] - bboxSize},${loc[1] - bboxSize},${loc[0] + bboxSize},${loc[1] + bboxSize}`;

                const url = queryString.stringifyUrl({
                    url: baseUrlKadasterWms,
                    query: {
                        QUERY_LAYERS: "Perceel",
                        INFO_FORMAT: "application/json",
                        REQUEST: "GetFeatureInfo",
                        SERVICE: "WMS",
                        VERSION: "1.3.0",
                        HEIGHT: 101,
                        WIDTH: 101,
                        I: 50,
                        J: 50,
                        layers: "Perceel",
                        CRS: "EPSG:3857",
                        BBOX: bbox,
                    },
                });

                fetch(url)
                    .then((res) => res.json())
                    .then((result) => {
                        const plotFeature = result as PlotGeoJSON;
                        const properties = plotFeature.features[0].properties;

                        const newPlot: Plot = {
                            brkGemeenteCode: properties.kadastraleGemeenteCode,
                            brkPerceelNummer: parseInt(properties.perceelnummer),
                            brkSectie: properties.sectie,
                            plotFeature,
                        };

                        setSelectedPlots([...selectedPlots, newPlot]);
                    });
            }
        },
        [selectedPlots],
    );

    useEffect(
        function updatePlotsLayer() {
            if (!selectedPlotLayerSource) return;

            const changed = !_.isEqual(selectedPlots, originalSelectedPlots);
            setPlotsChanged(changed);
            selectedPlotLayerSource.clear();
            for (const selectedPlot of selectedPlots) {
                const geojson = new GeoJSON().readFeatures(selectedPlot.plotFeature);
                selectedPlotLayerSource.addFeatures(geojson);
            }

            if (extent == null && originalSelectedPlots != null) {
                if (!selectedPlotLayerSource.isEmpty()) {
                    setExtent(selectedPlotLayerSource.getExtent());
                } else {
                    setExtent(mapBoundsToExtent(mapBounds));
                }
            }
        },
        [extent, originalSelectedPlots, selectedPlotLayerSource, selectedPlots, mapBounds],
    );

    useEffect(
        function zoomToExtent() {
            if (extent) {
                map?.getView().fit(extent);
            }
        },
        [extent, map],
    );

    useEffect(() => {
        if (!map) return;

        map.addEventListener("click", handleClick);
        return () => {
            map.removeEventListener("click", handleClick);
        };
    }, [handleClick, map]);

    useEffect(
        function createMap() {
            const osmLayer = new TileLayer({
                source: new OSM(),
            });

            const kadasterLayers = new TileLayer({
                source: new TileWMS({
                    url: "https://service.pdok.nl/kadaster/kadastralekaart/wms/v5_0",
                    params: { layers: "Kadastralekaart", minZoom: 17, maxNativeZoom: 22, maxZoom: 23, transparent: true },
                }),
            });

            const source = new VectorSource();
            const selectedPlotLayer = new VectorLayer({ source, style: selectedFeatureStyle as StyleFunction });

            setSelectedPlotLayerSource(source);

            const newMap = new Map({
                target: id,
                layers: [osmLayer, kadasterLayers, selectedPlotLayer],
                view: new View({
                    center: extentToCenter(mapBoundsToExtent(mapBounds)),
                    zoom: 12,
                }),
                controls: defaultControls({ attribution: false }),
            });
            setMap(newMap);
            const extent = mapBoundsToExtent(mapBounds);
            newMap.getView().fit(extent);

            return () => {
                newMap.dispose();
                const mapElement = document.getElementById(id);
                if (mapElement) {
                    mapElement.innerHTML = "";
                }
            };
        },
        [id, mapBounds, selectedFeatureStyle],
    );
    return { plotsChanged, selectedPlotCount: selectedPlots.length, handleCancelChange, handleSaveChange };
};

export default usePlotSelector;
