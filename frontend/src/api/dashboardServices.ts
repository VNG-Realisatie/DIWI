import { deleteJson, getJson, postJson, putJson } from "../utils/requests";
import { API_URI } from "../utils/urls";
import { GoalDirection, GoalType } from "./goalsServices";

type PhysicalAppearance = {
    name: string;
    amount: number;
};

export type PolicyGoal = {
    name: string;
    category: string;
    id: string;
    goal: number;
    amount: number;
    percentage: number;
    totalAmount: number;
    goalDirection: GoalDirection;
    goalType: GoalType;
};

export type Planning = {
    projectId: string;
    name: string;
    amount: number;
    year: number;
};

type PriceCategory = {
    id: string;
    name: string;
    amount: number;
    min: number;
    max?: number;
};

export type VisibilityElement =
    | "MUTATION"
    | "PROJECT_PHASE"
    | "TARGET_GROUP"
    | "PHYSICAL_APPEARANCE"
    | "OWNERSHIP_BUY"
    | "OWNERSHIP_RENT"
    | "PROJECT_MAP"
    | "DELIVERABLES"
    | "DELAYED_PROJECTS";

export type Blueprint = {
    uuid?: string;
    name: string;
    userGroups: { uuid: string }[];
    elements: VisibilityElement[];
    categories: string[];
};

export async function getDashboardProject(
    id: string,
): Promise<{ physicalAppearance: PhysicalAppearance[]; planning: Planning[]; priceCategoryOwn: PriceCategory[]; priceCategoryRent: PriceCategory[] }> {
    const todaysDate = new Date().toISOString().split("T")[0]; // change this logic later
    return getJson(`${API_URI}/dashboard/project/${id}?snapshotDate=${todaysDate}`);
}

export async function getDashboardProjects(): Promise<{
    physicalAppearance: PhysicalAppearance[];
    targetGroup: PhysicalAppearance[];
    priceCategoryOwn: PriceCategory[];
    priceCategoryRent: PriceCategory[];
    planning: Planning[];
}> {
    const todaysDate = new Date().toISOString().split("T")[0]; // change this logic later
    return getJson(`${API_URI}/dashboard/projects?snapshotDate=${todaysDate}`);
}

export async function getPolicyDashboardProjects(): Promise<PolicyGoal[]> {
    const todaysDate = new Date().toISOString().split("T")[0]; // change this logic later
    return getJson(`${API_URI}/dashboard/projects/policygoals?snapshotDate=${todaysDate}`);
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
