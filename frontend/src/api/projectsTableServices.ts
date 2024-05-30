import { getJson } from "../utils/requests";
import { API_URI } from "../utils/urls";
import { UserGroup } from "./projectsServices";

export async function filterTable(query: string) {
    return getJson(`${API_URI}/projects/table${query}`);
}

export async function getUserGroupList(includeSingleUser: boolean): Promise<Array<UserGroup>> {
    return getJson(`${API_URI}/groups?includeSingleUser=${includeSingleUser}`);
}
