import { getJson } from "../utils/requests";
import { API_URI } from "../utils/urls";

export async function getMunicipalityList(): Promise<Array<any>> {
    return getJson(`${API_URI}/municipality/list`);
}

export async function getBuurtList(): Promise<Array<any>> {
    return getJson(`${API_URI}/buurt/list`);
}

export async function getWijkList(): Promise<Array<any>> {
    return getJson(`${API_URI}/wijk/list`);
}


