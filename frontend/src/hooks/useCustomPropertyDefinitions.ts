import { components } from "../types/schema";
import { useCustomPropertyStore } from "./useCustomPropertyStore";

type Categories = components["schemas"]["SelectDisabledModel"];
export type CategoriesStrict = Required<Categories>;

export const useCustomPropertyDefinitions = () => {
    const { houseBlockCustomProperties } = useCustomPropertyStore();

    const physicalAppearance = houseBlockCustomProperties.filter((prop) => prop.name === "physicalAppearance")[0];
    const physicalAppearanceCategories = physicalAppearance?.categories?.filter((cat) => cat.id !== undefined && !cat.disabled) as CategoriesStrict[];

    const targetGroup = houseBlockCustomProperties.filter((prop) => prop.name === "targetGroup")[0];
    const targetGroupCategories = targetGroup?.categories?.filter((cat) => cat.id !== undefined && !cat.disabled) as CategoriesStrict[];

    return { physicalAppearanceCategories, targetGroupCategories };
};
