import { View, Map } from "ol";

import TileLayer from "ol/layer/Tile";
import VectorLayer from "ol/layer/Vector";
import { OSM, Vector as VectorSource } from "ol/source";
import GeoJSON from "ol/format/GeoJSON.js";

import queryString from "query-string";
import { useEffect, useId, useRef, useState } from "react";
import { dummyData } from "./dummy";

// type Props = {
//     height: string;
//     width: string;
//     mapData: Marker[];
//     plusButton?: boolean;
// };
type LayerType = {
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

const PlotSelectorMap = () => {
    const mapRef = useRef<Map>();
    const id = useId();
    const [selectedPlot, setSelectedPlot] = useState<LayerType>();
    const [selectedPlots, setSelectedPlots] = useState<LayerType[]>([]);

    // fetchStuff();

    useEffect(() => {
        if (mapRef.current) {
            return;
        }

        const raster = new TileLayer({
            source: new OSM(),
        });

        const source = new VectorSource({ wrapX: false });

        const vector = new VectorLayer({
            source: source,
        });

        const map = new Map({
            target: id,
            layers: [raster, vector],
            // target: "map",
            view: new View({
                center: [521407.57221923344, 6824704.512308201],
                zoom: 16,
            }),
        });
        //This will come from backend
        dummyData.map((d) => {
            return map.addLayer(
                new VectorLayer({
                    source: new VectorSource({
                        features: new GeoJSON().readFeatures(d),
                    }),
                }),
            );
        });

        map.addEventListener("click", (e) => {
            // console.log(e);
            // @ts-ignore
            const loc = e.coordinate;
            // console.log(loc);

            const bboxSize = 1;
            const bbox = `${loc[0] - bboxSize},${loc[1] - bboxSize},${loc[0] + bboxSize},${loc[1] + bboxSize}`;
            // console.log(bbox);
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
                .then((result) => {
                    setSelectedPlot(result);
                    map.addLayer(
                        new VectorLayer({
                            source: new VectorSource({
                                features: new GeoJSON().readFeatures(result),
                            }),
                        }),
                    );
                });
        });
        mapRef.current = map;
    }, [id, selectedPlots]);
    useEffect(() => {
        const filteredPlots: any = selectedPlots.filter((sp: any) => {
            return sp?.features[0].id !== selectedPlot?.features[0].id;
        });
        selectedPlot && setSelectedPlots(filteredPlots ? [...filteredPlots, selectedPlot] : [selectedPlot]);
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [selectedPlot]);
    //This will update backend later
    console.log(selectedPlot);
    return (
        <div id={id} style={{ width: "500px", height: "500px" }}>
            {/* {plusButton && <PlusButton color="#002C64" link={Paths.projectAdd.path} text={t("projects.createNewProject")} />} */}
        </div>
    );
};

export default PlotSelectorMap;

const baseUrlKadasterWms = "https://service.pdok.nl/kadaster/kadastralekaart/wms/v5_0";
