import { Typography } from "@mui/material";
import { WizardCard } from "../../WizardCard";
import { t } from "i18next";
import { HouseBlock } from "../types";
import { HouseBlockSize, SizeInput } from "./SizeInput";
import { NameInput } from "./NameInput";
import { StartDatePicker } from "./StartDatePicker";
import { EndDatePicker } from "./EndDatePicker";

export type GeneralInformationProps = {
    projectForm: HouseBlock;
    setProjectForm(project: HouseBlock): void;
    edit: boolean;
    editForm: boolean;
};

export const GeneralInformationGroup = ({ projectForm, setProjectForm, edit, editForm }: GeneralInformationProps) => {
    return (
        <WizardCard>
            <Typography fontWeight={600} mb={2}>
                {t("wizard.houseBlocks.generalInformation.title")}
            </Typography>
            <NameInput
                edit={edit}
                editForm={editForm}
                houseblockName={projectForm.houseblockName}
                upDateHouseBlockName={(newValue: string) => setProjectForm({ ...projectForm, houseblockName: newValue })}
            />
            <SizeInput
                edit={edit}
                editForm={editForm}
                houseBlockSize={projectForm.size}
                updateHouseBlockSize={(houseBlockSize: HouseBlockSize) => setProjectForm({ ...projectForm, size: houseBlockSize })}
            />
            <StartDatePicker edit={edit} editForm={editForm} projectForm={projectForm} setProjectForm={setProjectForm} />
            <EndDatePicker edit={edit} editForm={editForm} projectForm={projectForm} setProjectForm={setProjectForm} />
        </WizardCard>
    );
};
