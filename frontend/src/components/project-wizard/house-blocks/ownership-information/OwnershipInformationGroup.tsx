import { Grid, IconButton, Typography } from "@mui/material";
import { WizardCard } from "../../WizardCard";
import { t } from "i18next";
import { OwnershipSingleValue } from "../types";
import AddIcon from "@mui/icons-material/Add";
import { OwnershipRowInputs } from "./OwnershipRowInputs";

export type OwnershipInformationProps = {
    projectForm: OwnershipSingleValue[];
    setProjectForm(project: OwnershipSingleValue[]): void;
};

export const OwnershipInformationGroup = ({ projectForm, setProjectForm }: OwnershipInformationProps) => {
    const handleAddRow = () => {
        setProjectForm([
            ...projectForm,
            {
                type: "",
                amount: null,
                value: { value: null, min: null, max: null },
                rentalValue: { value: null, min: null, max: null },
            },
        ]);
    };

    const handleInputChange = (index: number, field: string, value: any) => {
        const updatedValues = [...projectForm];
        (updatedValues[index] as any)[field] = value;
        setProjectForm(updatedValues);
    };

    const handleRemoveRow = (index: number) => {
        const updatedValues = [...projectForm];
        updatedValues.splice(index, 1);
        setProjectForm(updatedValues);
    };

    return (
        <WizardCard>
            <Typography fontWeight={600} mb={2}>
                {t("createProject.houseBlocksForm.ownershipAndValue")}
            </Typography>
            <Grid container>
                {projectForm.map((ownership, index) => (
                    <OwnershipRowInputs index={index} handleRemoveRow={handleRemoveRow} handleInputChange={handleInputChange} ownership={ownership} />
                ))}
                <IconButton onClick={handleAddRow}>
                    <AddIcon sx={{ color: "green" }} />
                </IconButton>
            </Grid>
        </WizardCard>
    );
};
