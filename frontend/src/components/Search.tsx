import TextField from "@mui/material/TextField";
import Stack from "@mui/material/Stack";
import Autocomplete from "@mui/material/Autocomplete";
import { SearchItem } from "../api/dummyData";
import { useNavigate } from "react-router-dom";
import { useContext } from "react";
import ProjectContext from "../context/ProjectContext";

type SearchProps = {
  searchList: Array<SearchItem>;
  label: string;
  isDetailSearch?:boolean;
};
export default function Search({
  searchList,
  label,
  isDetailSearch
}: SearchProps) {
  const navigate= useNavigate();
  const {selectedProject,setSelectedProject}=useContext(ProjectContext)
  return (
    <Stack spacing={2}>
      <Autocomplete
        size="small"
        options={searchList}
        getOptionLabel={(option: SearchItem) => (option ? option.name : "")}
        value={selectedProject}
        onChange={(event: any, newValue: SearchItem) => {
          setSelectedProject(newValue);
          isDetailSearch && navigate(`/projects/${newValue?.id}`)
        }}
        renderInput={(params) => <TextField {...params} label={label} />}
      />
    </Stack>
  );
}
