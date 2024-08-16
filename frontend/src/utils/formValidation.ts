import { Project } from "../api/projectsServices";

export function validateForm(project: Project, validOwner: boolean = true) {
    if (
        !project.projectName ||
        !project.startDate ||
        !project.endDate ||
        !project.projectColor ||
        !project.projectPhase ||
        !project.confidentialityLevel ||
        !validOwner ||
        project.projectOwners.length === 0
    ) {
        return false;
    }
    return true;
}
