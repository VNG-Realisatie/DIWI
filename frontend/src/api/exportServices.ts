import { getJson, postJson, putJson } from "../utils/requests";
import { API_URI } from "../utils/urls";

export type ExportData = {
    id: string;
    name: string;
    type: string;
};

export async function getExportData(): Promise<ExportData[]> {
    return getJson(`${API_URI}/dataexchange`);
}

export async function addExportData(data: ExportData): Promise<ExportData> {
    return postJson(`${API_URI}/dataexchange`, data);
}

export async function updateExportData(id: string, data: ExportData): Promise<ExportData> {
    return putJson(`${API_URI}/dataexchange/${id}`, data);
}
