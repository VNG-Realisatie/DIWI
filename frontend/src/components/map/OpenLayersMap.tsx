import { View, Map } from "ol";

import TileLayer from "ol/layer/Tile";
import VectorLayer from "ol/layer/Vector";
import { OSM, Vector as VectorSource } from "ol/source";
import GeoJSON from "ol/format/GeoJSON.js";

import queryString from "query-string";
import { useEffect, useId, useRef } from "react";
import { useTranslation } from "react-i18next";

// type Props = {
//     height: string;
//     width: string;
//     mapData: Marker[];
//     plusButton?: boolean;
// };

const OpenLayersMap = () => {
    const mapRef = useRef<Map>();
    const id = useId();

    const mapZoom = 10;
    const { t } = useTranslation();

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
        map.addEventListener("click", (e) => {
            console.log(e);
            // @ts-ignore
            const loc = e.coordinate;
            console.log(loc);

            const bboxSize = 1;
            const bbox = `${loc[0] - bboxSize},${loc[1] - bboxSize},${loc[0] + bboxSize},${loc[1] + bboxSize}`;
            console.log(bbox);
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
                    console.log(result);
                    map.map.addLayer(
                        new VectorLayer({
                            source: new VectorSource({
                                features: new GeoJSON().readFeatures(result),
                            }),
                        }),
                    );
                });
        });

        mapRef.current = map;
    }, [id]);

    return (
        <div id={id} style={{ width: "500px", height: "500px" }}>
            {/* {plusButton && <PlusButton color="#002C64" link={Paths.projectAdd.path} text={t("projects.createNewProject")} />} */}
        </div>
    );
};

export default OpenLayersMap;

const baseUrlKadasterWms = "https://service.pdok.nl/kadaster/kadastralekaart/wms/v5_0";
