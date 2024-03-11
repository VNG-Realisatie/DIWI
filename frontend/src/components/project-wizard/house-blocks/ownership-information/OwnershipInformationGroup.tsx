import { Grid, IconButton, Stack, Typography } from "@mui/material";
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

    const translationPath = "createProject.houseBlocksForm";
    return (
        <WizardCard>
            <Typography fontWeight={600} mb={2}>
                {t(`${translationPath}.ownershipAndValue`)}
            </Typography>
            <Stack direction="row" alignItems="center" spacing={2} my={1}>
                <Typography fontWeight={600} flex={4.2}>
                    {t(`${translationPath}.type`)}
                </Typography>
                <Typography fontWeight={600} flex={2}>
                    {t(`${translationPath}.amount`)}
                </Typography>
                <Typography fontWeight={600} flex={2}>
                    {t(`${translationPath}.value`)}
                </Typography>
                <Typography fontWeight={600} flex={2}>
                    {t(`${translationPath}.rent`)}
                </Typography>
                <Typography fontWeight={600} flex={2}></Typography>
            </Stack>
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
