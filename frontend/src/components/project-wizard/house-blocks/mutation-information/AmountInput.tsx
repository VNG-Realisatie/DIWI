import { Stack, Typography, TextField } from "@mui/material";
import { t } from "i18next";
import { InputContainer } from "../InputContainer";
import { LabelComponent } from "../../../project/LabelComponent";
import { useEffect, useState } from "react";

type Props = {
    houseBlockAmount: number | undefined;
    updateHouseBlockAmount: (houseBlockNetPlan: number) => void;
    readOnly: boolean;
};
type AmountProps = {
    houseBlockAmount: number | undefined;
    updateHouseBlockAmount: (houseBlockNetPlan: number) => void;
};

export const AmountEditInput = ({ houseBlockAmount: houseBlockNetPlan, updateHouseBlockAmount }: AmountProps) => {
    const [stringValue, setStringValue] = useState<string>("");

    useEffect(() => {
        setStringValue(String(houseBlockNetPlan));
    }, [houseBlockNetPlan]);

    const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        setStringValue(e.target.value);
        updateHouseBlockAmount(+e.target.value);
    };

    const onFocus = () => {
        if (stringValue === "0") {
            setStringValue("");
        }
    };

    const onBlur = () => {
        if (stringValue === "") {
            setStringValue("0");
        }
    };

    return (
        <TextField type="number" id="grossPlan" size="small" variant="outlined" value={stringValue} onChange={handleChange} onFocus={onFocus} onBlur={onBlur} />
    );
};
export const AmountInput = ({ houseBlockAmount, updateHouseBlockAmount, readOnly }: Props) => {
    return (
        <Stack>
            <LabelComponent required={false} text={t("createProject.houseBlocksForm.amount")} />

            {!readOnly && <AmountEditInput houseBlockAmount={houseBlockAmount} updateHouseBlockAmount={updateHouseBlockAmount} />}
            {readOnly && (
                <InputContainer>
                    <Typography>{houseBlockAmount}</Typography>
                </InputContainer>
            )}
        </Stack>
    );
};
