import { Stack, Typography, TextField } from "@mui/material";
import { t } from "i18next";
import { InputContainer } from "../InputContainer";
import { LabelComponent } from "../../../project/LabelComponent";

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
    return (
        <TextField
            type="number"
            id="grossPlan"
            size="small"
            variant="outlined"
            value={houseBlockNetPlan !== null ? houseBlockNetPlan : ""}
            onChange={(e) => updateHouseBlockNetPlan(+e.target.value)}
        />
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
