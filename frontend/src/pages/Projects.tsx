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
import { projects } from "../api/dummyData";
import { ProjectList } from "../components/ProjectList";
import { useContext, useState } from "react";
import { ProjectsTableView } from "../components/ProjectsTableView";
import ProjectContext from "../context/ProjectContext";
import AddCircleIcon from "@mui/icons-material/AddCircle";
import { useNavigate } from "react-router-dom";
import * as Paths from "../Paths";

export const Projects = () => {
  const { selectedProject } = useContext(ProjectContext);
  const [tableview, setTableView] = useState(false);
  const handleTableSelect = (e: React.ChangeEvent<HTMLInputElement>) => {
    setTableView(e.target.checked);
  };
  const navigate= useNavigate();
  return (
    <Stack
      direction="row"
      justifyContent="space-between"
      maxHeight="81vh"
      position="relative"
    >
      <Box width="20%" overflow="auto" p={0.3}>
        <Search label="Zoeken..." searchList={projects} />
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
          <FormControl component="fieldset" variant="standard">
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
      <Box
        position="absolute"
        right="30px"
        bottom="44px"
        sx={{ cursor: "pointer" }}
        onClick={() => navigate(Paths.projectAdd.path)}
      >
        <AddCircleIcon color="info" sx={{ fontSize: "58px" }} />
      </Box>
    </Stack>
  );
};
