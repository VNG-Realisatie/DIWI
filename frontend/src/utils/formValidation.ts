import { Project } from "../api/projectsServices";

export function validateForm(project: Project) {
    if (
        !project.projectName ||
        !project.startDate ||
        !project.endDate ||
        !project.projectColor ||
        !project.projectPhase ||
        !project.confidentialityLevel ||
        project.projectOwners.length === 0
    ) {
        return false;
    }
    return true;
}
