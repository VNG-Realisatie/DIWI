import { ObjectType, PropertyType } from "../types/enums";
import { getJson } from "../utils/requests";
import { API_URI } from "../utils/urls";
export type CategoryType = {
    id: string;
    name: string;
};
export type CustomPropertyType = {
    id?: string;
    name: string;
    objectType: ObjectType;
    propertyType: PropertyType;
    disabled: boolean;
    categories: CategoryType[] | null;
};

export async function getCustomProperties(): Promise<Array<CustomPropertyType>> {
    return getJson(`${API_URI}/customproperties`);
}
