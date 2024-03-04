import { Checkbox, ListItemText, MenuItem, OutlinedInput, Select, SelectChangeEvent } from "@mui/material";
import { useEffect, useState } from "react";
import { getMunicipalityList } from "../../../api/projectsTableServices";
import { MenuProps } from "../../../utils/menuProps";
import { OptionType } from "../ProjectsTableView";

type Props = {
    selectedMunicipality: string[];
    setSelectedMunicipality: (sm: string[]) => void;
};

export const MunicipalityEditForm = ({ selectedMunicipality, setSelectedMunicipality }: Props) => {
    const [municipalityOptions, setMunicipalityOptions] = useState<OptionType[]>();

    const handleMunicipalityChange = (event: SelectChangeEvent<typeof selectedMunicipality>) => {
        const {
            target: { value },
        } = event;
        setSelectedMunicipality(typeof value === "string" ? [value] : value);
    };

    useEffect(() => {
        getMunicipalityList().then((municipalities) => setMunicipalityOptions(municipalities));
    }, []);

    const checkControl = (inputName: string) => {
        if (selectedMunicipality.length > 0) {
            return selectedMunicipality.indexOf(inputName) !== -1;
        }
    };

    return (
        <Select
            fullWidth
            size="small"
            id="municipality-checkbox"
            multiple
            value={selectedMunicipality}
            onChange={handleMunicipalityChange}
            input={<OutlinedInput />}
            renderValue={(selected) => selected.join(", ")}
            MenuProps={MenuProps}
        >
            {municipalityOptions?.map((municipality) => (
                <MenuItem key={municipality.id} value={municipality.name}>
                    <Checkbox checked={checkControl(municipality.name)} />
                    <ListItemText primary={municipality.name} />
                </MenuItem>
            ))}
        </Select>
    );
};
