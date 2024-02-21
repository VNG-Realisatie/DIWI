import React, { useRef, useEffect } from "react";
import * as d3 from "d3";
import { Box } from "@mui/material";

const ProjectTimeline = ({ projectData }: any) => {
    const svgRef = useRef(null);

    useEffect(() => {
        const width = 3000;
        const height = 200;
        const margin = { top: 40, right: 20, bottom: 120, left: 20 };
        const rectHeight = 80;

        if (!projectData) return;

        const svg = d3
            .select(svgRef.current)
            .attr("width", width + margin.left + margin.right)
            .attr("height", height + margin.top + margin.bottom)
            .append("g")
            .attr("transform", `translate(${margin.left},${margin.top})`);

        const startDate = new Date(projectData.startDate);
        const endDate = new Date(projectData.endDate);

        const testStartDate = new Date("2024-05-01");
        const testEndDate = new Date("2024-10-01");

        const adjustedStartDate = d3.timeMonth.floor(startDate);

        // Monthly scale
        const xScaleMonthly = d3.scaleTime().domain([adjustedStartDate, testEndDate]).range([0, width]);

        // Append a group for month text
        const monthTextGroup = svg.append("g").attr("class", "month-text-group");

        // Append a rectangle for the background
        monthTextGroup.append("rect").attr("width", width).attr("height", 20).attr("fill", "lightgrey").attr("opacity", 0.3);

        const dateRange = d3.timeMonth.range(adjustedStartDate, testEndDate);
        console.log(dateRange);

        monthTextGroup
            .selectAll(".month-text")
            .data(d3.timeMonth.range(adjustedStartDate, testEndDate, 1))
            .enter()
            .append("text")
            .attr("class", "month-text")
            .attr("x", (d: any, i: number) => xScaleMonthly(d) + i * 10)
            .attr("y", 15)
            .text((d: any) => d3.timeFormat("%b %Y")(d));

        const projectNames = projectData.projectName || []; //huisblokken ???
        const projectPhases = projectData.projectPhase || [];

        const createPhaseRectangles = (data: any, color: any, className: any) => {
            svg.selectAll(`.${className}`)
                .data(data)
                .enter()
                .append("rect")
                .attr("class", className)
                .attr("x", (d: any, i: number) => xScaleMonthly(new Date(d.startDate)) + i * 1.01)
                .attr("y", 30)
                .attr("width", (d: any) => xScaleMonthly(new Date(d.endDate)) - xScaleMonthly(new Date(d.startDate)))
                .attr("height", rectHeight)
                .attr("fill", color)
                .attr("opacity", 0.7)
                .append("title")
                .text((d: any) => d.data);
        };

        const createRectangles = (data: any, color: any, className: any) => {
            svg.selectAll(`.${className}`)
                .data(data)
                .enter()
                .append("rect")
                .attr("class", className)
                .attr("x", (d: any) => xScaleMonthly(new Date(d.startDate)))
                .attr("y", (d: any, i: number) => 120 + i * rectHeight)
                .attr("width", (d: any) => xScaleMonthly(new Date(d.endDate)) - xScaleMonthly(new Date(d.startDate)))
                .attr("height", rectHeight)
                .attr("fill", color)
                .attr("opacity", 0.7)
                .append("title")
                .text((d: any) => d.data);
        };
        createPhaseRectangles(projectPhases, "orange", "projectPhaseRect");
        createRectangles(projectNames, "steelblue", "projectNameRect");
    }, [projectData]);

    return (
        <Box sx={{ overflow: "scroll" }}>
            <svg ref={svgRef}></svg>
        </Box>
    );
};

export default ProjectTimeline;
