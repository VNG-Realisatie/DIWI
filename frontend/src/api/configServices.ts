import { getJson } from "../utils/requests";
import { API_URI } from "../utils/urls";

export type MapBounds = {
    corner1: {
        lat: number;
        lng: number;
    };
    corner2: {
        lat: number;
        lng: number;
    };
};

export type ConfigType = {
    defaultMapBounds: MapBounds;
    municipalityName: string;
};

export async function getConfig(): Promise<ConfigType> {
    return getJson(`${API_URI}/config`);
}
