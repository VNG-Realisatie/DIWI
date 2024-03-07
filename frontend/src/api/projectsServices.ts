import { getJson, postJson, deleteJson } from "../utils/requests";
import { components } from "../types/schema";
import { API_URI } from "../utils/urls";

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
    return postJson(`${API_URI}/projects/${id}/update`, newData);
}

export async function deleteProject(id: string | null) {
    return deleteJson(`${API_URI}/projects/${id}/`);
}
