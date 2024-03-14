import { Stack, Typography } from "@mui/material";
import { WizardCard } from "../../WizardCard";
import { t } from "i18next";
import { HouseBlock } from "../types";
import { SingleNumberInput } from "./SingleNumberInput";

export type HouseTypeInformationProps = {
    projectForm: HouseBlock;
    setProjectForm(project: HouseBlock): void;
    edit: boolean;
    editForm: boolean;
};

export const HouseTypeGroup = ({ projectForm, setProjectForm, edit, editForm }: HouseTypeInformationProps) => {
    const translationPath = "createProject.houseBlocksForm.houseType";
    return (
        <WizardCard>
            <Typography fontWeight={600} mb={2}>
                {t(`${translationPath}.title`)}
            </Typography>
            <Stack direction="row" alignItems="center" spacing={3} my={1}>
                <Typography fontWeight={600} flex={3}>
                    {t(`${translationPath}.type`)}
                </Typography>
                <Typography fontWeight={600} flex={1}>
                    {t(`${translationPath}.value`)}
                </Typography>
            </Stack>
            <SingleNumberInput state={{ projectForm, setProjectForm, edit, editForm }} value="meergezinswoning" translationPath={translationPath} />
            <SingleNumberInput state={{ projectForm, setProjectForm, edit, editForm }} value="eengezinswoning" translationPath={translationPath} />
        </WizardCard>
    );
};
