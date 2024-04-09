import { Typography } from "@mui/material";
import { WizardCard } from "../../WizardCard";
import { t } from "i18next";
import { HouseBlock } from "../../../../types/houseBlockTypes";
import { HouseBlockSize, SizeInput } from "./SizeInput";
import { NameInput } from "./NameInput";
import { StartDatePicker } from "./StartDatePicker";
import { EndDatePicker } from "./EndDatePicker";
import { Dayjs } from "dayjs";

export type GeneralInformationProps = {
    houseBlock: HouseBlock;
    setHouseBlock(houseBlock: HouseBlock): void;
    readOnly: boolean;
};

export const GeneralInformationGroup = ({ readOnly, houseBlock, setHouseBlock }: GeneralInformationProps) => {
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
            <StartDatePicker
                readOnly={readOnly}
                houseBlockStartDate={houseBlock.startDate}
                updateHouseBlockStartDate={(e: Dayjs | null) => {
                    setHouseBlock({ ...houseBlock, startDate: e ? e.format("YYYY-MM-DD") : null });
                }}
            />
            <EndDatePicker
                readOnly={readOnly}
                houseBlockEndDate={houseBlock.endDate}
                updateHouseBlockEndDate={(e: Dayjs | null) => setHouseBlock({ ...houseBlock, endDate: e ? e.format("YYYY-MM-DD") : null })}
            />
        </WizardCard>
    );
};
