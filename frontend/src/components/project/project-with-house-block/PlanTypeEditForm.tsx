import { Checkbox, ListItemText, MenuItem, OutlinedInput, Select, SelectChangeEvent } from "@mui/material";
import { useTranslation } from "react-i18next";
import { PlanTypeOptions } from "../../../types/enums";
import { MenuProps } from "../../../utils/menuProps";
import { planTypeOptions } from "../../table/constants";

type Props = {
    planType: PlanTypeOptions[];
    setPlanType: (planType: PlanTypeOptions[]) => void;
};
export const PlanTypeEditForm = ({ planType, setPlanType }: Props) => {
    const { t } = useTranslation();

    const handlePlanTypeChange = (event: SelectChangeEvent<typeof planType>) => {
        const {
            target: { value },
        } = event;
        if (typeof value !== "string") {
            setPlanType(value);
        }
    };

    const checkControl = (inputName: PlanTypeOptions) => {
        if (planType.length > 0) {
            return planType.indexOf(inputName) !== -1;
        }
    };

    return (
        <Select
            fullWidth
            size="small"
            id="plan-type-checkbox"
            multiple
            value={planType}
            onChange={handlePlanTypeChange}
            input={<OutlinedInput />}
            renderValue={(selected) => selected.join(", ")}
            MenuProps={MenuProps}
        >
            {planTypeOptions.map((pt) => (
                <MenuItem key={pt.id} value={pt.id}>
                    <Checkbox checked={checkControl(pt.id)} />
                    <ListItemText primary={t(`projectTable.planTypeOptions.${pt.name}`)} />
                </MenuItem>
            ))}
        </Select>
    );
};
