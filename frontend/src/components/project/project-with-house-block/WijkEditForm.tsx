import { Autocomplete, TextField } from "@mui/material";
import { useEffect, useState } from "react";
import { getWijkList } from "../../../api/projectsTableServices";
import { SelectModel } from "../../../api/projectsServices";

type Props = {
    selectedWijk: SelectModel[];
    setSelectedWijk: (wijk: SelectModel[]) => void;
};

export const WijkEditForm = ({ selectedWijk, setSelectedWijk }: Props) => {
    const [wijkOptions, setWijkOptions] = useState<SelectModel[]>();

    useEffect(() => {
        getWijkList().then((wijken) => setWijkOptions(wijken));
    }, []);

    return (
        <Autocomplete
            size="small"
            multiple
            id="wijk-select"
            options={wijkOptions ? wijkOptions : []}
            getOptionLabel={(option) => option.name}
            value={selectedWijk}
            filterSelectedOptions
            onChange={(_: any, newValue: SelectModel[]) => setSelectedWijk(newValue)}
            renderInput={(params) => <TextField {...params} />}
        />
    );
};
