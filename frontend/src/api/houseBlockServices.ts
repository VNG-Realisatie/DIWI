import { HouseBlock, HouseBlockWithCustomProperties } from "../types/houseBlockTypes";
import { deleteJson, getJson, postJson, putJson } from "../utils/requests";
import { API_URI } from "../utils/urls";
import { getBlockCustomPropertyValues, putBlockCustomPropertyValue } from "./customPropServices";

// exposed functionality
export async function getProjectHouseBlocksWithCustomProperties(id: string): Promise<HouseBlockWithCustomProperties[]> {
    // first get houseblocks that belong to project
    const newHouseBlocks = await getProjectHouseBlocks(id);
    // then get custom props for each houseblock
    return Promise.all(
        newHouseBlocks.map(async (block) => {
            const hbid = (block as HouseBlock & { houseblockId: string }).houseblockId;
            const newCustomproperties = await getBlockCustomPropertyValues(hbid);
            return { ...block, customProperties: newCustomproperties };
        }),
    );
}

export async function deleteHouseBlockWithCustomProperties(id: string | undefined) {
    // customproperties live and die by the block, so no need to explicitly remove them.
    if (id) return deleteHouseBlock(id);
    throw Error("No id for houseblock to delete");
}

export async function saveHouseBlockWithCustomProperties(houseBlock: HouseBlockWithCustomProperties): Promise<HouseBlockWithCustomProperties> {
    // destructure houseblock from customproperties as they need to be sent separately
    const { customProperties, ...houseBlockNoProperties } = houseBlock;

    if (houseBlockNoProperties.houseblockId) {
        const newHb = await updateHouseBlock(houseBlockNoProperties);
        const hbid = (newHb as HouseBlock & { houseblockId: string }).houseblockId;
        // for each custom prop, send it to the backend
        const [...newCustomProperties] = await Promise.all(customProperties.map((cp) => putBlockCustomPropertyValue(hbid, cp)));
        return { ...newHb, customProperties: newCustomProperties };
    } else {
        const newHb = await addHouseBlock(houseBlockNoProperties);
        const hbid = (newHb as HouseBlock & { houseblockId: string }).houseblockId;
        // for each custom prop, send it to the backend
        const [...newCustomProperties] = await Promise.all(customProperties.map((cp) => putBlockCustomPropertyValue(hbid, cp)));
        return { ...newHb, customProperties: newCustomProperties };
    }
}

// server api
async function getProjectHouseBlocks(id: string): Promise<HouseBlock[]> {
    return getJson(`${API_URI}/projects/${id}/houseblocks`);
}

async function addHouseBlock(newData: HouseBlock): Promise<HouseBlock> {
    return postJson(`${API_URI}/houseblock/add`, newData);
}

async function updateHouseBlock(newData: HouseBlock): Promise<HouseBlock> {
    return putJson(`${API_URI}/houseblock/update`, newData);
}

async function deleteHouseBlock(id: string) {
    return deleteJson(`${API_URI}/houseblock/${id}`);
}
export function updateHouseBlockWithCustomProperties() {
    throw new Error("Function not implemented.");
}
