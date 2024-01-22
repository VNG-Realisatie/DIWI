import { Box, Button, Stack, Typography } from "@mui/material";
import Search from "../components/Search";
import { useContext, useState } from "react";
import { Details } from "../components/Details";
import ProjectContext from "../context/ProjectContext";
import AddCircleIcon from "@mui/icons-material/AddCircle";
import { ReactComponent as TimeLineImg } from "../assets/temp/timeline.svg";
import { useNavigate } from "react-router-dom";
import * as Paths from "../Paths";
import { colorArray } from "../api/dummyData";
import { ProjectsWithHouseBlock } from "../components/ProjectWithHouseBlock";
import NetherlandsMap from "../components/map/NetherlandsMap";

export const ProjectDetail = () => {
    const { selectedProject, projects, id } = useContext(ProjectContext);
    const [selectedType, setSelectedType] = useState<"map" | "characteristics" | "timeline">("map");
    const navigate = useNavigate();
    return (
        <Stack direction="column" justifyContent="space-between" position="relative" border="solid 1px #ddd" mb={10}>
            <Stack direction="row" justifyContent="flex-start" alignItems="flex-start">
                <Box width="20%">
                    <Search label="Zoeken..." searchList={projects.map((p) => p.project)} isDetailSearch={true} />
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
                    <Typography> Peildatum: 12 Jan 2023</Typography>
                </Stack>
            </Stack>
            <Stack
                direction="row"
                alignItems="center"
                pl={2}
                sx={{
                    backgroundColor: id && colorArray[parseInt(id) - 1],
                    color: "#FFFFFF",
                    minHeight: "53px",
                }}
            >
                <Typography variant="h5">{selectedProject?.name}</Typography>
            </Stack>
            <Stack direction="row" justifyContent="flex-end" border="solid 1px #ddd" p={0.5}>
                <Button onClick={() => setSelectedType("map")}>Kaart</Button>
                <Button onClick={() => setSelectedType("characteristics")}>Eigenschappen</Button>
                <Button onClick={() => setSelectedType("timeline")}>Tijdlijn</Button>
                <Box sx={{ cursor: "pointer" }} onClick={() => navigate(Paths.projectAdd.path)}>
                    <AddCircleIcon color="info" sx={{ fontSize: "45px" }} />
                </Box>
            </Stack>
            {selectedType === "map" && (
                <Stack direction="row" alignItems="center" justifyContent="space-between">
                    <Stack overflow="auto" height="70vh">
                        {<Details project={selectedProject} />}
                    </Stack>
                    <NetherlandsMap height="640px" width="1000px" />
                </Stack>
            )}
            {selectedType === "timeline" && <TimeLineImg style={{ width: "100%" }} />}
            {selectedType === "characteristics" && (
                <ProjectsWithHouseBlock
                    project={selectedProject}
                    houseblocks={projects.filter((p) => selectedProject && p.project && p.project.id === selectedProject.id)[0].woningblokken}
                />
            )}
        </Stack>
    );
};
