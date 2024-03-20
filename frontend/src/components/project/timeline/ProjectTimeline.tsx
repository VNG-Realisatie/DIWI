import { useState } from "react";
import { Box, Button, Typography } from "@mui/material";
import RemoveIcon from "@mui/icons-material/Remove";
import AddIcon from "@mui/icons-material/Add";
import { ProjectTimelineSvg } from "./ProjectTimelineSvg";

export const ProjectTimeline = () => {
    const [timeScaleIndex, setTimeScaleIndex] = useState(1);

    const handleScaleChange = (increment: number) => {
        setTimeScaleIndex((prevIndex) => prevIndex + increment);
    };

    return (
        <>
            <Box sx={{ backgroundColor: "grey" }}>
                <Box sx={{ display: "flex", justifyContent: "end" }}>
                    <Button size="small" variant="contained" onClick={() => handleScaleChange(-1)} disabled={timeScaleIndex <= 1}>
                        <RemoveIcon />
                    </Button>
                    <Button size="small" variant="contained" onClick={() => handleScaleChange(1)} disabled={timeScaleIndex >= 5}>
                        <AddIcon />
                    </Button>
                    <Typography alignSelf="center" marginX={1}>{`Level: ${timeScaleIndex}`}</Typography>
                </Box>
            </Box>
            <ProjectTimelineSvg timeScaleIndex={timeScaleIndex} width={"100%"} height={500} />
        </>
    );
};
