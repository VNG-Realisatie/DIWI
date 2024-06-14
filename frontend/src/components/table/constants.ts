import { PlanStatusOptions, PlanTypeOptions, ProjectPhaseOptions } from "../../types/enums";
import { GenericOptionType, OptionType } from "../project/ProjectsTableView";

export const planningPlanStatus: GenericOptionType<PlanStatusOptions>[] = [
    { id: "_1A_ONHERROEPELIJK", name: "_1A_ONHERROEPELIJK" },
    { id: "_1B_ONHERROEPELIJK_MET_UITWERKING_NODIG", name: "_1B_ONHERROEPELIJK_MET_UITWERKING_NODIG" },
    { id: "_1C_ONHERROEPELIJK_MET_BW_NODIG", name: "_1C_ONHERROEPELIJK_MET_BW_NODIG" },
    { id: "_2A_VASTGESTELD", name: "_2A_VASTGESTELD" },
    { id: "_2B_VASTGESTELD_MET_UITWERKING_NODIG", name: "_2B_VASTGESTELD_MET_UITWERKING_NODIG" },
    { id: "_2C_VASTGESTELD_MET_BW_NODIG", name: "_2C_VASTGESTELD_MET_BW_NODIG" },
    { id: "_3_IN_VOORBEREIDING", name: "_3_IN_VOORBEREIDING" },
    { id: "_4A_OPGENOMEN_IN_VISIE", name: "_4A_OPGENOMEN_IN_VISIE" },
    { id: "_4B_NIET_OPGENOMEN_IN_VISIE", name: "_4B_NIET_OPGENOMEN_IN_VISIE" },
];

export const planTypeOptions: GenericOptionType<PlanTypeOptions>[] = [
    { id: "PAND_TRANSFORMATIE", name: "PAND_TRANSFORMATIE" },
    { id: "TRANSFORMATIEGEBIED", name: "TRANSFORMATIEGEBIED" },
    { id: "HERSTRUCTURERING", name: "HERSTRUCTURERING" },
    { id: "VERDICHTING", name: "VERDICHTING" },
    { id: "UITBREIDING_UITLEG", name: "UITBREIDING_UITLEG" },
    { id: "UITBREIDING_OVERIG", name: "UITBREIDING_OVERIG" },
];

export const projectPhaseOptions: GenericOptionType<ProjectPhaseOptions>[] = [
    { id: "_1_CONCEPT", name: "_1_CONCEPT" },
    { id: "_2_INITIATIVE", name: "_2_INITIATIVE" },
    { id: "_3_DEFINITION", name: "_3_DEFINITION" },
    { id: "_4_DESIGN", name: "_4_DESIGN" },
    { id: "_5_PREPARATION", name: "_5_PREPARATION" },
    { id: "_6_REALIZATION", name: "_6_REALIZATION" },
    { id: "_7_AFTERCARE", name: "_7_AFTERCARE" },
];

export const confidentialityLevelOptions: OptionType[] = [
    { id: "PRIVATE", name: "PRIVATE" },
    { id: "INTERNAL_CIVIL", name: "INTERNAL_CIVIL" },
    { id: "INTERNAL_MANAGEMENT", name: "INTERNAL_MANAGEMENT" },
    { id: "INTERNAL_COUNCIL", name: "INTERNAL_COUNCIL" },
    { id: "EXTERNAL_REGIONAL", name: "EXTERNAL_REGIONAL" },
    { id: "EXTERNAL_GOVERNMENTAL", name: "EXTERNAL_GOVERNMENTAL" },
    { id: "PUBLIC", name: "PUBLIC" },
];
