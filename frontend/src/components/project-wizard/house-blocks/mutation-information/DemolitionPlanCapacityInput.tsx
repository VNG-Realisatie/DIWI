import { Stack, Typography, TextField } from "@mui/material";
import { t } from "i18next";
import { InputContainer } from "../InputContainer";
import { LabelComponent } from "../../../project/LabelComponent";
import { useState } from "react";
type Props = {
    houseBlockDemolitionPlan: number | null;
    updateHouseBlockDemolitionPlan: (demolitionPlan: number) => void;
    readOnly: boolean;
};
type DemolitionPlanProps = {
    houseBlockDemolitionPlan: number | null;
    updateHouseBlockDemolitionPlan: (demolitionPlan: number) => void;
};
const DemolitionPlanEditInput = ({ houseBlockDemolitionPlan, updateHouseBlockDemolitionPlan }: DemolitionPlanProps) => {
    const [stringValue, setStringValue] = useState<string>(String(houseBlockDemolitionPlan));

    const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        setStringValue(e.target.value);
        updateHouseBlockDemolitionPlan(+e.target.value);
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
            id="demolitionPlan"
            size="small"
            variant="outlined"
            value={stringValue}
            onChange={handleChange}
            onFocus={onFocus}
            onBlur={onBlur}
        />
    );
};
export const DemolitionPlanCapacityInput = ({ houseBlockDemolitionPlan, updateHouseBlockDemolitionPlan, readOnly }: Props) => {
    return (
        <Stack>
            <LabelComponent required={false} text={t("createProject.houseBlocksForm.demolition")} />

            {!readOnly && (
                <DemolitionPlanEditInput houseBlockDemolitionPlan={houseBlockDemolitionPlan} updateHouseBlockDemolitionPlan={updateHouseBlockDemolitionPlan} />
            )}
            {readOnly && (
                <InputContainer>
                    <Typography>{houseBlockDemolitionPlan}</Typography>
                </InputContainer>
            )}
        </Stack>
    );
};
