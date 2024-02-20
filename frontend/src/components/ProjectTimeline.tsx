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

        // Monthly scale
        const xScaleMonthly = d3.scaleTime().domain([startDate, endDate]).range([0, width]);

        const axisMonthly = d3.axisBottom(xScaleMonthly).ticks(d3.timeMonth.every(1));

        // svg.append("g").call(axisMonthly);

        svg.append("rect")
            .attr("x", 0)
            .attr("y", height - margin.bottom)
            .attr("width", width)
            .attr("height", 20)
            .attr("fill", "lightgrey")
            .attr("opacity", 0.3);

        svg.append("text")
            .attr("x", 5)
            .attr("y", height - margin.bottom + 15)
            .text(() => {
                return d3.timeFormat("%b %Y")(startDate);
            });

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
        const projectNameMargin = 20;
        createRectangles(projectNameArray, "steelblue", projectNameMargin, "projectNameRect");
        const projectPhaseMargin = 40;
        createRectangles(projectPhaseArray, "orange", projectPhaseMargin, "projectPhaseRect");
    }, [projectData]);

    return <svg ref={svgRef}></svg>;
};

export default ProjectTimeline;
