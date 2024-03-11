import { Stack, Typography } from "@mui/material";
import { WizardCard } from "../../WizardCard";
import { t } from "i18next";
import { GroundPositionInformations } from "../types";
import { SingleNumberInput } from "./SingleNumberInput";

export type GroundPositionInformationProps = {
    projectForm: GroundPositionInformations;
    setProjectForm(project: GroundPositionInformations): void;
};

export const GroundPositionGroup = ({ projectForm, setProjectForm }: GroundPositionInformationProps) => {
    const translationPath = "createProject.houseBlocksForm.groundPosition";
    return (
        <WizardCard>
            <Typography fontWeight={600} mb={2}>
                {t(`${translationPath}.title`)}
            </Typography>
            <Stack direction="row" alignItems="center" spacing={3} my={1}>
                <Typography fontWeight={600} flex={3}>
                    {t(`${translationPath}.situation`)}
                </Typography>
                <Typography fontWeight={600} flex={1}>
                    {t(`${translationPath}.value`)}
                </Typography>
            </Stack>
            <SingleNumberInput state={{ projectForm, setProjectForm }} value="noPermissionOwner" translationPath={translationPath} />
            <SingleNumberInput state={{ projectForm, setProjectForm }} value="intentionPermissionOwner" translationPath={translationPath} />
            <SingleNumberInput state={{ projectForm, setProjectForm }} value="formalPermissionOwner" translationPath={translationPath} />
        </WizardCard>
    );
};
