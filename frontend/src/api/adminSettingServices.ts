import { ObjectType, PropertyType } from "../types/enums";
import { deleteJson, getJson, postJson, putJson } from "../utils/requests";
import { API_URI } from "../utils/urls";
export type CategoryType = {
    id?: string;
    name: string;
    disabled?: boolean;
};
export type OrdinalValuesType = {
    id?: string;
    name: string;
    disabled: boolean;
    level: number;
};
export type CustomPropertyType = {
    id?: string;
    name: string;
    objectType: ObjectType;
    propertyType: PropertyType;
    disabled: boolean;
    categoryValues: CategoryType[] | null;
    ordinalValues: OrdinalValuesType[] | null;
};

export async function getCustomProperties(): Promise<Array<CustomPropertyType>> {
    return getJson(`${API_URI}/customproperties`);
}

export async function getCustomPropertiesWithQuery(query: ObjectType): Promise<Array<CustomPropertyType>> {
    return getJson(`${API_URI}/customproperties?objectType=${query}`);
}

export async function getCustomProperty(id: string): Promise<CustomPropertyType> {
    return getJson(`${API_URI}/customproperties/${id}`);
}

export async function addCustomProperty(newData: CustomPropertyType): Promise<CustomPropertyType> {
    return postJson(`${API_URI}/customproperties`, newData);
}

export async function updateCustomProperty(id: string, newData: CustomPropertyType): Promise<CustomPropertyType> {
    return putJson(`${API_URI}/customproperties/${id}`, newData);
}

export async function deleteCustomProperty(id: string): Promise<CustomPropertyType> {
    return deleteJson(`${API_URI}/customproperties/${id}`);
}
