import { useEffect, useState } from "react";
import { getPriorityList } from "../../../api/projectsTableServices";
import { Autocomplete, TextField } from "@mui/material";
import { PriorityModel, SelectModel } from "../../../api/projectsServices";

type Props = {
    projectPriority: PriorityModel | null | undefined;
    setProjectPriority: (priority: PriorityModel | null) => void;
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
            value={projectPriority?.value}
            filterSelectedOptions
            onChange={(_: any, newValue: SelectModel | null) => setProjectPriority({ value: newValue || undefined })}
            renderInput={(params) => <TextField {...params} />}
        />
    );
};
