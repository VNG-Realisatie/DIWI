import { components } from "../types/schema";
import { getJson, putJson } from "../utils/requests";
import { API_URI } from "../utils/urls";

export type CustomPropDefinitions = components["schemas"]["PropertyModel"];
export type CustomPropertyValue = components["schemas"]["ProjectHouseblockCustomPropertyModel"];

export async function getCustomPropertyValues(id: string): Promise<CustomPropertyValue[]> {
    return getJson(`${API_URI}/projects/${id}/customproperties`);
}

export async function putCustomPropertyValue(id: string, newData: CustomPropertyValue): Promise<CustomPropertyValue> {
    return putJson(`${API_URI}/projects/${id}/customproperties`, newData);
}

export async function getBlockCustomPropertyValues(id: string): Promise<CustomPropertyValue[]> {
    return getJson(`${API_URI}/houseblock/${id}/customproperties`);
}

export async function putBlockCustomPropertyValue(id: string, newData: CustomPropertyValue): Promise<CustomPropertyValue> {
    return putJson(`${API_URI}/houseblock/${id}/customproperties`, newData);
}
