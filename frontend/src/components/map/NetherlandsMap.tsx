import { MapContainer, TileLayer } from "react-leaflet";
import type { LatLngTuple } from "leaflet";
import "leaflet/dist/leaflet.css";

import { MarkerItem } from "./MarkerItem";
const NetherlandsMap = () => {
    // Coordinates for the center of the Netherlands
    const center: LatLngTuple = [52.1326, 5.2913];
    const center2: LatLngTuple = [52.4326, 5.2913];

    return (
        <MapContainer center={center} zoom={12} style={{ height: "500px", width: "1000px" }}>
            <TileLayer
                url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
                attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
            />
            <MarkerItem markerColor="red" position={center} popupText="First project" />
            <MarkerItem markerColor="blue" position={center2} popupText="Second project" />
        </MapContainer>
    );
};

export default NetherlandsMap;
