import { OptionType } from "../ProjectsTableView";

export const planningPlanStatus:OptionType[] = [
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

export const municipalityRolesOptions: OptionType[] = [
    { id: "ACTIVE", name: "ACTIVE" },
    { id: "PASSIVE", name: "PASSIVE" },
    { id: "NOTHING", name: "NOTHING" },
];
