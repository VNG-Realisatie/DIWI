import _ from "lodash";
import { Feature, Map, MapBrowserEvent, View } from "ol";
import { defaults as defaultControls } from "ol/control.js";
import { Listener } from "ol/events";
import { Extent, buffer } from "ol/extent";
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
import useAlert from "./useAlert";
import { useTranslation } from "react-i18next";
import { useHasEditPermission } from "./useHasEditPermission";
import CircleStyle from "ol/style/Circle";
import { colors } from "../theme";
import * as turf from '@turf/turf';
import { GeoJSONPolygon } from "ol/format/GeoJSON";
import { Feature as Feat, Polygon as Poly } from "geojson";

const baseUrlKadasterWfs = "https://service.pdok.nl/kadaster/kadastralekaart/wfs/v5_0";

const projection = "EPSG:3857";

export enum Buttons {
    SELECT,
    CUT,
    DELETE,
}

const fetchPlotData = async (bbox: string): Promise<PlotGeoJSON> => {
    const url = queryString.stringifyUrl({
        url: baseUrlKadasterWfs,
        query: {
            request: "GetFeature",
            service: "WFS",
            version: "2.0.0",
            outputFormat: "application/json",
            typeName: "kadastralekaart:Perceel",
            srsName: "EPSG:3857",
            bbox: bbox + "," + projection,
        },
    });

    const response = await fetch(url);
    const result = await response.json();
    return result as PlotGeoJSON;
};

const usePlotSelector = (id: string) => {

    const { selectedProject, setSelectedProject } = useContext(ProjectContext);
    const { mapBounds } = useContext(ConfigContext);

    const [map, setMap] = useState<Map>();
    const [selectedPlotLayerSource, setSelectedPlotLayerSource] = useState<VectorSource>();
    const [projectLayerSource, setProjectLayerSource] = useState<VectorSource>();
    const [multiselectBoxLayerSource, setMultiselectBoxLayerSource] = useState<VectorSource>();
    const [cutLayerSource, setcutLayerSource] = useState<VectorSource>();

    const [originalSelectedPlots, setOriginalSelectedPlots] = useState<Plot[] | null>(null);
    const [selectedPlots, setSelectedPlots] = useState<Plot[]>([]);
    const [plotsChanged, setPlotsChanged] = useState(false);
    const [extent, setExtent] = useState<Extent | null>(null);

    const [selectionMode, setSelectionMode] = useState<Buttons | null>(null);
    const [previousSelectionMode, setPreviousSelectionMode] = useState<Buttons | null>(null);

    const { getEditPermission } = useHasEditPermission();

    const { setAlert } = useAlert();
    const { t } = useTranslation();

    const selectedFeatureStyle = useCallback((hasSubplot?: boolean): Style => {
        const fillOpacityHex = hasSubplot ? "00" : "99";
        const borderOpacityHex = hasSubplot ? "00" : "DD";
        const red = hasSubplot ? "#FFF" : "#ff4122";

        return new Style({
            fill: new Fill({
                color: (selectedProject?.projectColor ?? red) + fillOpacityHex
            }),
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
        multiselectBoxLayerSource?.clear();
        cutLayerSource?.clear();
    };

    const handleSaveChange = async () => {
        multiselectBoxLayerSource?.clear();
        cutLayerSource?.clear();
        try {
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

                setAlert(t("projectDetail.mapUpdatedSuccessfully"), "success");
            }
        } catch (error: unknown) {
            if (error instanceof Error) setAlert(error.message, "error");
        }
    };

    const handleSelect = useCallback(
        (e: MapBrowserEvent<UIEvent>) => {
            if (selectionMode !== Buttons.SELECT) return;

            const map: Map = e.map;
            if (!map || !getEditPermission()) return;

            const features = map.getFeaturesAtPixel(e.pixel, { layerFilter: (layer) => layer.getSource() === selectedPlotLayerSource });
            if (features.length > 0) {
                return;
            } else {
                const loc = e.coordinate;
                const bbox = `${loc[0]},${loc[1]},${loc[0]},${loc[1]}`;
                fetchPlotData(bbox).then((plotFeature) => {

                        if (plotFeature.features.length === 0) {
                            return;
                        }

                        const properties = plotFeature.features[0].properties;

                        const newPlot: Plot = {
                            brkGemeenteCode: properties.kadastraleGemeenteCode,
                            brkPerceelNummer: parseInt(properties.perceelnummer),
                            brkSectie: properties.sectie,
                            plotFeature,
                            subselectionGeometry: null
                        };

                        setSelectedPlots([...selectedPlots, newPlot]);
                    });
            }
        },
        [selectionMode, selectedPlotLayerSource, selectedPlots, getEditPermission],
    );

    const handleMultiselect = useCallback(
         (e: DrawEvent) => {
            if (selectionMode !== Buttons.SELECT || !map || !selectedPlotLayerSource || !multiselectBoxLayerSource || !getEditPermission()) return;

            const lineGeometry = e.feature.getGeometry();
            if (!lineGeometry || !(lineGeometry instanceof LineString)) return;

            const extent = lineGeometry.getExtent();
            const bufferDistance = 5;
            const bufferedExtent = buffer(extent, bufferDistance);

            const bbox = bufferedExtent.join(",");

            fetchPlotData(bbox).then((plotFeature) => {
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
                            subselectionGeometry: null
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
                multiselectBoxLayerSource.addFeature(new Feature({
                geometry: newPolygon,
            }));
        }, [selectionMode, map, selectedPlotLayerSource, multiselectBoxLayerSource, selectedPlots, getEditPermission]
    );

    const handleCut = useCallback((e: DrawEvent) => {
        if (selectionMode !== Buttons.CUT || !map || !selectedPlotLayerSource || !cutLayerSource || !getEditPermission()) return;

        const polygonGeometry = e.feature.getGeometry();
        if (!polygonGeometry || !(polygonGeometry instanceof Polygon)) return;

        const bbox = polygonGeometry.getExtent().join(",");
        const newCutPolygon = turf.polygon(polygonGeometry.getCoordinates());

        fetchPlotData(bbox).then((plotFeature) => {
            const updatedPlots = [...selectedPlots];
            let plotsUpdated = false;

            plotFeature.features.forEach((feature) => {
                const { kadastraleGemeenteCode, perceelnummer, sectie } = feature.properties;
                const existingPlot = selectedPlots.find(
                    (plot) =>
                        plot.brkGemeenteCode === kadastraleGemeenteCode &&
                        plot.brkPerceelNummer === parseInt(perceelnummer) &&
                        plot.brkSectie === sectie
                );

                if (existingPlot) {
                    const plotIndex = updatedPlots.indexOf(existingPlot);
                    const plotGeometry = feature.geometry as GeoJSONPolygon
                    const plotPolygon = turf.polygon(plotGeometry.coordinates as Coordinate[][]);
                    const intersectedPolygon = turf.intersect(turf.featureCollection([plotPolygon, newCutPolygon]));
                    const existingCoords = existingPlot.subselectionGeometry?.coordinates as Coordinate[][] || [];

                    const intersectingSubPolygons = existingPlot.subselectionGeometry?.coordinates
                        ?.map(coord => turf.polygon([coord] as Coordinate[][]))
                        .filter(subPlotPolygon => turf.booleanIntersects(subPlotPolygon, newCutPolygon)) || [];

                    let remainingCutPolygon = newCutPolygon;
                    let removeDrawing = false;

                    intersectingSubPolygons.forEach(subPolygon => {
                        const removeDrawingTest1 = turf.booleanWithin(subPolygon, newCutPolygon)
                        const removeDrawingTest2 = turf.booleanWithin(newCutPolygon, subPolygon)
                        if(removeDrawingTest1 || removeDrawingTest2){
                            removeDrawing = true;
                        } else {
                            const difference = turf.difference(turf.featureCollection([remainingCutPolygon, subPolygon]));

                            if (difference && difference.geometry.type === 'MultiPolygon') {
                                removeDrawing = true;
                            } else {
                                remainingCutPolygon = difference as Feat<Poly>;
                            }
                        }

                    });

                    if (remainingCutPolygon && !removeDrawing) {
                        const finalPolygon = intersectedPolygon
                            ? turf.intersect(turf.featureCollection([plotPolygon, remainingCutPolygon]))
                            : null;

                        if (finalPolygon) {
                            updatedPlots[plotIndex] = {
                                ...existingPlot,
                                subselectionGeometry: {
                                    type: "Polygon",
                                    coordinates: [...existingCoords, finalPolygon.geometry.coordinates[0] as Coordinate[]],
                                },
                            };
                            plotsUpdated = true;
                        }
                    } else {
                    cutLayerSource.removeFeature(e.feature);
                    }
                } else {
                    cutLayerSource.removeFeature(e.feature);
                }
            });

            if (plotsUpdated) {
                setSelectedPlots(updatedPlots);
            }
        });
    }, [selectionMode, map, selectedPlotLayerSource, cutLayerSource, selectedPlots, getEditPermission]);

const handleDelete = useCallback((e: MapBrowserEvent<UIEvent>) => {
    if (selectionMode !== Buttons.DELETE || !map || !selectedPlotLayerSource || !getEditPermission()) return;

    const features = map.getFeaturesAtPixel(e.pixel, { layerFilter: (layer) => layer.getSource() === selectedPlotLayerSource });
    const subFeatures = map.getFeaturesAtPixel(e.pixel, { layerFilter: (layer) => layer.getSource() === cutLayerSource });

    const clickedCoordinate = e.coordinate

    if (subFeatures.length > 0) {
        const clickedSubplotFeature = subFeatures[0];
        const clickedSubplotGeometry = clickedSubplotFeature.getGeometry() as Polygon;
        const clickedSubplotCoords = clickedSubplotGeometry.getCoordinates();

        let intersectingPolygon = null;
        for (let i = 0; i < clickedSubplotCoords.length; i++) {
            const polygonCoords = clickedSubplotCoords[i];
            const polygon = new Polygon([polygonCoords]);

            if (polygon.intersectsCoordinate(clickedCoordinate)) {
                intersectingPolygon = polygonCoords;
                break;
            }
        }

        if (intersectingPolygon) {
            const plot = selectedPlots.find(plot => {
                const id = plot.plotFeature.features[0].id;
                const clickedFeatureId = features[0]?.getId();
                return id === clickedFeatureId;
            });

            if (plot) {
                const updatedPlots = [...selectedPlots];
                const indexOfPlot = updatedPlots.indexOf(plot)

                const filteredSubselectionGeometry = (plot.subselectionGeometry?.coordinates as Coordinate[][]).filter((subselection) => {
                    return !_.isEqual(subselection, intersectingPolygon);
                });

                updatedPlots[indexOfPlot] = {
                    ...plot,
                    subselectionGeometry: filteredSubselectionGeometry.length > 0 ? {
                        type: "Polygon",
                        coordinates: filteredSubselectionGeometry,
                    } : null,
                };

                setSelectedPlots(updatedPlots);
            }
        }
    }

            else if (features.length > 0) {
                const newSelectedPlots = selectedPlots.filter((plot) => {
                    const id = plot.plotFeature.features[0].id;
                    const clickedFeatureId = features[0].getId();
                    return id !== clickedFeatureId;
                });
                setSelectedPlots(newSelectedPlots);
            }

}, [getEditPermission, map, selectedPlotLayerSource, selectionMode, selectedPlots, cutLayerSource]);

useEffect(
    function updatePlotsLayer() {
        if (!selectedPlotLayerSource) return;

        const changed = !_.isEqual(selectedPlots, originalSelectedPlots);
        setPlotsChanged(changed);
        selectedPlotLayerSource.clear();
        cutLayerSource?.clear();

        for (const selectedPlot of selectedPlots) {
            const geojson = new GeoJSON().readFeatures(selectedPlot.plotFeature);

            if (selectedPlot.subselectionGeometry) {
                const subselectionFeature = new GeoJSON().readFeature({
                    type: "Feature",
                    geometry: selectedPlot.subselectionGeometry,
                });

                cutLayerSource?.addFeature(subselectionFeature);
                geojson.forEach((feature) => {
                    feature.setStyle(selectedFeatureStyle(true));
                });
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
    }, [extent, originalSelectedPlots, selectedPlotLayerSource, selectedPlots, mapBounds, cutLayerSource, selectedFeatureStyle]);

    useEffect(
        function zoomToExtent() {
            if (extent) {
                map?.getView().fit(extent, { padding: [20, 20, 20, 20] });
            }
        },
        [extent, map],
    );

    useEffect(
        function updateProjectGeometry() {
            if (!projectLayerSource || !map) return;

            projectLayerSource.clear();

            if (selectedProject?.geometry) {
                try {
                    const geometry = JSON.parse(selectedProject.geometry);

                    const geojsonFeature = {
                        type: "Feature",
                        crs: geometry.crs,
                        geometry,
                        properties: {},
                    };

                    const feature = new GeoJSON().readFeature(geojsonFeature, {
                        featureProjection: map.getView().getProjection(),
                    });

                    projectLayerSource.addFeature(feature);

                    if (!projectLayerSource.isEmpty()) {
                        const extent = projectLayerSource.getExtent();
                        setExtent(extent);
                    }
                } catch (error) {
                    console.error("Error processing geometry:", error);
                }
            }
        },
        [projectLayerSource, selectedProject?.geometry, map, setExtent],
    );

    useEffect(() => {
        if (selectionMode !== Buttons.SELECT || !map || !getEditPermission()) return;

        const draw = new Draw({
            source: new VectorSource(),
            type: "LineString",
            maxPoints: 1,
            condition: (event) => {
                const click = event.originalEvent as MouseEvent;
                return click.button === 2; //Right click
            },
        });

        const handleEscape = (event: KeyboardEvent) => {
            if (event.key === "Escape") {
                draw.abortDrawing();
            }
        };

        document.addEventListener("keydown", handleEscape);

        draw.on("drawstart", () => {
            multiselectBoxLayerSource?.clear();
        })
        draw.on("drawend", handleMultiselect);
        map.addInteraction(draw);

        return () => {
            map.removeInteraction(draw);
            document.removeEventListener("keydown", handleEscape);
        };
    }, [map, selectionMode, handleMultiselect, multiselectBoxLayerSource, getEditPermission]);

    useEffect(() => {
        if (selectionMode !== Buttons.CUT || !map || !selectedPlotLayerSource || !getEditPermission()) return;

        let currentFeature: Feature<Polygon> | null = null;
        const snapTolerance = 10;

        const defaultPolygonStyle = new Style({
            stroke: new Stroke({
              color: colors.mapCutPolygonStrokeColor,
              width: 5
            }),
            image: new CircleStyle({
              radius: 6,
              fill: new Fill({
                color: colors.mapCutCircleFillColor
              }),
              stroke: new Stroke({
                color: colors.mapCutCircleStrokeColor,
                width: 1.5
              })
            }),
            fill: new Fill({
              color: colors.mapCutPolygonFillColor
            })
          });

        const draw = new Draw({
            source: cutLayerSource,
            type: "Polygon",
            minPoints: 3,
            style: defaultPolygonStyle,
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
            source: cutLayerSource,
            pixelTolerance: snapTolerance,
        });

        const snapInteractionProjectGeometry = new Snap({
            source: projectLayerSource,
            pixelTolerance: snapTolerance,
        });

        const handleEnter = (event: KeyboardEvent) => {
            if (event.key === "Enter") {
                if (currentFeature) {
                    const coordinates = currentFeature.getGeometry()?.getCoordinates()[0];
                    if (coordinates && coordinates.length > 4) {
                        draw.finishDrawing();
                    }
                }
            }
        };

        const handleEscape = (event: KeyboardEvent) => {
            if (event.key === "Escape") {
                draw.abortDrawing();
            }
        };

        document.addEventListener("keydown", handleEnter);
        document.addEventListener("keydown", handleEscape);

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
        map.addInteraction(snapInteractionProjectGeometry);

        return () => {
            map.removeInteraction(draw);
            map.removeInteraction(snapInteraction);
            map.removeInteraction(snapInteractionSubPlot);
            map.removeInteraction(snapInteractionProjectGeometry);
            document.removeEventListener("keydown", handleEnter);
            document.removeEventListener("keydown", handleEscape);
        };
    }, [map, selectionMode, handleCut, selectedPlotLayerSource, cutLayerSource, getEditPermission, projectLayerSource]);

    useEffect(() => {
        if (selectionMode !== Buttons.DELETE || !map || !getEditPermission()) return;

        const deletePointStyle = new Style({
            image: new CircleStyle({
                radius: 6,
                fill: new Fill({
                  color: colors.mapDeleteCircleFillColor
                }),
                stroke: new Stroke({
                  color: colors.mapDeleteCircleStrokeColor,
                  width: 1.5
                })
              })
          })

        const draw = new Draw({
            source: new VectorSource(),
            type: "Point",
            maxPoints: 1,
            style: deletePointStyle
        });

        map.addEventListener("click", handleDelete as Listener);
        map.addInteraction(draw);

        return () => {
            map.removeInteraction(draw);
            map.removeEventListener("click", handleDelete as Listener);
        };
    }, [map, selectionMode, handleDelete, getEditPermission]);

    useEffect(() => {
        if (!map || !getEditPermission()) return;

        const keyDownHandler = (e: KeyboardEvent) => {
            if (e.key === "Shift") {
                setPreviousSelectionMode(selectionMode);
                setSelectionMode(Buttons.DELETE);
            }
        };

        const keyUpHandler = (e: KeyboardEvent) => {
            if (e.key === "Shift") {
                setSelectionMode(previousSelectionMode);
            }
        };

        document.addEventListener("keydown", keyDownHandler);
        document.addEventListener("keyup", keyUpHandler);

        return () => {
            document.removeEventListener("keydown", keyDownHandler);
            document.removeEventListener("keyup", keyUpHandler);
        };
    }, [map, selectionMode, previousSelectionMode, getEditPermission]);

    useEffect(() => {
        if (!map || !getEditPermission()) return;

        map.addEventListener("click", handleSelect as Listener);
        return () => {
            map.removeEventListener("click", handleSelect as Listener);
        };
    }, [getEditPermission, handleSelect, map]);

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
            const selectedPlotLayer = new VectorLayer({ source: selectedPlotSource, style: selectedFeatureStyle(false) as unknown as StyleFunction });
            setSelectedPlotLayerSource(selectedPlotSource);

            const projectGeometrySource = new VectorSource();

            const projectGeometryLayer = new VectorLayer({
                source: projectGeometrySource,
                style: new Style({
                    fill: new Fill({ color: "rgba(0, 0, 0, 0.2)" }),
                    stroke: new Stroke({ color: "#000000", width: 5 }),
                }),
            });
            setProjectLayerSource(projectGeometrySource);


            const multiselectSource = new VectorSource();
            const multiselectLayer = new VectorLayer({
                source: multiselectSource,
                style: new Style({
                    fill: new Fill({ color: colors.mapMultiselectFillColor }),
                    stroke: new Stroke({ color: colors.mapMultiselectStrokeColor, width: 1 }),
                }),
            });
            setMultiselectBoxLayerSource(multiselectSource);

            const cutSource = new VectorSource();
            const cutLayer = new VectorLayer({
                source: cutSource,
                style: selectedFeatureStyle(false) as unknown as StyleFunction
            });
            setcutLayerSource(cutSource);

            const newMap = new Map({
                target: id,
                layers: [osmLayer, kadasterLayers, selectedPlotLayer, projectGeometryLayer, multiselectLayer, cutLayer],
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
