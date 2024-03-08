import { Autocomplete, TextField } from "@mui/material";
import { useEffect, useState } from "react";
import { getMunicipalityList } from "../../../api/projectsTableServices";
import { SelectModel } from "../../../api/projectsServices";

type Props = {
    selectedMunicipality: SelectModel[];
    setSelectedMunicipality: (sm: SelectModel[]) => void;
};

export const MunicipalityEditForm = ({ selectedMunicipality, setSelectedMunicipality }: Props) => {
    const [municipalityOptions, setMunicipalityOptions] = useState<SelectModel[]>();

    useEffect(() => {
        getMunicipalityList().then((municipalities) => setMunicipalityOptions(municipalities));
    }, []);

    return (
        <Autocomplete
            size="small"
            multiple
            id="tags-outlined"
            options={municipalityOptions ? municipalityOptions : []}
            getOptionLabel={(option) => option.name}
            value={selectedMunicipality}
            filterSelectedOptions
            onChange={(_: any, newValue: SelectModel[]) => setSelectedMunicipality(newValue)}
            renderInput={(params) => <TextField {...params} />}
        />
    );
};
