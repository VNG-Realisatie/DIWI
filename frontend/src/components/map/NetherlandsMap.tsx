import type { LatLngTuple, Map } from "leaflet";
import "leaflet/dist/leaflet.css";
import "leaflet-draw/dist/leaflet.draw.css";
import L from "leaflet";
import "leaflet-draw";
import { useEffect, useRef } from "react";

type Marker = {
    projectColor: string;
    projectName: string;
    coordinate: any;
};

type Props = {
    height: string;
    width: string;
    mapData: Marker[];
};

//Dummy Coordinates for the center of the Netherlands
const center: LatLngTuple = [52.1326, 5.2913];

const NetherlandsMap = ({ height, width, mapData }: Props) => {
    const mapRef = useRef<Map>();

    const mapZoom = 10;

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

        mapRef.current = L.map("map");
        const map = mapRef.current;

        map.setView(center, mapZoom);

        L.tileLayer.wms("https://service.pdok.nl/kadaster/kadastralekaart/wms/v5_0", { layers: "Kadastralekaart", minZoom: 17 }).addTo(map);

        L.tileLayer("https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png", {
            maxZoom: 17,
        }).addTo(map);
        // Initialize Leaflet.draw control
        const drawnItems = new L.FeatureGroup();
        map.addLayer(drawnItems);

        const drawControl = new L.Control.Draw({
            edit: {
                featureGroup: drawnItems,
            },
        });
        map.addControl(drawControl);

        // Handle draw events
        map.on(L.Draw.Event.CREATED, function (event) {
            const layer = event.layer;
            drawnItems.addLayer(layer);
        });

        //Get this from backend
        mapData.map((data) => {
            return L.marker(data.coordinate, { icon: createMarkerIcon(data.projectColor) })
                .addTo(map)
                .bindPopup(data.projectName);
        });
    }, [mapData]);

    return <div id="map" style={{ width, height }}></div>;
};

export default NetherlandsMap;
