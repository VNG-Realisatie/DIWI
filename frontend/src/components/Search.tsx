import TextField from "@mui/material/TextField";
import Stack from "@mui/material/Stack";
import Autocomplete from "@mui/material/Autocomplete";
import { useNavigate } from "react-router-dom";
import { useContext } from "react";
import ProjectContext from "../context/ProjectContext";
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
    const navigate = useNavigate();
    const { selectedProject, setSelectedProject } = useContext(ProjectContext);

    return (
        <Stack spacing={2}>
            <Autocomplete
                size="small"
                options={searchList}
                disableClearable={isDetailSearch}
                getOptionLabel={(option) =>
                    option ? option.projectName : ""
                }
                value={selectedProject}
                onChange={(_, newValue) => {
                    if (newValue !== null) {
                        setSelectedProject(newValue);

                        isDetailSearch && navigate(`/projects/${newValue?.projectId}`);
                    }
                }}
                renderInput={(params) => (
                    <TextField {...params} label={label} />
                )}
            />
        </Stack>
    );
}
