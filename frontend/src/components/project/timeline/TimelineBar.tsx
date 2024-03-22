import type { Dayjs } from "dayjs";
import type { ScaleTime } from "d3";
import dayjs from "dayjs";
import weekOfYear from "dayjs/plugin/weekOfYear";

dayjs.extend(weekOfYear);

type TimelineBarProps = {
    startDate: Dayjs;
    endDate: Dayjs;
    timeScaleIndex: number;
    xScale: ScaleTime<number, number, never>;
    size: { x: number; y: number };
    spacing: { x: number; y: number };
    textSpacing: { x: number; y: number };
};

type DateRange = {
    start: Dayjs;
    end: Dayjs;
};

const createDateRangeData = (earliestDate: Dayjs, latestDate: Dayjs, increment: "week" | "month" | "year"): Array<DateRange> => {
    const dateRanges = new Array<DateRange>();

    // add earliestdate and check if increment falls beyond latestDate
    const earliestEndDate = earliestDate.endOf(increment);
    if (earliestEndDate.isAfter(latestDate)) {
        dateRanges.push({ start: earliestDate, end: latestDate });
        return dateRanges;
    }
    // then skip to the first monday AFTER earliestdate
    dateRanges.push({ start: earliestDate, end: earliestEndDate });
    let current = dayjs(earliestDate).startOf(increment);
    current = current.add(1, increment);
    // keep adding all 'first' mondays before latestdate
    while (current.isBefore(latestDate) && current.add(1, increment).isBefore(latestDate)) {
        dateRanges.push({ start: current, end: current.add(1, increment) });
        current = current.add(1, increment);
    }
    dateRanges.push({ start: current, end: latestDate });
    return dateRanges;
};

export const TimelineBar = ({ startDate, endDate, timeScaleIndex, xScale, size, spacing, textSpacing }: TimelineBarProps) => {
    // timescaleindex 1 = smallest scale, timescaleindex 5 = largest scale
    const increment = (timeScaleIndex === 1 && "year") || (timeScaleIndex === 5 && "week") || "month";
    const timedata = createDateRangeData(startDate, endDate, increment);

    return (
        <>
            <rect x={spacing.x} y={spacing.y} width={size.x - 2 * spacing.x} height={size.y} fill="#111111" />
            {timedata &&
                timedata.map((d: DateRange) => {
                    const x = xScale(d.start) + spacing.x;
                    const y = spacing.y;
                    const w = xScale(d.end) - xScale(d.start) - 2 * spacing.x;
                    const h = size.y - 2 * spacing.y;
                    return (
                        <g key={"timeline block" + d.start} transform="translate(0,0)">
                            <rect x={x} y={y} width={w} height={h} fill="#d9d9d9" />
                            <text x={x + textSpacing.x} y={y + h - textSpacing.y}>
                                {increment === "year" && d.start.format("YYYY")}
                                {increment === "month" && d.start.format("MMM-YYYY") /* TODO add translation for month names? */}
                                {increment === "week" && d.start.week()}
                            </text>
                        </g>
                    );
                })}
        </>
    );
};

// comment might still be usefull for zoom/formatting for timeline

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
