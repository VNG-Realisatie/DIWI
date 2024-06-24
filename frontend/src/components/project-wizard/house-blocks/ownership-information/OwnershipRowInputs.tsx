import DeleteIcon from "@mui/icons-material/Delete";
import { Grid, IconButton, TextField, Typography } from "@mui/material";
import { t } from "i18next";
import { OwnershipValueType, ownershipValueOptions } from "../../../../types/enums";
import { OwnershipSingleValue } from "../../../../types/houseBlockTypes";
import { InputContainer } from "../InputContainer";
import CategoryInput from "../../../project/inputs/CategoryInput";
import RangeNumberInput from "../../../project/inputs/RangeNumberInput";

type Props = {
    ownership: OwnershipSingleValue;
    index: number;
    handleInputChange: (index: number, value: OwnershipSingleValue) => void;
    handleRemoveRow: (index: number) => void;
    readOnly: boolean;
    isOwnerShipValueAndMutationConsistent: boolean;
};
type OwnershipProps = {
    ownership: OwnershipSingleValue;
    index: number;
    handleInputChange: (index: number, value: OwnershipSingleValue) => void;
    isOwnerShipValueAndMutationConsistent: boolean;
};

// eslint-disable-next-line react-refresh/only-export-components
export const isOwnershipAmountValid = (amount: number): boolean => {
    return Number.isInteger(amount) && amount >= 0;
};

const OwnershipAmountInput = ({ handleInputChange, ownership, index, isOwnerShipValueAndMutationConsistent }: OwnershipProps) => {
    const isAmountValid = isOwnershipAmountValid(ownership.amount);
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
        <TextField
            size="small"
            label={t("createProject.houseBlocksForm.amount")}
            type="number"
            required
            fullWidth
            value={ownership.amount !== 0 && !Number.isNaN(ownership.amount) ? ownership.amount : ""}
            onChange={(e) => handleInputChange(index, { ...ownership, amount: parseInt(e.target.value) })}
            error={!isAmountValid || !isConsistent}
            helperText={checkIfOwnerShipValueAndMutationConsistent()}
        />
    );
};

export const OwnershipRowInputs = ({ ownership, index, handleInputChange, handleRemoveRow, readOnly, isOwnerShipValueAndMutationConsistent }: Props) => {
    const isKoopwoning = ownership.type === "KOOPWONING";
    const isHuurwoning = ownership.type === "HUURWONING_PARTICULIERE_VERHUURDER" || ownership.type === "HUURWONING_WONINGCORPORATIE";

    return (
        <Grid container spacing={2} mt={1}>
            <Grid item xs={4} className="ownership-category">
                <CategoryInput
                    readOnly={readOnly}
                    values={ownership.type ? { id: ownership.type, name: ownership.type } : null}
                    setValue={(_, newValue) => handleInputChange(index, { ...ownership, type: newValue ? (newValue.id as OwnershipValueType) : undefined })}
                    mandatory={false}
                    options={ownershipValueOptions.map((value) => ({ id: value, name: value }))}
                    multiple={false}
                    translationPath="createProject.houseBlocksForm.ownershipAndValue.type."
                />
            </Grid>
            <Grid item xs={1.25} className="ownership-house-amount">
                {!readOnly && (
                    <OwnershipAmountInput
                        index={index}
                        handleInputChange={handleInputChange}
                        ownership={ownership}
                        isOwnerShipValueAndMutationConsistent={isOwnerShipValueAndMutationConsistent}
                    />
                )}
                {readOnly && (
                    <InputContainer>
                        <Typography>{ownership?.amount}</Typography>
                    </InputContainer>
                )}
            </Grid>
            <Grid item xs={2.8} className="ownership-house-value">
                <RangeNumberInput
                    value={!isHuurwoning ? ownership.value : { ...ownership.value, value: null }}
                    labelText={t("createProject.houseBlocksForm.value")}
                    updateCallBack={(e) => handleInputChange(index, { ...ownership, value: e })}
                    readOnly={readOnly ? true : isHuurwoning}
                    mandatory={false}
                    isMonetary={true}
                />
            </Grid>
            <Grid item xs={2.8} className="ownership-house-rent">
                <RangeNumberInput
                    value={!isKoopwoning ? ownership.rentalValue : { ...ownership.value, value: null }}
                    labelText={t("createProject.houseBlocksForm.rentalAmount")}
                    updateCallBack={(e) => handleInputChange(index, { ...ownership, rentalValue: e })}
                    readOnly={readOnly ? true : isKoopwoning}
                    mandatory={false}
                    isMonetary={true}
                />
            </Grid>
            <Grid item xs={0.5} className="ownership-delete-icon">
                {!readOnly && (
                    <IconButton onClick={() => handleRemoveRow(index)}>
                        <DeleteIcon sx={{ color: "red" }} />
                    </IconButton>
                )}
            </Grid>
        </Grid>
    );
};
