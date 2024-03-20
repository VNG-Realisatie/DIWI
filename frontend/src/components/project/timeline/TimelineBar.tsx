import type { Dayjs } from "dayjs";
import type { ScaleTime } from "d3";
import dayjs from "dayjs";

type TimelineBarProps = {
    startDate: Dayjs;
    endDate: Dayjs;
    timeScaleIndex: number;
    xScale: ScaleTime<number, number, never>;
    size: { x: number; y: number };
    spacing: { x: number; y: number };
    textSpacing: { x: number; y: number };
};

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

export const TimelineBar = ({ startDate, endDate, timeScaleIndex, xScale, size, spacing, textSpacing }: TimelineBarProps) => {
    const timedata = createTimeData(startDate, endDate, timeScaleIndex);

    return (
        <g className="timeline">
            <rect x={spacing.x} y={spacing.y} width={size.x - 2 * spacing.x} height={size.y} fill="#111111" />
            {timedata &&
                timedata.map((d: TimeDataType) => {
                    const x = xScale(d.startDate) + spacing.x;
                    const y = spacing.y;
                    const w = xScale(d.endDate) - xScale(d.startDate) - 2 * spacing.x;
                    const h = size.y - 2 * spacing.y;
                    return (
                        <g key={"timeline block" + d.startDate}>
                            <rect x={x} y={y} width={w} height={h} fill="#d9d9d9" />
                            <text x={x + textSpacing.x} y={y + h - textSpacing.y}>
                                {d.label}
                            </text>
                        </g>
                    );
                })}
        </g>
    );
};
