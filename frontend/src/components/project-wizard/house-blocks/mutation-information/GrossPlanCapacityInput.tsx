import { Stack, Typography, TextField } from "@mui/material";
import { t } from "i18next";
import { InputContainer } from "../InputContainer";
import { LabelComponent } from "../../../project/LabelComponent";
import { useEffect, useState } from "react";
type Props = {
    houseBlockGrossPlan: number | null;
    updateHouseBlockGrossPlan: (houseBlockGrossPlan: number) => void;
    readOnly: boolean;
};
type GrosPlanProps = {
    houseBlockGrossPlan: number | null;
    updateHouseBlockGrossPlan: (houseBlockGrossPlan: number) => void;
};
export const GrossPlanEditInput = ({ houseBlockGrossPlan, updateHouseBlockGrossPlan }: GrosPlanProps) => {
    const [stringValue, setStringValue] = useState<string>("");

    useEffect(() => {
        setStringValue(String(houseBlockGrossPlan));
    }, [houseBlockGrossPlan]);

    const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        setStringValue(e.target.value);
        updateHouseBlockGrossPlan(+e.target.value);
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
        <TextField
            InputProps={{
                inputProps: {
                    min: 0,
                },
            }}
            type="number"
            id="grossPlan"
            size="small"
            variant="outlined"
            value={stringValue}
            onChange={handleChange}
            onFocus={onFocus}
            onBlur={onBlur}
        />
    );
};
export const GrossPlanCapacityInput = ({ houseBlockGrossPlan, updateHouseBlockGrossPlan, readOnly }: Props) => {
    return (
        <Stack>
            <LabelComponent required={false} text={t("createProject.houseBlocksForm.grossPlanCapacity")} />

            {!readOnly && <GrossPlanEditInput houseBlockGrossPlan={houseBlockGrossPlan} updateHouseBlockGrossPlan={updateHouseBlockGrossPlan} />}
            {readOnly && (
                <InputContainer>
                    <Typography>{houseBlockGrossPlan}</Typography>
                </InputContainer>
            )}
        </Stack>
    );
};
