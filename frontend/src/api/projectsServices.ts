import { getJson, postJson, deleteJson, putJson } from "../utils/requests";
import { components } from "../types/schema";
import { API_URI } from "../utils/urls";
import { HouseBlock } from "../components/project-wizard/house-blocks/types";

export type Organization = components["schemas"]["OrganizationModel"];
export type Project = components["schemas"]["ProjectListModel"];
export type ProjectUpdate = components["schemas"]["ProjectUpdateModel"];
export type SelectModel = components["schemas"]["SelectModel"];

export async function getProjects(pageNumber: number, pageSize: number): Promise<Array<Project>> {
    return getJson(`${API_URI}/projects/table?pageNumber=${pageNumber}&pageSize=${pageSize}`);
}

export async function getProject(id: string): Promise<Project> {
    return getJson(`${API_URI}/projects/${id}`);
}

export async function updateProjects(newData: any): Promise<any> {
    return postJson(`${API_URI}/projects/update`, newData);
}

export async function updateProject(id: string, newData: ProjectUpdate): Promise<any> {
    return putJson(`${API_URI}/projects/${id}/update`, newData);
}

export async function deleteProject(id: string | null) {
    return deleteJson(`${API_URI}/projects/${id}`);
}

export async function createProject(projectData: Project): Promise<Project> {
    return postJson(`${API_URI}/projects`, projectData);
}

export async function getProjectHouseBlocks(id: string): Promise<HouseBlock[]> {
    return getJson(`${API_URI}/projects/${id}/houseblocks`);
}

export async function addHouseBlock(newData: HouseBlock): Promise<HouseBlock> {
    return postJson(`${API_URI}/houseblock/add`, newData);
}
