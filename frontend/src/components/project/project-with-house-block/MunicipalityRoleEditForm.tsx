import { Autocomplete, TextField } from "@mui/material";
import { SelectModel } from "../../../api/projectsServices";

type Props = {
    selectedMunicipalityRole: SelectModel[];
    setSelectedMunicipalityRole: (mr: SelectModel[]) => void;
    options: SelectModel[];
};

export const MunicipalityRoleEditForm = ({ selectedMunicipalityRole, setSelectedMunicipalityRole, options }: Props) => {
    return (
        <Autocomplete
            size="small"
            multiple
            id="tags-outlined"
            options={options}
            getOptionLabel={(option) => option.name}
            value={selectedMunicipalityRole}
            filterSelectedOptions
            onChange={(_: any, newValue: SelectModel[]) => setSelectedMunicipalityRole(newValue)}
            renderInput={(params) => <TextField {...params} />}
        />
    );
};
