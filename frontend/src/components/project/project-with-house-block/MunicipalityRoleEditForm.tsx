import { Checkbox, ListItemText, MenuItem, OutlinedInput, Select, SelectChangeEvent } from "@mui/material";
import { useEffect, useState } from "react";
import { getMunicipalityRoleList } from "../../../api/projectsTableServices";
import { MenuProps } from "../../../utils/menuProps";
import { OptionType } from "../ProjectsTableView";

type Props = {
    selectedMunicipalityRole: string[];
    setSelectedMunicipalityRole: (mr: string[]) => void;
};

export const MunicipalityRoleEditForm = ({ selectedMunicipalityRole, setSelectedMunicipalityRole }: Props) => {
    const [municipalityRolesOptions, setMunicipalityRolesOptions] = useState<OptionType[]>();

    const handleMunicipalityRoleChange = (event: SelectChangeEvent<typeof selectedMunicipalityRole>) => {
        const {
            target: { value },
        } = event;
        setSelectedMunicipalityRole(typeof value === "string" ? [value] : value);
    };

    useEffect(() => {
        getMunicipalityRoleList().then((roles) => setMunicipalityRolesOptions(roles));
    }, []);

    const checkControl = (inputName: string) => {
        if (selectedMunicipalityRole.length > 0) {
            return selectedMunicipalityRole.indexOf(inputName) !== -1;
        }
    };

    return (
        <Select
            fullWidth
            size="small"
            id="municipality-role-checkbox"
            multiple
            value={selectedMunicipalityRole}
            onChange={handleMunicipalityRoleChange}
            input={<OutlinedInput />}
            renderValue={(selected) => selected.join(", ")}
            MenuProps={MenuProps}
        >
            {municipalityRolesOptions?.map((municipality) => (
                <MenuItem key={municipality.id} value={municipality.name}>
                    <Checkbox checked={checkControl(municipality.name)} />
                    <ListItemText primary={municipality.name} />
                </MenuItem>
            ))}
        </Select>
    );
};
