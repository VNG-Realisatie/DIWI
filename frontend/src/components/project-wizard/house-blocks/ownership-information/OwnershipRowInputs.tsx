import { Grid, Select, MenuItem, TextField, IconButton, Typography } from "@mui/material";
import { ownershipValueOptions } from "../constants";
import { OwnershipSingleValue } from "../types";
import DeleteIcon from "@mui/icons-material/Delete";
import { OwnershipValueType } from "../../../../types/enums";
import { InputContainer } from "../InputContainer";

type Props = {
    ownership: OwnershipSingleValue;
    index: number;
    handleInputChange: (index: number, value: OwnershipSingleValue) => void;
    handleRemoveRow: (index: number) => void;
    edit: boolean;
    editForm: boolean;
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
    return (
        <TextField
            size="small"
            label="Amount"
            type="number"
            fullWidth
            value={ownership.amount}
            onChange={(e) => handleInputChange(index, { ...ownership, amount: parseInt(e.target.value) })}
        />
    );
};
const OwnershipValueInput = ({ handleInputChange, ownership, index }: OwnershipProps) => {
    return (
        <TextField
            size="small"
            label="Value"
            type="number"
            fullWidth
            value={ownership.value.value}
            onChange={(e) => handleInputChange(index, { ...ownership, value: { min: null, max: null, value: parseInt(e.target.value) } })}
        />
    );
};

const OwnershipRentalValueInput = ({ handleInputChange, ownership, index }: OwnershipProps) => {
    return (
        <TextField
            size="small"
            label="RentalValue"
            type="number"
            fullWidth
            value={ownership.rentalValue.value}
            onChange={(e) => handleInputChange(index, { ...ownership, rentalValue: { min: null, max: null, value: parseInt(e.target.value) } })}
        />
    );
};

export const OwnershipRowInputs = ({ ownership, index, handleInputChange, handleRemoveRow, edit, editForm }: Props) => {
    return (
        <Grid container spacing={2} mt={1}>
            <Grid item xs={4}>
                {edit && editForm && <OwnershipTypeOption index={index} handleInputChange={handleInputChange} ownership={ownership} />}
                {!edit && editForm && (
                    <InputContainer>
                        <Typography>{ownership?.type}</Typography>
                    </InputContainer>
                )}
                {!edit && !editForm && <OwnershipTypeOption index={index} handleInputChange={handleInputChange} ownership={ownership} />}
            </Grid>
            <Grid item xs={2}>
                {edit && editForm && <OwnershipAmountInput index={index} handleInputChange={handleInputChange} ownership={ownership} />}
                {!edit && editForm && (
                    <InputContainer>
                        <Typography>{ownership?.amount}</Typography>
                    </InputContainer>
                )}
                {!edit && !editForm && <OwnershipAmountInput index={index} handleInputChange={handleInputChange} ownership={ownership} />}
            </Grid>
            <Grid item xs={2}>
                {edit && editForm && <OwnershipValueInput index={index} handleInputChange={handleInputChange} ownership={ownership} />}
                {!edit && editForm && (
                    <InputContainer>
                        <Typography>{ownership?.value?.value}</Typography>
                    </InputContainer>
                )}
                {!edit && !editForm && <OwnershipValueInput index={index} handleInputChange={handleInputChange} ownership={ownership} />}
            </Grid>
            <Grid item xs={2}>
                {edit && editForm && <OwnershipRentalValueInput index={index} handleInputChange={handleInputChange} ownership={ownership} />}
                {!edit && editForm && (
                    <InputContainer>
                        <Typography>{ownership?.rentalValue?.value}</Typography>
                    </InputContainer>
                )}
                {!edit && !editForm && <OwnershipRentalValueInput index={index} handleInputChange={handleInputChange} ownership={ownership} />}
            </Grid>
            <Grid item xs={1}>
                {((edit && editForm) || (!edit && !editForm)) && (
                    <IconButton onClick={() => handleRemoveRow(index)}>
                        <DeleteIcon sx={{ color: "red" }} />
                    </IconButton>
                )}
            </Grid>
        </Grid>
    );
};
