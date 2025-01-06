import { components } from "../types/schema";
import { useCustomPropertyStore } from "../context/CustomPropertiesContext";

type Categories = components["schemas"]["SelectDisabledModel"];
export type CategoriesStrict = Required<Categories>;

export const useCustomPropertyDefinitions = () => {
    const { customProperties } = useCustomPropertyStore();

    const physicalAppearance = customProperties.filter((prop) => prop.objectType === "WONINGBLOK" && prop.name === "physicalAppearance")[0];
    const physicalAppearanceCategories = physicalAppearance?.categories?.filter((cat) => cat.id !== undefined && !cat.disabled) as CategoriesStrict[];

    const targetGroup = customProperties.filter((prop) => prop.objectType === "WONINGBLOK" && prop.name === "targetGroup")[0];
    const targetGroupCategories = targetGroup?.categories?.filter((cat) => cat.id !== undefined && !cat.disabled) as CategoriesStrict[];

    return { physicalAppearanceCategories, targetGroupCategories };
};
