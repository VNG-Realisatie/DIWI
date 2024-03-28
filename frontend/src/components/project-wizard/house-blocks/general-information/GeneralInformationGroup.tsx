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
    readOnly: boolean;
};

export const GeneralInformationGroup = ({ readOnly, projectForm, setProjectForm }: GeneralInformationProps) => {
    return (
        <WizardCard>
            <Typography fontWeight={600} mb={2}>
                {t("wizard.houseBlocks.generalInformation.title")}
            </Typography>
            <NameInput
                readOnly={readOnly}
                houseblockName={projectForm.houseblockName}
                upDateHouseBlockName={(newValue: string) => setProjectForm({ ...projectForm, houseblockName: newValue })}
            />
            <SizeInput
                readOnly={readOnly}
                houseBlockSize={projectForm.size}
                updateHouseBlockSize={(houseBlockSize: HouseBlockSize) => setProjectForm({ ...projectForm, size: houseBlockSize })}
            />
            <StartDatePicker
                readOnly={readOnly}
                houseBlockStartDate={projectForm.startDate}
                updateHouseBlockStartDate={(e: Dayjs | null) => {
                    setProjectForm({ ...projectForm, startDate: e ? e.format("YYYY-MM-DD") : null });
                }}
            />
            <EndDatePicker
                readOnly={readOnly}
                houseBlockEndDate={projectForm.endDate}
                updateHouseBlockEndDate={(e: Dayjs | null) => setProjectForm({ ...projectForm, endDate: e ? e.format("YYYY-MM-DD") : null })}
            />
        </WizardCard>
    );
};
