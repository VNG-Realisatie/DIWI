import { components } from "../types/schema";
import { getJson, putJson } from "../utils/requests";
import { API_URI } from "../utils/urls";

export type CustomPropDefinitions = components["schemas"]["PropertyModel"];
export type CustomPropertyValue = components["schemas"]["ProjectHouseblockCustomPropertyModel"];

export async function getCustomPropertyValues(id: string): Promise<CustomPropertyValue[]> {
    return getJson(`${API_URI}/projects/${id}/customproperties`);
}

export async function putCustomPropertyValues(id: string, newData: CustomPropertyValue): Promise<any> {
    return putJson(`${API_URI}/projects/${id}/customproperties`, newData);
}

export async function getBlockCustomPropertyValues(id: string): Promise<CustomPropertyValue[]> {
    return getJson(`${API_URI}/houseblock/${id}/customproperties`);
}

export async function putBlockCustomPropertyValues(id: string, newData: CustomPropertyValue): Promise<any> {
    return putJson(`${API_URI}/houseblock/${id}/customproperties`, newData);
}
