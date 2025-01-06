import { CategoryType } from "../api/adminSettingServices";
import { SelectModel } from "../api/projectsServices";
import { useCustomPropertyStore } from "../context/CustomPropertiesContext";

const useProperties = () => {
    const { customProperties } = useCustomPropertyStore();

    const priorityOptionList: SelectModel[] =
        customProperties
            .filter((property) => property.name === "priority")[0]
            .ordinals?.filter((ordinal) => !ordinal.disabled)
            .sort((a, b) => a.level - b.level)
            .map((ordinal) => ({ id: ordinal.id as string, name: ordinal.name })) || [];

    const municipalityRolesOptions: SelectModel[] =
        customProperties
            .filter((property) => property.name === "municipalityRole")[0]
            .categories?.filter((category: CategoryType) => !category.disabled)
            .map((category) => ({ id: category.id as string, name: category.name })) || [];

    const districtOptions: SelectModel[] =
        customProperties
            .filter((property) => property.name === "district")[0]
            .categories?.filter((category: CategoryType) => !category.disabled)
            .map((category) => ({ id: category.id as string, name: category.name })) || [];

    const neighbourhoodOptions: SelectModel[] =
        customProperties
            .filter((property) => property.name === "neighbourhood")[0]
            .categories?.filter((category: CategoryType) => !category.disabled)
            .map((category) => ({ id: category.id as string, name: category.name })) || [];

    const municipalityOptions: SelectModel[] =
        customProperties
            .filter((property) => property.name === "municipality")[0]
            .categories?.filter((category: CategoryType) => !category.disabled)
            .map((category) => ({ id: category.id as string, name: category.name })) || [];

    return { priorityOptionList, municipalityRolesOptions, properties: customProperties, districtOptions, neighbourhoodOptions, municipalityOptions };
};

export default useProperties;
