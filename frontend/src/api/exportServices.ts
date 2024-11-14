import { ConfidentialityLevel } from "../types/enums";
import { getJson, postJson, putJson, deleteJson, downloadPost } from "../utils/requests";
import { API_URI } from "../utils/urls";

export type PropertyOption = {
    id: string;
    name: string;
    propertyCategoryValueIds?: string[];
    propertyOrdinalValueIds?: string[];
};

export type ExportProperty = {
    name: string;
    customPropertyId?: string | null;
    objectType: "PROJECT" | "WONINGBLOK";
    mandatory: boolean;
    options?: PropertyOption[];
    propertyTypes: string[];
    singleSelect?: boolean;
    id?: string;
};

export type ExportData = {
    id: string;
    name: string;
    type: string;
    apiKey?: string;
    projectUrl?: string;
    projectdetailUrl?: string;
    properties?: ExportProperty[];
    valid?: boolean;
    validationErrors?: ValidationError[];
};

export type ValidationError = {
    dxProperty: string;
    error: string;
    errorCode: string;
    diwiOption: string | null;
};

export type DownloadType = {
    exportDate?: string;
    projectIds?: string[];
    confidentialityLevels?: ConfidentialityLevel[]
};

export async function getExportData(): Promise<ExportData[]> {
    return getJson(`${API_URI}/dataexchange`);
}

export async function getExportDataById(id: string): Promise<ExportData> {
    return getJson(`${API_URI}/dataexchange/${id}`);
}

export async function addExportData(data: ExportData): Promise<ExportData> {
    return postJson(`${API_URI}/dataexchange`, data);
}

export async function updateExportData(id: string, data: ExportData): Promise<ExportData> {
    return putJson(`${API_URI}/dataexchange/${id}`, data);
}

export async function deleteExportData(id: string): Promise<void> {
    return deleteJson(`${API_URI}/dataexchange/${id}`);
}

export async function downloadExportData(id: string, body: DownloadType): Promise<void> {
    return downloadPost(`${API_URI}/dataexchange/${id}/download`, "export.geojson", body);
}

//this dunction needs to be updated
export async function exportProjects(exportId: string, projectIds?: string[]): Promise<void> {
    const url = `${API_URI}/projects/export/${exportId}`;
    const body = projectIds && projectIds.length > 0 ? { projectIds } : undefined;
    await postJson(url, body);
}
