import { useEffect, useState } from "react";
import { getPriorityList } from "../../../api/projectsTableServices";
import { Autocomplete, TextField } from "@mui/material";
import { SelectModel } from "../../../api/projectsServices";

type Props = {
    projectPriority: SelectModel | null | undefined;
    setProjectPriority: (priority: SelectModel | null) => void;
};
export const PriorityEditForm = ({ projectPriority, setProjectPriority }: Props) => {
    const [priorityOptionList, setPriorityOptionList] = useState<SelectModel[]>();

    useEffect(() => {
        getPriorityList().then((priorityList) => setPriorityOptionList(priorityList));
    }, []);

    return (
        <Autocomplete
            id="priority-select"
            size="small"
            options={priorityOptionList ? priorityOptionList : []}
            getOptionLabel={(option) => option.name}
            value={projectPriority}
            filterSelectedOptions
            onChange={(_: any, newValue: SelectModel | null) => setProjectPriority(newValue)}
            renderInput={(params) => <TextField {...params} />}
        />
    );
};
