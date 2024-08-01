import { Box, Stack, Typography } from "@mui/material";
import { t } from "i18next";
import { BarChart } from "@mui/x-charts";
import { Project } from "../../api/projectsServices";

type Props = {
    chartData: ChartData;
    selectedProject: Project | null;
};

type ChartDataPoint = {
    year: number;
    projectId: string;
    amount: number;
    name: string;
};
type ChartData = ChartDataPoint[];

type OutputDataItem = {
    data: (number | null)[];
    label: string;
    stack: string;
    color?: string;
};
export type OutputData = OutputDataItem[];

export function convertData(input: ChartData, selectedProjectId: string): OutputData {
    // Get list of deduplicated years in order
    const years = input
        .map((dataPoint) => dataPoint.year)
        .sort()
        .filter((value, index, self) => self.indexOf(value) === index);

    const groupedByProject: { [projectId: string]: OutputDataItem } = {};
    input.forEach((dataPoint) => {
        if (!groupedByProject[dataPoint.projectId]) {
            groupedByProject[dataPoint.projectId] = { label: dataPoint.name, data: years.map(() => null), stack: "total" };
        }
        groupedByProject[dataPoint.projectId].data[years.indexOf(dataPoint.year)] = dataPoint.amount;
        if (dataPoint.projectId === selectedProjectId) {
            groupedByProject[dataPoint.projectId].color = selectedProjectColor;
        }
    });

    // const result: OutputData = Object.keys(groupedByProject).map((year) => ({
    //     year: parseInt(year),
    //     ...groupedByProject[year as unknown as number],
    // }));

    return Object.values(groupedByProject);
    // map((data) => {
    //     return {
    //         data: data.data,
    //         label: data.label,
    //         stack: data.stack,
    //         color: data.label === selectedProjectId ? selectedProjectColor : undefined,
    //     };
    // });
}

export const selectedProjectColor = "#00A9F3";

export const MyResponsiveBar = ({ chartData, selectedProject }: Props) => {
    if (!selectedProject) return null;
    const grayScaleColors = ["#2f2f2f", "#3f3f3f", "#4f4f4f", "#5f5f5f", "#6f6f6f", "#7f7f7f", "#8f8f8f", "#9f9f9f", "#afafaf", "#bfbfbf"];
    let currentIndex = 0;
    function getUniqueRandomColor() {
        if (currentIndex >= grayScaleColors.length) {
            currentIndex = 0;
        }
        const color = grayScaleColors[currentIndex++];
        return color;
    }

    const convertedData = convertData(chartData, selectedProject.projectId);
    const keys = [...new Set(chartData.map((d) => d.name))];

    const series = [
        { data: [null, 200], label: selectedProject.projectName, stack: "total", color: selectedProjectColor },
        { data: [100, 200], label: selectedProject.projectName, stack: "total" },
    ];

    return (
        <Stack height={500} pb={3}>
            <BarChart height={500} series={convertedData} colors={grayScaleColors} />
            {/* <ResponsiveBar
                data={convertedData}
                keys={keys}
                indexBy="year"
                margin={{ top: 50, right: 130, bottom: 50, left: 60 }}
                padding={0.3}
                valueScale={{ type: "linear" }}
                indexScale={{ type: "band", round: true }}
                colors={(bar) => (bar.id === selectedProjectName ? "#00A9F3" : getUniqueRandomColor())}
                axisTop={null}
                axisRight={null}
                axisBottom={{
                    tickSize: 5,
                    tickPadding: 5,
                    tickRotation: 0,
                    legendPosition: "middle",
                    legendOffset: 32,
                }}
                axisLeft={{
                    tickSize: 5,
                    tickPadding: 5,
                    tickRotation: 0,
                    legendPosition: "middle",
                    legendOffset: -40,
                }}
                labelTextColor="#ffffff"
                labelSkipWidth={12}
                labelSkipHeight={12}
                role="application"
                ariaLabel="Bar Chart"
                barAriaLabel={(e) => `${e.id}: ${e.formattedValue} in year: ${e.indexValue}`}
            /> */}
            <Stack flexDirection="row" m="auto">
                <Box height={18} width={18} sx={{ backgroundColor: "#00A9F3" }}></Box>
                <Typography variant="caption" ml={1}>
                    {selectedProject.projectName}
                </Typography>
                <Stack flexDirection="row" alignItems="center" ml={2}>
                    <Box height={18} width={6} sx={{ backgroundColor: grayScaleColors[0] }}></Box>
                    <Box height={18} width={6} sx={{ backgroundColor: grayScaleColors[5] }}></Box>
                    <Box height={18} width={6} sx={{ backgroundColor: grayScaleColors[9] }}></Box>
                    <Typography variant="caption" ml={1}>
                        {t("dashboard.otherProjects")}
                    </Typography>
                </Stack>
            </Stack>
        </Stack>
    );
};
