import { CustomPropertyValue } from "../api/customPropServices";
import { MutationSelectOptions, OwnershipValueType } from "./enums";
import { components } from "./schema";

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

export type AmountInformation = AmountModel[];

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

export type HouseBlockWithCustomProperties = HouseBlock & { customProperties: CustomPropertyValue[] };

export type HouseBlock = {
    tempId?: number;
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

    physicalAppearance: AmountInformation;

    houseType: HouseTypeInformations;

    targetGroup: AmountInformation;
};

export type AmountModel = components["schemas"]["AmountModel"];
