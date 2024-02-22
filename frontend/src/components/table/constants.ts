import { OptionType } from "../ProjectsTableView";

export const planningPlanStatus: OptionType[] = [
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

export const planTypeOptions: OptionType[] = [
    { id: "PAND_TRANSFORMATIE", name: "PAND_TRANSFORMATIE" },
    { id: "TRANSFORMATIEGEBIED", name: "TRANSFORMATIEGEBIED" },
    { id: "HERSTRUCTURERING", name: "HERSTRUCTURERING" },
    { id: "VERDICHTING", name: "VERDICHTING" },
    { id: "UITBREIDING_UITLEG", name: "UITBREIDING_UITLEG" },
    { id: "UITBREIDING_OVERIG", name: "UITBREIDING_OVERIG" },
];

export const projectPhaseOptions: OptionType[] = [
    { id: "_1_INITIATIEFFASE", name: "_1_INITIATIEFFASE" },
    { id: "_2_PROJECTFASE", name: "_2_PROJECTFASE" },
    { id: "_3_VERGUNNINGSFASE", name: "_3_VERGUNNINGSFASE" },
    { id: "_4_REALISATIEFASE", name: "_4_REALISATIEFASE" },
    { id: "_5_OPLEVERINGSFASE", name: "_5_OPLEVERINGSFASE" },
];

export const confidentialityLevelOptions: OptionType[] = [
    { id: "PRIVE", name: "PRIVE" },
    { id: "INTERN_UITVOERING", name: "INTERN_UITVOERING" },
    { id: "INTERN_RAPPORTAGE", name: "INTERN_RAPPORTAGE" },
    { id: "EXTERN_RAPPORTAGE", name: "EXTERN_RAPPORTAGE" },
    { id: "OPENBAAR", name: "OPENBAAR" },
];
