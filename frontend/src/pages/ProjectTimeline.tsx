import { useState } from "react";
import { Box, Button, Typography } from "@mui/material";
import RemoveIcon from "@mui/icons-material/Remove";
import AddIcon from "@mui/icons-material/Add";
import { ProjectTimelineSvg } from "../components/project/timeline/ProjectTimelineSvg";

export const ProjectTimeline = () => {
    const [timeScaleIndex, setTimeScaleIndex] = useState(3);
    const [showToday, setShowToday] = useState(true);

    const handleScaleChange = (increment: number) => {
        setTimeScaleIndex((prevIndex) => prevIndex + increment);
    };

    const handleClickShowToday = () => {
        setShowToday(!showToday);
    };

    return (
        <>
            <Box sx={{ backgroundColor: "grey" }}>
                <Box sx={{ display: "flex", justifyContent: "end" }}>
                    <Button size="small" variant="contained" onClick={() => handleClickShowToday()}>
                        <Typography alignSelf="center" marginX={1}>
                            {/* TODO use translation here? */}
                            {showToday ? "Hide Today" : "Show Today"}
                        </Typography>
                    </Button>
                    <Button size="small" variant="contained" onClick={() => handleScaleChange(-1)} disabled={timeScaleIndex <= 1}>
                        <RemoveIcon />
                    </Button>
                    <Button size="small" variant="contained" onClick={() => handleScaleChange(1)} disabled={timeScaleIndex >= 5}>
                        <AddIcon />
                    </Button>
                    <Typography alignSelf="center" marginX={1}>{`Level: ${timeScaleIndex}`}</Typography>
                </Box>
            </Box>
            <ProjectTimelineSvg timeScaleIndex={timeScaleIndex} showToday={showToday} width={"100%"} height={500} />
        </>
    );
};
