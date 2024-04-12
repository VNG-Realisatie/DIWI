import { useEffect, useState } from "react";
import { getCustomProperties } from "../api/adminSettingServices";
import { components } from "../types/schema";

type Categories = components["schemas"]["SelectDisabledModel"];
type CategoriesStrict = Required<Categories>;

export const useCustomPropertyDefinitions = () => {
    const [physicalAppearanceCategories, setPhysicalAppearances] = useState<Array<CategoriesStrict>>();
    const [targetGroupCategories, setTargetGroup] = useState<Array<CategoriesStrict>>();

    useEffect(() => {
        getCustomProperties().then((res) => {
            const pa = res.filter((prop) => prop.objectType === "WONINGBLOK" && prop.name === "physicalAppearance")[0];
            const paCategories = pa?.categories?.filter((cat) => cat.id !== undefined) as CategoriesStrict[];
            setPhysicalAppearances(paCategories);

            const tg = res.filter((prop) => prop.objectType === "WONINGBLOK" && prop.name === "targetGroup")[0];
            const tgCategories = tg?.categories?.filter((cat) => cat.id !== undefined) as CategoriesStrict[];
            setTargetGroup(tgCategories);
        });
    }, []);

    return { physicalAppearanceCategories, targetGroupCategories };
};
