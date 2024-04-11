import { useState, useEffect, useContext } from "react";
import * as d3 from "d3";
import { Box } from "@mui/material";
import { getProjectTimeline } from "../../../api/projectTimeLine";
import ProjectContext from "../../../context/ProjectContext";
import { getProjectHouseBlocks } from "../../../api/projectsServices";
import dayjs from "dayjs";
import type { Dayjs } from "dayjs";
import { components } from "../../../types/schema";
import { HouseBlock } from "../../../types/houseBlockTypes";
import { TimelineBar } from "./TimelineBar";
import { Phases } from "./Phases";
import { HouseBlocks } from "./HouseBlocks";
import { TodayLine } from "./TodayLine";

export const ProjectTimelineSvg = ({ timeScaleIndex, showToday, width, height }: any) => {
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

    const { projectId, selectedProject } = useContext(ProjectContext);
    const [projectPhaseData, setProjectPhaseData] = useState<Array<components["schemas"]["DatedDataModelProjectPhase"]> | null>(null);
    const [houseBlockData, setHouseBlockData] = useState<Array<HouseBlock> | null>(null);
    const [chartHeight, setChartHeight] = useState<number>(height - margin.top);

    useEffect(() => {
        if (projectId) {
            getProjectTimeline(projectId)
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
    }, [projectId]);

    useEffect(() => {
        if (projectId) {
            getProjectHouseBlocks(projectId)
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
    }, [projectId]);

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
    const timelineHeight = 30;

    // Phases
    const phaseTitleHeight = 30;
    const phaseBlockHeight = 90;

    // Documents
    // const documentTitleHeight = 30;
    // const documentTrackHeight = 90;

    // House blocks
    const houseblockTitleHeight = 30;
    const houseblockTrackHeight = 30; /* TODO make bigger when more info is placed inside */

    //Create the horizontal scale and its axis generator.
    const xScale = d3.scaleTime().domain([projectStartDate, projectEndDate]).range([0, chartWidth]);

    return (
        <Box sx={{ overflow: "scroll" }} width={width} height={height} lineHeight={0} /* lineheight is to prevent vertical scrollbar */>
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
                    {/* <g className="documents">
                    <rect x="100" y="100" width="50" height="50" fill="#d09c6c" />
                    <rect x="100" y="100" width="50" height="50" fill="#f5d3b3" />
                    </g> */}
                    <g className="houseblocks" transform={`translate(${0}, ${timelineHeight + phaseTitleHeight + phaseBlockHeight})`}>
                        <HouseBlocks
                            houseBlockData={houseBlockData}
                            xScale={xScale}
                            titleHeight={houseblockTitleHeight}
                            blockHeight={houseblockTrackHeight}
                            chartWidth={chartWidth}
                            spacing={spacing}
                            textSpacing={textSpacing}
                        />
                    </g>

                    <TodayLine
                        startDate={projectStartDate}
                        endDate={projectEndDate}
                        xScale={xScale}
                        showToday={showToday}
                        timelineHeight={timelineHeight}
                        chartHeight={chartHeight}
                        spacing={spacing}
                    />
                </g>
            </svg>
        </Box>
    );
};
