import { useState } from "react";
import { Box, Button } from "@mui/material";
import RemoveIcon from "@mui/icons-material/Remove";
import AddIcon from "@mui/icons-material/Add";
import { ProjectTimelineSvg } from "./ProjectTimelineSvg";

export const ProjectTimeline = () => {
    // timescale: 0 = years, 1 = months, 2 = weeks
    const [timeScaleIndex, setTimeScaleIndex] = useState(1);

    const handleScaleChange = (increment: number) => {
        setTimeScaleIndex((prevIndex) => prevIndex + increment);
    };

    return (
        <>
            <Box sx={{ backgroundColor: "grey", height: "20px" }}>
                <Box sx={{ display: "flex", justifyContent: "end" }}>
                    <Button size="small" variant="contained" onClick={() => handleScaleChange(-1)} disabled={timeScaleIndex === 0}>
                        <RemoveIcon />
                    </Button>
                    <Button size="small" variant="contained" onClick={() => handleScaleChange(1)} disabled={timeScaleIndex === 2}>
                        <AddIcon />
                    </Button>
                </Box>
            </Box>
            <ProjectTimelineSvg timeScaleIndex={timeScaleIndex} width={"100%"} height={500} />
        </>
    );
};
