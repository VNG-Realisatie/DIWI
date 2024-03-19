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
                    map.addLayer(
                        new VectorLayer({
                            source: new VectorSource({
                                features: new GeoJSON().readFeatures(result),
                            }),
                        }),
                    );
                });

            // map.addLayer();
        });

        mapRef.current = map;
        // map.on("click", async (event) => {
        //     // const crs = L.CRS.EPSG4326;
        //     console.log(event.latlng);
        //     // event.latlng.lat

        //     // const bbox = map.getBounds().toBBoxString();
        //     // console.log(bbox);
        //     // const bbox = bounds.toBBoxString();
        //     const bounds = map.getBounds();
        //     const nw = crs.project(bounds.getNorthWest());
        //     const se = crs.project(bounds.getSouthEast());
        //     // const bbox = `${nw.x},${nw.y},${se.x},${se.y}`;

        //     const loc = crs.project(event.latlng);

        //     const bboxSize = 1 / 100_000;
        //     const bbox = `${loc.x - bboxSize},${loc.y - bboxSize},${loc.x + bboxSize},${loc.y + bboxSize}`;
        //     console.log(bbox);
        //     const url = queryString.stringifyUrl({
        //         url: baseUrlKadasterWms,
        //         query: {
        //             QUERY_LAYERS: "Perceel",
        //             INFO_FORMAT: "application/json",
        //             REQUEST: "GetFeatureInfo",
        //             SERVICE: "WMS",
        //             VERSION: "1.3.0",
        //             HEIGHT: "101", // What?
        //             WIDTH: "101", // What?
        //             I: "50", // What?
        //             J: "50", // What?
        //             layers: "Perceel",
        //             CRS: crs.code,
        //             BBOX: bbox,
        //         },
        //     });

        //     const res = await fetch(url);
        //     const result = await res.json();
        //     // selectedPlotLayer.addData(epsg);
        //     console.log("on click", result);
        // });
        //Get this from backend
        // mapData.map((data) => {
        //     return L.marker(data.coordinate, { icon: createMarkerIcon(data.projectColor) })
        //         .addTo(map)
        //         .bindPopup(data.projectName);
        // });
    }, [id]);

    return (
        <div id={id} style={{ width: "500px", height: "500px" }}>
            {/* {plusButton && <PlusButton color="#002C64" link={Paths.projectAdd.path} text={t("projects.createNewProject")} />} */}
        </div>
    );
};

export default OpenLayersMap;

async function fetchStuff() {
    fetchCapabilityWms();
    fetchFeatureInfoWms3857();
    // fetchFeatureInfoWms();
    fetchFeatureInfoWms4326();
}

const baseUrlKadasterWms = "https://service.pdok.nl/kadaster/kadastralekaart/wms/v5_0";

async function fetchCapabilityWms() {
    const url = queryString.stringifyUrl({
        url: baseUrlKadasterWms,
        query: {
            REQUEST: "GetCapabilities",
            SERVICE: "WMS",
        },
    });

    const res = await fetch(url);
    const result = await res.text();
    console.log("Capabilities", result);
}

async function fetchFeatureInfoWms4326() {
    const lat = 571179.1781929513 / 100_000;
    const lng = 6816654.835548575 / 100_000;
    const lat2 = 571193.89834407 / 100_000;
    const lng2 = 6816669.697605649 / 100_000;
    const bbox = `${lat},${lng},${lat2},${lng2}`;
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
            // CRS: "EPSG:28992", // RD coords
            CRS: "EPSG:4326",
            BBOX: bbox,
        },
    });

    const res = await fetch(url);
    const result = await res.text();
    console.log("EPSG:4326", result);
}

async function fetchFeatureInfoWms3857() {
    const crs = "EPSG:3857";
    const lat = 571179.1781929513; /// 100_000;
    const lng = 6816654.835548575; /// 100_000;
    const lat2 = 571193.89834407; /// 100_000;
    const lng2 = 6816669.697605649; /// 100_000;
    const bbox = `${lat},${lng},${lat2},${lng2}`;
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
            // CRS: "EPSG:28992", // RD coords
            CRS: crs,
            BBOX: bbox,
        },
    });

    const res = await fetch(url);
    const result = await res.text();
    console.log("EPSG:3857", result);
}

// QUERY_LAYERS
// 	Perceel
// INFO_FORMAT
// 	application/json
// REQUEST
// 	GetFeatureInfo
// SERVICE
// 	WMS
// VERSION
// 	1.3.0
// FORMAT
// 	image/png
// STYLES

// TRANSPARENT
// 	true
// layers
// 	Perceel
// FEATURE_COUNT
// 	8
// I
// 	50
// J
// 	50
// WIDTH
// 	101
// HEIGHT
// 	101
// CRS
// 	EPSG:28992
// BBOX
// 	137441.18940484137,455927.1762410904,137450.28376863306,455936.27060488204
