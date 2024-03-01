import { Checkbox, ListItemText, MenuItem, OutlinedInput, Select, SelectChangeEvent } from "@mui/material";
import { MenuProps } from "../../../utils/menuProps";
import { useContext, useEffect, useState } from "react";
import ProjectContext from "../../../context/ProjectContext";
import { OptionType } from "../ProjectsTableView";
import { getBuurtList } from "../../../api/projectsTableServices";

type Props = {
    selectedBuurt: string[];
    setSelectedBuurt: (buurt: string[]) => void;
};

export const BuurtEditForm = ({ selectedBuurt, setSelectedBuurt }: Props) => {
    const [buurtOptions, setBuurtOptions] = useState<OptionType[]>();
    const { selectedProject } = useContext(ProjectContext);

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
            value={selectedBuurt.length > 0 ? selectedBuurt : selectedProject?.buurt ? selectedProject?.buurt : []}
            onChange={handleBuurtChange}
            input={<OutlinedInput />}
            renderValue={(selected) => selected.join(", ")}
            MenuProps={MenuProps}
        >
            {buurtOptions?.map((buurt) => (
                <MenuItem key={buurt.id} value={buurt.name}>
                    <Checkbox
                        checked={
                            selectedBuurt.length > 0
                                ? selectedBuurt.indexOf(buurt.name) > -1
                                : selectedProject?.buurt !== undefined && selectedProject?.buurt !== null && selectedProject.buurt.indexOf(buurt.name) > -1
                        }
                    />
                    <ListItemText primary={buurt.name} />
                </MenuItem>
            ))}
        </Select>
    );
};
