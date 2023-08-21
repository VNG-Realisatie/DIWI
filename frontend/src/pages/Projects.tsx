import {
  Box,
  FormControl,
  FormControlLabel,
  Stack,
  Switch,
  Typography,
} from "@mui/material";
import mapImg from "../assets/temp/map.png";
import Search from "../components/Search";
import { SearchItem, projects } from "../api/dummyData";
import { ProjectList } from "../components/ProjectList";
import { useState } from "react";
import { ProjectsTableView } from "../components/ProjectsTableView";

export const Projects = () => {
  const [selectedProject, setSelectedProject] = useState<SearchItem | null>(
    null
  );
  const [tableview, setTableView] = useState(false);
  const handleTableSelect = (e: React.ChangeEvent<HTMLInputElement>) => {
    setTableView(e.target.checked);
  };
  return (
    <Stack direction="row" justifyContent="space-between" maxHeight="81vh">
      <Box width="20%" overflow="auto" p={0.3}>
        <Search
          label="Zoeken..."
          searchList={projects}
          setSearchParam={setSelectedProject}
          searchParam={selectedProject}
        />
        <ProjectList
          projectList={selectedProject ? [selectedProject] : projects}
        />
      </Box>
      <Box>
       
        <Stack
          direction="row"
          alignItems="center"
          justifyContent="space-between"
          sx={{ backgroundColor: "#002C64", color: "#FFFFFF" }}
          p={1}
        >
          <Typography> Projecten overzicht: </Typography>
          <Typography> Pijldatum: 12 Jan 2023</Typography>
        </Stack>
       <Stack direction="row" justifyContent="flex-end">
       <FormControl component="fieldset" variant="standard" >
            <FormControlLabel
              control={
                <Switch
                  checked={tableview}
                  onChange={handleTableSelect}
                  name="table"
                />
              }
              label="Tabel weergave "
            />
          </FormControl>
       </Stack>
        {!tableview && <img src={mapImg} alt="maps" />}
        {tableview && <ProjectsTableView />}
      </Box>
    </Stack>
  );
};
