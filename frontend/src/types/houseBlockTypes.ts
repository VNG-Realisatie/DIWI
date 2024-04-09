import { MutationSelectOptions, OwnershipValueType } from "./enums";

export type GeneralInformation = {
    startDate: null | string;
    endDate: null | string;
    houseblockName: string;
    size: {
        value: number | null;
        min: number | null;
        max: number | null;
    };
};

export type MutationInformations = {
    mutationKind: MutationSelectOptions[];
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

export type GroundPositionInformations = {
    [key: string]: number | null;
};

export type RangeValue = {
    value: number | null;
    min: number | null;
    max: number | null;
};

export type OwnershipSingleValue = {
    type: OwnershipValueType;
    amount: number | null;
    value: RangeValue;
    rentalValue: RangeValue;
};

export type HouseBlock = {
    startDate: null | string;
    endDate: null | string;
    projectId?: string;
    houseblockId?: string;
    houseblockName: string;
    size: RangeValue;
    programming: boolean | null;

    mutation: MutationInformations;

    ownershipValue: OwnershipSingleValue[];

    groundPosition: GroundPositionInformations;

    physicalAppearance: PhysicalInformations;

    houseType: HouseTypeInformations;

    purpose: PurposeInformations;
};
