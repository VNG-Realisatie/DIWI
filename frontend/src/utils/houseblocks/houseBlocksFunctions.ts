import { AlertColor } from "@mui/material";
import { HouseBlock, HouseBlockWithCustomProperties } from "../../types/houseBlockTypes";
import { t } from "i18next";
import { Property } from "../../api/adminSettingServices";
import { validateCustomProperties } from "../formValidation";


export const MAX_INT = Math.pow(2, 31) - 1;
export const MAX_INT_LARGER = Math.pow(2, 56);

export const validateOwnership = (houseBlock: HouseBlockWithCustomProperties) => {
    return (
        houseBlock.ownershipValue.some((owner) => owner.amount !== undefined && !isOwnershipAmountValid(owner.amount)) ||
        houseBlock.ownershipValue.some(
            (owner) =>
                !owner.rentalValueCategoryId &&
                !owner.valueCategoryId &&
                JSON.stringify(owner.rentalValue) === JSON.stringify({ value: null, min: null, max: null }) &&
                JSON.stringify(owner.value) === JSON.stringify({ value: null, min: null, max: null }),
            ) ||
            houseBlock.ownershipValue.some((owner) => {
                const { value, rentalValue } = owner;
                return (
                    (value?.value && value.value > MAX_INT) ||
                    (value?.max && value.max > MAX_INT) ||
                    (value?.min && value.min > MAX_INT) ||
                    (rentalValue?.value && rentalValue.value > MAX_INT) ||
                    (rentalValue?.max && rentalValue.max > MAX_INT) ||
                    (rentalValue?.min && rentalValue.min > MAX_INT)
                );
            })
    );
};

export const isHouseBlockInvalid = (houseBlock: HouseBlockWithCustomProperties, invalidOwnershipAmount: boolean, customDefinitions: Property[]): boolean => {
    return (
        houseBlock.size.value && houseBlock.size.value > MAX_INT_LARGER ||
        !houseBlock.endDate ||
        !houseBlock.startDate ||
        houseBlock.endDate < houseBlock.startDate ||
        !houseBlock.houseblockName ||
        houseBlock.mutation.amount === null ||
        houseBlock.mutation.amount < 0 ||
        !houseBlock.mutation.kind ||
        invalidOwnershipAmount ||
        !checkConsistencyOwnerShipValueAndMutation(houseBlock) ||
        !validateCustomProperties(houseBlock.customProperties, customDefinitions)
    );
};

export const validateHouseBlock = (
    houseBlock: HouseBlockWithCustomProperties,
    setAlert: (message: string, type: AlertColor) => void,
    customDefinitions: Property[],
): boolean => {
    let isValid = true;
    const invalidOwnershipAmount = validateOwnership(houseBlock);
    if (isHouseBlockInvalid(houseBlock, invalidOwnershipAmount, customDefinitions)) {
        isValid = false;
    }
    if (!isValid) {
        setAlert(t("createProject.houseBlocksForm.notifications.error"), "warning");
    }
    return isValid;
};

export const isOwnershipAmountValid = (amount: number): boolean => {
    return Number.isInteger(amount) && amount >= 0;
};

export function checkConsistencyOwnerShipValueAndMutation(houseBlock: HouseBlock) {
    const ownershipValue = houseBlock.ownershipValue.reduce((acc, curr) => acc + (curr.amount ?? 0), 0);
    const mutation = houseBlock.mutation.amount ?? 0;
    if (ownershipValue <= mutation) {
        return true;
    }
    return false;
}
export function calculateAmounts(houseBlocks: HouseBlock[]) {
    const constructionAmount = houseBlocks
        .filter((hb) => hb.mutation.kind === "CONSTRUCTION")
        .map((hb) => hb.mutation.amount ?? 0)
        .reduce((a, b) => a + b, 0);

    const demolitionAmount = houseBlocks
        .filter((hb) => hb.mutation.kind === "DEMOLITION")
        .map((hb) => hb.mutation.amount ?? 0)
        .reduce((a, b) => a + b, 0);

    return { constructionAmount, demolitionAmount };
}
