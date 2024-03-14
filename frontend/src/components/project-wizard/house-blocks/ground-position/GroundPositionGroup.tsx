import { Stack, Typography } from "@mui/material";
import { WizardCard } from "../../WizardCard";
import { t } from "i18next";
import { HouseBlock } from "../types";
import { SingleNumberInput } from "./SingleNumberInput";

export type GroundPositionInformationProps = {
    projectForm: HouseBlock;
    setProjectForm(project: HouseBlock): void;
    edit: boolean;
    editForm: boolean;
};

export const GroundPositionGroup = ({ projectForm, setProjectForm, edit, editForm }: GroundPositionInformationProps) => {
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
            <SingleNumberInput state={{ projectForm, setProjectForm, edit, editForm }} value="noPermissionOwner" translationPath={translationPath} />
            <SingleNumberInput state={{ projectForm, setProjectForm, edit, editForm }} value="intentionPermissionOwner" translationPath={translationPath} />
            <SingleNumberInput state={{ projectForm, setProjectForm, edit, editForm }} value="formalPermissionOwner" translationPath={translationPath} />
        </WizardCard>
    );
};
