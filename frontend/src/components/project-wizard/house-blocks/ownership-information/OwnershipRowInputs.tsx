import { Grid, Select, MenuItem, TextField, IconButton } from "@mui/material";
import { ownershipValueOptions } from "../constants";
import { OwnershipSingleValue } from "../types";
import DeleteIcon from "@mui/icons-material/Delete";
import { OwnershipValueType } from "../../../../types/enums";

type Props = {
    ownership: OwnershipSingleValue;
    index: number;
    handleInputChange: (index: number, value: OwnershipSingleValue) => void;
    handleRemoveRow: (index: number) => void;
};

export const OwnershipRowInputs = ({ ownership, index, handleInputChange, handleRemoveRow }: Props) => {
    return (
        <Grid container spacing={2} mt={1}>
            <Grid item xs={4}>
                <Select
                    fullWidth
                    size="small"
                    id="demo-simple-select"
                    value={ownership.type}
                    label="Type"
                    onChange={(e) => handleInputChange(index, { ...ownership, type: e.target.value as OwnershipValueType })}
                >
                    {ownershipValueOptions.map((type) => {
                        return <MenuItem value={type}>{type}</MenuItem>;
                    })}
                </Select>
            </Grid>
            <Grid item xs={2}>
                <TextField
                    size="small"
                    label="Amount"
                    type="number"
                    fullWidth
                    value={ownership.amount}
                    onChange={(e) => handleInputChange(index, { ...ownership, amount: parseInt(e.target.value) })}
                />
            </Grid>
            <Grid item xs={2}>
                <TextField
                    size="small"
                    label="Value"
                    type="number"
                    fullWidth
                    value={ownership.value.value}
                    onChange={(e) => handleInputChange(index, { ...ownership, value: { min: null, max: null, value: parseInt(e.target.value) } })}
                />
            </Grid>
            <Grid item xs={2}>
                <TextField
                    size="small"
                    label="RentalValue"
                    type="number"
                    fullWidth
                    value={ownership.rentalValue.value}
                    onChange={(e) => handleInputChange(index, { ...ownership, rentalValue: { min: null, max: null, value: parseInt(e.target.value) } })}
                />
            </Grid>
            <Grid item xs={1}>
                <IconButton onClick={() => handleRemoveRow(index)}>
                    <DeleteIcon sx={{ color: "red" }} />
                </IconButton>
            </Grid>
        </Grid>
    );
};
