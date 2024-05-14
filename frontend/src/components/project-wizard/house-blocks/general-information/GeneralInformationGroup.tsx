import { Typography } from "@mui/material";
import { WizardCard } from "../../WizardCard";
import { t } from "i18next";
import { HouseBlock } from "../../../../types/houseBlockTypes";
import { StartDatePicker } from "./StartDatePicker";
import { EndDatePicker } from "./EndDatePicker";
import { DateValidationErrors } from "../../../../pages/ProjectWizardBlocks";
import TetxInput from "../../../project/inputs/TextInput";
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
                updateCallBack={(houseBlockSize: any) => setHouseBlock({ ...houseBlock, size: houseBlockSize })}
                mandatory={false}
                title={t("createProject.houseBlocksForm.size")}
            />
            <StartDatePicker
                readOnly={readOnly}
                houseBlockStartDate={houseBlock.startDate}
                updateHouseBlockStartDate={(e) => {
                    setHouseBlock({ ...houseBlock, startDate: e ? e.format("YYYY-MM-DD") : null });
                }}
                errors={errors}
            />
            <EndDatePicker
                readOnly={readOnly}
                houseBlockEndDate={houseBlock.endDate}
                updateHouseBlockEndDate={(e) => setHouseBlock({ ...houseBlock, endDate: e ? e.format("YYYY-MM-DD") : null })}
                errors={errors}
            />
        </WizardCard>
    );
};
