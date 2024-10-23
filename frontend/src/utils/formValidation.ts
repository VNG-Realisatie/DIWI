import { Property } from "../api/adminSettingServices";
import { CustomPropertyValue } from "../api/customPropServices";
import { Project } from "../api/projectsServices";

type CustomValueType =
    | string
    | number
    | boolean
    | string[]
    | { value?: string; min?: string; max?: string }
    | { value?: number; min?: number; max?: number }
    | null
    | undefined;

export function getCustomValue(customValue: CustomPropertyValue | undefined): CustomValueType {
    let value: CustomValueType;
    if (customValue?.propertyType) {
        if (customValue.propertyType === "TEXT") {
            value = customValue.textValue;
        } else if (customValue.propertyType === "NUMERIC") {
            value = customValue.numericValue?.value || customValue.numericValue?.min || 0;
        } else if (customValue.propertyType === "BOOLEAN") {
            value = customValue.booleanValue;
        } else if (customValue.propertyType === "CATEGORY") {
            value = customValue.categories && customValue.categories.length > 0 ? "true" : "";
        } else if (customValue.propertyType === "ORDINAL") {
            value = customValue.ordinals?.value;
        }
    } else if (customValue) {
        if (customValue.textValue) {
            value = customValue.textValue;
        } else if (customValue.numericValue) {
            value = customValue.numericValue.value || customValue.numericValue.min || 0;
        } else if (customValue.booleanValue !== undefined) {
            value = customValue.booleanValue;
        } else if (customValue.categories) {
            value = customValue.categories.length > 0 ? "true" : "";
        } else if (customValue.ordinals) {
            value = customValue.ordinals.value;
        }
    }
    return value;
}
export function validateCustomProperties(customValues: CustomPropertyValue[], customDefinitions: Property[]) {
    for (const property of customDefinitions) {
        const customValue = customValues.find((cv) => cv.customPropertyId === property.id);
        const value = getCustomValue(customValue);

        if (property.mandatory && (value === null || value === "" || value === undefined)) {
            return false;
        }
    }
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
