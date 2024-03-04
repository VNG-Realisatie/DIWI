import { getJson, postJson, deleteJson } from "../utils/requests";
import { components } from "../types/schema";
import { API_URI } from "../utils/urls";

export type Organization = components["schemas"]["OrganizationModel"];
export type Project = components["schemas"]["ProjectListModel"];
export type ProjectUpdate = components["schemas"]["ProjectUpdateModel"];

export async function getProjects(pageNumber: number, pageSize: number): Promise<Array<Project>> {
    return getJson(`${API_URI}/projects/table?pageNumber=${pageNumber}&pageSize=${pageSize}`);
}

//TODO will be updated later after endpoint changed
export async function updateProject(id: string, newData: ProjectUpdate): Promise<any> {
    return postJson(`${API_URI}/projects/${id}/update`, newData);
}

export async function deleteProject(id: string | null) {
    return deleteJson(`${API_URI}/projects/${id}/`);
}
