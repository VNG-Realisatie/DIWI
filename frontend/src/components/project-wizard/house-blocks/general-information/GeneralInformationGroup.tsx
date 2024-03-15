import { Typography } from "@mui/material";
import { WizardCard } from "../../WizardCard";
import { t } from "i18next";
import { HouseBlock } from "../types";
import { HouseBlockSize, SizeInput } from "./SizeInput";
import { NameInput } from "./NameInput";
import { StartDatePicker } from "./StartDatePicker";
import { EndDatePicker } from "./EndDatePicker";
import { Dayjs } from "dayjs";

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
            <StartDatePicker
                edit={edit}
                editForm={editForm}
                houseBlockStartDate={projectForm.startDate}
                updateHouseBlockStartDate={(e: Dayjs | null) => {
                    setProjectForm({ ...projectForm, startDate: e ? e.toISOString() : null });
                }}
            />
            <EndDatePicker
                edit={edit}
                editForm={editForm}
                houseBlockEndDate={projectForm.endDate}
                updateHouseBlockEndDate={(e: Dayjs | null) => setProjectForm({ ...projectForm, endDate: e ? e.toISOString() : null })}
            />
        </WizardCard>
    );
};
