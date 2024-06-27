import { getJson } from "../utils/requests";
import { API_URI } from "../utils/urls";
type PhysicalAppearance = {
    name: string;
    amount: number;
};
export async function getDashboardProject(id: string): Promise<{ physicalAppearance: PhysicalAppearance[] }> {
    const todaysDate = new Date().toISOString().split("T")[0]; // change this logic later
    return getJson(`${API_URI}/dashboard/project/${id}?snapshotDate=${todaysDate}`);
}
