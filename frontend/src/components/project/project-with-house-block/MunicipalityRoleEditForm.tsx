import { Autocomplete, TextField } from "@mui/material";
import { useEffect, useState } from "react";
import { getMunicipalityRoleList } from "../../../api/projectsTableServices";
import { SelectModel } from "../../../api/projectsServices";

type Props = {
    selectedMunicipalityRole: SelectModel[];
    setSelectedMunicipalityRole: (mr: SelectModel[]) => void;
};

export const MunicipalityRoleEditForm = ({ selectedMunicipalityRole, setSelectedMunicipalityRole }: Props) => {
    const [municipalityRolesOptions, setMunicipalityRolesOptions] = useState<SelectModel[]>();

    useEffect(() => {
        getMunicipalityRoleList().then((roles) => setMunicipalityRolesOptions(roles));
    }, []);

    return (
        <Autocomplete
            size="small"
            multiple
            id="tags-outlined"
            options={municipalityRolesOptions ? municipalityRolesOptions : []}
            getOptionLabel={(option) => option.name}
            value={selectedMunicipalityRole}
            filterSelectedOptions
            onChange={(_: any, newValue: SelectModel[]) => setSelectedMunicipalityRole(newValue)}
            renderInput={(params) => <TextField {...params} />}
        />
    );
};
