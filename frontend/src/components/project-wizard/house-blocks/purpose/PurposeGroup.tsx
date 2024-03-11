import { Stack, Typography } from "@mui/material";
import { WizardCard } from "../../WizardCard";
import { t } from "i18next";
import { PurposeInformations } from "../types";
import { SingleNumberInput } from "./SingleNumberInput";

export type PurposeInformationProps = {
    projectForm: PurposeInformations;
    setProjectForm(project: PurposeInformations): void;
};

export const PurposeGroup = ({ projectForm, setProjectForm }: PurposeInformationProps) => {
    const translationPath = "createProject.houseBlocksForm.purpose";
    return (
        <WizardCard>
            <Typography fontWeight={600} mb={2}>
                {t(`${translationPath}.title`)}
            </Typography>
            <Stack direction="row" alignItems="center" spacing={3} my={1}>
                <Typography fontWeight={600} flex={3}>
                    {t(`${translationPath}.typeOfResidents`)}
                </Typography>
                <Typography fontWeight={600} flex={1}>
                    {t(`${translationPath}.value`)}
                </Typography>
            </Stack>
            <SingleNumberInput state={{ projectForm, setProjectForm }} value="regular" translationPath={translationPath} />
            <SingleNumberInput state={{ projectForm, setProjectForm }} value="youth" translationPath={translationPath} />
            <SingleNumberInput state={{ projectForm, setProjectForm }} value="student" translationPath={translationPath} />
            <SingleNumberInput state={{ projectForm, setProjectForm }} value="elderly" translationPath={translationPath} />
            <SingleNumberInput state={{ projectForm, setProjectForm }} value="largeFamilies" translationPath={translationPath} />
            <SingleNumberInput state={{ projectForm, setProjectForm }} value="ghz" translationPath={translationPath} />
        </WizardCard>
    );
};
