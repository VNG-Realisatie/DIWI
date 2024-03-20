import type { ScaleTime } from "d3";
import { components } from "../../../types/schema";
import dayjs from "dayjs";

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
    return (
        <>
            <g className="phaseTitle">
                <rect x={spacing.x} y={spacing.y} width={chartWidth - 2 * spacing.x} height={titleHeight - 2 * spacing.y} fill="#e7b85d" />
                {/* TODO make based on translation? */}
                <text x={spacing.x + textSpacing.x} y={titleHeight - 2 * spacing.y - textSpacing.y}>
                    Phases
                </text>
            </g>
            <g className="phaseBlocks">
                {phaseData &&
                    phaseData.map((phase) => {
                        const x = xScale(dayjs(phase.startDate)) + spacing.x;
                        const y = titleHeight + spacing.y;
                        const w = xScale(dayjs(phase.endDate)) - xScale(dayjs(phase.startDate)) - 2 * spacing.x;
                        const h = blockHeight - 2 * spacing.y;
                        return (
                            <g key={"phase" + phase.startDate}>
                                <rect x={x} y={y} width={w} height={h} fill="#edcf95" />
                                <text x={x + textSpacing.x} y={y + h - textSpacing.y}>
                                    {phase.data}
                                </text>
                            </g>
                        );
                    })}
            </g>
        </>
    );
};
