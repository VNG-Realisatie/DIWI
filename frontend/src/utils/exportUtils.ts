import { Property } from "../api/adminSettingServices";
import { ExportProperty } from "../api/exportServices";
import { ConfidentialityLevel } from "../types/enums";

const confidentialityLevels: ConfidentialityLevel[] = [
    "PRIVATE",
    "INTERNAL_CIVIL",
    "INTERNAL_MANAGEMENT",
    "INTERNAL_COUNCIL",
    "EXTERNAL_REGIONAL",
    "EXTERNAL_GOVERNMENTAL",
    "PUBLIC",
];

export function doesPropertyMatchExportProperty(property: ExportProperty, customProperty: Property): boolean {
    if (!property.propertyTypes.some((type) => type === customProperty.propertyType)) return false;
    if (property.objectType !== customProperty.objectType) return false;
    if (["CATEGORY", "ORDINAL"].includes(customProperty.propertyType)) {
        if (property.mandatory && !customProperty.mandatory) return false;
        if (property.singleSelect && !customProperty.singleSelect) return false;
    }
    return true;
}

export const getAllowedConfidentialityLevels = (minLevel: ConfidentialityLevel): ConfidentialityLevel[] => {
    const minIndex = confidentialityLevels.indexOf(minLevel);
    return confidentialityLevels.slice(minIndex);
};
