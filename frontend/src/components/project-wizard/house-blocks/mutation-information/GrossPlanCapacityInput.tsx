import { Stack, Typography, TextField } from "@mui/material";
import { t } from "i18next";
import { InputContainer } from "../InputContainer";
type Props = {
    houseBlockGrossPlan: number | null;
    updateHouseBlockGrossPlan: (houseBlockGrossPlan: number) => void;
    edit: boolean;
    editForm: boolean;
};
type GrosPlanProps = {
    houseBlockGrossPlan: number | null;
    updateHouseBlockGrossPlan: (houseBlockGrossPlan: number) => void;
};
const GrossPlanEditInput = ({ houseBlockGrossPlan, updateHouseBlockGrossPlan }: GrosPlanProps) => {
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
            value={houseBlockGrossPlan !== null ? houseBlockGrossPlan : ""}
            onChange={(e) => updateHouseBlockGrossPlan(+e.target.value)}
        />
    );
};
export const GrossPlanCapacityInput = ({ houseBlockGrossPlan, updateHouseBlockGrossPlan, edit, editForm }: Props) => {
    return (
        <Stack>
            <Typography variant="subtitle1" fontWeight="500">
                {t("createProject.houseBlocksForm.grossPlanCapacity")}
            </Typography>
            {edit && editForm && <GrossPlanEditInput houseBlockGrossPlan={houseBlockGrossPlan} updateHouseBlockGrossPlan={updateHouseBlockGrossPlan} />}
            {!edit && editForm && (
                <InputContainer>
                    <Typography>{houseBlockGrossPlan}</Typography>
                </InputContainer>
            )}
            {!edit && !editForm && <GrossPlanEditInput houseBlockGrossPlan={houseBlockGrossPlan} updateHouseBlockGrossPlan={updateHouseBlockGrossPlan} />}
        </Stack>
    );
};
