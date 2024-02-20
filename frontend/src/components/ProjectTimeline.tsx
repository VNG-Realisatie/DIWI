import React, { useRef, useEffect } from "react";
import * as d3 from "d3";

const ProjectTimeline = ({ projectData }: any) => {
    const svgRef = useRef(null);

    useEffect(() => {
        const width = 800;
        const height = 200;
        const margin = { top: 40, right: 20, bottom: 120, left: 20 };

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

        const adjustedStartDate = d3.timeMonth.floor(testStartDate);

        // Monthly scale
        const xScaleMonthly = d3.scaleTime().domain([adjustedStartDate, testEndDate]).range([0, width]);

        const axisMonthly = d3.axisBottom(xScaleMonthly).ticks(d3.timeMonth.every(1));

        // svg.append("g").call(axisMonthly);

        // Append a group for month text
        const monthTextGroup = svg
            .append("g")
            .attr("class", "month-text-group")
            .attr("transform", `translate(0, ${height - margin.bottom + 15})`);

        // Append a rectangle for the background
        monthTextGroup.append("rect").attr("width", width).attr("height", 30).attr("fill", "lightgrey").attr("opacity", 0.3);

        const dateRange = d3.timeMonth.range(adjustedStartDate, testEndDate);
        console.log(dateRange);

        monthTextGroup
            .selectAll(".month-text")
            .data(d3.timeMonth.range(adjustedStartDate, testEndDate, 1))
            .enter()
            .append("text")
            .attr("class", "month-text")
            .attr("x", (d: any, i: number) => xScaleMonthly(d) + i * 60)
            .text((d: any) => d3.timeFormat("%b %Y")(d));

        const projectNameArray = projectData.projectName || [];
        const projectPhaseArray = projectData.projectPhase || [];

        const createRectangles = (data: any, color: any, yOffset: any, className: any) => {
            svg.selectAll(`.${className}`)
                .data(data)
                .enter()
                .append("rect")
                .attr("class", className)
                .attr("x", (d: any, i: number) => xScaleMonthly(new Date(d.startDate)) + i * 1.1)
                .attr("y", height - margin.bottom + yOffset)
                .attr("width", (d: any) => xScaleMonthly(new Date(d.endDate)) - xScaleMonthly(new Date(d.startDate)))
                .attr("height", 20)
                .attr("fill", color)
                .attr("opacity", 0.7)
                .append("title")
                .text((d: any) => d.data);
        };
        const projectNameMargin = 60;
        createRectangles(projectNameArray, "steelblue", projectNameMargin, "projectNameRect");
        const projectPhaseMargin = 80;
        createRectangles(projectPhaseArray, "orange", projectPhaseMargin, "projectPhaseRect");
    }, [projectData]);

    return <svg ref={svgRef}></svg>;
};

export default ProjectTimeline;
