import { useEffect, useState } from "react";
import { Property, getCustomProperties, CategoryType } from "../api/adminSettingServices";
import { SelectModel } from "../api/projectsServices";

const useProperties = () => {
    const [priorityOptionList, setPriorityOptionList] = useState<SelectModel[]>();
    const [municipalityRolesOptions, setMunicipalityRolesOptions] = useState<SelectModel[]>();
    const [districtOptions, setDistrictOptions] = useState<SelectModel[]>();
    const [neighbourhoodOptions, setNeighbourhoodOptions] = useState<SelectModel[]>();
    const [municipalityOptions, setMunicipalityOptions] = useState<SelectModel[]>();
    const [properties, setProperties] = useState<Property[]>();

    useEffect(() => {
        getCustomProperties().then((customProperties) => {
            setProperties(customProperties);
            for (const customProperty of customProperties) {
                if (customProperty.name === "priority") {
                    const sortedOrdinals = customProperty.ordinals?.sort((a, b) => a.level - b.level);
                    const options = sortedOrdinals?.map((ordinal) => ({ id: ordinal.id as string, name: ordinal.name }));
                    setPriorityOptionList(options);
                } else if (customProperty.name === "municipalityRole") {
                    const options = customProperty.categories?.map((category: CategoryType) => ({ id: category.id as string, name: category.name }));
                    setMunicipalityRolesOptions(options);
                } else if (customProperty.name === "district") {
                    const options = customProperty.categories?.map((category: CategoryType) => ({ id: category.id as string, name: category.name }));
                    setDistrictOptions(options);
                } else if (customProperty.name === "neighbourhood") {
                    const options = customProperty.categories?.map((category: CategoryType) => ({ id: category.id as string, name: category.name }));
                    setNeighbourhoodOptions(options);
                } else if (customProperty.name === "municipality") {
                    const options = customProperty.categories?.map((category: CategoryType) => ({ id: category.id as string, name: category.name }));
                    setMunicipalityOptions(options);
                }
            }
        });
    }, []);
    return { priorityOptionList, municipalityRolesOptions, properties, districtOptions, neighbourhoodOptions, municipalityOptions };
};

export default useProperties;
