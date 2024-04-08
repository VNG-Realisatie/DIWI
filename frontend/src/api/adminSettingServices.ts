import { ObjectType } from "../types/enums";
import { components } from "../types/schema";
import { deleteJson, getJson, postJson, putJson } from "../utils/requests";
import { API_URI } from "../utils/urls";

export type CategoryType = components["schemas"]["SelectDisabledModel"];
export type Property = components["schemas"]["PropertyModel"];

export async function getCustomProperties(): Promise<Array<Property>> {
    return getJson(`${API_URI}/properties`);
}

export async function getCustomPropertiesWithQuery(query: ObjectType): Promise<Array<Property>> {
    return getJson(`${API_URI}/properties?objectType=${query}`);
}

export async function getCustomProperty(id: string): Promise<Property> {
    return getJson(`${API_URI}/properties/${id}`);
}

export async function addCustomProperty(newData: Property): Promise<Property> {
    return postJson(`${API_URI}/properties`, newData);
}

export async function updateCustomProperty(id: string, newData: Property): Promise<Property> {
    return putJson(`${API_URI}/properties/${id}`, newData);
}

export async function deleteCustomProperty(id: string): Promise<Property> {
    return deleteJson(`${API_URI}/properties/${id}`);
}
