import { ScaleTime } from "d3";
import dayjs, { Dayjs } from "dayjs";

type TodayLineProps = {
    startDate: Dayjs;
    endDate: Dayjs;
    xScale: ScaleTime<number, number, never>;
    showToday: boolean;
    timelineHeight: number;
    chartHeight: number;
    spacing: { x: number; y: number };
};

export const TodayLine = ({ startDate, endDate, xScale, showToday, timelineHeight, chartHeight, spacing }: TodayLineProps) => {
    const today = dayjs();
    const todayX = xScale(today);

    return (
        <g className="nowline">
            {showToday && today.isAfter(startDate) && today.isBefore(endDate) && (
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
    );
};
