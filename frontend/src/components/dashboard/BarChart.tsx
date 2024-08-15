import { Box, Stack, Typography } from "@mui/material";
import { t } from "i18next";
import { BarChart } from "@mui/x-charts";
import { Project } from "../../api/projectsServices";
import { generateColorsArray, grayScaleColors } from "../../utils/dashboardChartColors";
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
    const convertedData = convertData(chartData, selectedProject?.projectId);

    const size = {
        height: 500,
    };

    const inValidData = chartData.every((item) => item.amount === 0);

    return (
        <>
            {inValidData ? (
                <Typography variant="h6" color="error" sx={{ textAlign: "center", mt: `${size.height / 2}px` }} {...size}>
                    {t("dashboard.chartData.noData")}
                </Typography>
            ) : (
                <Stack height={size.height} pb={3}>
                    <BarChart
                        height={size.height}
                        xAxis={[{ scaleType: "band", data: convertedData.years }]}
                        series={convertedData.convertedData}
                        colors={selectedProject ? grayScaleColors : generateColorsArray(chartData.length)}
                        slotProps={{ legend: { hidden: true } }}
                        tooltip={{ trigger: "item" }}
                        grid={{ horizontal: true }}
                    />
                    <Stack flexDirection="row" m="auto">
                        {selectedProject && <Box height={18} width={18} sx={{ backgroundColor: "#00A9F3" }}></Box>}
                        <Typography variant="caption" ml={1}>
                            {selectedProject && selectedProject.projectName ? selectedProject.projectName : ""}
                        </Typography>
                        <Stack flexDirection="row" alignItems="center" ml={2}>
                            {selectedProject ? (
                                <>
                                    <Box height={18} width={6} sx={{ backgroundColor: grayScaleColors[0] }}></Box>
                                    <Box height={18} width={6} sx={{ backgroundColor: grayScaleColors[5] }}></Box>
                                    <Box height={18} width={6} sx={{ backgroundColor: grayScaleColors[9] }}></Box>
                                    <Typography variant="caption" ml={1}>
                                        {t("dashboard.otherProjects")}
                                    </Typography>
                                </>
                            ) : (
                                <Box sx={{ display: "flex", flexWrap: "wrap" }}>
                                    {chartData.map((data, i) => {
                                        return (
                                            <Stack direction="row" sx={{ m: 0.5 }}>
                                                <Box height={15} width={15} sx={{ backgroundColor: generateColorsArray(chartData.length)[i] }}></Box>
                                                <Typography variant="caption" ml={0.5}>
                                                    {data.name}
                                                </Typography>
                                            </Stack>
                                        );
                                    })}
                                </Box>
                            )}
                        </Stack>
                    </Stack>
                </Stack>
            )}
        </>
    );
};
