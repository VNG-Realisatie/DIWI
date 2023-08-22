import { Box, Stack, Typography } from "@mui/material";
import Search from "../components/Search";
import { projects } from "../api/dummyData";
import { useContext } from "react";
import { Details } from "../components/Details";
import mapImg from "../assets/temp/map.png";
import ProjectContext from "../context/ProjectContext";

export const ProjectDetail = () => {
  const { selectedProject } = useContext(ProjectContext);
  return (
    <Stack direction="column" justifyContent="space-between" maxHeight="81vh">
      <Stack
        direction="row"
        justifyContent="flex-start"
        alignItems="flex-start"
      >
        <Box width="20%">
          <Search
            label="Zoeken..."
            searchList={projects}
            isDetailSearch={true}
          />
        </Box>
        <Stack
          width="80%"
          direction="row"
          alignItems="center"
          justifyContent="space-between"
          sx={{ backgroundColor: "#002C64", color: "#FFFFFF" }}
          p={1}
        >
          <Typography> Projecten overzicht: </Typography>
          <Typography> Pijldatum: 12 Jan 2023</Typography>
        </Stack>
      </Stack>
      <Stack
        direction="row"
        alignItems="center"
        pl={2}
        sx={{ backgroundColor: "#900A0A", color: "#FFFFFF", minHeight: "53px" }}
      >
        <Typography variant="h5">{selectedProject?.name}</Typography>
      </Stack>
      <Stack direction="row" justifyContent="flex-end">
        Kaart | Eigenschappen | Tijdlijn
      </Stack>
      <Stack
        direction="row"
        alignItems="flex-start"
        justifyContent="space-between"
      >
        <Stack width="20%" mr={0.5}>
          <Details project={selectedProject} />
        </Stack>
        <Stack width="80%">
          {" "}
          {<img src={mapImg} alt="maps" style={{ paddingTop: "8px" }} />}
        </Stack>
      </Stack>
    </Stack>
  );
};
