import { makeAutoObservable } from "mobx";
import { addCustomProperty, deleteCustomProperty, getCustomProperties, Property } from "../api/adminSettingServices";

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
    }

    addCustomProperty = async (newData: Property) => {
        const added = await addCustomProperty(newData);
        this.customProperties.push(added);
        return added;
    };
}

export const customPropertyStore = new CustomPropertyStore();
