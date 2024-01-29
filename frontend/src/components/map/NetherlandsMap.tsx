import type { LatLngTuple } from "leaflet";
import "leaflet/dist/leaflet.css";
import L from "leaflet";
import { useEffect } from "react";

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
const NetherlandsMap = ({ height, width, mapData }: Props) => {
    //Dummy Coordinates for the center of the Netherlands

    const mapZoom = 10;

    function createMarkerIcon(color: string) {
        return L.divIcon({
            className: "location-icon",
            html: `<div class="location-icon" style="width: 30px; height: 30px; background-color: ${color}; border-radius: 50%; position: relative;"><div style="content: ''; position: absolute; width: 10px; height: 10px; background-color: #fff; border-radius: 50%; top: 50%; left: 50%; transform: translate(-50%, -50%);"></div><div style="content: ''; position: absolute; width: 10px; height: 10px; background-color: #fff; border-radius: 50%; top: 50%; left: 50%; transform: translate(-50%, -50%); "></div></div>`,
        });
    }
    useEffect(() => {
        const center: LatLngTuple = [52.1326, 5.2913];
        let map = new L.Map("map");
        map.on("click", (e) => map.setView(e.latlng, map.getZoom()));
        map.setView(center, mapZoom);
        L.tileLayer("https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png", {
            maxZoom: 19,
        }).addTo(map);

        let container = L.DomUtil.get("map");

        if (container != null) {
            //@ts-ignore
            container._leaflet_id = null;
        }

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
