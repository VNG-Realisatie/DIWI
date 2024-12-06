import { makeAutoObservable } from "mobx";
import { addCustomProperty, getCustomProperties, Property } from "../api/adminSettingServices";

class CustomPropertyStore {
    customProperties: Property[] = [];

    constructor() {
        makeAutoObservable(this);
        this.fetchCustomProperties();
    }

    fetchCustomProperties = async () => {
        this.customProperties = await getCustomProperties();
    };

    addCustomProperty = async (newData: Property) => {
        const added = await addCustomProperty(newData);
        this.customProperties.push(added);
        return added;
    };
}

export const customPropertyStore = new CustomPropertyStore();
