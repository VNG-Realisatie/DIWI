import { Stack, Typography } from "@mui/material";
import { WizardCard } from "../../WizardCard";
import { t } from "i18next";
import { PhysicalInformations } from "../types";
import { SingleNumberInput } from "./SingleNumberInput";

export type PhysicalAppeareanceInformationProps = {
    projectForm: PhysicalInformations;
    setProjectForm(project: PhysicalInformations): void;
};

export const PhysicalAppeareanceGroup = ({ projectForm, setProjectForm }: PhysicalAppeareanceInformationProps) => {
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
            <SingleNumberInput state={{ projectForm, setProjectForm }} value="tussenwoning" translationPath={translationPath} />
            <SingleNumberInput state={{ projectForm, setProjectForm }} value="tweeondereenkap" translationPath={translationPath} />
            <SingleNumberInput state={{ projectForm, setProjectForm }} value="portiekflat" translationPath={translationPath} />
            <SingleNumberInput state={{ projectForm, setProjectForm }} value="hoekwoning" translationPath={translationPath} />
            <SingleNumberInput state={{ projectForm, setProjectForm }} value="vrijstaand" translationPath={translationPath} />
            <SingleNumberInput
                state={{ projectForm, setProjectForm }}
                value="gallerijflat"
                translationPath="createProject.houseBlocksForm.physicalAppearance"
            />
        </WizardCard>
    );
};
