import { Stack, Typography, TextField } from "@mui/material";
import { t } from "i18next";
import { InputContainer } from "../InputContainer";
import { LabelComponent } from "../../../project/LabelComponent";
type Props = {
    houseBlockDemolitionPlan: number | null;
    updateHouseBlockDemolitionPlan: (demolitionPlan: number) => void;
    edit: boolean;
    editForm: boolean;
};
type DemolitionPlanProps = {
    houseBlockDemolitionPlan: number | null;
    updateHouseBlockDemolitionPlan: (demolitionPlan: number) => void;
};
const DemolitionPlanEditInput = ({ houseBlockDemolitionPlan, updateHouseBlockDemolitionPlan }: DemolitionPlanProps) => {
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
            value={houseBlockDemolitionPlan !== null ? houseBlockDemolitionPlan : ""}
            onChange={(e) => updateHouseBlockDemolitionPlan(+e.target.value)}
        />
    );
};
export const DemolitionPlanCapacityInput = ({ houseBlockDemolitionPlan, updateHouseBlockDemolitionPlan, edit, editForm }: Props) => {
    return (
        <Stack>
            <LabelComponent required={false} text={t("createProject.houseBlocksForm.demolition")} />

            {edit && editForm && (
                <DemolitionPlanEditInput houseBlockDemolitionPlan={houseBlockDemolitionPlan} updateHouseBlockDemolitionPlan={updateHouseBlockDemolitionPlan} />
            )}
            {!edit && editForm && (
                <InputContainer>
                    <Typography>{houseBlockDemolitionPlan}</Typography>
                </InputContainer>
            )}
            {!edit && !editForm && (
                <DemolitionPlanEditInput houseBlockDemolitionPlan={houseBlockDemolitionPlan} updateHouseBlockDemolitionPlan={updateHouseBlockDemolitionPlan} />
            )}
        </Stack>
    );
};
