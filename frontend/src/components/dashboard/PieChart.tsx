import { PieChart, pieArcLabelClasses } from "@mui/x-charts/PieChart";
import { ChartType } from "../../pages/DashboardProject";
import { useTheme } from "@mui/material/styles";
import { useMediaQuery } from "@mui/material";
import { LegendRendererProps } from "@mui/x-charts";

type Props = {
    chartData: ChartType[];
    colors: string[];
};

export const DashboardPieChart = ({ chartData, colors }: Props) => {
    const theme = useTheme();

    const isSmallScreen = useMediaQuery(theme.breakpoints.down("sm"));
    const isLargeScreen = useMediaQuery(theme.breakpoints.up("lg"));
    const size = {
        height: 300,
    };
    const chartMargin = isSmallScreen ? { top: 10, bottom: 100, left: 50 } : { top: 10, bottom: 50, left: -100 };
    const getLegendPosition = (): Partial<LegendRendererProps> | undefined => {
        if (isSmallScreen) {
            return {
                direction: "row",
                position: { vertical: "bottom", horizontal: "left" },
                padding: 10,
                itemGap: 10,
                labelStyle: {
                    fontSize: 12,
                },
            };
        }
        if (isLargeScreen) {
            return {
                direction: "column",
                position: { vertical: "middle", horizontal: "right" },
                padding: 0,
            };
        }
    };

    return (
        <PieChart
            colors={colors}
            margin={chartMargin}
            series={[
                {
                    arcLabel: (item) => `${item.value}`,
                    arcLabelMinAngle: 10,
                    data: chartData,
                },
            ]}
            slotProps={{
                legend: getLegendPosition(),
            }}
            sx={{
                [`& .${pieArcLabelClasses.root}`]: {
                    fill: "white",
                    fontWeight: "bold",
                },
            }}
            {...size}
        />
    );
};
