import { Grid, Select, MenuItem, TextField, IconButton } from "@mui/material";
import { ownershipValueOptions } from "../constants";
import { OwnershipSingleValue } from "../types";
import DeleteIcon from "@mui/icons-material/Delete";

type Props = {
    ownership: OwnershipSingleValue;
    index: number;
    handleInputChange: (index: number, field: string, value: any) => void;
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
                    onChange={(e) => handleInputChange(index, "type", e.target.value)}
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
                    onChange={(e) => handleInputChange(index, "amount", e.target.value)}
                />
            </Grid>
            <Grid item xs={2}>
                <TextField
                    size="small"
                    label="Value"
                    type="number"
                    fullWidth
                    value={ownership.value.value}
                    onChange={(e) => handleInputChange(index, "value", { ...ownership.value, value: e.target.value })}
                />
            </Grid>
            <Grid item xs={2}>
                <TextField
                    size="small"
                    label="RentalValue"
                    type="number"
                    fullWidth
                    value={ownership.rentalValue.value}
                    onChange={(e) => handleInputChange(index, "rentalValue", { ...ownership.rentalValue, rentalValue: e.target.value })}
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
