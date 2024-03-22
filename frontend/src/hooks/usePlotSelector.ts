import { Feature, Map, View } from "ol";

import GeoJSON from "ol/format/GeoJSON.js";
import TileLayer from "ol/layer/Tile";
import VectorLayer from "ol/layer/Vector";
import { OSM, TileWMS, Vector as VectorSource } from "ol/source";

import { defaults as defaultControls } from "ol/control.js";
import { Extent } from "ol/extent";
import queryString from "query-string";
import { useCallback, useContext, useEffect, useState } from "react";
import { Plot, PlotGeoJSON, getProjectPlots, updateProjectPlots, updateProjects } from "../api/projectsServices";
import ProjectContext from "../context/ProjectContext";
import _ from "lodash";
import { Style, Fill, Stroke } from "ol/style";
import { StyleFunction } from "ol/style/Style";

const baseUrlKadasterWms = "https://service.pdok.nl/kadaster/kadastralekaart/wms/v5_0";

const usePlotSelector = (id: string) => {
    const { selectedProject } = useContext(ProjectContext);

    const [map, setMap] = useState<Map>();
    const [selectedPlotLayerSource, setSelectedPlotLayerSource] = useState<VectorSource>();

    const [originalSelectedPlots, setOriginalSelectedPlots] = useState<Plot[]>([]);
    const [selectedPlots, setSelectedPlots] = useState<Plot[]>([]);

    const [plotsChanged, setPlotsChanged] = useState(false);
    const [extent, setExtent] = useState<Extent | null>(null);

    const selectedFeatureStyle = (f: Feature): Style => {
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
    };

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
        setSelectedPlots(originalSelectedPlots);
    };

    const handleSaveChange = async () => {
        if (selectedProject) {
            await updateProjectPlots(selectedProject.projectId, selectedPlots);
            setOriginalSelectedPlots(selectedPlots);

            const extent = selectedPlotLayerSource?.getExtent();
            if (extent) {
                selectedProject.location = { lat: extent[0] + (extent[2] - extent[0]) / 2, lng: extent[1] + (extent[3] - extent[1]) / 2 };
            }
            await updateProjects(selectedProject);
        }
    };

    const handleClick = useCallback(
        (e: any) => {
            // @ts-ignore
            const loc = e.coordinate;
            const bboxSize = 1;
            const bbox = `${loc[0] - bboxSize},${loc[1] - bboxSize},${loc[0] + bboxSize},${loc[1] + bboxSize}`;
            const url = queryString.stringifyUrl({
                url: baseUrlKadasterWms,
                query: {
                    QUERY_LAYERS: "Perceel",
                    INFO_FORMAT: "application/json",
                    REQUEST: "GetFeatureInfo",
                    SERVICE: "WMS",
                    VERSION: "1.3.0",
                    HEIGHT: "101", // What?
                    WIDTH: "101", // What?
                    I: "50", // What?
                    J: "50", // What?
                    layers: "Perceel",
                    CRS: "EPSG:3857",
                    BBOX: bbox,
                },
            });

            fetch(url)
                .then((res) => res.json())
                .then((result): void => {
                    const geojson = result as PlotGeoJSON;
                    const properties = geojson.features[0].properties;
                    const newPlot: Plot = {
                        brkGemeenteCode: properties.kadastraleGemeenteCode,
                        brkPerceelNummer: parseInt(properties.perceelnummer),
                        brkSectie: properties.sectie,
                        brkSelectie: "TBD", // TODO what to put here?
                        geoJson: geojson,
                    };
                    const newSelectedPlots = selectedPlots.filter((p) => {
                        const notEqual =
                            p.brkGemeenteCode !== newPlot.brkGemeenteCode ||
                            p.brkPerceelNummer !== newPlot.brkPerceelNummer ||
                            p.brkSectie !== newPlot.brkSectie ||
                            p.brkSelectie !== newPlot.brkSelectie;
                        return notEqual;
                    });
                    if (newSelectedPlots.length !== selectedPlots.length) {
                        setSelectedPlots(newSelectedPlots);
                    } else {
                        setSelectedPlots([...selectedPlots, newPlot]);
                    }
                });
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
                const geojson = new GeoJSON().readFeatures(selectedPlot.geoJson);
                selectedPlotLayerSource.addFeatures(geojson);
            }

            if (extent == null && !selectedPlotLayerSource.isEmpty()) {
                setExtent(selectedPlotLayerSource.getExtent());
            }
        },
        [extent, originalSelectedPlots, selectedPlotLayerSource, selectedPlots],
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
                    center: [521407.57221923344, 6824704.512308201],
                    zoom: 16,
                }),
                controls: defaultControls({ attribution: false }),
            });
            setMap(newMap);

            return () => {
                newMap.dispose();
                const mapElement = document.getElementById(id);
                if (mapElement) {
                    mapElement.innerHTML = "";
                }
            };
        },
        [id],
    );
    return { plotsChanged, handleCancelChange, handleSaveChange };
};

export default usePlotSelector;
