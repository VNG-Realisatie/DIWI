import { Checkbox, ListItemText, MenuItem, OutlinedInput, Select, SelectChangeEvent } from "@mui/material";
import { MenuProps } from "../../../utils/menuProps";
import { useContext, useEffect, useState } from "react";
import ProjectContext from "../../../context/ProjectContext";
import { getMunicipalityList } from "../../../api/projectsTableServices";
import { OptionType } from "../ProjectsTableView";

type Props = {
    selectedMunicipality: string[];
    setSelectedMunicipality: (sm: string[]) => void;
};

export const MunicipalityEditForm = ({ selectedMunicipality, setSelectedMunicipality }: Props) => {
    const [municipalityOptions, setMunicipalityOptions] = useState<OptionType[]>();
    const { selectedProject } = useContext(ProjectContext);

    const handleMunicipalityChange = (event: SelectChangeEvent<typeof selectedMunicipality>) => {
        const {
            target: { value },
        } = event;
        setSelectedMunicipality(typeof value === "string" ? value.split(",") : value);
    };

    useEffect(() => {
        getMunicipalityList().then((municipalities) => setMunicipalityOptions(municipalities));
    }, []);

    return (
        <Select
            fullWidth
            size="small"
            id="municipality-checkbox"
            multiple
            value={selectedMunicipality.length > 0 ? selectedMunicipality : selectedProject?.municipality ? selectedProject?.municipality : []}
            onChange={handleMunicipalityChange}
            input={<OutlinedInput />}
            renderValue={(selected) => selected.join(", ")}
            MenuProps={MenuProps}
        >
            {municipalityOptions?.map((municipality) => (
                <MenuItem key={municipality.id} value={municipality.name}>
                    <Checkbox
                        checked={
                            selectedMunicipality.length > 0
                                ? selectedMunicipality.indexOf(municipality.name) > -1
                                : selectedProject?.municipality && selectedProject.municipality.indexOf(municipality.name) > -1
                        }
                    />
                    <ListItemText primary={municipality.name} />
                </MenuItem>
            ))}
        </Select>
    );
};
