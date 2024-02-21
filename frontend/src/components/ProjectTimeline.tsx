import React, { useState, useEffect } from "react";
import * as d3 from "d3";
import { Box, Button } from "@mui/material";
import RemoveIcon from "@mui/icons-material/Remove";
import AddIcon from "@mui/icons-material/Add";
import ProjectTimelineSvg from "./ProjectTimelineSvg";

const testStartDate = new Date("2024-05-01");
const testEndDate = new Date("2024-10-01");

const ProjectTimeline = ({ projectData }: any) => {
    const [timeScaleIndex, setTimeScaleIndex] = useState(1);
    const [dateRange, setDateRange] = useState<Date[]>([]);
    const [timeFormat, setTimeFormat] = useState("");

    const handleScaleIncrease = () => {
        setTimeScaleIndex(timeScaleIndex + 1);
    };

    const handleScaleDecrease = () => {
        setTimeScaleIndex(timeScaleIndex - 1);
    };

    const startDate = new Date(projectData.startDate);
    const endDate = new Date(projectData.endDate);

    useEffect(() => {
        let range;
        if (timeScaleIndex === 0) {
            let adjustedStartDate = d3.timeDay.floor(startDate);
            range = d3.timeWeek.range(adjustedStartDate, testEndDate, 1);
            setTimeFormat("%d %B");
        } else if (timeScaleIndex === 1) {
            let adjustedStartDate = d3.timeMonth.floor(startDate);
            range = d3.timeMonth.range(adjustedStartDate, testEndDate, 1);
            setTimeFormat("%b %Y");
        } else if (timeScaleIndex === 2) {
            let adjustedStartDate = d3.timeYear.floor(startDate);
            range = d3.timeYear.range(adjustedStartDate, testEndDate, 1);
            setTimeFormat("%Y");
        }
        setDateRange(range || []);
    }, [timeScaleIndex]);

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
                <ProjectTimelineSvg projectData={projectData} dateRange={dateRange} timeFormat={timeFormat} />
            </Box>
        </>
    );
};

export default ProjectTimeline;
