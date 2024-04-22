import { Autocomplete, TextField } from "@mui/material";
import { PriorityModel, SelectModel } from "../../../api/projectsServices";

type Props = {
    projectPriority: PriorityModel | null | undefined;
    setProjectPriority: (priority: PriorityModel | null) => void;
    options: SelectModel[];
};
export const PriorityEditForm = ({ projectPriority, setProjectPriority, options }: Props) => {
    return (
        <Autocomplete
            id="priority-select"
            size="small"
            options={options}
            getOptionLabel={(option) => option.name}
            value={projectPriority?.value}
            filterSelectedOptions
            onChange={(_: any, newValue: SelectModel | null) => setProjectPriority({ value: newValue || undefined })}
            renderInput={(params) => <TextField {...params} />}
        />
    );
};
