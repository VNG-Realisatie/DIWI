import { MutationSelectOptions, OwnershipValueType } from "../../../types/enums";
import { HouseBlock } from "./types";

export const mutationSelectOptions: MutationSelectOptions[] = ["BOUW", "SLOOP", "TRANSFORMATIE", "SPLITSING"];
export const ownershipValueOptions: OwnershipValueType[] = ["KOOPWONING", "HUURWONING_PARTICULIERE_VERHUURDER", "HUURWONING_WONINGCORPORATIE"];

export const emptyHouseBlockForm: HouseBlock = {
    startDate: null,
    endDate: null,
    houseblockName: "",
    size: {
        value: 0,
        min: null,
        max: null,
    },
    programming: null,
    mutation: {
        mutationKind: [],
        grossPlanCapacity: 0,
        netPlanCapacity: 0,
        demolition: 0,
    },
    ownershipValue: [
        {
            type: "KOOPWONING",
            amount: null,
            value: { value: 0, min: null, max: null },
            rentalValue: { value: 0, min: null, max: null },
        },
        {
            type: "HUURWONING_PARTICULIERE_VERHUURDER",
            amount: null,
            value: { value: 0, min: null, max: null },
            rentalValue: { value: 0, min: null, max: null },
        },
        {
            type: "HUURWONING_WONINGCORPORATIE",
            amount: null,
            value: { value: 0, min: null, max: null },
            rentalValue: { value: 0, min: null, max: null },
        },
    ],
    groundPosition: {
        noPermissionOwner: null,
        intentionPermissionOwner: null,
        formalPermissionOwner: null,
    },
    physicalAppearance: {
        tussenwoning: null,
        tweeondereenkap: null,
        portiekflat: null,
        hoekwoning: null,
        vrijstaand: null,
        gallerijflat: null,
    },
    houseType: {
        meergezinswoning: null,
        eengezinswoning: null,
    },
    purpose: {
        regular: null,
        youth: null,
        student: null,
        elderly: null,
        largeFamilies: null,
        ghz: null,
    },
};
