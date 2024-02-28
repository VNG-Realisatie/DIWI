import React, { useRef, useEffect } from "react";
import * as d3 from "d3";

const ProjectTimelineSvg = ({ projectData, dateRange, timeFormat, width }: any) => {
    const svgRef = useRef(null);
    const svg = d3.select(svgRef.current);
    svg.selectAll("*").remove();

    useEffect(() => {
        const height = 200;
        const margin = { top: 0, right: 20, bottom: 120, left: 20 };
        const rectHeight = 80;

        const svg = d3
            .select(svgRef.current)
            .attr("width", width + margin.left + margin.right)
            .attr("height", height + margin.top + margin.bottom)
            .append("g")
            .attr("transform", `translate(${margin.left},${margin.top})`);
        //using 1st and last elements of dateRange
        const xScale = d3
            .scaleTime()
            .domain([dateRange[0], dateRange[dateRange.length - 1]])
            .range([0, width]);

        const monthTextGroup = svg.append("g").attr("class", "month-text-group");

        monthTextGroup.append("rect").attr("width", width).attr("height", 20).attr("fill", "lightgrey").attr("opacity", 0.3);

        const projectFasenTitle = svg.append("g");
        projectFasenTitle.append("rect").attr("width", width).attr("height", 20).attr("y", 20).attr("fill", "rgb(231,184,93)");

        projectFasenTitle.append("text").attr("x", 10).attr("y", 35).attr("fill", "white").text("Projectfasen");

        monthTextGroup
            .selectAll(".month-text")
            .data(dateRange)
            .enter()
            .append("text")
            .attr("class", "month-text")
            .attr("x", (d: any, i: number) => xScale(d) + 5)
            .attr("y", 15)
            .text((d: any) => d3.timeFormat(timeFormat)(d));

        monthTextGroup
            .selectAll(".month-divider")
            .data(dateRange.slice(0, -1))
            .enter()
            .append("line")
            .attr("class", "month-divider")
            .attr("x1", (d: any) => xScale(d))
            .attr("x2", (d: any) => xScale(d))
            .attr("y1", 0)
            .attr("y2", 20)
            .attr("stroke", "grey")
            .attr("stroke-width", 3);

        const projectNames = projectData.projectName || []; //currently represents huisblokken
        const projectPhases = projectData.projectPhase || [];

        //move functions to helpers later
        const createPhaseRectangles = (data: any, color: any, className: any) => {
            svg.selectAll(`.${className}`)
                .data(data)
                .enter()
                .append("rect")
                .attr("class", className)
                .attr("x", (d: any, i: number) => xScale(new Date(d.startDate)))
                .attr("y", 40)
                .attr("width", (d: any) => xScale(new Date(d.endDate)) - xScale(new Date(d.startDate)) + 10) //+ 10 adjusts width so it looks closer to the month end
                .attr("height", rectHeight)
                .attr("fill", color)
                .attr("opacity", 0.7)
                .append("title")
                .text((d: any) => d.data);
        };

        const createHouseBlockRectangles = (data: any, color: any, className: any) => {
            svg.selectAll(`.${className}`)
                .data(data)
                .enter()
                .append("rect")
                .attr("class", className)
                .attr("x", (d: any) => xScale(new Date(d.startDate)))
                .attr("y", (_, i: number) => 120 + i * rectHeight)
                .attr("width", (d: any) => xScale(new Date(d.endDate)) - xScale(new Date(d.startDate)) + 10) //+ 10 adjusts width so it looks closer to the month end
                .attr("height", rectHeight)
                .attr("fill", color)
                .attr("opacity", 0.7)
                .append("title")
                .text((d: any) => d.data);
        };
        createPhaseRectangles(projectPhases, "orange", "projectPhaseRect");
        createHouseBlockRectangles(projectNames, "steelblue", "projectNameRect");
    }, [projectData, dateRange, timeFormat, width]);

    return <svg ref={svgRef}></svg>;
};

export default ProjectTimelineSvg;
