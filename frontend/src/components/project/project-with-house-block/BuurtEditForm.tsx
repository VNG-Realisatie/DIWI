import { Checkbox, ListItemText, MenuItem, OutlinedInput, Select, SelectChangeEvent } from "@mui/material";
import { useEffect, useState } from "react";
import { getBuurtList } from "../../../api/projectsTableServices";
import { MenuProps } from "../../../utils/menuProps";
import { OptionType } from "../ProjectsTableView";

type Props = {
    selectedBuurt: string[];
    setSelectedBuurt: (buurt: string[]) => void;
};

export const BuurtEditForm = ({ selectedBuurt, setSelectedBuurt }: Props) => {
    const [buurtOptions, setBuurtOptions] = useState<OptionType[]>();

    const handleBuurtChange = (event: SelectChangeEvent<typeof selectedBuurt>) => {
        const {
            target: { value },
        } = event;
        setSelectedBuurt(typeof value === "string" ? [value] : value);
    };

    useEffect(() => {
        getBuurtList().then((buurten) => setBuurtOptions(buurten));
    }, []);

    return (
        <Select
            fullWidth
            size="small"
            id="buurt-checkbox"
            multiple
            value={selectedBuurt}
            onChange={handleBuurtChange}
            input={<OutlinedInput />}
            renderValue={(selected) => selected.join(", ")}
            MenuProps={MenuProps}
        >
            {buurtOptions?.map((buurt) => (
                <MenuItem key={buurt.id} value={buurt.name}>
                    <Checkbox checked={selectedBuurt.indexOf(buurt.name) > -1} />
                    <ListItemText primary={buurt.name} />
                </MenuItem>
            ))}
        </Select>
    );
};
