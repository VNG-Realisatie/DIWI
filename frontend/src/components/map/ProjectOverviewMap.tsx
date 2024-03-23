import { Map, View } from "ol";

import TileLayer from "ol/layer/Tile";
import VectorLayer from "ol/layer/Vector";
import { OSM, TileWMS, Vector as VectorSource } from "ol/source";

import { Stack } from "@mui/material";
import { defaults as defaultControls } from "ol/control.js";
import { useContext, useEffect, useId, useState } from "react";
import { useNavigate } from "react-router-dom";
import * as Paths from "../../Paths";

import Feature from "ol/Feature";
import { Point } from "ol/geom";
import { Circle as CircleStyle, Fill, Stroke, Style, Text as StyleText } from "ol/style";
import { StyleFunction } from "ol/style/Style";
import { ProjectListModel, getProjects } from "../../api/projectsServices";
import ConfigContext from "../../context/ConfigContext";
import { components } from "../../types/schema";
import { mapBoundsToExtent } from "../../utils/map";

const geoMarker = (f: Feature): Style => {
    // check feature props for style
    const fcolor = f.get("color") ?? "white";
    const ftext = f.get("name") ?? "";
    return new Style({
        image: new CircleStyle({
            radius: 10,
            fill: new Fill({ color: fcolor }),
            stroke: new Stroke({
                color: "white",
                width: 2,
            }),
        }),
        // draw rectangle behind text !!! does not work cannot have duplicate image keys in style object...

        // const charHeight = 20;
        // const charWidth = 5;
        // const ftextLength = ftext.length * 1.33 * charWidth; // this currenlty needs a weird factor: 1.33 to account for weird scaling
        // image: new RegularShape({
        //     fill: new Fill({ color: fcolor }),
        //     points: 4,
        //     radius: ftextLength,
        //     angle: Math.PI / 4,
        //     scale: [1, charHeight / ftextLength],
        // }),
        // insert project name
        text: new StyleText({
            text: ftext,
            offsetY: -20,
            font: "16px Calibri,sans-serif",
            fill: new Fill({ color: "#222" }),
            stroke: new Stroke({ color: "#fff", width: 0 }),
        }),
    });
};

const ProjectOverviewMap = () => {
    const id = useId();
    const navigate = useNavigate();

    const { mapBounds } = useContext(ConfigContext);

    const [projects, setProjects] = useState<ProjectListModel[]>();
    const [projectsLayerSource, setProjectsLayerSource] = useState<VectorSource>();

    useEffect(function fetchPlots() {
        getProjects(1, 1000).then((projects) => {
            setProjects(projects);
        });
    }, []);
    useEffect(
        function makeMarkers() {
            if (!projectsLayerSource) return;
            if (!projects) return;

            const markers = projects
                .filter((p) => p.location)
                .map((p) => {
                    const location = p.location as components["schemas"]["LocationModel"];
                    const marker = new Feature({
                        type: "icon",
                        geometry: new Point([location.lat, location.lng]),
                        id: p.projectId,
                        color: p.projectColor,
                        name: p.projectName,
                    });
                    return marker;
                });
            projectsLayerSource.clear();
            projectsLayerSource.addFeatures(markers);
        },
        [projects, projectsLayerSource],
    );

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
            const selectedPlotLayer = new VectorLayer({ source, style: geoMarker as StyleFunction });

            setProjectsLayerSource(source);

            const extent = mapBoundsToExtent(mapBounds);
            let view = new View({ extent: extent });
            view.fit(extent);

            const newMap = new Map({
                target: id,
                layers: [osmLayer, kadasterLayers, selectedPlotLayer],
                view: view,
                controls: defaultControls({ attribution: false }),
            });

            /* Add a pointermove handler to the map to render the popup.*/
            newMap.on("singleclick", function (evt) {
                const feature = newMap.forEachFeatureAtPixel(evt.pixel, function (feat, layer) {
                    return feat;
                });
                if (feature) {
                    const projectId = feature.get("id");
                    if (projectId) {
                        navigate(Paths.projectDetail.path.replace(":id", projectId));
                    }
                }
            });

            return () => {
                newMap.dispose();
                const mapElement = document.getElementById(id);
                if (mapElement) {
                    mapElement.innerHTML = "";
                }
            };
        },
        [id, mapBounds, navigate],
    );

    return (
        <Stack my={1} p={1} mb={10}>
            <div id={id} style={{ height: "70vh", width: "100%" }}></div>
        </Stack>
    );
};

export default ProjectOverviewMap;
