import { Grid, IconButton, Stack, Typography } from "@mui/material";
import { WizardCard } from "../../WizardCard";
import { t } from "i18next";
import { HouseBlock, OwnershipSingleValue } from "../types";
import AddIcon from "@mui/icons-material/Add";
import { OwnershipRowInputs } from "./OwnershipRowInputs";

export type OwnershipInformationProps = {
    projectForm: HouseBlock;
    setProjectForm(project: HouseBlock): void;
    edit: boolean;
    editForm: boolean;
};

export const OwnershipInformationGroup = ({ projectForm, setProjectForm, edit, editForm }: OwnershipInformationProps) => {
    const handleAddRow = () => {
        setProjectForm({
            ...projectForm,
            ownershipValue: [
                ...projectForm.ownershipValue,
                {
                    type: "",
                    amount: null,
                    value: { value: 0, min: null, max: null },
                    rentalValue: { value: 0, min: null, max: null },
                },
            ],
        });
    };

    const handleInputChange = (index: number, value: OwnershipSingleValue) => {
        const updatedValues = [...projectForm.ownershipValue];
        updatedValues[index] = value;
        setProjectForm({
            ...projectForm,
            ownershipValue: updatedValues,
        });
    };

    const handleRemoveRow = (index: number) => {
        const updatedValues = [...projectForm.ownershipValue];
        updatedValues.splice(index, 1);
        setProjectForm({
            ...projectForm,
            ownershipValue: updatedValues,
        });
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
                {projectForm.ownershipValue.map((ownership, index) => (
                    <OwnershipRowInputs
                        key={index}
                        index={index}
                        handleRemoveRow={handleRemoveRow}
                        handleInputChange={handleInputChange}
                        ownership={ownership}
                        edit={edit}
                        editForm={editForm}
                    />
                ))}
                {((edit && editForm) || (!edit && !editForm)) && (
                    <IconButton onClick={handleAddRow}>
                        <AddIcon sx={{ color: "green" }} />
                    </IconButton>
                )}
            </Grid>
        </WizardCard>
    );
};
