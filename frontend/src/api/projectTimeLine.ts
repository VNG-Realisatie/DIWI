import { getJson } from "../utils/requests";
import { API_URI } from "../utils/urls";

export async function getProjectTimeline(id: number | string): Promise<any> {
    return getJson(`${API_URI}/projects/${id}/timeline`);
}
