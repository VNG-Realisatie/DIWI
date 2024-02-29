import { getJson, postJson, deleteJson } from "../utils/requests";
import { API_URI } from "../utils/urls";

export type User = {
    uuid: string;
    firstName: string;
    lastName: string;
    initials: string;
};

export type Organization = {
    uuid: string;
    name: string;
    users: User[];
};

export type Project = {
    totalValue: number | null;
    projectId: string | null;
    projectStateId: string | null;
    projectName: string | null;
    projectColor: string | null;
    confidentialityLevel: string | null;
    projectLeaders: Organization[] | null;
    projectOwners: Organization[] | null;
    planType: string[] | null;
    startDate: string | null;
    endDate: string | null;
    priority: string[] | null;
    projectPhase: string | null;
    municipalityRole: string[] | null;
    wijk: string[] | null;
    buurt: string[] | null;
    municipality: string[] | null;
    planningPlanStatus: string[] | null;
};

export async function getProjects(pageNumber: number, pageSize: number): Promise<Array<Project>> {
    return getJson(`${API_URI}/projects/table?pageNumber=${pageNumber}&pageSize=${pageSize}`);
}

//TODO will be updated later after endpoint changed
export async function updateProject(id: string, newData: any): Promise<any> {
    return postJson(`${API_URI}/projects/${id}/update`, newData);
}

export async function deleteProject(id: string | null) {
    return deleteJson(`${API_URI}/projects/${id}/`);
}
