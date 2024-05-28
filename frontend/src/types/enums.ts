import { components } from "./schema";

export type PlanStatusOptions =
    | "_1A_ONHERROEPELIJK"
    | "_1B_ONHERROEPELIJK_MET_UITWERKING_NODIG"
    | "_1C_ONHERROEPELIJK_MET_BW_NODIG"
    | "_2A_VASTGESTELD"
    | "_2B_VASTGESTELD_MET_UITWERKING_NODIG"
    | "_2C_VASTGESTELD_MET_BW_NODIG"
    | "_3_IN_VOORBEREIDING"
    | "_4A_OPGENOMEN_IN_VISIE"
    | "_4B_NIET_OPGENOMEN_IN_VISIE";

export type ProjectPhaseOptions = "_1_CONCEPT" | "_2_INITIATIVE" | "_3_DEFINITION" | "_4_DESIGN" | "_5_PREPARATION" | "_6_REALIZATION" | "_7_AFTERCARE";

export type PlanTypeOptions = "PAND_TRANSFORMATIE" | "TRANSFORMATIEGEBIED" | "HERSTRUCTURERING" | "VERDICHTING" | "UITBREIDING_UITLEG" | "UITBREIDING_OVERIG";

export type MutationKind = components["schemas"]["Mutation"]["kind"];
export const mutationKindOptions = ["CONSTRUCTION", "DEMOLITION"];

export type OwnershipValueType = components["schemas"]["OwnershipValue"]["type"];
export const ownershipValueOptions = ["KOOPWONING", "HUURWONING_PARTICULIERE_VERHUURDER", "HUURWONING_WONINGCORPORATIE"];

export type ConfidentialityLevel =
    | "PRIVATE"
    | "INTERNAL_CIVIL"
    | "INTERNAL_MANAGEMENT"
    | "INTERNAL_COUNCIL"
    | "EXTERNAL_REGIONAL"
    | "EXTERNAL_GOVERNMENTAL"
    | "PUBLIC";

export type ObjectType = "PROJECT" | "WONINGBLOK";
export const objectType = ["PROJECT", "WONINGBLOK"];

export type PropertyType = "BOOLEAN" | "CATEGORY" | "ORDINAL" | "NUMERIC" | "TEXT";
export const propertyType = ["BOOLEAN", "CATEGORY", "ORDINAL", "NUMERIC", "TEXT"];
