import { Typography } from "@mui/material";
import { WizardCard } from "../../WizardCard";
import { t } from "i18next";
import { HouseBlock } from "../../../../types/houseBlockTypes";
import { HouseBlockSize, SizeInput } from "./SizeInput";
import { NameInput } from "./NameInput";
import { DateValidationErrors } from "../../../../pages/ProjectWizardBlocks";
import { Dayjs } from "dayjs";
import DateInput from "../../../project/inputs/DateInput";

export type GeneralInformationProps = {
    houseBlock: HouseBlock;
    setHouseBlock(houseBlock: HouseBlock): void;
    readOnly: boolean;
    errors: DateValidationErrors;
};

export const GeneralInformationGroup = ({ readOnly, houseBlock, setHouseBlock, errors }: GeneralInformationProps) => {
    const updateHouseBlockStartDate = (e: Dayjs | null) => {
        const newStartDate = e ? e.format("YYYY-MM-DD") : null;
        setHouseBlock({ ...houseBlock, startDate: newStartDate });
    };

    const updateHouseBlockEndDate = (e: Dayjs | null) => {
        const newEndDate = e ? e.format("YYYY-MM-DD") : null; //
        setHouseBlock({ ...houseBlock, endDate: newEndDate });
    };
    return (
        <WizardCard>
            <Typography fontWeight={600} mb={2}>
                {t("wizard.houseBlocks.generalInformation.title")}
            </Typography>
            <NameInput
                readOnly={readOnly}
                houseblockName={houseBlock.houseblockName}
                upDateHouseBlockName={(newValue: string) => setHouseBlock({ ...houseBlock, houseblockName: newValue })}
            />
            <SizeInput
                readOnly={readOnly}
                houseBlockSize={houseBlock.size}
                updateHouseBlockSize={(houseBlockSize: HouseBlockSize) => setHouseBlock({ ...houseBlock, size: houseBlockSize })}
            />
            <DateInput
                readOnly={readOnly}
                value={houseBlock.startDate}
                setValue={updateHouseBlockStartDate}
                error={errors.startDateError}
                mandatory={true}
                label={t("createProject.houseBlocksForm.startDate")}
                errorText={t("wizard.houseBlocks.startDateWarningMissing")}
            />
            <DateInput
                readOnly={readOnly}
                value={houseBlock.endDate}
                setValue={updateHouseBlockEndDate}
                error={errors.endDateError}
                mandatory={true}
                label={t("createProject.houseBlocksForm.endDate")}
                errorText={t("wizard.houseBlocks.endDateWarningMissing")}
            />
        </WizardCard>
    );
};
