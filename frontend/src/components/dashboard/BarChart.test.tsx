import { convertData } from "../../utils/convertChartData";
import { OutputData, selectedProjectColor } from "./BarChart";

it("Should convert chart data to format suitable for MUI bar chart", () => {
    const chartData = [
        { year: 2021, projectId: "1", amount: 100, name: "Project 1" },
        { year: 2022, projectId: "2", amount: 200, name: "Project 2" },
        { year: 2023, projectId: "3", amount: 300, name: "Project 3" },
    ];
    const expected: OutputData = {
        convertedData: [
            {
                data: [100, null, null],
                label: "Project 1",
                stack: "total",
                color: selectedProjectColor,
            },
            {
                data: [null, 200, null],
                label: "Project 2",
                stack: "total",
            },
            {
                data: [null, null, 300],
                label: "Project 3",
                stack: "total",
            },
        ],
        years: [2021, 2022, 2023],
    };
    expect(convertData(chartData, "1")).toEqual(expected);
});
