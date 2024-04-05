import { Map, Overlay, View } from "ol";

import TileLayer from "ol/layer/Tile";
import VectorLayer from "ol/layer/Vector";
import { OSM, TileWMS, Vector as VectorSource } from "ol/source";

import { Stack } from "@mui/material";
import { defaults as defaultControls } from "ol/control.js";
import { useContext, useEffect, useRef, useState } from "react";
import { useNavigate } from "react-router-dom";
import * as Paths from "../../Paths";

import Feature from "ol/Feature";
import { Point } from "ol/geom";
import { Circle as CircleStyle, Fill, Stroke, Style } from "ol/style";
import { StyleFunction } from "ol/style/Style";
import { ProjectListModel, getProjects } from "../../api/projectsServices";
import ConfigContext from "../../context/ConfigContext";
import { components } from "../../types/schema";
import { mapBoundsToExtent } from "../../utils/map";

const geoMarker = (f: Feature): Style => {
    // check feature props for style
    const fcolor = f.get("color") ?? "white";
    return new Style({
        image: new CircleStyle({
            radius: 10,
            fill: new Fill({ color: fcolor }),
            stroke: new Stroke({
                color: "white",
                width: 2,
            }),
        }),
    });
};

const ProjectOverviewMap = () => {
    const navigate = useNavigate();

    const { mapBounds } = useContext(ConfigContext);

    const mapElement = useRef<HTMLDivElement>(null);
    const tooltipElement = useRef<HTMLDivElement>(null);

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

            const tooltipOverlay = new Overlay({
                element: tooltipElement.current as HTMLElement,
                offset: [0, -10],
                positioning: "bottom-center",
            });

            const newMap = new Map({
                target: mapElement.current as HTMLElement,
                layers: [osmLayer, kadasterLayers, selectedPlotLayer],
                view: view,
                controls: defaultControls({ attribution: false }),
                overlays: [tooltipOverlay],
            });

            /* Add a pointermove handler to the map to render the tooltip.*/
            newMap.on("pointermove", function (evt) {
                const feature = newMap.forEachFeatureAtPixel(evt.pixel, (f) => f);
                if (feature && tooltipElement.current) {
                    tooltipElement.current.style.display = "";
                    tooltipElement.current.innerHTML = feature.get("name");
                    const featureCoord = feature.get("geometry") as Point;
                    tooltipOverlay.setPosition(featureCoord.getFlatCoordinates());
                } else if (tooltipElement.current) {
                    tooltipElement.current.style.display = "none";
                }
            });

            /* Add a pointerclick handler to navigate to selected project */
            newMap.on("singleclick", function (evt) {
                const feature = newMap.forEachFeatureAtPixel(evt.pixel, (f) => f);
                if (feature) {
                    const projectId = feature.get("id");
                    if (projectId) {
                        navigate(Paths.projectDetail.toPath(":projectId", projectId));
                    }
                }
            });

            return () => newMap.setTarget(undefined);
        },
        [mapBounds, navigate],
    );

    return (
        <Stack my={1} p={1} mb={10}>
            <div ref={mapElement} className="map-container" style={{ height: "70vh", width: "100%" }}>
                <div
                    ref={tooltipElement}
                    className="tooltip"
                    style={{
                        position: "relative",
                        padding: "3px",
                        background: "rgba(0, 0, 0, .7)",
                        color: "white",
                        opacity: 1,
                        whiteSpace: "nowrap",
                        font: "10pt sans-serif",
                    }}
                />
            </div>
        </Stack>
    );
};

export default ProjectOverviewMap;
