import { Checkbox, ListItemText, MenuItem, OutlinedInput, Select, SelectChangeEvent } from "@mui/material";
import { getMunicipalityRoleList } from "../../../api/projectsTableServices";
import { useContext, useEffect, useState } from "react";
import { OptionType } from "../ProjectsTableView";
import { MenuProps } from "../../../utils/menuProps";
import ProjectContext from "../../../context/ProjectContext";

type Props = {
    selectedMunicipalityRole: string[];
    setSelectedMunicipalityRole: (mr: string[]) => void;
};

export const MunicipalityRoleEditForm = ({ selectedMunicipalityRole, setSelectedMunicipalityRole }: Props) => {
    const [municipalityRolesOptions, setMunicipalityRolesOptions] = useState<OptionType[]>();

    const { selectedProject } = useContext(ProjectContext);

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
        } else if (selectedProject) {
            if (selectedProject.municipality !== null && selectedProject.municipality !== undefined) {
                return selectedProject.municipality.indexOf(inputName) !== -1;
            }
        }
    };

    return (
        <Select
            fullWidth
            size="small"
            id="municipality-role-checkbox"
            multiple
            value={selectedMunicipalityRole.length > 0 ? selectedMunicipalityRole : selectedProject?.municipalityRole ? selectedProject?.municipalityRole : []}
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
