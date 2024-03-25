import { Checkbox, ListItemText, MenuItem, OutlinedInput, Select, SelectChangeEvent } from "@mui/material";
import { planningPlanStatus } from "../../table/constants";
import { useTranslation } from "react-i18next";
import { MenuProps } from "../../../utils/menuProps";
import { PlanStatusOptions } from "../../../types/enums";

type Props = {
    planStatus: PlanStatusOptions[];
    setPlanStatus: (status: PlanStatusOptions[]) => void;
};

export const PlanStatusEditForm = ({ planStatus, setPlanStatus }: Props) => {
    const { t } = useTranslation();

    const handlePlanStatusChange = (event: SelectChangeEvent<typeof planStatus>) => {
        const {
            target: { value },
        } = event;
        if (typeof value !== "string") {
            setPlanStatus(value);
        }
    };

    return (
        <Select
            fullWidth
            size="small"
            id="plan-status-checkbox"
            multiple
            value={planStatus}
            onChange={handlePlanStatusChange}
            input={<OutlinedInput />}
            renderValue={(selected) => selected.map((s) => t(`projectTable.planningPlanStatus.${s}`)).join(", ")}
            MenuProps={MenuProps}
        >
            {planningPlanStatus.map((pt) => (
                <MenuItem key={pt.id} value={pt.id}>
                    <Checkbox checked={planStatus.indexOf(pt.id) !== -1} />
                    <ListItemText primary={t(`projectTable.planningPlanStatus.${pt.name}`)} />
                </MenuItem>
            ))}
        </Select>
    );
};
