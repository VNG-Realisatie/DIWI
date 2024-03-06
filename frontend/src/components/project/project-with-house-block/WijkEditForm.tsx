import { Checkbox, ListItemText, MenuItem, OutlinedInput, Select, SelectChangeEvent } from "@mui/material";
import { MenuProps } from "../../../utils/menuProps";
import { useContext, useEffect, useState } from "react";
import ProjectContext from "../../../context/ProjectContext";
import { OptionType } from "../ProjectsTableView";
import { getWijkList } from "../../../api/projectsTableServices";

type Props = {
    selectedWijk: string[];
    setSelectedWijk: (wijk: string[]) => void;
};

export const WijkEditForm = ({ selectedWijk, setSelectedWijk }: Props) => {
    const [wijkOptions, setWijkOptions] = useState<OptionType[]>();
    const { selectedProject } = useContext(ProjectContext);

    const handleWijkChange = (event: SelectChangeEvent<typeof selectedWijk>) => {
        const {
            target: { value },
        } = event;
        setSelectedWijk(typeof value === "string" ? [value] : value);
    };

    useEffect(() => {
        getWijkList().then((wijken) => setWijkOptions(wijken));
    }, []);

    return (
        <Select
            fullWidth
            size="small"
            id="wijk-checkbox"
            multiple
            value={selectedProject?.wijk?.map((w) => w.name) || []}
            onChange={handleWijkChange}
            input={<OutlinedInput />}
            renderValue={(selected) => selected.join(", ")}
            MenuProps={MenuProps}
        >
            {wijkOptions?.map((wijk) => (
                <MenuItem key={wijk.id} value={wijk.name}>
                    <Checkbox checked={selectedWijk.indexOf(wijk.name) !== -1} />
                    <ListItemText primary={wijk.name} />
                </MenuItem>
            ))}
        </Select>
    );
};
