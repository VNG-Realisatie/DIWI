import { API_URI } from "../utils/urls";

export const importExcelProjects = async (file: FileList) => {
    const formData = new FormData();
    //Array.from(file).forEach((f) => formData.append("file", f));
    formData.append("uploadFile", file[0]);

    const response = await fetch(`${API_URI}/projects/import`, {
        method: "POST",
        body: formData,
    });

    if (!response.ok) {
        throw new Error("File upload failed");
    }

    return response.json();
};
