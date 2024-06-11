import { getJson } from "../utils/requests";
import { API_URI } from "../utils/urls";

// eslint-disable-next-line @typescript-eslint/no-explicit-any
export async function getProjectTimeline(id: number | string): Promise<any> {
    return getJson(`${API_URI}/projects/${id}/timeline`);
}
