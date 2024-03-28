import { Stack, Typography } from "@mui/material";
import { t } from "i18next";
import { InputContainer } from "../InputContainer";
import { LabelComponent } from "../../../project/LabelComponent";
import { NumericRangeInput } from "../NumericRangeInput";
export type HouseBlockSize = {
    value: number | null;
    min: number | null;
    max: number | null;
};
type Props = {
    houseBlockSize: HouseBlockSize;
    updateHouseBlockSize: (houseBlockSize: HouseBlockSize) => void;
    readOnly: boolean;
};

export const SizeInput = ({ houseBlockSize, updateHouseBlockSize, readOnly }: Props) => {
    return (
        <Stack width="100%">
            <LabelComponent required={false} text={t("createProject.houseBlocksForm.size")} />
            {!readOnly && <NumericRangeInput value={houseBlockSize} updateCallBack={updateHouseBlockSize} />}
            {readOnly && (
                <InputContainer>
                    <Typography minHeight="20px">{houseBlockSize?.value}</Typography>
                </InputContainer>
            )}
        </Stack>
    );
};
