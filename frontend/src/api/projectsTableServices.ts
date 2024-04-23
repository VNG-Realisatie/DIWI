import { getJson } from "../utils/requests";
import { API_URI } from "../utils/urls";
import { Organization } from "./projectsServices";

export async function filterTable(query: string) {
    return getJson(`${API_URI}/projects/table${query}`);
}

export async function getOrganizationList(): Promise<Array<Organization>> {
    return getJson(`${API_URI}/organizations/list`);
}
