import { Typography } from "@mui/material";
import { WizardCard } from "../../WizardCard";
import { t } from "i18next";
import { PhysicalInformations } from "../types";
import { SingleNumberInput } from "./SingleNumberInput";

export type PhysicalAppeareanceInformationProps = {
    projectForm: PhysicalInformations;
    setProjectForm(project: PhysicalInformations): void;
};

export const PhysicalAppeareanceGroup = ({ projectForm, setProjectForm }: PhysicalAppeareanceInformationProps) => {
    return (
        <WizardCard>
            <Typography fontWeight={600} mb={2}>
                {t("createProject.houseBlocksForm.physicalAppearance.title")}
            </Typography>
            <SingleNumberInput
                state={{ projectForm, setProjectForm }}
                value="tussenwoning"
                translationPath="createProject.houseBlocksForm.physicalAppearance"
            />
            <SingleNumberInput
                state={{ projectForm, setProjectForm }}
                value="tweeondereenkap"
                translationPath="createProject.houseBlocksForm.physicalAppearance"
            />
            <SingleNumberInput state={{ projectForm, setProjectForm }} value="portiekflat" translationPath="createProject.houseBlocksForm.physicalAppearance" />
            <SingleNumberInput state={{ projectForm, setProjectForm }} value="hoekwoning" translationPath="createProject.houseBlocksForm.physicalAppearance" />
            <SingleNumberInput state={{ projectForm, setProjectForm }} value="vrijstaand" translationPath="createProject.houseBlocksForm.physicalAppearance" />
            <SingleNumberInput
                state={{ projectForm, setProjectForm }}
                value="gallerijflat"
                translationPath="createProject.houseBlocksForm.physicalAppearance"
            />
        </WizardCard>
    );
};
