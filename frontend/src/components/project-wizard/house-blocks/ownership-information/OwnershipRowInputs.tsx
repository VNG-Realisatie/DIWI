import DeleteIcon from "@mui/icons-material/Delete";
import { Grid, IconButton, TextField } from "@mui/material";
import { t } from "i18next";
import { OwnershipValueType, ownershipValueOptions } from "../../../../types/enums";
import { OwnershipSingleValue } from "../../../../types/houseBlockTypes";
import CategoryInput from "../../../project/inputs/CategoryInput";
import RangeNumberInput from "../../../project/inputs/RangeNumberInput";
import InputLabelStack from "../../../project/inputs/InputLabelStack";
import { useEffect } from "react";
import { useCustomPropertyStore } from "../../../../hooks/useCustomPropertyStore";

const translationPath = "createProject.houseBlocksForm";

type Props = {
    ownership: OwnershipSingleValue;
    index: number;
    handleInputChange: (index: number, value: OwnershipSingleValue) => void;
    handleRemoveRow: (index: number) => void;
    readOnly: boolean;
    isOwnerShipValueAndMutationConsistent: boolean;
    isPolicyGoal?: boolean;
};
type OwnershipProps = {
    ownership: OwnershipSingleValue;
    index: number;
    handleInputChange: (index: number, value: OwnershipSingleValue) => void;
    readOnly: boolean;
    title: string;
    mandatory: boolean;
    isOwnerShipValueAndMutationConsistent: boolean;
};

// eslint-disable-next-line react-refresh/only-export-components
export const isOwnershipAmountValid = (amount: number): boolean => {
    return Number.isInteger(amount) && amount >= 0;
};

const OwnershipAmountInput = ({ handleInputChange, ownership, index, readOnly, title, mandatory, isOwnerShipValueAndMutationConsistent }: OwnershipProps) => {
    const isAmountValid = isOwnershipAmountValid(ownership.amount ? ownership.amount : 0);
    const isConsistent = isOwnerShipValueAndMutationConsistent;
    const checkIfOwnerShipValueAndMutationConsistent = () => {
        if (!isAmountValid) {
            return t("createProject.hasMissingRequiredAreas.amount");
        } else {
            if (!isConsistent) {
                return t("createProject.hasMissingRequiredAreas.amountNotConsistent");
            }
            return "";
        }
    };
    return (
        <InputLabelStack title={title || ""} mandatory={mandatory}>
            <TextField
                size="small"
                type="number"
                required
                fullWidth
                value={!Number.isNaN(ownership.amount) ? ownership.amount : ""}
                onChange={(e) => handleInputChange(index, { ...ownership, amount: parseInt(e.target.value) })}
                error={!isAmountValid || !isConsistent}
                helperText={checkIfOwnerShipValueAndMutationConsistent()}
                disabled={readOnly}
                InputProps={{
                    inputProps: {
                        min: 0,
                    },
                }}
            />
        </InputLabelStack>
    );
};

export const OwnershipRowInputs = ({
    ownership,
    index,
    handleInputChange,
    handleRemoveRow,
    readOnly,
    isOwnerShipValueAndMutationConsistent,
    isPolicyGoal = false,
}: Props) => {
    const isKoopwoning = ownership.type === "KOOPWONING";
    const isHuurwoning = ownership.type === "HUURWONING_PARTICULIERE_VERHUURDER" || ownership.type === "HUURWONING_WONINGCORPORATIE";
    const { houseBlockCustomProperties } = useCustomPropertyStore();

    const rangeCategories = houseBlockCustomProperties.filter((property) => !property.disabled && property.propertyType === "RANGE_CATEGORY");

    const filteredCategories = rangeCategories.filter((property) => {
        if (isKoopwoning && property.name === "priceRangeBuy") {
            return true;
        }
        if (isHuurwoning && property.name === "priceRangeRent") {
            return true;
        }
        return false;
    });

    const customProperty = filteredCategories[0];

    const priceCategoryOptions = customProperty ? customProperty?.ranges?.filter((option) => !option.disabled) : [];
    const selectedValueCategory = priceCategoryOptions?.find((option) => option.id === ownership.valueCategoryId);
    const valueCategoryName = selectedValueCategory ? selectedValueCategory.name : "";
    const selectedRentalValueCategory = priceCategoryOptions?.find((option) => option.id === ownership.rentalValueCategoryId);
    const rentalValueCategoryName = selectedRentalValueCategory ? selectedRentalValueCategory.name : "";

    const isPriceCategorySelected = Boolean(ownership.valueCategoryId || ownership.rentalValueCategoryId);

    const showErrors = () => {
        if (
            ownership.amount &&
            ownership.amount > 0 &&
            !isPriceCategorySelected &&
            JSON.stringify(ownership.rentalValue) === JSON.stringify({ value: null, min: null, max: null }) &&
            JSON.stringify(ownership.value) === JSON.stringify({ value: null, min: null, max: null })
        ) {
            return true;
        }
        return false;
    };
    const errorText = t("createProject.houseBlocksForm.ownershipAndValue.error");

    useEffect(() => {
        handleInputChange(index, {
            ...ownership,
            value: isPriceCategorySelected || isHuurwoning ? { value: null, min: null, max: null } : ownership.value,
            rentalValue: isPriceCategorySelected || isKoopwoning ? { value: null, min: null, max: null } : ownership.rentalValue,
            valueCategoryId: isKoopwoning ? ownership.valueCategoryId : undefined,
            rentalValueCategoryId: isHuurwoning ? ownership.rentalValueCategoryId : undefined,
        });
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [isPriceCategorySelected, isKoopwoning, isHuurwoning]);

    return (
        <Grid container spacing={2} mt={1} direction="row">
            <Grid item xs={3} className="ownership-category">
                <CategoryInput
                    title={t(`${translationPath}.type`)}
                    readOnly={readOnly}
                    values={ownership.type ? { id: ownership.type, name: ownership.type } : null}
                    setValue={(_, newValue) => handleInputChange(index, { ...ownership, type: newValue ? (newValue.id as OwnershipValueType) : undefined })}
                    mandatory={false}
                    options={ownershipValueOptions.map((value) => ({ id: value, name: value }))}
                    multiple={false}
                    translationPath="createProject.houseBlocksForm.ownershipAndValue.type."
                    tooltipInfoText={t("tooltipInfo.soort.title")}
                />
            </Grid>
            {!isPolicyGoal && (
                <Grid item xs={1} className="ownership-house-amount">
                    <OwnershipAmountInput
                        mandatory={false}
                        readOnly={readOnly}
                        title={t(`${translationPath}.amount`)}
                        index={index}
                        handleInputChange={handleInputChange}
                        ownership={ownership}
                        isOwnerShipValueAndMutationConsistent={isOwnerShipValueAndMutationConsistent}
                    />
                </Grid>
            )}
            <Grid item xs={2} className="price-category">
                <CategoryInput
                    title={t(`${translationPath}.priceCategory`)}
                    readOnly={readOnly}
                    values={
                        ownership.rangeCategoryOption?.id && ownership.rangeCategoryOption?.name
                            ? {
                                  id: ownership.rangeCategoryOption.id,
                                  name: ownership.rangeCategoryOption.name,
                              }
                            : ownership.valueCategoryId && valueCategoryName
                              ? {
                                    id: ownership.valueCategoryId,
                                    name: valueCategoryName,
                                }
                              : ownership.rentalValueCategoryId && rentalValueCategoryName
                                ? {
                                      id: ownership.rentalValueCategoryId,
                                      name: rentalValueCategoryName,
                                  }
                                : null
                    }
                    setValue={(_, newValue) => {
                        if (isKoopwoning) {
                            handleInputChange(index, { ...ownership, valueCategoryId: newValue ? newValue.id : undefined, rentalValueCategoryId: undefined });
                        } else if (isHuurwoning) {
                            handleInputChange(index, { ...ownership, rentalValueCategoryId: newValue ? newValue.id : undefined, valueCategoryId: undefined });
                        }
                    }}
                    mandatory={false}
                    options={priceCategoryOptions ?? []}
                    multiple={false}
                    displayError={showErrors()}
                    error={errorText}
                />
            </Grid>
            {!isPolicyGoal && (
                <Grid item xs={2.8} className="ownership-house-value">
                    <RangeNumberInput
                        value={!isHuurwoning && ownership.value ? ownership.value : { value: null, min: null, max: null }}
                        updateCallBack={(e) => handleInputChange(index, { ...ownership, value: e })}
                        readOnly={readOnly || isPriceCategorySelected || isHuurwoning}
                        mandatory={false}
                        isMonetary={true}
                        title={t(`${translationPath}.value`)}
                        displayError={showErrors() && !isHuurwoning}
                        errorText={errorText}
                    />
                </Grid>
            )}
            {!isPolicyGoal && (
                <Grid item xs={2.8} className="ownership-house-rent">
                    <RangeNumberInput
                        value={!isKoopwoning && ownership.rentalValue ? ownership.rentalValue : { value: null, min: null, max: null }}
                        updateCallBack={(e) => handleInputChange(index, { ...ownership, rentalValue: e })}
                        readOnly={readOnly || isPriceCategorySelected || isKoopwoning}
                        mandatory={false}
                        isMonetary={true}
                        title={t(`${translationPath}.rent`)}
                        displayError={showErrors() && !isKoopwoning}
                        errorText={errorText}
                    />
                </Grid>
            )}
            {!isPolicyGoal && (
                <Grid item xs={0.3} className="ownership-delete-icon" style={{ display: "flex", flexDirection: "column", justifyContent: "flex-end" }}>
                    {!readOnly && (
                        <IconButton onClick={() => handleRemoveRow(index)}>
                            <DeleteIcon sx={{ color: "red" }} />
                        </IconButton>
                    )}
                </Grid>
            )}
        </Grid>
    );
};
