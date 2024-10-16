import { PieChart, pieArcLabelClasses } from "@mui/x-charts/PieChart";
import { ChartType } from "../../pages/DashboardProject";
import { useTheme } from "@mui/material/styles";
import { Box, Stack, Typography, useMediaQuery } from "@mui/material";
import { DefaultizedPieValueType, PieValueType } from "@mui/x-charts";
import { useTranslation } from "react-i18next";
import { generateColorsArray } from "../../utils/dashboardChartColors";
import { MakeOptional } from "@mui/x-charts/internals";

type Props = {
    chartData: ChartType[];
    isPdfChart?: boolean;
};

export const DashboardPieChart = ({ chartData, isPdfChart }: Props) => {
    const theme = useTheme();
    const { t } = useTranslation();

    const isSmallScreen = useMediaQuery(theme.breakpoints.down("sm"));

    const size = {
        height: 300,
    };

    const chartDataWithoutNegativeValues = chartData.filter((data) => data.value >= 0);

    const chartMargin = isSmallScreen && !isPdfChart ? { top: 10, bottom: 100, left: 50 } : { top: 10, bottom: 50, left: 10, right: 10 };

    const TOTAL = chartDataWithoutNegativeValues.map((item) => item.value).reduce((a, b) => a + b, 0);

    const getArcLabel = (params: DefaultizedPieValueType) => {
        return `${params.value}`;
    };

    const getTooltipLabel = (params: MakeOptional<PieValueType, "id">) => {
        const percent = params.value / TOTAL;
        return `${(percent * 100).toFixed(0)}%`;
    };
    return (
        <>
            {chartData.every((data) => data.value <= 0) ? (
                <Typography variant="h6" color="error" sx={{ textAlign: "center", mt: `${size.height / 2}px` }} {...size}>
                    {t("dashboard.chartData.noData")}
                </Typography>
            ) : (
                <Stack direction={isSmallScreen && !isPdfChart ? "column" : "row"} overflow="scroll" justifyContent="space-between">
                    <PieChart
                        colors={generateColorsArray(chartData.length)}
                        margin={chartMargin}
                        series={[
                            {
                                arcLabel: (item) => `${getArcLabel(item)}`,
                                arcLabelMinAngle: 24,
                                data: chartDataWithoutNegativeValues,
                                valueFormatter: (params: MakeOptional<PieValueType, "id">) => getTooltipLabel(params),
                            },
                        ]}
                        slotProps={{
                            legend: { hidden: true },
                        }}
                        sx={{
                            [`& .${pieArcLabelClasses.root}`]: {
                                fill: "white",
                                fontWeight: "bold",
                            },
                        }}
                        {...size}
                    />
                    <Stack direction={isSmallScreen && !isPdfChart ? "row" : "column"} flexWrap="wrap" maxHeight={"260px"} width="100%">
                        {chartDataWithoutNegativeValues.map((data, i) => {
                            return (
                                <Stack direction="row" sx={{ m: 1 }} key={i}>
                                    <Box height={20} width={20} sx={{ backgroundColor: generateColorsArray(chartData.length)[i] }}></Box>
                                    <Typography variant="caption" ml={1}>
                                        {data.label}
                                    </Typography>
                                </Stack>
                            );
                        })}
                    </Stack>
                </Stack>
            )}
        </>
    );
};
