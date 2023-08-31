import TextField from "@mui/material/TextField";
import Stack from "@mui/material/Stack";
import Autocomplete from "@mui/material/Autocomplete";
import { useNavigate } from "react-router-dom";
import { useContext } from "react";
import ProjectContext, { ProjectType } from "../context/ProjectContext";

type SearchProps = {
    searchList: Array<ProjectType>;
    label: string;
    isDetailSearch?: boolean;
};

export default function Search({
    searchList,
    label,
    isDetailSearch,
}: SearchProps) {
    const navigate = useNavigate();
    const { selectedProject, setSelectedProject } = useContext(ProjectContext);

    return (
        <Stack spacing={2}>
            <Autocomplete
                size="small"
                options={searchList}
                disableClearable={isDetailSearch}
                getOptionLabel={(option: ProjectType) =>
                    option ? option.name : ""
                }
                value={selectedProject}
                onChange={(event: any, newValue: ProjectType) => {
                    if (newValue !== undefined) {
                        setSelectedProject(newValue);

                        isDetailSearch && navigate(`/projects/${newValue?.id}`);
                    }
                }}
                renderInput={(params) => (
                    <TextField {...params} label={label} />
                )}
            />
        </Stack>
    );
}
