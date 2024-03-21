import { Map, View } from "ol";

import TileLayer from "ol/layer/Tile";
import VectorLayer from "ol/layer/Vector";
import { OSM, TileWMS, Vector as VectorSource } from "ol/source";

import { Stack } from "@mui/material";
import { defaults as defaultControls } from "ol/control.js";
import { useEffect, useId, useState } from "react";

import Feature from "ol/Feature";
import { Point } from "ol/geom";
import { Circle as CircleStyle, Fill, Stroke, Style } from "ol/style";
import { ProjectListModel, getProjects } from "../../api/projectsServices";
import { components } from "../../types/schema";

const geoMarker = new Style({
    image: new CircleStyle({
        radius: 7,
        fill: new Fill({ color: "black" }),
        stroke: new Stroke({
            color: "white",
            width: 2,
        }),
    }),
});
const ProjectOverviewMap = () => {
    const id = useId();

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
            const selectedPlotLayer = new VectorLayer({ source, style: geoMarker });

            setProjectsLayerSource(source);

            const newMap = new Map({
                target: id,
                layers: [osmLayer, kadasterLayers, selectedPlotLayer],
                view: new View({
                    center: [521407.57221923344, 6824704.512308201],
                    zoom: 12,
                }),
                controls: defaultControls({ attribution: false }),
            });

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
            <div id={id} style={{ height: "70vh", width: "100%" }}></div>
        </Stack>
    );
};

export default ProjectOverviewMap;
