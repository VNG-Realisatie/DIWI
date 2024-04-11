import { HouseBlock } from "../components/project-wizard/house-blocks/types";
import { deleteJson, getJson, postJson, putJson } from "../utils/requests";
import { API_URI } from "../utils/urls";

export async function getProjectHouseBlocks(id: string): Promise<HouseBlock[]> {
    return getJson(`${API_URI}/projects/${id}/houseblocks`);
}

export async function addHouseBlock(newData: HouseBlock): Promise<HouseBlock> {
    return postJson(`${API_URI}/houseblock/add`, newData);
}

export async function updateHouseBlock(newData: HouseBlock): Promise<HouseBlock> {
    return putJson(`${API_URI}/houseblock/update`, newData);
}

export async function deleteHouseBlock(id: string | null) {
    return deleteJson(`${API_URI}/houseblock/${id}`);
}
