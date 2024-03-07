import { useContext } from "react";
import { Stack } from "@mui/material";
import { Details } from "../components/Details";
import NetherlandsMap from "../components/map/NetherlandsMap";
import ProjectContext from "../context/ProjectContext";
import { dummyMapData } from "../pages/ProjectDetail";

const DetailsWithMap = () => {
    const { selectedProject } = useContext(ProjectContext);
    return (
        <Stack direction="row" alignItems="center" justifyContent="space-between">
            <Stack overflow="auto" height="70vh">
                {<Details project={selectedProject} />}
            </Stack>
            <NetherlandsMap height="66vh" width="100%" mapData={dummyMapData} />
        </Stack>
    );
};

export default DetailsWithMap;
