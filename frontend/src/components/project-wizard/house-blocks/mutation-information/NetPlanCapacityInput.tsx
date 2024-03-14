import { Stack, Typography, TextField } from "@mui/material";
import { t } from "i18next";
import { InputContainer } from "../InputContainer";

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
            InputProps={{
                inputProps: {
                    min: 0,
                },
            }}
            type="number"
            id="grossPlan"
            size="small"
            variant="outlined"
            value={houseBlockNetPlan ? houseBlockNetPlan : null}
            onChange={(e) => updateHouseBlockNetPlan(+e.target.value)}
        />
    );
};
export const NetPlanCapacityInput = ({ houseBlockNetPlan, updateHouseBlockNetPlan, edit, editForm }: Props) => {
    return (
        <Stack>
            <Typography variant="subtitle1" fontWeight="500">
                {t("createProject.houseBlocksForm.netPlanCapacity")}
            </Typography>
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
