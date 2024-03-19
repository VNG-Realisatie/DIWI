import type { LatLngTuple, Map } from "leaflet";
import "leaflet/dist/leaflet.css";
import "leaflet-draw/dist/leaflet.draw.css";
import L, { latLng } from "leaflet";
import "leaflet-draw";
import { useEffect, useId, useRef } from "react";
import PlusButton from "../PlusButton";
import * as Paths from "../../Paths";
import { useTranslation } from "react-i18next";
import queryString from "query-string";

type Marker = {
    projectColor: string;
    projectName: string;
    coordinate: any;
};

type Props = {
    height: string;
    width: string;
    mapData: Marker[];
    plusButton?: boolean;
};
//Dummy Coordinates for the center of the Netherlands
const center: LatLngTuple = [52.1326, 5.2913];

const crs = L.CRS.EPSG4326;
const NetherlandsMap = ({ height, width, mapData, plusButton }: Props) => {
    const mapRef = useRef<Map>();
    const id = useId();

    const mapZoom = 10;
    const { t } = useTranslation();

    fetchStuff();

    function createMarkerIcon(color: string) {
        return L.divIcon({
            className: "location-icon",
            html: `<div class="location-icon" style="width: 30px; height: 30px; background-color: ${color}; border-radius: 50%; position: relative;"><div style="content: ''; position: absolute; width: 10px; height: 10px; background-color: #fff; border-radius: 50%; top: 50%; left: 50%; transform: translate(-50%, -50%);"></div><div style="content: ''; position: absolute; width: 10px; height: 10px; background-color: #fff; border-radius: 50%; top: 50%; left: 50%; transform: translate(-50%, -50%); "></div></div>`,
        });
    }
    useEffect(() => {
        if (mapRef.current) {
            return;
        }

        mapRef.current = L.map(id, { crs: crs });
        const map = mapRef.current;

        map.setView(center, mapZoom);

        // L.tileLayer
        //     .wms("https://service.pdok.nl/kadaster/kadastralekaart/wms/v5_0", { layers: "Kadastralekaart", minZoom: 17, maxNativeZoom: 22, maxZoom: 23 })
        //     .addTo(map);

        L.tileLayer("https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png", {
            maxZoom: 17,
        }).addTo(map);

        // selectedPlotLayer.addData(geojson as GeoJSON.GeoJsonObject);

        // Initialize Leaflet.draw control
        // const drawnItems = new L.FeatureGroup();
        // map.addLayer(drawnItems);

        // const drawControl = new L.Control.Draw({
        //     edit: {
        //         featureGroup: drawnItems,
        //     },
        // });
        // map.addControl(drawControl);

        // Handle draw events
        // map.on(L.Draw.Event.CREATED, function (event) {
        //     const layer = event.layer;
        //     drawnItems.addLayer(layer);
        // });

        map.on("click", async (event) => {
            // const crs = L.CRS.EPSG4326;
            console.log(event.latlng);
            // event.latlng.lat

            // const bbox = map.getBounds().toBBoxString();
            // console.log(bbox);
            // const bbox = bounds.toBBoxString();
            const bounds = map.getBounds();
            // const bbox = `${nw.x},${nw.y},${se.x},${se.y}`;

            const loc = crs.project(event.latlng);

            const bboxSize = 1;
            const bbox = `${loc.x - bboxSize},${loc.y - bboxSize},${loc.x + bboxSize},${loc.y + bboxSize}`;
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
                    CRS: crs.code,
                    BBOX: bbox,
                },
            });

            const res = await fetch(url);
            const result = await res.json();
            delete result.crs;
            result.features[0].coordinates[0].map((c: [number, number]) => {
                return crs.unproject(c);
            });
            L.geoJSON(result, {
                style: {
                    fillColor: "#FF00FF",
                    fillOpacity: 1,
                    color: "#B04173",
                },
            }).addTo(map);
            console.log("on click", result);
        });
        //Get this from backend
        mapData.map((data) => {
            return L.marker(data.coordinate, { icon: createMarkerIcon(data.projectColor) })
                .addTo(map)
                .bindPopup(data.projectName);
        });
    }, [mapData, id]);

    return (
        <div id={id} style={{ width, height }}>
            {plusButton && <PlusButton color="#002C64" link={Paths.projectAdd.path} text={t("projects.createNewProject")} />}
        </div>
    );
};

export default NetherlandsMap;

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
