import { AlertColor } from "@mui/material";
import { HouseBlock, HouseBlockWithCustomProperties } from "../../types/houseBlockTypes";
import { t } from "i18next";

export const validateOwnership = (houseBlock: HouseBlockWithCustomProperties) => {
    return (
        houseBlock.ownershipValue.some((owner) => !isOwnershipAmountValid(owner.amount)) ||
        houseBlock.ownershipValue.some(
            (owner) =>
                owner.amount > 0 &&
                !owner.rentalValueCategoryId &&
                !owner.valueCategoryId &&
                JSON.stringify(owner.rentalValue) === JSON.stringify({ value: null, min: null, max: null }) &&
                JSON.stringify(owner.value) === JSON.stringify({ value: null, min: null, max: null }),
        )
    );
};

export const isHouseBlockInvalid = (houseBlock: HouseBlockWithCustomProperties, invalidOwnershipAmount: boolean): boolean => {
    return (
        !houseBlock.endDate ||
        !houseBlock.startDate ||
        houseBlock.endDate < houseBlock.startDate ||
        !houseBlock.houseblockName ||
        !houseBlock.mutation.amount ||
        houseBlock.mutation.amount <= 0 ||
        !houseBlock.mutation.kind ||
        invalidOwnershipAmount ||
        !checkConsistencyOwnerShipValueAndMutation(houseBlock)
    );
};

export const validateHouseBlock = (houseBlock: HouseBlockWithCustomProperties, setAlert: (message: string, type: AlertColor) => void): boolean => {
    let isValid = true;
    const invalidOwnershipAmount = validateOwnership(houseBlock);
    if (isHouseBlockInvalid(houseBlock, invalidOwnershipAmount)) {
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
    const ownershipValue = houseBlock.ownershipValue.reduce((acc, curr) => acc + curr.amount, 0);
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
