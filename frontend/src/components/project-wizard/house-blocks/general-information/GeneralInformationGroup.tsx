import { Typography } from "@mui/material";
import { WizardCard } from "../../WizardCard";
import { t } from "i18next";
import { HouseBlock } from "../../../../types/houseBlockTypes";
import { HouseBlockSize, SizeInput } from "./SizeInput";
import { DateValidationErrors } from "../../../../pages/ProjectWizardBlocks";
import TetxInput from "../../../project/inputs/TextInput";
import { Dayjs } from "dayjs";
import DateInput from "../../../project/inputs/DateInput";

export type GeneralInformationProps = {
    houseBlock: HouseBlock;
    setHouseBlock(houseBlock: HouseBlock): void;
    readOnly: boolean;
    errors: DateValidationErrors;
};

export const GeneralInformationGroup = ({ readOnly, houseBlock, setHouseBlock, errors }: GeneralInformationProps) => {
    const handleNameChange = (event: React.ChangeEvent<HTMLInputElement>) => {
        const newName = event.target.value.trimStart();
        setHouseBlock({ ...houseBlock, houseblockName: newName });
    };
    return (
        <WizardCard>
            <Typography fontWeight={600} mb={2}>
                {t("wizard.houseBlocks.generalInformation.title")}
            </Typography>
            <TetxInput
                readOnly={readOnly}
                value={houseBlock.houseblockName}
                setValue={handleNameChange}
                mandatory={true}
                title={t("createProject.houseBlocksForm.nameLabel")}
                errorText={t("createProject.nameIsRequried")}
            />
            <SizeInput
                readOnly={readOnly}
                houseBlockSize={houseBlock.size}
                updateHouseBlockSize={(houseBlockSize: HouseBlockSize) => setHouseBlock({ ...houseBlock, size: houseBlockSize })}
            />
            <DateInput
                readOnly={readOnly}
                value={houseBlock.startDate}
                setValue={(e: Dayjs | null) => {
                    const newStartDate = e ? e.format("YYYY-MM-DD") : null;
                    setHouseBlock({ ...houseBlock, startDate: newStartDate });
                }}
                error={errors.startDateError}
                mandatory={true}
                title={t("createProject.houseBlocksForm.startDate")}
                errorText={t("wizard.houseBlocks.startDateWarningMissing")}
            />
            <DateInput
                readOnly={readOnly}
                value={houseBlock.endDate}
                setValue={(e: Dayjs | null) => {
                    const newEndDate = e ? e.format("YYYY-MM-DD") : null;
                    setHouseBlock({ ...houseBlock, endDate: newEndDate });
                }}
                error={errors.endDateError}
                mandatory={true}
                title={t("createProject.houseBlocksForm.endDate")}
                errorText={t("wizard.houseBlocks.endDateWarningMissing")}
            />
        </WizardCard>
    );
};
