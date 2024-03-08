import { Grid, IconButton, InputLabel, MenuItem, Paper, Select, TextField, Typography } from "@mui/material";
import { WizardCard } from "../../WizardCard";
import { t } from "i18next";
import { OwnershipSingleValue } from "../types";
import { useState } from "react";
import AddIcon from "@mui/icons-material/Add";
import DeleteIcon from "@mui/icons-material/Delete";
import { ownershipValueOptions } from "../constants";

export type OwnershipInformationProps = {
    projectForm: OwnershipSingleValue[];
    setProjectForm(project: OwnershipSingleValue[]): void;
};

export const OwnershipInformationGroup = ({ projectForm, setProjectForm }: OwnershipInformationProps) => {
    const [ownershipValues, setOwnershipValues] = useState([
        {
            type: "",
            amount: null,
            value: { value: null, min: null, max: null },
            rentalValue: { value: null, min: null, max: null },
        },
    ]);

    const handleAddRow = () => {
        setOwnershipValues((prevValues) => [
            ...prevValues,
            {
                type: "",
                amount: null,
                value: { value: null, min: null, max: null },
                rentalValue: { value: null, min: null, max: null },
            },
        ]);
    };

    const handleInputChange = (index: number, field: string, value: any) => {
        setOwnershipValues((prevValues) => {
            const updatedValues = [...prevValues];
            (updatedValues[index] as any)[field] = value;

            return updatedValues;
        });
    };
    const handleRemoveRow = (index: number) => {
        setOwnershipValues((prevValues) => {
            const updatedValues = [...prevValues];
            updatedValues.splice(index, 1);
            return updatedValues;
        });
    };

    const handleSubmit = () => {
        // Send ownershipValues to the backend for create or update
        console.log("Form submitted:", ownershipValues);
    };
    return (
        <WizardCard>
            <Typography fontWeight={600} mb={2}>
                {t("createProject.houseBlocksForm.ownershipAndValue")}
            </Typography>
            <Grid container>
                {ownershipValues.map((ownership, index) => (
                    <Grid container spacing={2}>
                        <Grid item xs={3}>
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
                                <DeleteIcon />
                            </IconButton>
                        </Grid>
                    </Grid>
                ))}
                <IconButton onClick={handleAddRow}>
                    <AddIcon />
                </IconButton>
                <button onClick={handleSubmit}>Submit</button>
            </Grid>
        </WizardCard>
    );
};
