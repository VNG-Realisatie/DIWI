import { Checkbox, ListItemText, MenuItem, OutlinedInput, Select, SelectChangeEvent } from "@mui/material";
import { planTypeOptions } from "../../table/constants";
import { MenuProps } from "../../../utils/menuProps";
import { useContext } from "react";
import ProjectContext from "../../../context/ProjectContext";
import { useTranslation } from "react-i18next";

type Props = {
    planType: string[];
    setPlanType: (planType: string[]) => void;
};
export const PlanTypeEditForm = ({ planType, setPlanType }: Props) => {
    const { t } = useTranslation();
    const { selectedProject } = useContext(ProjectContext);

    const handlePlanTypeChange = (event: SelectChangeEvent<typeof planType>) => {
        const {
            target: { value },
        } = event;
        setPlanType(typeof value === "string" ? value.split(",") : value);
    };
    return (
        <Select
            fullWidth
            size="small"
            id="plan-type-checkbox"
            multiple
            value={planType.length > 0 ? planType : selectedProject?.planType}
            onChange={handlePlanTypeChange}
            input={<OutlinedInput />}
            renderValue={(selected) => selected.join(", ")}
            MenuProps={MenuProps}
        >
            {planTypeOptions.map((pt) => (
                <MenuItem key={pt.id} value={pt.id}>
                    <Checkbox
                        checked={
                            planType.length > 0 ? planType.indexOf(pt.id) > -1 : selectedProject?.planType && selectedProject.planType.indexOf(pt.name) !== -1
                        }
                    />
                    <ListItemText primary={t(`projectTable.planTypeOptions.${pt.name}`)} />
                </MenuItem>
            ))}
        </Select>
    );
};
