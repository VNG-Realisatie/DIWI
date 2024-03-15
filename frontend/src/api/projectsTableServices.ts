import { getJson } from "../utils/requests";
import { API_URI } from "../utils/urls";
import { Organization } from "./projectsServices";

export type ProjectTableOption = {
    name: string;
    id: string;
};

export async function getMunicipalityList(): Promise<Array<ProjectTableOption>> {
    return getJson(`${API_URI}/municipality/list`);
}

export async function getNeighbourhoodList(): Promise<Array<ProjectTableOption>> {
    return getJson(`${API_URI}/buurt/list`);
}

export async function getWijkList(): Promise<Array<ProjectTableOption>> {
    return getJson(`${API_URI}/wijk/list`);
}

export async function filterTable(query: string) {
    return getJson(`${API_URI}/projects/table${query}`);
}

export async function getMunicipalityRoleList(): Promise<Array<ProjectTableOption>> {
    return getJson(`${API_URI}/municipalityrole/list`);
}

export async function getPriorityList(): Promise<Array<ProjectTableOption>> {
    return getJson(`${API_URI}/priority/list`);
}

export async function getOrganizationList(): Promise<Array<Organization>> {
    return getJson(`${API_URI}/organizations/list`);
}
