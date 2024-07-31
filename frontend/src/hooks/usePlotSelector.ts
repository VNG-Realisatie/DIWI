import _ from "lodash";
import { Feature, Map, MapBrowserEvent, View } from "ol";
import { defaults as defaultControls } from "ol/control.js";
import { Listener } from "ol/events";
import { Extent } from "ol/extent";
import { GeoJSON } from "ol/format";
import TileLayer from "ol/layer/Tile";
import VectorLayer from "ol/layer/Vector";
import { OSM, TileWMS, Vector as VectorSource } from "ol/source";
import { Fill, Stroke, Style } from "ol/style";
import { StyleFunction } from "ol/style/Style";
import queryString from "query-string";
import { useCallback, useContext, useEffect, useState } from "react";
import { Draw } from "ol/interaction";
import { Plot, PlotGeoJSON, getProjectPlots, updateProject, updateProjectPlots } from "../api/projectsServices";
import ConfigContext from "../context/ConfigContext";
import ProjectContext from "../context/ProjectContext";
import { extentToCenter, mapBoundsToExtent } from "../utils/map";
import { DrawEvent } from "ol/interaction/Draw";
import { LineString, Polygon } from "ol/geom";

const baseUrlKadasterWms = "https://service.pdok.nl/kadaster/kadastralekaart/wms/v5_0";

const projection = "EPSG:3857";

export enum Buttons {
    SELECT = "select",
    CUT = "cut",
}

const usePlotSelector = (id: string) => {

    const { selectedProject, setSelectedProject } = useContext(ProjectContext);
    const { mapBounds } = useContext(ConfigContext);

    const [map, setMap] = useState<Map>();
    const [selectedPlotLayerSource, setSelectedPlotLayerSource] = useState<VectorSource>();
    const [projectLayerSource, setProjectLayerSource] = useState<VectorSource>();
    const [bboxLayerSource, setBboxLayerSource] = useState<VectorSource>();

    const [originalSelectedPlots, setOriginalSelectedPlots] = useState<Plot[] | null>(null);
    const [selectedPlots, setSelectedPlots] = useState<Plot[]>([]);
    const [plotsChanged, setPlotsChanged] = useState(false);
    const [extent, setExtent] = useState<Extent | null>(null);

    const [selectionMode, setSelectionMode] = useState<Buttons | null>(null);

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
            if (selectedPlots.length > 0) {
                const newProject = await updateProject({ ...selectedProject, geometry: undefined });
                setSelectedProject(newProject);
            }
        }
    };

    const handleClick = useCallback(
        (e: MapBrowserEvent<UIEvent>) => {
            if (selectionMode !== Buttons.SELECT) return;

            const map: Map = e.map;
            if (!map) return;

            const features = map.getFeaturesAtPixel(e.pixel, { layerFilter: (layer) => layer.getSource() === selectedPlotLayerSource });

            if (features.length > 0) {
                const newSelectedPlots = selectedPlots.filter((plot) => {
                    const id = plot.plotFeature.features[0].id;
                    const clickedFeatureId = features[0].getId();
                    return id !== clickedFeatureId;
                });
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

                        if (plotFeature.features.length === 0) {
                            return;
                        }

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
        [selectionMode, selectedPlotLayerSource, selectedPlots],
    );

    const handleLineDrawEnd = useCallback(
         (e: DrawEvent) => {
            if (selectionMode !== Buttons.SELECT || !map || !selectedPlotLayerSource || !bboxLayerSource) return;

            const lineGeometry = e.feature.getGeometry();
            if (!lineGeometry || !(lineGeometry instanceof LineString)) return;

            const extent = lineGeometry.getExtent();
            const bbox = extent.join(",");

            const gridSize = 10;
            const maxRequests = 100;
            let requestCount = 0;

            const fetchPromises = [];

            for (let i = 0; i <= 100; i += gridSize) {
                for (let j = 0; j <= 100; j += gridSize) {
                    if (requestCount >= maxRequests) break;

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
                            I: i,
                            J: j,
                            layers: "Perceel",
                            CRS: "EPSG:3857",
                            BBOX: bbox,
                        },
                    });

                    fetchPromises.push(
                        fetch(url)
                            .then((res) => res.json())
                            .then((result) => {
                                const plotFeature = result as PlotGeoJSON;

                                if (plotFeature.features.length === 0) {
                                    return;
                                }

                                const properties = plotFeature.features[0].properties;

                                const newPlot: Plot = {
                                    brkGemeenteCode: properties.kadastraleGemeenteCode,
                                    brkPerceelNummer: parseInt(properties.perceelnummer),
                                    brkSectie: properties.sectie,
                                    plotFeature,
                                };

                                setSelectedPlots((prevPlots) => {
                                    const isPlotAlreadySelected = prevPlots.some(
                                        (plot) =>
                                            plot.brkGemeenteCode === newPlot.brkGemeenteCode &&
                                            plot.brkPerceelNummer === newPlot.brkPerceelNummer &&
                                            plot.brkSectie === newPlot.brkSectie
                                    );
                                    return isPlotAlreadySelected ? prevPlots : [...prevPlots, newPlot];
                                });
                            })
                            .catch((error) => {
                                console.error("Error fetching plot data:", error);
                            })
                    );

                    requestCount++;
                }
                if (requestCount >= maxRequests) break;
            }

            Promise.all(fetchPromises);

            try {
                const [minX, minY, maxX, maxY] = extent;

                const boundingBoxCoords = [
                    [minX, minY],
                    [maxX, minY],
                    [maxX, maxY],
                    [minX, maxY],
                    [minX, minY],
                ];

                const boundingBoxPolygon = new Polygon([boundingBoxCoords]);

                bboxLayerSource.clear();
                bboxLayerSource.addFeature(new Feature({
                    geometry: boundingBoxPolygon,
                }));
            } catch (error) {
                console.error("Error updating bounding box layer:", error);
            }
        },
        [selectionMode, map, selectedPlotLayerSource, bboxLayerSource]
);

    const handleCut = useCallback((e: DrawEvent) => {
        if (selectionMode !== Buttons.CUT || !map || !selectedPlotLayerSource) return;

        const lineGeometry = e.feature.getGeometry();
        if (!lineGeometry || !(lineGeometry instanceof Polygon)) return;

        const extent = lineGeometry.getExtent();
        console.log(extent);

    }, [selectionMode, map, selectedPlotLayerSource])

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
        function updateProjectGeometry() {
            if (!projectLayerSource) {
                return;
            }

            projectLayerSource.clear();

            if (selectedProject?.geometry) {
                const geometry = JSON.parse(selectedProject.geometry);

                const options = {
                    featureProjection: map?.getView().getProjection(),
                };
                const geojsonFeature = {
                    type: "Feature",
                    crs: geometry.crs,
                    geometry,
                    properties: {},
                };
                // Convert geometry to a feature while converting the projection
                const feature = new GeoJSON().readFeature(geojsonFeature, options);

                projectLayerSource.addFeature(feature);
            }
        },
        [projectLayerSource, selectedProject?.geometry, map],
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
        if (selectionMode !== Buttons.SELECT || !map) return;

        const draw = new Draw({
            source: new VectorSource(),
            type: "LineString",
            maxPoints: 1,
            condition: (event) => {
                const click = event.originalEvent as MouseEvent;
                return click.button === 2; //Right click
            },
        });

        draw.on("drawend", handleLineDrawEnd);
        map.addInteraction(draw);

        return () => {
            map.removeInteraction(draw);
        };
    }, [map, selectionMode, handleLineDrawEnd]);

    useEffect(() => {
        if (selectionMode !== Buttons.CUT || !map) return;

        const draw = new Draw({
            source: new VectorSource(),
            type: "Polygon",
            // condition: (event) => {
            //     const click = event.originalEvent as MouseEvent;
            //     return click.button === 2; //Right click
            // },
        });

        draw.on("drawend", handleCut);
        map.addInteraction(draw);

        return () => {
            map.removeInteraction(draw);
        };
    }, [map, selectionMode, handleCut]);

    useEffect(() => {
        if (!map) return;

        map.addEventListener("click", handleClick as Listener);
        return () => {
            map.removeEventListener("click", handleClick as Listener);
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

            const selectedPlotSource = new VectorSource();
            const selectedPlotLayer = new VectorLayer({ source: selectedPlotSource, style: selectedFeatureStyle as StyleFunction });
            setSelectedPlotLayerSource(selectedPlotSource);

            const projectGeometrySource = new VectorSource();
            const projectGeometryLayer = new VectorLayer({
                source: projectGeometrySource,
                style: new Style({
                    fill: new Fill({ color: "rgba(0, 0, 0, 0)" }),
                    stroke: new Stroke({ color: "#000000", width: 5 }),
                }),
            });
            setProjectLayerSource(projectGeometrySource);

            const bboxSource = new VectorSource();
            const bboxLayer = new VectorLayer({
                source: bboxSource,
                style: new Style({
                    fill: new Fill({ color: "rgba(155, 193, 228, 0.5)" }),
                    stroke: new Stroke({ color: "rgba(7, 62, 168, 0.5)", width: 1 }),
                }),
            });
            setBboxLayerSource(bboxSource);

            const newMap = new Map({
                target: id,
                layers: [osmLayer, projectGeometryLayer, kadasterLayers, selectedPlotLayer, bboxLayer],
                view: new View({
                    center: extentToCenter(mapBoundsToExtent(mapBounds)),
                    zoom: 12,
                    projection: projection,
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

    const toggleSelectionMode = (mode: Buttons) => {
        setSelectionMode((prevMode) => (prevMode === mode ? null : mode));
    };

    return {
        plotsChanged,
        selectedPlotCount: selectedPlots.length,
        handleCancelChange,
        handleSaveChange,
        selectionMode,
        toggleSelectionMode,
    };
};

export default usePlotSelector;
