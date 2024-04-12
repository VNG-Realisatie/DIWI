import { HouseBlock, HouseBlockWithCustomProperties } from "../types/houseBlockTypes";
import { deleteJson, getJson, postJson, putJson } from "../utils/requests";
import { API_URI } from "../utils/urls";
import { getBlockCustomPropertyValues, putBlockCustomPropertyValue } from "./customPropServices";

// exposed functionality
export async function getProjectHouseBlocksWithCustomProperties(id: string): Promise<HouseBlockWithCustomProperties[]> {
    // first get houseblocks that belong to project
    return getProjectHouseBlocks(id).then(async (houseBlocks) => {
        // then get custom props for each houseblock
        return await Promise.all(
            houseBlocks.map((block) => {
                if (!block.houseblockId) return { ...block, customProperties: [] };
                return getBlockCustomPropertyValues(block.houseblockId).then((props) => {
                    return { ...block, customProperties: props };
                });
            }),
        );
    });
}

export async function deleteHouseBlockWithCustomProperties(id: string | undefined) {
    // customproperties live and die by the block, so no need to explicitly remove them.
    if (id) return deleteHouseBlock(id);
    throw Error("No id for houseblock to delete");
}

export async function saveHouseBlockWithCustomProperties(houseBlock: HouseBlockWithCustomProperties) {
    // destructure houseblock from customproperties as they need to be sent separately
    const { customProperties, ...houseBlockNoProperties } = houseBlock;

    if (houseBlockNoProperties.houseblockId) {
        return updateHouseBlock(houseBlockNoProperties).then((newHb) => {
            const hbid = (newHb as HouseBlock & { houseblockId: string }).houseblockId;
            // for each custom prop, send it to the backend
            Promise.all(
                customProperties.map((cp) => {
                    return putBlockCustomPropertyValue(hbid, cp);
                }),
            );
        });
    } else {
        return addHouseBlock(houseBlockNoProperties).then((newHb) => {
            const hbid = (newHb as HouseBlock & { houseblockId: string }).houseblockId;
            // for each custom prop, send it to the backend
            Promise.all(
                customProperties.map((cp) => {
                    return putBlockCustomPropertyValue(hbid, cp);
                }),
            );
        });
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
