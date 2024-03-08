import { Typography } from "@mui/material";
import { WizardCard } from "../../WizardCard";
import { t } from "i18next";
import { GeneralInformation } from "../types";
import { SizeInput } from "./SizeInput";
import { NameInput } from "./NameInput";
import { StartDatePicker } from "./StartDatePicker";
import { EndDatePicker } from "./EndDatePicker";

export type GeneralInformationProps = {
    projectForm: GeneralInformation;
    setProjectForm(project: GeneralInformation): void;
};

export const GeneralInformationGroup = ({ projectForm, setProjectForm }: GeneralInformationProps) => {
    return (
        <WizardCard>
            <Typography fontWeight={600} mb={2}>
                {t("wizard.houseBlocks.generalInformation.title")}
            </Typography>
            <NameInput projectForm={projectForm} setProjectForm={setProjectForm} />
            <SizeInput projectForm={projectForm} setProjectForm={setProjectForm} />
            <StartDatePicker projectForm={projectForm} setProjectForm={setProjectForm} />
            <EndDatePicker projectForm={projectForm} setProjectForm={setProjectForm} />
        </WizardCard>
    );
};
