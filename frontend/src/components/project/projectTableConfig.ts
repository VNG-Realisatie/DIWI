export type ColumnField =
    | "projectName"
    | "totalValue"
    | "projectOwners"
    | "confidentialityLevel"
    | "startDate"
    | "endDate"
    | "planType"
    | "priority"
    | "municipalityRole"
    | "projectPhase"
    | "planningPlanStatus"
    | "municipality"
    | "district"
    | "neighbourhood";

export type ColumnConfig = {
    [key in ColumnField]: {
        width?: number;
        show?: boolean;
    };
};

export const initialColumnConfig: ColumnConfig = {
    projectName: {},
    totalValue: {},
    projectOwners: {},
    confidentialityLevel: {},
    startDate: {},
    endDate: {},
    planType: {},
    priority: {},
    municipalityRole: {},
    projectPhase: {},
    planningPlanStatus: {},
    municipality: {},
    district: {},
    neighbourhood: {},
};

export const saveColumnConfig = (config: ColumnConfig) => {
    localStorage.setItem("projectsTableColumnConfig", JSON.stringify(config));
};

export const loadColumnConfig = (): ColumnConfig | null => {
    const config = localStorage.getItem("projectsTableColumnConfig");
    return config ? JSON.parse(config) : null;
};

export const disabledConfidentialityLevelsForExport = ["PRIVATE", "INTERNAL_CIVIL", "INTERNAL_MANAGEMENT", "INTERNAL_COUNCIL", "EXTERNAL_REGIONAL"];
