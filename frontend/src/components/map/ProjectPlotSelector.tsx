import { View, Map } from "ol";

import TileLayer from "ol/layer/Tile";
import VectorLayer from "ol/layer/Vector";
import { OSM, Vector as VectorSource } from "ol/source";
import GeoJSON from "ol/format/GeoJSON.js";

import queryString from "query-string";
import { useCallback, useEffect, useId, useRef, useState } from "react";

type PlotGeoJSON = {
    type: string;
    features: [
        {
            id: string;
            type: string;
            bbox: number[];
            geometry: {
                type: string;
                coordinates: number[][][];
            };
            properties: {
                AKRKadastraleGemeenteCodeCode: string;
                AKRKadastraleGemeenteCodeWaarde: string;
                beginGeldigheid: string;
                identificatieLokaalID: string;
                identificatieNamespace: string;
                kadastraleGemeenteCode: string;
                kadastraleGemeenteWaarde: string;
                kadastraleGrootteWaarde: string;
                perceelnummer: string;
                perceelnummerPlaatscoordinaatX: string;
                perceelnummerPlaatscoordinaatY: string;
                perceelnummerRotatie: string;
                perceelnummerVerschuivingDeltaX: string;
                perceelnummerVerschuivingDeltaY: string;
                sectie: string;
                soortGrootteCode: string;
                soortGrootteWaarde: string;
                statusHistorieCode: string;
                statusHistorieWaarde: string;
                tijdstipRegistratie: string;
                volgnummer: string;
            };
        },
    ];
    crs: {
        properties: {
            name: string;
        };
        type: string;
    };
};

type Props = {
    height?: string;
    width?: string;
    plusButton?: boolean;
};

const ProjectPlotSelector = ({ height, width, plusButton }: Props) => {
    const id = useId();

    const [map, setMap] = useState<Map>();
    const [selectedPlotLayerSource, setSelectedPlotLayerSource] = useState<VectorSource>();
    const [selectedPlots, setSelectedPlots] = useState<PlotGeoJSON[]>([]);

    // fetchStuff();
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
                    const newSelectedPlots = [...selectedPlots, result];
                    console.log("new selected plots:", newSelectedPlots);
                    setSelectedPlots(newSelectedPlots);
                });
        },
        [selectedPlots],
    );

    useEffect(() => {
        if (!selectedPlotLayerSource) return;

        selectedPlotLayerSource.clear();
        for (const selectedPlot of selectedPlots) {
            const geojson = new GeoJSON().readFeatures(selectedPlot);
            selectedPlotLayerSource.addFeatures(geojson);
        }
    }, [selectedPlotLayerSource, selectedPlots]);

    useEffect(() => {
        if (!map) return;

        map.addEventListener("click", handleClick);
        return () => {
            map.removeEventListener("click", handleClick);
        };
    }, [handleClick, map]);

    useEffect(() => {
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
        });
        setMap(newMap);
        return () => {
            console.log("clear!");
            newMap.dispose();
            // mapRef.current = undefined;
            const mapElement = document.getElementById(id);
            if (mapElement) {
                mapElement.innerHTML = "";
            }
        };
    }, [id]);

    return (
        <div id={id} style={{ width, height }}>
            {/* {plusButton && <PlusButton color="#002C64" link={Paths.projectAdd.path} text={t("projects.createNewProject")} />} */}
        </div>
    );
};

export default ProjectPlotSelector;

const baseUrlKadasterWms = "https://service.pdok.nl/kadaster/kadastralekaart/wms/v5_0";
