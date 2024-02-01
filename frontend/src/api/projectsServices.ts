import { API_URI } from "../utils/urls";

export type Project = {
    projectId: string;
    projectStateId: string;
    projectName: string;
    projectColor: string;
    confidentialityLevel: string;
    organizationName: string;
    planType: string[];
    startDate: string;
    endDate: string;
    priority: string[];
    projectPhase: string;
    municipalityRole: string[];
    wijk:string[];
    buurt:string[];
    municipality:string[];
    planningPlanStatus: string[];
};

export async function getProjects(pageNumber:number,pageSize:number): Promise<Array<Project>> {
    const response = await fetch(`${API_URI}/projects/table?pageNumber=${pageNumber}&pageSize=${pageSize}`);
    return await response.json();
}
