import { Property } from "../api/adminSettingServices";
import { CustomPropertyValue } from "../api/customPropServices";
import { Project } from "../api/projectsServices";

export function validateCustomProperties(customValues: CustomPropertyValue[], customDefinitions: Property[]) {
    customDefinitions.forEach((property) => {
        const customValue = customValues.find((cv) => cv.customPropertyId === property.id);
        if (!customValue && property.mandatory) {
            return false;
        }
    });
    return true;
}

export function validateForm(project: Project, validOwner: boolean = true, customDefinitions: Property[]) {
    if (
        !project.projectName ||
        !project.startDate ||
        !project.endDate ||
        !project.projectColor ||
        !project.projectPhase ||
        !project.confidentialityLevel ||
        !validOwner ||
        project.projectOwners.length === 0 ||
        (project.customProperties && !validateCustomProperties(project.customProperties, customDefinitions))
    ) {
        return false;
    }
    return true;
}
