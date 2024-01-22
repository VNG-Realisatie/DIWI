import L, { LatLngTuple } from "leaflet";
import { Marker, Popup } from "react-leaflet";
type Props = {
    markerColor: string;
    position: LatLngTuple;
    popupText: string;
};
export const MarkerItem = ({ markerColor, position, popupText }: Props) => {
    const createCustomIcon = (color: string) => {
        return L.icon({
            iconUrl: `https://raw.githubusercontent.com/pointhi/leaflet-color-markers/master/img/marker-icon-2x-${color}.png`,
            iconSize: [25, 41],
            iconAnchor: [12, 41],
            popupAnchor: [1, -34],
            shadowSize: [41, 41],
        });
    };
    const icon = createCustomIcon(markerColor);
    return (
        <Marker position={position} icon={icon}>
            <Popup>{popupText}</Popup>
        </Marker>
    );
};
