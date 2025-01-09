import { makeAutoObservable, runInAction } from "mobx";
import { isEqual } from "lodash";
import { addCustomProperty, deleteCustomProperty, getCustomProperties, Property, updateCustomProperty } from "../api/adminSettingServices";

class CustomPropertyStore {
    customProperties: Property[] = [];

    constructor() {
        makeAutoObservable(this);
        this.fetchCustomProperties();
    }

    get projectCustomProperties(): Property[] {
        return this.customProperties.filter((property) => property.objectType === "PROJECT");
    }

    get houseBlockCustomProperties(): Property[] {
        return this.customProperties.filter((property) => property.objectType === "WONINGBLOK");
    }

    fetchCustomProperties = async () => {
        const newProperties = await getCustomProperties();
        runInAction(() => {
            if (!isEqual(newProperties, this.customProperties)) {
                this.customProperties = newProperties;
            }
        });
    };

    deleteCustomProperty = async (id: string) => {
        await deleteCustomProperty(id);
        runInAction(() => {
            this.customProperties = this.customProperties.filter((property) => property.id !== id);
        });
    };

    addCustomProperty = async (newData: Property) => {
        const added = await addCustomProperty(newData);
        runInAction(() => {
            this.customProperties.push(added);
        });
        return added;
    };

    updateCustomProperty = async (id: string, newData: Property): Promise<Property> => {
        const updated = await updateCustomProperty(id, newData);
        runInAction(() => {
            this.customProperties = this.customProperties.map((property) => {
                if (property.id === id) {
                    return updated;
                }
                return property;
            });
        });
        return updated;
    };
}

export const customPropertyStore = new CustomPropertyStore();
