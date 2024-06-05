import { Typography } from "@mui/material";
import { WizardCard } from "../../WizardCard";
import { t } from "i18next";
import { HouseBlock } from "../../../../types/houseBlockTypes";
import { DateValidationErrors } from "../../../../pages/ProjectWizardBlocks";
import TetxInput from "../../../project/inputs/TextInput";
import { Dayjs } from "dayjs";
import DateInput from "../../../project/inputs/DateInput";
import RangeNumberInput from "../../../project/inputs/RangeNumberInput";

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
            <RangeNumberInput
                readOnly={readOnly}
                value={houseBlock.size}
                // eslint-disable-next-line @typescript-eslint/no-explicit-any
                updateCallBack={(houseBlockSize: any) => setHouseBlock({ ...houseBlock, size: houseBlockSize })}
                mandatory={false}
                title={t("createProject.houseBlocksForm.size")}
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
