import { getJson, postJson, putJson, deleteJson } from "../utils/requests";
import { API_URI } from "../utils/urls";

type DashboardProject = {
    name: string;
    amount: number;
};

export type VisibilityElement =
    | "MUTATION"
    | "PROJECT_PHASE"
    | "TARGET_GROUP"
    | "PHYSICAL_APPEARANCE"
    | "OWNERSHIP_BUY"
    | "OWNERSHIP_RENT"
    | "PROJECT_MAP"
    | "RESIDENTIAL_PROJECTS"
    | "DELIVERABLES"
    | "DELAYED_PROJECTS";

export type Blueprint = {
    uuid?: string;
    name: string;
    userGroups: { uuid: string }[];
    elements: VisibilityElement[];
};

export async function getDashboardProject(id: string): Promise<{ physicalAppearance: DashboardProject[] }> {
    const todaysDate = new Date().toISOString().split("T")[0]; // change this logic later
    return getJson(`${API_URI}/dashboard/project/${id}?snapshotDate=${todaysDate}`);
}
export async function getDashboardProjects(): Promise<{ physicalAppearance: DashboardProject[]; targetGroup: DashboardProject[] }> {
    const todaysDate = new Date().toISOString().split("T")[0]; // change this logic later
    return getJson(`${API_URI}/dashboard/projects?snapshotDate=${todaysDate}`);
}
export async function getAllBlueprints(): Promise<Blueprint[]> {
    return getJson(`${API_URI}/blueprints`);
}
export async function getAssignedBlueprints(): Promise<Blueprint[]> {
    return getJson(`${API_URI}/dashboard/blueprints`);
}
export async function createBlueprint(blueprint: Blueprint): Promise<Blueprint> {
    return postJson(`${API_URI}/blueprints`, blueprint);
}
export async function getBlueprint(id: string): Promise<Blueprint> {
    return getJson(`${API_URI}/blueprints/${id}`);
}

export async function updateBlueprint(blueprint: Blueprint): Promise<Blueprint> {
    return putJson(`${API_URI}/blueprints/${blueprint.uuid}`, blueprint);
}

export async function deleteBlueprint(blueprint: Blueprint): Promise<void> {
    return deleteJson(`${API_URI}/blueprints/${blueprint.uuid}`);
}
