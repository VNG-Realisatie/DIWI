import { Checkbox, ListItemText, MenuItem, OutlinedInput, Select, SelectChangeEvent } from "@mui/material";
import { planningPlanStatus } from "../../table/constants";
import { useContext } from "react";
import { useTranslation } from "react-i18next";
import ProjectContext from "../../../context/ProjectContext";
import { MenuProps } from "../../../utils/menuProps";
import { PlanStatusOptions } from "../../../types/enums";

type Props = {
    planStatus: PlanStatusOptions[];
    setPlanStatus: (status: PlanStatusOptions[]) => void;
};

export const PlanStatusEditForm = ({ planStatus, setPlanStatus }: Props) => {
    const { t } = useTranslation();
    const { selectedProject } = useContext(ProjectContext);

    const handlePlanStatusChange = (event: SelectChangeEvent<typeof planStatus>) => {
        const {
            target: { value },
        } = event;
        if (typeof value !== "string") {
            setPlanStatus(value);
        }
    };

    const checkControl = (inputName: PlanStatusOptions) => {
        if (planStatus.length > 0) {
            return planStatus.indexOf(inputName) !== -1;
        } else if (selectedProject) {
            if (selectedProject.planningPlanStatus !== null && selectedProject.planningPlanStatus !== undefined) {
                return selectedProject.planningPlanStatus.indexOf(inputName) !== -1;
            }
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
            renderValue={(selected) => selected.join(", ")}
            MenuProps={MenuProps}
        >
            {planningPlanStatus.map((pt) => (
                <MenuItem key={pt.id} value={pt.id}>
                    <Checkbox checked={checkControl(pt.id)} />
                    <ListItemText primary={t(`projectTable.planningPlanStatus.${pt.name}`)} />
                </MenuItem>
            ))}
        </Select>
    );
};
