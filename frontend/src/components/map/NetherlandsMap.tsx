import type { LatLngTuple } from "leaflet";
import "leaflet/dist/leaflet.css";
import "leaflet-draw/dist/leaflet.draw.css";
import L from "leaflet";
import { MarkerItem } from "./MarkerItem";
import { FullscreenControl } from "react-leaflet-fullscreen";
import { EditControl } from "react-leaflet-draw";
import { FeatureGroup, MapContainer, TileLayer } from "react-leaflet";
type Props = {
    height: string;
    width: string;
};
const NetherlandsMap = ({ height, width }: Props) => {
    //Dummy Coordinates for the center of the Netherlands
    const center: LatLngTuple = [52.1326, 5.2913];
    const center2: LatLngTuple = [52.4326, 5.2913];

    return (
        <MapContainer center={center} zoom={12} style={{ height, width }}>
            <FullscreenControl position="topleft" />
            <TileLayer
                url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
                attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
            />
            <MarkerItem markerColor="red" position={center} popupText="First project" />
            <MarkerItem markerColor="blue" position={center2} popupText="Second project" />
            <FeatureGroup>
                <EditControl
                    onDrawStart={(e) => console.log(e)}
                    position="topleft"
                    onEdited={(e) => console.log(e)}
                    onCreated={(e) => console.log(e)}
                    onDeleted={(e) => console.log(e)}
                    draw={{
                        polyline: {
                            icon: new L.DivIcon({
                                iconSize: new L.Point(8, 8),
                                className: "leaflet-div-icon leaflet-editing-icon",
                            }),
                            shapeOptions: {
                                guidelineDistance: 10,
                                color: "navy",
                                weight: 3,
                            },
                        },
                        rectangle: true,
                        circlemarker: true,
                        circle: false,
                        polygon: true,
                        marker: false,
                    }}
                />
            </FeatureGroup>
        </MapContainer>
    );
};

export default NetherlandsMap;
