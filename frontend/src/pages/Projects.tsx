import { Box, Stack, Typography } from "@mui/material";
import mapImg from "../assets/temp/map.png";
import Search from "../components/Search";
import { SearchItem, projects } from "../api/dummyData";
import { ProjectList } from "../components/ProjectList";
import { useState } from "react";

export const Projects = () => {
  const [selectedProject, setSelectedProject] = useState<SearchItem | null>(
    null
  );
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
        <img src={mapImg} alt="maps" />
        
      </Box>
    </Stack>
  );
};
