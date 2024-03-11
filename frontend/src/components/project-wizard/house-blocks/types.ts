import { Dayjs } from "dayjs";
import { MutationSelectOptions, OwnershipValueType } from "../../../types/enums";

export type GeneralInformation = {
    startDate: Dayjs | null | string;
    endDate: Dayjs | null | string;
    houseblockName: string;
    size: {
        value: number | null;
        min: number | null;
        max: number | null;
    };
};

export type MutationInformations = {
    mutationKind: MutationSelectOptions[] | []; // list of enum values: [BOUW, SLOOP, TRANSFORMATIE, SPLITSING]
    grossPlanCapacity: number | null;
    netPlanCapacity: number | null;
    demolition: number | null;
};

export type PhysicalInformations = {
    [key: string]: number | null;
};
export type PurposeInformations = {
    [key: string]: number | null;
};
export type HouseTypeInformations = {
    [key: string]: number | null;
};

export type OwnershipSingleValue = {
    type: OwnershipValueType;
    amount: number | null;
    value: {
        value: number | null;
        min: number | null;
        max: number | null;
    };
    rentalValue: {
        value: number | null;
        min: number | null;
        max: number | null;
    };
};

export type HouseBlock = {
    startDate: Dayjs | null | string;
    endDate: Dayjs | null | string;
    projectId?: string;
    houseblockId?: string;
    houseblockName: string;
    size: {
        value: number | null;
        min: number | null;
        max: number | null;
    };
    programming: boolean | null; //true/false/null

    mutation: MutationInformations;

    //eigendom en waarde/ ownership and value
    ownershipValue: OwnershipSingleValue[];

    //grond positie
    groundPosition: {
        noPermissionOwner: number | null;
        intentionPermissionOwner: number | null;
        formalPermissionOwner: number | null;
    };

    // fysiek voorkomen
    physicalAppeareance: PhysicalInformations;

    // huizen type
    houseType: HouseTypeInformations;

    //doel
    purpose: PurposeInformations;
};
