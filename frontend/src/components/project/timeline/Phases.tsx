import type { ScaleTime } from "d3";
import { components } from "../../../types/schema";
import dayjs from "dayjs";
import { useTranslation } from "react-i18next";

type PhasesProps = {
    phaseData: Array<components["schemas"]["DatedDataModelProjectPhase"]> | null;
    xScale: ScaleTime<number, number, never>;
    titleHeight: number;
    blockHeight: number;
    chartWidth: number;
    spacing: { x: number; y: number };
    textSpacing: { x: number; y: number };
};

export const Phases = ({ phaseData, xScale, titleHeight, blockHeight, chartWidth, spacing, textSpacing }: PhasesProps) => {
    const textVerticalSpace = (blockHeight - 4 * textSpacing.y) / 3;

    const { t } = useTranslation();
    return (
        <>
            <g className="phaseTitle">
                <rect x={spacing.x} y={spacing.y} width={chartWidth - 2 * spacing.x} height={titleHeight - 2 * spacing.y} fill="#e7b85d" />
                <text x={spacing.x + textSpacing.x} y={titleHeight - 2 * spacing.y - textSpacing.y} fill="#FFFFFF">
                    {t("timeline.phases")}
                </text>
            </g>
            <g className="phaseBlocks">
                {phaseData &&
                    phaseData.map((phase) => {
                        const start = dayjs(phase.startDate);
                        const end = dayjs(phase.endDate);
                        const x = xScale(start) + spacing.x;
                        const y = titleHeight;
                        const w = xScale(end) - xScale(start) - 2 * spacing.x;
                        const h = blockHeight - 2 * spacing.y;
                        const dateBoxWidth = 100;
                        return (
                            <g key={"phase" + start}>
                                {/* Bounding block */}
                                <rect x={x} y={y + spacing.y} width={w} height={h} fill="#edcf95" />
                                {/* name on first textrow */}
                                <svg transform={`translate(${x + textSpacing.x},${y + textSpacing.y})`} width={w} height={textVerticalSpace}>
                                    <text y="60%" dominantBaseline="middle" fill="#FFFFFF">
                                        {t(`dashboard.projectPhaseOptions.${phase.data}`)}
                                    </text>
                                </svg>
                                {/* from on second textrow*/}
                                <svg
                                    transform={`translate(${x + textSpacing.x},${y + textSpacing.y + textVerticalSpace})`}
                                    width={w}
                                    height={textVerticalSpace}
                                >
                                    <text y="60%" dominantBaseline="middle" fill="#FFFFFF">
                                        {t("timeline.from")}
                                    </text>
                                    <svg transform={`translate(${50},${textSpacing.y})`} width={dateBoxWidth} height={textVerticalSpace - 2 * textSpacing.y}>
                                        <rect
                                            x={0}
                                            y={0}
                                            width={dateBoxWidth}
                                            height={textVerticalSpace - 2 * textSpacing.y}
                                            strokeLinejoin="round"
                                            rx={5}
                                            ry={5}
                                            fill="#FFFFFF"
                                        ></rect>
                                        <text x="50%" y="60%" dominantBaseline="middle" textAnchor="middle" fill="#edcf95">
                                            {start.format("DD-MM-YYYY")}
                                        </text>
                                    </svg>
                                </svg>
                                {/* to on third textrow*/}
                                <svg
                                    transform={`translate(${x + textSpacing.x},${y + textSpacing.y + textVerticalSpace * 2})`}
                                    width={w}
                                    height={textVerticalSpace}
                                >
                                    <text y="60%" dominantBaseline="middle" fill="#FFFFFF">
                                        {t("timeline.to")}
                                    </text>
                                    <svg transform={`translate(${50},${textSpacing.y})`} width={dateBoxWidth} height={textVerticalSpace - 2 * textSpacing.y}>
                                        <rect
                                            x={0}
                                            y={0}
                                            width={dateBoxWidth}
                                            height={textVerticalSpace - 2 * textSpacing.y}
                                            strokeLinejoin="round"
                                            rx={5}
                                            ry={5}
                                            fill="#FFFFFF"
                                        ></rect>
                                        <text x="50%" y="60%" dominantBaseline="middle" textAnchor="middle" fill="#edcf95">
                                            {end.format("DD-MM-YYYY")}
                                        </text>
                                    </svg>
                                </svg>
                            </g>
                        );
                    })}
            </g>
        </>
    );
};
// Potential for drag to move phases around

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
