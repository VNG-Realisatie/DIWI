import React, { useState, useEffect } from "react";
import * as d3 from "d3";
import { Box, Button } from "@mui/material";
import RemoveIcon from "@mui/icons-material/Remove";
import AddIcon from "@mui/icons-material/Add";
import ProjectTimelineSvg from "./ProjectTimelineSvg";

const testStartDate = new Date("2024-01-01");
const testEndDate = new Date("2029-10-20");

const ProjectTimeline = ({ projectData }: any) => {
    const [timeScaleIndex, setTimeScaleIndex] = useState(1);
    const [timeFormat, setTimeFormat] = useState("");
    const [svgWidth, setSvgWidth] = useState(0);
    const [dateRange, setDateRange] = useState<Date[]>([]);

    const handleScaleChange = (increment: number) => {
        setTimeScaleIndex((prevIndex) => prevIndex + increment);
    };

    const calculateDateRange = (scaleIndex: number, startDate: Date, endDate: Date) => {
        let range: Date[] = [];
        let adjustedStartDate = d3.timeDay.floor(startDate);
        if (scaleIndex === 0) {
            let adjustedEndDate = d3.timeWeek.offset(endDate, 1);
            range = d3.timeWeek.range(adjustedStartDate, adjustedEndDate, 1);
            setTimeFormat("%d %B %Y"); // clarify
            setSvgWidth(range.length * 200);
            setDateRange(range);
        } else if (scaleIndex === 1) {
            let adjustedEndDate = d3.timeMonth.offset(endDate, 1);
            range = d3.timeMonth.range(adjustedStartDate, adjustedEndDate, 1);
            setTimeFormat("%b %Y");
            setSvgWidth(range.length * 300);
            setDateRange(range);
        } else if (scaleIndex === 2) {
            let adjustedEndDate = d3.timeYear.offset(endDate, 1);
            range = d3.timeYear.range(adjustedStartDate, adjustedEndDate, 1);

            setTimeFormat("%Y");
            setSvgWidth(range.length * 400);
            setDateRange(range);
        }
    };

    useEffect(() => {
        calculateDateRange(timeScaleIndex, testStartDate, testEndDate); //new Date(projectData.startDate)
    }, [timeScaleIndex]);

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
