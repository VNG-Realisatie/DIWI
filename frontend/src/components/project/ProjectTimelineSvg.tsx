import { useState, useEffect, useContext } from "react";
import * as d3 from "d3";
import { Box } from "@mui/material";
import { getProjectTimeline } from "../../api/projectTimeLine";
import ProjectContext from "../../context/ProjectContext";
import { getProjectHouseBlocks } from "../../api/projectsServices";
import dayjs from "dayjs";
import type { Dayjs } from "dayjs";
import { components } from "../../types/schema";
import { HouseBlock } from "../project-wizard/house-blocks/types";

type TimeDataType = {
    startDate: Dayjs;
    endDate: Dayjs;
    label: string;
};

const createTimeData = (earliestDate: Dayjs, latestDate: Dayjs, timeScaleIndex: number): Array<TimeDataType> => {
    // TODO implement timescaleIndex
    const result = new Array<TimeDataType>();

    const createdDate = dayjs(earliestDate);
    var firstOfMonth = createdDate.startOf("month");
    var lastOfMonth = createdDate.endOf("month");

    // if start and end are in the same month return it, otherwise make it the first element
    if (lastOfMonth.isAfter(latestDate)) {
        return [{ startDate: firstOfMonth, endDate: lastOfMonth, label: firstOfMonth.format("MM-YYYY") }];
    }
    // keep adding months, checking for the last
    while (firstOfMonth.isBefore(latestDate)) {
        result.push({ startDate: firstOfMonth, endDate: lastOfMonth, label: firstOfMonth.format("MM-YYYY") });
        firstOfMonth = firstOfMonth.add(1, "month");
        lastOfMonth = lastOfMonth.add(1, "month").endOf("month");
    }
    return result;
};

export const ProjectTimelineSvg = ({ timeScaleIndex, width, height }: any) => {
    /* explanation for sizing of/in this element:
    width and height are outer boundaries of this component in the webpage.
    svgWidth and svgHeight are the dimensions for the svg, including margins so elements are not placed directly on a border.
    SVG dimensions are based on daterange (start-end dates) for x and project data (#houseblocks) for y.
    PixelsPerDay can be adjusted to widen or shrink x.

    Example: width and height can be 500x300, svgWidth and svgHeight can be 1300x700, scrollbars ensure you can inspect all data.
    */
    const { id, selectedProject } = useContext(ProjectContext);
    const [projectPhaseData, setProjectPhaseData] = useState<Array<components["schemas"]["DatedDataModelProjectPhase"]> | null>(null);
    const [houseBlockData, setHouseBlockData] = useState<Array<HouseBlock> | null>(null);

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

    // Generic, used by all items below
    const margin = { top: 5, right: 5, bottom: 5, left: 5 };
    const earliestDate: Dayjs = dayjs(selectedProject?.startDate).startOf("month");
    const latestDate: Dayjs = dayjs(selectedProject?.endDate).endOf("month");
    const diffDays = latestDate.diff(earliestDate, "days");
    const pixelsPerDay = timeScaleIndex * 2;
    const chartWidth = diffDays * pixelsPerDay;
    const chartHeight = height - margin.top - margin.bottom; // TODO make this depend on its content (timeline, phases, documents and houseblocks)
    const svgWidth = chartWidth + margin.left + margin.right;
    const svgHeight = chartHeight + margin.top + margin.bottom;
    const textSpacing = 3;

    const spacingHorizontal = 1;
    const spacingVertical = 1;

    // Time line
    const timelineHeight = 20;
    const timedata = createTimeData(earliestDate, latestDate, timeScaleIndex);

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
    const xScale = d3.scaleTime().domain([earliestDate, latestDate]).range([0, chartWidth]);

    // Todayline
    const today = dayjs();
    const todayX = xScale(today);

    return (
        <Box sx={{ overflow: "scroll" }} width={width} height={height} lineHeight={0}>
            <svg width={svgWidth} height={svgHeight} viewBox={`0 0 ${svgWidth} ${svgHeight}`} className="viz">
                <g className="chartArea" transform={`translate(${margin.left}, ${margin.top})`}>
                    {/* Timeline indicator years, months, or weeks */}
                    <g className="timeline">
                        <rect x={spacingHorizontal} y={spacingVertical} width={chartWidth - 2 * spacingHorizontal} height={phaseTitleHeight} fill="#111111" />
                        {timedata &&
                            timedata.map((d: TimeDataType) => {
                                const x = xScale(d.startDate) + spacingHorizontal;
                                const y = spacingVertical;
                                const width = xScale(d.endDate) - xScale(d.startDate) - 2 * spacingHorizontal;
                                const height = timelineHeight - 2 * spacingVertical;
                                return (
                                    <g key={"timeline block" + d.startDate}>
                                        <rect x={x} y={y} width={width} height={height} fill="#d9d9d9" />
                                        <text x={x + textSpacing} y={y + height - textSpacing}>
                                            {d.label}
                                        </text>
                                    </g>
                                );
                            })}
                    </g>
                    {/* Phases */}
                    <g className="phases">
                        {/* Title */}
                        <g className="phaseTitle">
                            <rect
                                x={spacingHorizontal}
                                y={timelineHeight + spacingVertical}
                                width={chartWidth - 2 * spacingHorizontal}
                                height={phaseTitleHeight - 2 * spacingVertical}
                                fill="#e7b85d"
                            />
                            {/* TODO make based on translation? */}
                            <text x={spacingHorizontal + textSpacing} y={timelineHeight + phaseTitleHeight - 2 * spacingVertical - textSpacing}>
                                Phases
                            </text>
                        </g>
                        {/* Blocks */}
                        <g className="phaseTrack">
                            {projectPhaseData &&
                                projectPhaseData.map((phase) => {
                                    const x = xScale(dayjs(phase.startDate)) + spacingHorizontal;
                                    const y = timelineHeight + phaseTitleHeight + spacingVertical;
                                    const width = xScale(dayjs(phase.endDate)) - xScale(dayjs(phase.startDate)) - 2 * spacingHorizontal;
                                    const height = phaseBlockHeight - 2 * spacingVertical;
                                    return (
                                        <g key={"phase" + phase.startDate}>
                                            <rect x={x} y={y} width={width} height={height} fill="#edcf95" />
                                            <text x={x + textSpacing} y={y + height - textSpacing}>
                                                {phase.data}
                                            </text>
                                        </g>
                                    );
                                })}
                        </g>
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
                                x={spacingHorizontal}
                                y={timelineHeight + phaseTitleHeight + phaseBlockHeight + spacingVertical}
                                width={chartWidth - 2 * spacingHorizontal}
                                height={houseblockTitleHeight - 2 * spacingVertical}
                                fill="#00A9F3"
                            />
                            {/* TODO make based on translation? */}
                            <text
                                x={spacingHorizontal + textSpacing}
                                y={timelineHeight + phaseTitleHeight + phaseBlockHeight + houseblockTitleHeight - 2 * spacingVertical - textSpacing}
                            >
                                Houseblocks
                            </text>
                        </g>
                        {/* Blocks */}
                        <g className="houseblockTrack">
                            {houseBlockData &&
                                houseBlockData.map((block, index: number) => {
                                    const x = xScale(dayjs(block.startDate)) + spacingHorizontal;
                                    const y =
                                        timelineHeight +
                                        phaseTitleHeight +
                                        phaseBlockHeight +
                                        houseblockTitleHeight +
                                        spacingVertical +
                                        index * houseblockTrackHeight;
                                    const width = xScale(dayjs(block.endDate)) - xScale(dayjs(block.startDate)) - 2 * spacingHorizontal;
                                    const height = phaseBlockHeight - 2 * spacingVertical;
                                    return (
                                        <g key={"houseBlock" + block.houseblockId}>
                                            <rect x={x} y={y} width={width} height={height} fill="#5cc6f5" />
                                            <text x={x + textSpacing} y={y + height - textSpacing}>
                                                {block.houseblockName}
                                            </text>
                                        </g>
                                    );
                                })}
                        </g>
                    </g>

                    {/* Nowline */}
                    <g className="nowline">
                        {today.isAfter(earliestDate) && today.isBefore(latestDate) && (
                            <line
                                x1={todayX}
                                y1={timelineHeight + spacingVertical}
                                x2={todayX}
                                y2={chartHeight - spacingVertical}
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

// const calculateDateRange = (scaleIndex: number, startDate: Date, endDate: Date) => {
//     let range: Date[] = [];
//     let adjustedStartDate = d3.timeDay.floor(startDate);
//     if (scaleIndex === 0) {
//         let adjustedEndDate = d3.timeWeek.offset(endDate, 1);
//         range = d3.timeWeek.range(adjustedStartDate, adjustedEndDate, 1);
//         setTimeFormat("%d %B %Y"); // clarify
//         setSvgWidth(range.length * 200);
//         setDateRange(range);
//     } else if (scaleIndex === 1) {
//         let adjustedEndDate = d3.timeMonth.offset(endDate, 1);
//         range = d3.timeMonth.range(adjustedStartDate, adjustedEndDate, 1);
//         setTimeFormat("%b %Y");
//         setSvgWidth(range.length * 300);
//         setDateRange(range);
//     } else if (scaleIndex === 2) {
//         let adjustedEndDate = d3.timeYear.offset(endDate, 1);
//         range = d3.timeYear.range(adjustedStartDate, adjustedEndDate, 1);

// useEffect(() => {
//     calculateDateRange(timeScaleIndex, testStartDate, testEndDate); //new Date(projectData.startDate)
// }, [timeScaleIndex]);

//         setTimeFormat("%Y");
//         setSvgWidth(range.length * 400);
//         setDateRange(range);
//     }
// };

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
