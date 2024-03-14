import { Stack, Typography } from "@mui/material";
import { WizardCard } from "../../WizardCard";
import { t } from "i18next";
import { HouseBlock } from "../types";
import { SingleNumberInput } from "./SingleNumberInput";

export type PhysicalAppeareanceInformationProps = {
    projectForm: HouseBlock;
    setProjectForm(project: HouseBlock): void;
    edit: boolean;
    editForm: boolean;
};

export const PhysicalAppeareanceGroup = ({ projectForm, setProjectForm, edit, editForm }: PhysicalAppeareanceInformationProps) => {
    const translationPath = "createProject.houseBlocksForm.physicalAppearance";
    return (
        <WizardCard>
            <Typography fontWeight={600} mb={2}>
                {t(`${translationPath}.title`)}
            </Typography>
            <Stack direction="row" alignItems="center" spacing={3} my={1}>
                <Typography fontWeight={600} flex={3}>
                    {t(`${translationPath}.houseType`)}
                </Typography>
                <Typography fontWeight={600} flex={1}>
                    {t(`${translationPath}.value`)}
                </Typography>
            </Stack>
            <SingleNumberInput state={{ projectForm, setProjectForm, edit, editForm }} value="tussenwoning" translationPath={translationPath} />
            <SingleNumberInput state={{ projectForm, setProjectForm, edit, editForm }} value="tweeondereenkap" translationPath={translationPath} />
            <SingleNumberInput state={{ projectForm, setProjectForm, edit, editForm }} value="portiekflat" translationPath={translationPath} />
            <SingleNumberInput state={{ projectForm, setProjectForm, edit, editForm }} value="hoekwoning" translationPath={translationPath} />
            <SingleNumberInput state={{ projectForm, setProjectForm, edit, editForm }} value="vrijstaand" translationPath={translationPath} />
            <SingleNumberInput
                state={{ projectForm, setProjectForm, edit, editForm }}
                value="gallerijflat"
                translationPath="createProject.houseBlocksForm.physicalAppearance"
            />
        </WizardCard>
    );
};
