import _ from "lodash";
import { Feature, Map, MapBrowserEvent, View } from "ol";
import { defaults as defaultControls } from "ol/control.js";
import { Listener } from "ol/events";
import { Extent, buffer, containsCoordinate } from "ol/extent";
import { GeoJSON } from "ol/format";
import TileLayer from "ol/layer/Tile";
import VectorLayer from "ol/layer/Vector";
import { OSM, TileWMS, Vector as VectorSource } from "ol/source";
import { Fill, Stroke, Style } from "ol/style";
import { StyleFunction } from "ol/style/Style";
import queryString from "query-string";
import { useCallback, useContext, useEffect, useState } from "react";
import { Draw, Snap } from "ol/interaction";
import { Plot, PlotGeoJSON, getProjectPlots, updateProject, updateProjectPlots } from "../api/projectsServices";
import ConfigContext from "../context/ConfigContext";
import ProjectContext from "../context/ProjectContext";
import { extentToCenter, mapBoundsToExtent } from "../utils/map";
import { DrawEvent } from "ol/interaction/Draw";
import { LineString, Polygon } from "ol/geom";
import { Coordinate } from "ol/coordinate";
import { fromExtent } from "ol/geom/Polygon";

const baseUrlKadasterWfs = "https://service.pdok.nl/kadaster/kadastralekaart/wfs/v5_0";

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
    const [bboxLayerSourceCut, setBboxLayerSourceCut] = useState<VectorSource>();
    const [subselectionSource, setSubselectionSource] = useState<VectorSource>();

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
        bboxLayerSource?.clear();
        bboxLayerSourceCut?.clear();
    };

    const handleSaveChange = async () => {
        bboxLayerSource?.clear();
        bboxLayerSourceCut?.clear();
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
                const loc = e.coordinate;
                const bbox = `${loc[0]},${loc[1]},${loc[0]},${loc[1]}`;
                const url = queryString.stringifyUrl({
                    url: baseUrlKadasterWfs,
                    query: {
                        request: "GetFeature",
                            service: "WFS",
                            version: "2.0.0",
                            outputFormat: "application/json",
                            typeName: "kadastralekaart:Perceel",
                            srsName: projection,
                            bbox: bbox + "," + projection
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
            const bufferDistance = 5;
            const bufferedExtent = buffer(extent, bufferDistance);

            const bbox = bufferedExtent.join(",");

            const url = queryString.stringifyUrl({
                url: baseUrlKadasterWfs,
                query: {
                    request: "GetFeature",
                    service: "WFS",
                    version: "2.0.0",
                    outputFormat: "application/json",
                    typeName: "kadastralekaart:Perceel",
                    srsName: "EPSG:3857",
                    bbox: bbox + "," + projection
                },
            });

            fetch(url)
                .then((res) => res.json())
                .then((result) => {
                    const plotFeature = result as PlotGeoJSON;
                    const newPlots: Plot[] = [];

                    for (let i = 0; i < plotFeature.features.length; i++) {

                        const properties = plotFeature.features[i].properties;
                        const singleFeaturePlot = {
                            ...plotFeature,
                            features: [plotFeature.features[i]],
                        };
                        const newPlot: Plot = {
                            brkGemeenteCode: properties.kadastraleGemeenteCode,
                            brkPerceelNummer: parseInt(properties.perceelnummer),
                            brkSectie: properties.sectie,
                            plotFeature: singleFeaturePlot,
                        };
                        const isPlotAlreadySelected = selectedPlots.some(
                            (plot) =>
                                plot.brkGemeenteCode === newPlot.brkGemeenteCode &&
                                plot.brkPerceelNummer === newPlot.brkPerceelNummer &&
                                plot.brkSectie === newPlot.brkSectie
                        );

                        if (!isPlotAlreadySelected) {
                            newPlots.push(newPlot);
                        }
                    }

                    setSelectedPlots([...selectedPlots, ...newPlots]);
                });
                const newPolygon = fromExtent(bufferedExtent);
                bboxLayerSource.addFeature(new Feature({
                geometry: newPolygon,
            }));
        },
        [selectionMode, map, selectedPlotLayerSource, bboxLayerSource, selectedPlots]
);

    const handleCut = useCallback((e: DrawEvent) => {
        if (selectionMode !== Buttons.CUT || !map || !selectedPlotLayerSource || !bboxLayerSourceCut) return;

        const polygonGeometry = e.feature.getGeometry();
        if (!polygonGeometry || !(polygonGeometry instanceof Polygon)) return;

        const extent = polygonGeometry.getExtent();
        const bbox = extent.join(",");

        const coords = polygonGeometry.getCoordinates();

            const url = queryString.stringifyUrl({
                url: baseUrlKadasterWfs,
                query: {
                    request: "GetFeature",
                    service: "WFS",
                    version: "2.0.0",
                    outputFormat: "application/json",
                    typeName: "kadastralekaart:Perceel",
                    srsName: "EPSG:3857",
                    bbox: bbox + "," + projection
                },
            });

            fetch(url)
    .then((res) => res.json())
    .then((result) => {
        const plotFeature = result as PlotGeoJSON;

        plotFeature.features.forEach((feature) => {
            const properties = feature.properties;
            const existingPlot = selectedPlots.find(
                (plot) =>
                    plot.brkGemeenteCode === properties.kadastraleGemeenteCode &&
                    plot.brkPerceelNummer === parseInt(properties.perceelnummer) &&
                    plot.brkSectie === properties.sectie
            );

            if (existingPlot) {
                const updatedPlots = [...selectedPlots];
                const existingCoords = existingPlot.subselectionGeometry?.coordinates as Coordinate[][] || [];
                const newCoords = coords as Coordinate[][];
                const mergedCoords: Coordinate[][] = [...existingCoords, ...newCoords];

                const updatedPlotIndex = updatedPlots.indexOf(existingPlot);
                updatedPlots[updatedPlotIndex] = {
                    ...existingPlot,
                        subselectionGeometry: {
                            type: "Polygon",
                            coordinates: mergedCoords,
                        },
                    };


                setSelectedPlots(updatedPlots);
                console.log("selectedPlots", updatedPlots);
                }

        });
    });
}, [selectionMode, map, selectedPlotLayerSource, bboxLayerSourceCut, selectedPlots]);

useEffect(
    function updatePlotsLayer() {
        if (!selectedPlotLayerSource) return;

        const changed = !_.isEqual(selectedPlots, originalSelectedPlots);
        setPlotsChanged(changed);
        selectedPlotLayerSource.clear();
        subselectionSource?.clear();

        for (const selectedPlot of selectedPlots) {
            const geojson = new GeoJSON().readFeatures(selectedPlot.plotFeature);

            if (selectedPlot.subselectionGeometry) {
                const subselectionFeature = new GeoJSON().readFeature({
                    type: "Feature",
                    geometry: selectedPlot.subselectionGeometry,
                });

                subselectionSource?.addFeature(subselectionFeature);
            }

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
    [extent, originalSelectedPlots, selectedPlotLayerSource, selectedPlots, mapBounds, subselectionSource],
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

        document.addEventListener("keydown", function(event) {
            if (event.key == "Escape") {
                draw.abortDrawing();
            }
        });

        draw.on("drawstart", () => {
            bboxLayerSource?.clear();
        })
        draw.on("drawend", handleLineDrawEnd);
        map.addInteraction(draw);

        return () => {
            map.removeInteraction(draw);
        };
    }, [map, selectionMode, handleLineDrawEnd, bboxLayerSource]);

    useEffect(() => {
        if (selectionMode !== Buttons.CUT || !map || !selectedPlotLayerSource) return;

        let currentFeature: Feature<Polygon> | null = null;
        const snapTolerance = 10;

        const draw = new Draw({
            source: bboxLayerSourceCut,
            type: "Polygon",
            minPoints: 3,
            condition: (event) => {
                const point = event.coordinate;
                return isPointInsideSelectedPlot(point);
            },
            finishCondition: () => {
                if (!currentFeature) return false;
                const coordinates = currentFeature.getGeometry()?.getCoordinates()[0];
                if (!coordinates) return false
                const firstPoint = coordinates[0];
                const lastPoint = coordinates[coordinates.length - 1];

                const isClosed = firstPoint[0] === lastPoint[0] && firstPoint[1] === lastPoint[1];

                return isClosed;
            },
            freehandCondition: () => {
                return false;
            },
        });

        const snapInteraction = new Snap({
            source: selectedPlotLayerSource,
            pixelTolerance: snapTolerance,
        });

        const snapInteractionSubPlot = new Snap({
            source: subselectionSource,
            pixelTolerance: snapTolerance,
        });

          const isPointInsideSelectedPlot = (point: Coordinate) => {
            const features = selectedPlotLayerSource.getFeatures();
            if (!features || features.length === 0) return false;

            for (const feature of features) {
                const geometry = feature.getGeometry() as Polygon;
                const extent: Extent = geometry.getExtent();

                if (containsCoordinate(extent, point)) {
                        return true;
                }
            }

            return false;
        };

        document.addEventListener("keypress", function(event) {
            if (event.key == "Enter") {
                if (currentFeature) {
                    const coordinates = currentFeature.getGeometry()?.getCoordinates()[0];

                    if (coordinates && coordinates.length > 4) {
                        draw.finishDrawing();
                    }
                }
            }
        });

        document.addEventListener("keydown", function(event) {
            if (event.key == "Escape") {
                draw.abortDrawing();
            }
        });

        draw.on('drawstart', (event) => {
            currentFeature = event.feature as Feature<Polygon>;
        });

        draw.on('drawend', (event) => {
            currentFeature = null;
            handleCut(event);
        });
        map.addInteraction(draw);
        map.addInteraction(snapInteraction);
        map.addInteraction(snapInteractionSubPlot);

        return () => {
            map.removeInteraction(draw);
            map.removeInteraction(snapInteraction);
            map.removeInteraction(snapInteractionSubPlot);
        };
    }, [map, selectionMode, handleCut, selectedPlotLayerSource, subselectionSource, bboxLayerSourceCut]);

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

            const bboxSourceCut = new VectorSource();
            const bboxLayerCut = new VectorLayer({
                source: bboxSourceCut,
                style: new Style({
                    fill: new Fill({ color: "rgba(155, 193, 228, 0.5)" }),
                    stroke: new Stroke({ color: "rgba(7, 62, 168, 0.5)", width: 1 }),
                }),

            });
            setBboxLayerSourceCut(bboxSourceCut);
            setSubselectionSource(bboxSourceCut);

            const newMap = new Map({
                target: id,
                layers: [osmLayer, projectGeometryLayer, kadasterLayers, selectedPlotLayer, bboxLayer, bboxLayerCut],
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
