import { CategoryType } from "../api/adminSettingServices";

export const getDuplicatedPropertyInfo = (list: CategoryType[]) => {
    const nameCounts = list.reduce((acc, { name }) => {
        const lowercaseName = name.toLowerCase();
        //@ts-expect-error reduce function
        acc[lowercaseName] = (acc[lowercaseName] || 0) + 1;
        return acc;
    }, {});
    //@ts-expect-error reduce function
    const duplicatedNames = Object.keys(nameCounts).filter((name) => nameCounts[name] > 1);
    const duplicatedNameString = duplicatedNames.join("");

    return {
        duplicatedStatus: duplicatedNames.length > 0,
        duplicatedNames: duplicatedNameString,
    };
};
