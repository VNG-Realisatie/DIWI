import { ConfidentialityLevel } from "../types/enums";
import { getJson, postJson, putJson, deleteJson, downloadPost, postJsonParcedError } from "../utils/requests";
import { API_URI } from "../utils/urls";

export type ExportType = "ESRI_ZUID_HOLLAND" | "ESRI_GELDERLAND" | "GEO_JSON" | "EXCEL";

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
    clientId?: string;
    apiKey?: string;
    projectUrl?: string;
    properties?: ExportProperty[];
    valid?: boolean;
    validationErrors?: ValidationError[];
    minimumConfidentiality?: ConfidentialityLevel;
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
    confidentialityLevels?: ConfidentialityLevel[];
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

export async function downloadExportData(id: string, body: DownloadType, type: ExportType): Promise<void> {
    return downloadPost(`${API_URI}/dataexchange/${id}/download`, type === "EXCEL" ? "export.xlsx" : "export.geojson", body);
}

export async function exportProjects(
    exportId: string,
    token: string | null,
    projectIds: string[],
    confidentialityLevels: string[],
    userName: string | null,
): Promise<void> {
    const url = `${API_URI}/dataexchange/${exportId}/export`;
    const exportDate = new Date().toISOString().split("T")[0];
    const exportTime = new Date().toISOString().split("T")[1].split(".")[0].replace(/:/g, "-");
    const filename = `export_${exportDate}_${exportTime}`;
    const body = {
        filename,
        projectIds: projectIds && projectIds.length > 0 ? projectIds : undefined,
        confidentialityLevels: projectIds && projectIds.length === 0 ? confidentialityLevels : undefined,
        token,
        exportDate,
        username: userName,
    };
    return postJsonParcedError(url, body);
}

export async function getExportTypes(): Promise<ExportType[]> {
    return getJson(`${API_URI}/dataexchange/types`);
}
