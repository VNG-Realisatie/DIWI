import { getJson, postJson } from "../utils/requests";
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
    totalValue: number;
    projectId: string;
    projectStateId: string;
    projectName: string;
    projectColor: string;
    confidentialityLevel: string;
    projectLeaders: Organization[];
    projectOwners: Organization[];
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

//TODO will be updated later after endpoint changed
export async function updateProject(id: string, newData: any): Promise<any> {
    return postJson(`${API_URI}/projects/${id}/update`, newData);
}
