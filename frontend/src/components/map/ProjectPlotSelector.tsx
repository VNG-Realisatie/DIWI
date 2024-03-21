import { Map, View } from "ol";

import GeoJSON from "ol/format/GeoJSON.js";
import TileLayer from "ol/layer/Tile";
import VectorLayer from "ol/layer/Vector";
import { OSM, Vector as VectorSource } from "ol/source";

import ClearIcon from "@mui/icons-material/Clear";
import SaveIcon from "@mui/icons-material/Save";
import Tooltip from "@mui/material/Tooltip";

import _ from "lodash";
import queryString from "query-string";
import { useCallback, useContext, useEffect, useId, useState } from "react";
import { useTranslation } from "react-i18next";
import { Plot, PlotGeoJSON, getProjectPlots, updateProjectPlots } from "../../api/projectsServices";
import { Box, Stack } from "@mui/material";
import ProjectContext from "../../context/ProjectContext";
import { Details } from "../Details";
import { Extent } from "ol/extent";
import { Attribution, defaults as defaultControls } from "ol/control.js";
const ProjectPlotSelector = () => {
    const { t } = useTranslation();

    const { selectedProject } = useContext(ProjectContext);

    const id = useId();

    const [map, setMap] = useState<Map>();
    const [selectedPlotLayerSource, setSelectedPlotLayerSource] = useState<VectorSource>();

    const [originalSelectedPlots, setOriginalSelectedPlots] = useState<Plot[]>([]);
    const [selectedPlots, setSelectedPlots] = useState<Plot[]>([]);

    const [plotsChanged, setPlotsChanged] = useState(false);
    const [extent, setExtent] = useState<Extent | null>(null);

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

    const handleSaveChange = () => {
        if (selectedProject) {
            updateProjectPlots(selectedProject.projectId, selectedPlots).then(() => setOriginalSelectedPlots(selectedPlots));
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
                    const newSelectedPlots = [...selectedPlots, newPlot];
                    setSelectedPlots(newSelectedPlots);
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

            const source = new VectorSource();
            const selectedPlotLayer = new VectorLayer({ source });

            setSelectedPlotLayerSource(source);

            const newMap = new Map({
                target: id,
                layers: [osmLayer, selectedPlotLayer],
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

    return (
        <Stack my={1} p={1} mb={10}>
            <Box sx={{ cursor: "pointer" }} position="absolute" right={10} top={55}>
                {plotsChanged && (
                    <>
                        <Tooltip placement="top" title={t("generic.cancelChanges")}>
                            <ClearIcon sx={{ mr: 2, color: "#FFFFFF" }} onClick={handleCancelChange} />
                        </Tooltip>
                        <Tooltip placement="top" title={t("generic.saveChanges")}>
                            <SaveIcon sx={{ color: "#FFFFFF" }} onClick={handleSaveChange} />
                        </Tooltip>
                    </>
                )}
            </Box>
            <Stack direction="row" alignItems="center" justifyContent="space-between">
                <Stack overflow="auto" height="70vh">
                    <Details project={selectedProject} />
                </Stack>
                <div id={id} style={{ height: "70vh", width: "100%" }}></div>
            </Stack>
        </Stack>
    );
};

export default ProjectPlotSelector;

const baseUrlKadasterWms = "https://service.pdok.nl/kadaster/kadastralekaart/wms/v5_0";
