import { API_URI } from "../utils/urls";

export const importExcelProjects = async (file: FileList) => {
    const formData = new FormData();
    formData.append("uploadFile", file[0]);

    const response = await fetch(`${API_URI}/projects/import`, {
        method: "POST",
        body: formData,
    });

    if (response.ok || response.status === 400) {
        return response;
    }

    throw new Error("File upload failed");
};

export const importGeoJsonProjects = async (file: FileList) => {
    const formData = new FormData();
    formData.append("uploadFile", file[0]);

    const response = await fetch(`${API_URI}/projects/import?fileType=GEOJSON`, {
        method: "POST",
        body: formData,
    });

    if (response.ok || response.status === 400) {
        return response.json();
    }

    throw new Error("File upload failed");
};
