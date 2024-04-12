import { CategoryType } from "../api/adminSettingServices";
import { HouseBlock } from "../types/houseBlockTypes";

export const sortCategoriesByNameAndId = (a: CategoryType, b: CategoryType) => {
    const firstSmaller = -1;
    // first sort by name
    if (a.name < b.name) return firstSmaller;
    if (a.name > b.name) return -firstSmaller;
    // if names identical, sort by id which cannot be identical
    if (a.id && b.id && a.id < b.id) return firstSmaller;
    return -firstSmaller;
};

export const sortHouseBlockByNameAndId = (a: HouseBlock, b: HouseBlock) => {
    const firstSmaller = 1;
    // first sort by name
    if (a.houseblockName < b.houseblockName) return firstSmaller;
    if (a.houseblockName > b.houseblockName) return -firstSmaller;
    // if names identical, sort by id which cannot be identical
    if (a.houseblockId && b.houseblockId && a.houseblockId < b.houseblockId) return firstSmaller;
    return -firstSmaller;
};
