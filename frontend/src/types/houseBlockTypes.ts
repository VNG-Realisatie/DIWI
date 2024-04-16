import { CustomPropertyValue } from "../api/customPropServices";
import { OwnershipValueType } from "./enums";
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

export type Mutation = components["schemas"]["Mutation"];

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

    mutation: Mutation;

    ownershipValue: OwnershipSingleValue[];

    groundPosition: GroundPositionInformations;

    physicalAppearance: AmountInformation;

    houseType: HouseTypeInformations;

    targetGroup: AmountInformation;
};

export type AmountModel = components["schemas"]["AmountModel"];
