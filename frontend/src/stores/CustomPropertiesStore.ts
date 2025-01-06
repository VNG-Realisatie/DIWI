import { makeAutoObservable } from "mobx";
import { addCustomProperty, deleteCustomProperty, getCustomProperties, Property, updateCustomProperty } from "../api/adminSettingServices";

class CustomPropertyStore {
    customProperties: Property[] = [];

    constructor() {
        makeAutoObservable(this);
        this.fetchCustomProperties();
    }

    fetchCustomProperties = async () => {
        this.customProperties = await getCustomProperties();
    };

    deleteCustomProperty = async (id: string) => {
        await deleteCustomProperty(id);
        this.customProperties = this.customProperties.filter((property) => property.id !== id);
    };

    addCustomProperty = async (newData: Property) => {
        const added = await addCustomProperty(newData);
        this.customProperties.push(added);
        return added;
    };

    updateCustomProperty = async (id: string, newData: Property): Promise<Property> => {
        const updated = await updateCustomProperty(id, newData);
        this.customProperties = this.customProperties.map((property) => {
            if (property.id === id) {
                return updated;
            }
            return property;
        });
        return updated;
    };
}

export const customPropertyStore = new CustomPropertyStore();
