import TextField from "@mui/material/TextField";
import Stack from "@mui/material/Stack";
import Autocomplete from "@mui/material/Autocomplete";
import { Project } from "../api/projectsServices";

type SearchProps = {
    searchList: Array<Project>;
    label: string;
    isDetailSearch?: boolean;
};

export default function Search({
    searchList,
    label,
    isDetailSearch,
}: SearchProps) {
    return (
        <Stack spacing={2}>
            <Autocomplete
                size="small"
                options={searchList}
                disableClearable={isDetailSearch}
                renderInput={(params) => (
                    <TextField {...params} label={label} />
                )}
            />
        </Stack>
    );
}
