import { AlertColor } from "@mui/material";
import { HouseBlock, HouseBlockWithCustomProperties } from "../../types/houseBlockTypes";
import { t } from "i18next";

export const validateHouseBlock = (houseBlock: HouseBlockWithCustomProperties, setAlert: (message: string, type: AlertColor) => void): boolean => {
    let isValid = true;
    const invalidOwnershipAmount =
        houseBlock.ownershipValue.some((owner) => !isOwnershipAmountValid(owner.amount)) ||
        houseBlock.ownershipValue.some(
            (owner) =>
                owner.amount > 0 &&
                !owner.rentalValueCategoryId &&
                !owner.valueCategoryId &&
                JSON.stringify(owner.rentalValue) === JSON.stringify({ value: null, min: null, max: null }) &&
                JSON.stringify(owner.value) === JSON.stringify({ value: null, min: null, max: null }),
        );
    if (
        !houseBlock.endDate ||
        !houseBlock.startDate ||
        !houseBlock.houseblockName ||
        !houseBlock.mutation.amount ||
        houseBlock.mutation.amount <= 0 ||
        !houseBlock.mutation.kind ||
        invalidOwnershipAmount ||
        !checkConsistencyOwnerShipValueAndMutation(houseBlock)
    ) {
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
