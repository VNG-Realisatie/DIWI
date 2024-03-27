import { Stack, Typography, TextField } from "@mui/material";
import { t } from "i18next";
import { InputContainer } from "../InputContainer";
import { LabelComponent } from "../../../project/LabelComponent";
import { useState } from "react";

type Props = {
    houseBlockNetPlan: number | null;
    updateHouseBlockNetPlan: (houseBlockNetPlan: number) => void;
    edit: boolean;
    editForm: boolean;
};
type NetPlanProps = {
    houseBlockNetPlan: number | null;
    updateHouseBlockNetPlan: (houseBlockNetPlan: number) => void;
};

const NetPlanEditInput = ({ houseBlockNetPlan, updateHouseBlockNetPlan }: NetPlanProps) => {
    const [stringValue, setStringValue] = useState<string>(String(houseBlockNetPlan));

    const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        setStringValue(e.target.value);
        updateHouseBlockNetPlan(+e.target.value);
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
export const NetPlanCapacityInput = ({ houseBlockNetPlan, updateHouseBlockNetPlan, edit, editForm }: Props) => {
    return (
        <Stack>
            <LabelComponent required={false} text={t("createProject.houseBlocksForm.netPlanCapacity")} />

            {edit && editForm && <NetPlanEditInput houseBlockNetPlan={houseBlockNetPlan} updateHouseBlockNetPlan={updateHouseBlockNetPlan} />}
            {!edit && editForm && (
                <InputContainer>
                    <Typography>{houseBlockNetPlan}</Typography>
                </InputContainer>
            )}
            {!edit && !editForm && <NetPlanEditInput houseBlockNetPlan={houseBlockNetPlan} updateHouseBlockNetPlan={updateHouseBlockNetPlan} />}
        </Stack>
    );
};
