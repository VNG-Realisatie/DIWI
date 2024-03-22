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
    edit: boolean;
    editForm: boolean;
};

export const SizeInput = ({ houseBlockSize, updateHouseBlockSize, edit, editForm }: Props) => {
    return (
        <Stack width="100%">
            <LabelComponent required={false} text={t("createProject.houseBlocksForm.size")} />
            {edit && editForm && <NumericRangeInput value={houseBlockSize} updateCallBack={updateHouseBlockSize} />}
            {!edit && editForm && (
                <InputContainer>
                    <Typography>{houseBlockSize?.value !== null ? houseBlockSize?.value : houseBlockSize?.min + "-" + houseBlockSize?.max}</Typography>
                </InputContainer>
            )}
            {!edit && !editForm && <NumericRangeInput value={houseBlockSize} updateCallBack={updateHouseBlockSize} />}
        </Stack>
    );
};
