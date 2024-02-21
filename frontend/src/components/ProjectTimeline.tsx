import React, { useState } from "react";
import * as d3 from "d3";
import { Box, Button } from "@mui/material";
import RemoveIcon from "@mui/icons-material/Remove";
import AddIcon from "@mui/icons-material/Add";
import ProjectTimelineSvg from "./ProjectTimelineSvg";

const ProjectTimeline = ({ projectData }: any) => {
    const [timeScaleIndex, setTimeScaleIndex] = useState(1);

    const handleScaleIncrease = () => {
        setTimeScaleIndex(timeScaleIndex + 1);
    };

    const handleScaleDecrease = () => {
        setTimeScaleIndex(timeScaleIndex - 1);
    };
    return (
        <>
            <Box sx={{ backgroundColor: "grey", height: "20px" }}>
                <Box sx={{ display: "flex", justifyContent: "end" }}>
                    <Button size="small" variant="contained" onClick={handleScaleDecrease} disabled={timeScaleIndex === 0}>
                        <RemoveIcon />
                    </Button>
                    <Button size="small" variant="contained" onClick={handleScaleIncrease} disabled={timeScaleIndex === 2}>
                        <AddIcon />
                    </Button>
                </Box>
            </Box>
            <Box sx={{ overflow: "scroll" }}>
                <ProjectTimelineSvg projectData={projectData} timeScaleIndex={timeScaleIndex} />
            </Box>
        </>
    );
};

export default ProjectTimeline;
