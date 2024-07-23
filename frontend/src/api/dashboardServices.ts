import { getJson } from "../utils/requests";
import { API_URI } from "../utils/urls";

type PhysicalAppearence = {
    name: string;
    amount: number;
};

export type Planning = {
    projectId: string;
    name: string;
    amount: number;
    year: number;
};

export async function getDashboardProject(id: string): Promise<{ physicalAppearance: PhysicalAppearence[], planning: Planning[] }> {
    const todaysDate = new Date().toISOString().split("T")[0]; // change this logic later
    return getJson(`${API_URI}/dashboard/project/${id}?snapshotDate=${todaysDate}`);
}
export async function getDashboardProjects(): Promise<{ physicalAppearance: PhysicalAppearence[]; targetGroup: PhysicalAppearence[] }> {
    const todaysDate = new Date().toISOString().split("T")[0]; // change this logic later
    return getJson(`${API_URI}/dashboard/projects?snapshotDate=${todaysDate}`);
}
