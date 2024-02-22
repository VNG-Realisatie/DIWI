import { Checkbox, ListItemText, MenuItem, OutlinedInput, Select, SelectChangeEvent } from "@mui/material";
import { planningPlanStatus } from "../../table/constants";
import { useContext } from "react";
import { useTranslation } from "react-i18next";
import ProjectContext from "../../../context/ProjectContext";
import { MenuProps } from "../../../utils/menuProps";

type Props = {
    planStatus: string[];
    setPlanStatus: (status: string[]) => void;
};

export const PlanStatusEditForm = ({ planStatus, setPlanStatus }: Props) => {
    const { t } = useTranslation();
    const { selectedProject } = useContext(ProjectContext);

    const handlePlanStatusChange = (event: SelectChangeEvent<typeof planStatus>) => {
        const {
            target: { value },
        } = event;
        setPlanStatus(typeof value === "string" ? value.split(",") : value);
    };
    return (
        <Select
            fullWidth
            size="small"
            id="plan-status-checkbox"
            multiple
            value={planStatus.length > 0 ? planStatus : selectedProject?.planningPlanStatus}
            onChange={handlePlanStatusChange}
            input={<OutlinedInput />}
            renderValue={(selected) => selected.join(", ")}
            MenuProps={MenuProps}
        >
            {planningPlanStatus.map((pt) => (
                <MenuItem key={pt.id} value={pt.id}>
                    <Checkbox
                        checked={
                            planStatus.length > 0
                                ? planStatus.indexOf(pt.id) > -1
                                : selectedProject?.planningPlanStatus && selectedProject.planningPlanStatus.indexOf(pt.id) > -1
                        }
                    />
                    <ListItemText primary={t(`projectTable.planningPlanStatus.${pt.name}`)} />
                </MenuItem>
            ))}
        </Select>
    );
};
