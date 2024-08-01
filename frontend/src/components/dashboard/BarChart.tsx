import { Box, Stack, Typography } from "@mui/material";
import { t } from "i18next";
import { BarChart } from "@mui/x-charts";
import { Project } from "../../api/projectsServices";
import { grayScaleColors } from "../../utils/dashboardChartColors";
import { convertData } from "../../utils/convertChartData";

type Props = {
    chartData: ChartData;
    selectedProject: Project | null;
};

export type ChartDataPoint = {
    year: number;
    projectId: string;
    amount: number;
    name: string;
};
export type ChartData = ChartDataPoint[];

export type OutputDataItem = {
    data: (number | null)[];
    label: string;
    stack: string;
    color?: string;
};

export type OutputData = { convertedData: OutputDataItem[]; years: number[] };

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
