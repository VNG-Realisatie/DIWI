import { Grid, Select, MenuItem, TextField, IconButton, Typography } from "@mui/material";
import { OwnershipSingleValue, RangeValue } from "../../../../types/houseBlockTypes";
import DeleteIcon from "@mui/icons-material/Delete";
import { OwnershipValueType, ownershipValueOptions } from "../../../../types/enums";
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

const NumericRangeLabel = ({ value }: { value: RangeValue }) => {
    const { t } = useTranslation();
    if (value.value !== null) {
        return <Typography>{value.value}</Typography>;
    } else if (value.max != null && value.min != null) {
        return (
            <Typography>
                {value.min} - {value.max}
            </Typography>
        );
    } else if (value.min != null) {
        return (
            <Typography>
                {value.min} {t("generic.andMore")}
            </Typography>
        );
    } else if (value.max != null) {
        return (
            <Typography>
                {value.max} {t("generic.andLess")}
            </Typography>
        );
    } else {
        return <></>;
    }
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
                        <NumericRangeLabel value={ownership.value} />
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
                        <NumericRangeLabel value={ownership.rentalValue} />
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
