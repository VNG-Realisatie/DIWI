import { Grid, Select, MenuItem, TextField, IconButton, Typography } from "@mui/material";
import { ownershipValueOptions } from "../constants";
import { OwnershipSingleValue } from "../../../../types/houseBlockTypes";
import DeleteIcon from "@mui/icons-material/Delete";
import { OwnershipValueType } from "../../../../types/enums";
import { InputContainer } from "../InputContainer";
import { useTranslation } from "react-i18next";
import { NumericRangeInput } from "../NumericRangeInput";

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

const OwnershipTypeOption = ({ handleInputChange, ownership, index }: OwnershipProps) => {
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
                        {type}
                    </MenuItem>
                );
            })}
        </Select>
    );
};
const OwnershipAmountInput = ({ handleInputChange, ownership, index }: OwnershipProps) => {
    const { t } = useTranslation();
    return (
        <TextField
            size="small"
            label="Amount"
            type="number"
            required
            fullWidth
            value={ownership.amount !== null && !Number.isNaN(ownership.amount) ? ownership.amount : ""}
            onChange={(e) => handleInputChange(index, { ...ownership, amount: parseInt(e.target.value) })}
            error={!ownership.amount}
            helperText={!ownership.amount ? t("createProject.hasMissingRequiredAreas.amount") : ""}
        />
    );
};

export const OwnershipRowInputs = ({ ownership, index, handleInputChange, handleRemoveRow, readOnly }: Props) => {
    return (
        <Grid container spacing={2} mt={1}>
            <Grid item xs={4}>
                {!readOnly && <OwnershipTypeOption index={index} handleInputChange={handleInputChange} ownership={ownership} />}
                {readOnly && (
                    <InputContainer>
                        <Typography>{ownership?.type}</Typography>
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
                    <NumericRangeInput value={ownership.value} labelText="Value" updateCallBack={(e) => handleInputChange(index, { ...ownership, value: e })} />
                )}
                {readOnly && (
                    <InputContainer>
                        <Typography>
                            {ownership?.value.value !== null
                                ? ownership?.value.value
                                : ownership?.value.min == null && ownership?.value.max === null
                                  ? ""
                                  : ownership?.value.min + "-" + ownership?.value.max}
                        </Typography>
                    </InputContainer>
                )}
            </Grid>
            <Grid item xs={2}>
                {!readOnly && (
                    <NumericRangeInput
                        value={ownership.rentalValue}
                        labelText="RentalValue"
                        updateCallBack={(e) => handleInputChange(index, { ...ownership, rentalValue: e })}
                    />
                )}
                {readOnly && (
                    <InputContainer>
                        <Typography>
                            {ownership?.rentalValue.value !== null
                                ? ownership?.rentalValue.value
                                : ownership?.rentalValue.min == null && ownership?.rentalValue.max === null
                                  ? ""
                                  : ownership?.rentalValue.min + "-" + ownership?.rentalValue.max}
                        </Typography>
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
