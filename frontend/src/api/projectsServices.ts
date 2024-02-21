import { getJson, postJson } from "../utils/requests";
import { API_URI } from "../utils/urls";

export type ProjectLeader = string[];
export type ProjectOwner = string[];
export type Project = {
    totalValue: number;
    projectId: string;
    projectStateId: string;
    projectName: string;
    projectColor: string;
    confidentialityLevel: string;
    projectLeaders: ProjectLeader[];
    projectOwners: ProjectOwner[];
    planType: string[];
    startDate: string;
    endDate: string;
    priority: string[];
    projectPhase: string;
    municipalityRole: string[];
    wijk: string[];
    buurt: string[];
    municipality: string[];
    planningPlanStatus: string[];
};

export async function getProjects(pageNumber: number, pageSize: number): Promise<Array<Project>> {
    return getJson(`${API_URI}/projects/table?pageNumber=${pageNumber}&pageSize=${pageSize}`);
}

export async function updateProject(id: string, newData: any): Promise<any> {
    return postJson(`${API_URI}/projects/${id}/update`, newData);
}
