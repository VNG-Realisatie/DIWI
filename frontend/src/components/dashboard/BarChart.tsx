import { Box, Stack, Typography } from "@mui/material";
import { t } from "i18next";
import { BarChart } from "@mui/x-charts";
import { Project } from "../../api/projectsServices";
import { grayScaleColors } from "../../utils/dashboardChartColors";

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
export type OutputData = { convertedData: OutputDataItem[]; years: number[] };

const convertData = (input: ChartData, selectedProjectId: string): OutputData => {
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
    return { convertedData: Object.values(groupedByProject), years };
};

export const selectedProjectColor = "#00A9F3";

export const MyResponsiveBar = ({ chartData, selectedProject }: Props) => {
    if (!selectedProject) return null;

    const convertedData = convertData(chartData, selectedProject.projectId);

    return (
        <Stack height={500} pb={3}>
            <BarChart
                height={500}
                xAxis={[{ scaleType: "band", data: convertedData.years }]}
                series={convertedData.convertedData}
                colors={grayScaleColors}
                slotProps={{ legend: { hidden: true } }}
                tooltip={{ trigger: "item" }}
                grid={{ horizontal: true }}
            />
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
