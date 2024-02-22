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
    const [svgWidth, setSvgWidth] = useState(0);

    const handleScaleChange = (increment: number) => {
        setTimeScaleIndex((prevIndex) => prevIndex + increment);
    };

    const calculateDateRange = (scaleIndex: number, startDate: Date, endDate: Date) => {
        let adjustedStartDate;
        let range;

        if (scaleIndex === 0) {
            adjustedStartDate = d3.timeDay.floor(startDate);
            range = d3.timeWeek.range(adjustedStartDate, endDate, 1);
            setTimeFormat("%d %B");
            setSvgWidth(range.length * 200);
        } else if (scaleIndex === 1) {
            adjustedStartDate = d3.timeMonth.floor(startDate);
            range = d3.timeMonth.range(adjustedStartDate, endDate, 1);
            setTimeFormat("%b %Y");
            setSvgWidth(range.length * 300);
        } else if (scaleIndex === 2) {
            adjustedStartDate = d3.timeYear.floor(startDate);
            range = d3.timeYear.range(adjustedStartDate, endDate, 1);
            setTimeFormat("%Y");
            setSvgWidth(range.length * 300);
        }

        setDateRange(range || []);
    };

    useEffect(() => {
        calculateDateRange(timeScaleIndex, new Date(projectData.startDate), testEndDate);
    }, [timeScaleIndex, projectData.startDate]);

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
            <Box sx={{ overflow: "scroll" }}>
                <ProjectTimelineSvg projectData={projectData} dateRange={dateRange} timeFormat={timeFormat} width={svgWidth} />
            </Box>
        </>
    );
};

export default ProjectTimeline;
