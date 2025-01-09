import { useEffect } from "react";
import { CategoryType } from "../api/adminSettingServices";
import { SelectModel } from "../api/projectsServices";
import { useCustomPropertyStore } from "./useCustomPropertyStore";

const useProperties = () => {
    const { projectCustomProperties, fetchCustomProperties } = useCustomPropertyStore();

    useEffect(() => {
        fetchCustomProperties();
    }, [fetchCustomProperties]);

    const priorityProperty = projectCustomProperties.find((property) => property.name === "priority");
    const priorityOptionList: SelectModel[] =
        priorityProperty?.ordinals
            ?.filter((ordinal) => !ordinal.disabled)
            .sort((a, b) => a.level - b.level)
            .map((ordinal) => ({ id: ordinal.id as string, name: ordinal.name })) || [];

    const municipalityRoleProperty = projectCustomProperties.find((property) => property.name === "municipalityRole");
    const municipalityRolesOptions: SelectModel[] =
        municipalityRoleProperty?.categories
            ?.filter((category: CategoryType) => !category.disabled)
            .map((category) => ({ id: category.id as string, name: category.name })) || [];

    const districtProperty = projectCustomProperties.find((property) => property.name === "district");
    const districtOptions: SelectModel[] =
        districtProperty?.categories
            ?.filter((category: CategoryType) => !category.disabled)
            .map((category) => ({ id: category.id as string, name: category.name })) || [];

    const neighbourhoodProperty = projectCustomProperties.find((property) => property.name === "neighbourhood");
    const neighbourhoodOptions: SelectModel[] =
        neighbourhoodProperty?.categories
            ?.filter((category: CategoryType) => !category.disabled)
            .map((category) => ({ id: category.id as string, name: category.name })) || [];

    const municipalityProperty = projectCustomProperties.find((property) => property.name === "municipality");
    const municipalityOptions: SelectModel[] =
        municipalityProperty?.categories
            ?.filter((category: CategoryType) => !category.disabled)
            .map((category) => ({ id: category.id as string, name: category.name })) || [];

    return { priorityOptionList, municipalityRolesOptions, properties: projectCustomProperties, districtOptions, neighbourhoodOptions, municipalityOptions };
};

export default useProperties;
