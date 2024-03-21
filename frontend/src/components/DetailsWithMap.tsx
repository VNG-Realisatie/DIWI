import { Stack } from "@mui/material";
import { useContext } from "react";
import { Details } from "../components/Details";
import ProjectContext from "../context/ProjectContext";
import ProjectPlotSelector from "./map/ProjectPlotSelector";

const DetailsWithMap = () => {
    const { selectedProject } = useContext(ProjectContext);
    return (
        <Stack direction="row" alignItems="center" justifyContent="space-between">
            <Stack overflow="auto" height="70vh">
                <Details project={selectedProject} />
            </Stack>
            <ProjectPlotSelector height="70vh" width="100%" />
        </Stack>
    );
};

export default DetailsWithMap;
