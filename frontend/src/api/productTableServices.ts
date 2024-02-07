import { getJson } from "../utils/requests";
import { API_URI } from "../utils/urls";

export type ProductTableOption = {
    name: string;
    id: string;
};
export async function getMunicipalityList(): Promise<Array<ProductTableOption>> {
    return getJson(`${API_URI}/municipality/list`);
}

export async function getBuurtList(): Promise<Array<ProductTableOption>> {
    return getJson(`${API_URI}/buurt/list`);
}

export async function getWijkList(): Promise<Array<ProductTableOption>> {
    return getJson(`${API_URI}/wijk/list`);
}
