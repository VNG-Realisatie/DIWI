import { Autocomplete, TextField } from "@mui/material";
import { useEffect, useState } from "react";
import { getNeighbourhoodList } from "../../../api/projectsTableServices";

import { SelectModel } from "../../../api/projectsServices";

type Props = {
    selectedNeighbourhood: SelectModel[];
    setSelectedNeighbourhood: (neighbourhood: SelectModel[]) => void;
};

export const NeighbourhoodEditForm = ({ selectedNeighbourhood, setSelectedNeighbourhood }: Props) => {
    const [neighbourhoodOptions, setNeighbourhoodOptions] = useState<SelectModel[]>();

    useEffect(() => {
        getNeighbourhoodList().then((neighbourhoods) => setNeighbourhoodOptions(neighbourhoods));
    }, []);

    return (
        <Autocomplete
            size="small"
            multiple
            id="tags-outlined"
            options={neighbourhoodOptions ? neighbourhoodOptions : []}
            getOptionLabel={(option) => option.name}
            value={selectedNeighbourhood}
            filterSelectedOptions
            onChange={(_: any, newValue: SelectModel[]) => setSelectedNeighbourhood(newValue)}
            renderInput={(params) => <TextField {...params} />}
        />
    );
};
