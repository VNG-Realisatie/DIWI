import { useState, useEffect, useContext } from "react";
import * as d3 from "d3";
import { Box } from "@mui/material";
import { getProjectTimeline } from "../../../api/projectTimeLine";
import ProjectContext from "../../../context/ProjectContext";
import { getProjectHouseBlocks } from "../../../api/projectsServices";
import dayjs from "dayjs";
import type { Dayjs } from "dayjs";
import { components } from "../../../types/schema";
import { HouseBlock } from "../../project-wizard/house-blocks/types";
import { TimelineBar } from "./TimelineBar";
import { Phases } from "./Phases";

export const ProjectTimelineSvg = ({ timeScaleIndex, width, height }: any) => {
    /* explanation for sizing of/in this element:
    width and height are outer boundaries of this component in the webpage.
    svgWidth and svgHeight are the dimensions for the svg, including margins so elements are not placed directly on a border.
    SVG dimensions are based on daterange (start-end dates) for x and project data (#houseblocks) for y.
    PixelsPerDay can be adjusted to widen or shrink x.

    Example: width and height can be 500x300, svgWidth and svgHeight can be 1300x700, scrollbars ensure you can inspect all data.
    */
    const margin = { top: 5, right: 5, bottom: 5, left: 5 };
    const spacing = { x: 1, y: 1 };
    const textSpacing = { x: 3, y: 3 };

    const { id, selectedProject } = useContext(ProjectContext);
    const [projectPhaseData, setProjectPhaseData] = useState<Array<components["schemas"]["DatedDataModelProjectPhase"]> | null>(null);
    const [houseBlockData, setHouseBlockData] = useState<Array<HouseBlock> | null>(null);
    const [chartHeight, setChartHeight] = useState<number>(height - margin.top);

    useEffect(() => {
        if (id) {
            getProjectTimeline(id)
                .then((projectData) => {
                    const phaseData = projectData?.projectPhase;
                    if (phaseData) {
                        setProjectPhaseData(projectData.projectPhase);
                    } else {
                        setProjectPhaseData(null);
                        console.error("No projectPhase data available!");
                    }
                })
                .catch((error) => {
                    console.error(error);
                    setProjectPhaseData(null);
                });
        }
    }, [id]);

    useEffect(() => {
        if (id) {
            getProjectHouseBlocks(id)
                .then((houseBlockData) => {
                    if (houseBlockData) {
                        setHouseBlockData(houseBlockData.sort((a, b) => (dayjs(a.startDate).isBefore(dayjs(b.startDate)) ? -1 : 1)));
                    } else {
                        setHouseBlockData(null);
                        console.error("No houseBlock data available!");
                    }
                })
                .catch((error) => {
                    console.log(error);
                    setHouseBlockData(null);
                });
        }
    }, [id]);

    useEffect(() => {
        setChartHeight(timelineHeight + phaseTitleHeight + phaseBlockHeight + houseblockTitleHeight + (houseBlockData?.length ?? 1) * houseblockTrackHeight);
    }, [houseBlockData]);

    // Generic, used by all items below
    const projectStartDate: Dayjs = dayjs(selectedProject?.startDate).startOf("month");
    const projectEndDate: Dayjs = dayjs(selectedProject?.endDate).endOf("month");
    const diffDays = projectEndDate.diff(projectStartDate, "days");
    const pixelsPerDay = timeScaleIndex * 2;
    const chartWidth = diffDays * pixelsPerDay;
    const svgWidth = chartWidth + margin.left + margin.right;
    const svgHeight = chartHeight + margin.top + margin.bottom;

    // Time line
    const timelineHeight = 20;

    // Phases
    const phaseTitleHeight = 20;
    const phaseBlockHeight = 50;

    // Documents
    // const documentTitleHeight = 20;
    // const documentTrackHeight = 50;

    // House blocks
    const houseblockTitleHeight = 20;
    const houseblockTrackHeight = 50;

    //Create the horizontal scale and its axis generator.
    const xScale = d3.scaleTime().domain([projectStartDate, projectEndDate]).range([0, chartWidth]);

    // Todayline
    const today = dayjs();
    const todayX = xScale(today);

    return (
        <Box sx={{ overflow: "scroll" }} width={width} height={height} lineHeight={0}>
            <svg width={svgWidth} height={svgHeight} viewBox={`0 0 ${svgWidth} ${svgHeight}`} className="viz">
                <g className="chartArea" transform={`translate(${margin.left}, ${margin.top})`}>
                    <g className="timeline" transform={`translate(${0}, ${0})`}>
                        <TimelineBar
                            startDate={projectStartDate}
                            endDate={projectEndDate}
                            timeScaleIndex={timeScaleIndex}
                            xScale={xScale}
                            size={{
                                x: chartWidth,
                                y: timelineHeight,
                            }}
                            spacing={spacing}
                            textSpacing={textSpacing}
                        />
                    </g>
                    <g className="timeline" transform={`translate(${0}, ${timelineHeight})`}>
                        <Phases
                            phaseData={projectPhaseData}
                            xScale={xScale}
                            titleHeight={phaseTitleHeight}
                            blockHeight={phaseBlockHeight}
                            chartWidth={chartWidth}
                            spacing={spacing}
                            textSpacing={textSpacing}
                        />
                    </g>
                    {/* Documents */}
                    {/* <g className="documents">
                    <rect x="100" y="100" width="50" height="50" fill="#d09c6c" />
                    <rect x="100" y="100" width="50" height="50" fill="#f5d3b3" />
                    </g> */}

                    {/* Housebocks */}
                    <g className="houseblocks">
                        {/* Title */}
                        <g className="houseblockTitle">
                            <rect
                                x={spacing.x}
                                y={timelineHeight + phaseTitleHeight + phaseBlockHeight + spacing.y}
                                width={chartWidth - 2 * spacing.x}
                                height={houseblockTitleHeight - 2 * spacing.y}
                                fill="#00A9F3"
                            />
                            {/* TODO make based on translation? */}
                            <text
                                x={spacing.x + textSpacing.x}
                                y={timelineHeight + phaseTitleHeight + phaseBlockHeight + houseblockTitleHeight - 2 * spacing.y - textSpacing.y}
                            >
                                Houseblocks
                            </text>
                        </g>
                        {/* Blocks */}
                        <g className="houseblockTrack">
                            {houseBlockData &&
                                houseBlockData.map((block, index: number) => {
                                    const x = xScale(dayjs(block.startDate)) + spacing.x;
                                    const y =
                                        timelineHeight +
                                        phaseTitleHeight +
                                        phaseBlockHeight +
                                        houseblockTitleHeight +
                                        spacing.y +
                                        index * houseblockTrackHeight;
                                    const width = xScale(dayjs(block.endDate)) - xScale(dayjs(block.startDate)) - 2 * spacing.x;
                                    const height = phaseBlockHeight - 2 * spacing.y;
                                    return (
                                        <g key={"houseBlock" + block.houseblockId}>
                                            <rect x={x} y={y} width={width} height={height} fill="#5cc6f5" />
                                            <text x={x + textSpacing.x} y={y + height - textSpacing.y}>
                                                {block.houseblockName}
                                            </text>
                                        </g>
                                    );
                                })}
                        </g>
                    </g>

                    {/* Nowline */}
                    <g className="nowline">
                        {today.isAfter(projectStartDate) && today.isBefore(projectEndDate) && (
                            <line
                                x1={todayX}
                                y1={timelineHeight + spacing.y}
                                x2={todayX}
                                y2={chartHeight - spacing.y}
                                stroke="#333333"
                                strokeDasharray={3}
                                strokeWidth={1}
                            />
                        )}
                    </g>
                </g>
            </svg>
        </Box>
    );
};

// Comment below still usefull for TODO's regarding ZOOM and DRAG functionality

//         const drag = d3.drag().on("drag", handleDrag);

//         function handleDrag(e: any) {
//             e.subject.x = e.x;
//             update();
//         }

//         function update() {
//             d3.select("svg")
//                 .selectAll("drag-handle")
//                 .attr("x", (d: any) => d.x);
//         }

//         function initDrag() {
//             const dragHandles = d3.select<SVGSVGElement, unknown>("svg").selectAll<SVGSVGElement, any>(".drag-handle");
//             dragHandles.call(drag as any);
//         }

//         initDrag();
//         // svg.selectAll(".drag-handle").call(dragHandle);
//     }, [projectData, dateRange, timeFormat, width]);
