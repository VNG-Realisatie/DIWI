import { useState } from "react";
import { Box, Button, Typography } from "@mui/material";
import RemoveIcon from "@mui/icons-material/Remove";
import AddIcon from "@mui/icons-material/Add";
import { ProjectTimelineSvg } from "../components/project/timeline/ProjectTimelineSvg";
import { useTranslation } from "react-i18next";

export const ProjectTimeline = () => {
    const [timeScaleIndex, setTimeScaleIndex] = useState(3);
    const [showToday, setShowToday] = useState(true);
    const { t } = useTranslation();

    const handleScaleChange = (increment: number) => {
        setTimeScaleIndex((prevIndex) => prevIndex + increment);
    };

    const handleClickShowToday = () => {
        setShowToday(!showToday);
    };

    return (
        <>
            <Box sx={{ backgroundColor: "grey", p: 1 }}>
                <Box sx={{ display: "flex", justifyContent: "end" }}>
                    <Button size="small" sx={{ mr: 1 }} variant="contained" onClick={() => handleClickShowToday()}>
                        <Typography alignSelf="center" marginX={1}>
                            {showToday ? t("timeline.hideToday") : t("timeline.showToday")}
                        </Typography>
                    </Button>
                    <Button size="small" variant="contained" onClick={() => handleScaleChange(-1)} disabled={timeScaleIndex <= 1}>
                        <RemoveIcon />
                    </Button>
                    <Button size="small" variant="contained" onClick={() => handleScaleChange(1)} disabled={timeScaleIndex >= 6}>
                        <AddIcon />
                    </Button>
                </Box>
            </Box>
            <ProjectTimelineSvg timeScaleIndex={timeScaleIndex} setTimeScaleIndex={setTimeScaleIndex} showToday={showToday} width={"100%"} height={500} />
        </>
    );
};
