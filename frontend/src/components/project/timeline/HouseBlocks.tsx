import type { ScaleTime } from "d3";
import { HouseBlock } from "../../project-wizard/house-blocks/types";
import dayjs from "dayjs";

type PhasesProps = {
    houseBlockData: Array<HouseBlock> | null;
    xScale: ScaleTime<number, number, never>;
    titleHeight: number;
    blockHeight: number;
    chartWidth: number;
    spacing: { x: number; y: number };
    textSpacing: { x: number; y: number };
};

export const HouseBlocks = ({ houseBlockData, xScale, titleHeight, blockHeight, chartWidth, spacing, textSpacing }: PhasesProps) => {
    return (
        <>
            <g className="houseblockTitle">
                <rect x={spacing.x} y={spacing.y} width={chartWidth - 2 * spacing.x} height={titleHeight - 2 * spacing.y} fill="#00A9F3" />
                {/* TODO make based on translation? */}
                <text x={spacing.x + textSpacing.x} y={titleHeight - 2 * spacing.y - textSpacing.y} fill="#FFFFFF">
                    Houseblocks
                </text>
            </g>
            <g className="houseblockBlocks">
                {houseBlockData &&
                    houseBlockData.map((block, index: number) => {
                        const x = xScale(dayjs(block.startDate)) + spacing.x;
                        const y = titleHeight + spacing.y + index * blockHeight;
                        const w = xScale(dayjs(block.endDate)) - xScale(dayjs(block.startDate)) - 2 * spacing.x;
                        const h = blockHeight - 2 * spacing.y;
                        return (
                            <svg key={"houseBlock" + block.houseblockId}>
                                <rect x={x} y={y} width={w} height={h} fill="#5cc6f5" />
                                <text x={x + textSpacing.x} y={y + textSpacing.y + 20} fill="#FFFFFF">
                                    {block.houseblockName}
                                </text>
                            </svg>
                        );
                    })}
            </g>
        </>
    );
};
