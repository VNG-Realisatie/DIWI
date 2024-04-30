import DeleteIcon from "@mui/icons-material/Delete";
import { Grid, IconButton, MenuItem, Select, TextField, Typography } from "@mui/material";
import { useTranslation } from "react-i18next";
import { OwnershipValueType, ownershipValueOptions } from "../../../../types/enums";
import { OwnershipSingleValue } from "../../../../types/houseBlockTypes";
import { InputContainer } from "../InputContainer";
import { MonetaryRangeInput } from "../MonetaryRangeInput";
import MonetaryRangeLabel from "../MonetaryRangeLabel";

type Props = {
    ownership: OwnershipSingleValue;
    index: number;
    handleInputChange: (index: number, value: OwnershipSingleValue) => void;
    handleRemoveRow: (index: number) => void;
    readOnly: boolean;
};
type OwnershipProps = {
    ownership: OwnershipSingleValue;
    index: number;
    handleInputChange: (index: number, value: OwnershipSingleValue) => void;
};

export const isOwnershipAmountValid = (amount: number): boolean => {
    return Number.isInteger(amount) && amount >= 0;
};

const OwnershipTypeOption = ({ handleInputChange, ownership, index }: OwnershipProps) => {
    const { t } = useTranslation();
    return (
        <Select
            fullWidth
            size="small"
            id="demo-simple-select"
            value={ownership.type}
            label="Type"
            onChange={(e) => handleInputChange(index, { ...ownership, type: e.target.value as OwnershipValueType })}
        >
            {ownershipValueOptions.map((type) => {
                return (
                    <MenuItem key={type} value={type}>
                        {t(`createProject.houseBlocksForm.ownershipAndValue.type.${type}`)}
                    </MenuItem>
                );
            })}
        </Select>
    );
};
const OwnershipAmountInput = ({ handleInputChange, ownership, index }: OwnershipProps) => {
    const { t } = useTranslation();
    const isAmountValid = isOwnershipAmountValid(ownership.amount);
    return (
        <TextField
            size="small"
            label="Amount"
            type="number"
            required
            fullWidth
            value={ownership.amount !== 0 && !Number.isNaN(ownership.amount) ? ownership.amount : ""}
            onChange={(e) => handleInputChange(index, { ...ownership, amount: parseInt(e.target.value) })}
            error={!isAmountValid}
            helperText={!isAmountValid ? t("createProject.hasMissingRequiredAreas.amount") : ""}
        />
    );
};

export const OwnershipRowInputs = ({ ownership, index, handleInputChange, handleRemoveRow, readOnly }: Props) => {
    const { t } = useTranslation();
    return (
        <Grid container spacing={2} mt={1}>
            <Grid item xs={4}>
                {!readOnly && <OwnershipTypeOption index={index} handleInputChange={handleInputChange} ownership={ownership} />}
                {readOnly && (
                    <InputContainer>
                        <Typography>{t(`createProject.houseBlocksForm.ownershipAndValue.type.${ownership?.type}`)}</Typography>
                    </InputContainer>
                )}
            </Grid>
            <Grid item xs={2}>
                {!readOnly && <OwnershipAmountInput index={index} handleInputChange={handleInputChange} ownership={ownership} />}
                {readOnly && (
                    <InputContainer>
                        <Typography>{ownership?.amount}</Typography>
                    </InputContainer>
                )}
            </Grid>
            <Grid item xs={2}>
                {!readOnly && (
                    <MonetaryRangeInput
                        value={ownership.value}
                        labelText="Value"
                        updateCallBack={(e) => handleInputChange(index, { ...ownership, value: e })}
                    />
                )}
                {readOnly && (
                    <InputContainer>
                        <MonetaryRangeLabel value={ownership.value} />
                    </InputContainer>
                )}
            </Grid>
            <Grid item xs={2}>
                {!readOnly && (
                    <MonetaryRangeInput
                        value={ownership.rentalValue}
                        labelText="RentalValue"
                        updateCallBack={(e) => handleInputChange(index, { ...ownership, rentalValue: e })}
                    />
                )}
                {readOnly && (
                    <InputContainer>
                        <MonetaryRangeLabel value={ownership.rentalValue} />
                    </InputContainer>
                )}
            </Grid>
            <Grid item xs={1}>
                {!readOnly && (
                    <IconButton onClick={() => handleRemoveRow(index)}>
                        <DeleteIcon sx={{ color: "red" }} />
                    </IconButton>
                )}
            </Grid>
        </Grid>
    );
};
