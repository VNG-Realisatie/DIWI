import { Box, Stack, Typography } from "@mui/material";
import { ResponsiveBar } from "@nivo/bar";
import { t } from "i18next";

type Props = {
    chartData: ChartData;
    selectedProjectName: string | undefined;
};

type ChartData = {
    year: number;
    projectId: string;
    amount: number;
    name: string;
}[];

type OutputData = {
    year: number;
    [key: string]: number;
}[];

function convertData(input: ChartData): OutputData {
    const groupedByYear: { [year: number]: { [name: string]: number } } = {};

    input.forEach(({ year, name, amount }) => {
        if (!groupedByYear[year]) {
            groupedByYear[year] = {};
        }
        groupedByYear[year][name] = amount;
    });

    const result: OutputData = Object.keys(groupedByYear).map((year) => ({
        year: parseInt(year),
        ...groupedByYear[year as unknown as number],
    }));

    return result;
}

export const MyResponsiveBar = ({ chartData, selectedProjectName }: Props) => {
    if (!selectedProjectName) return null;
    const grayScaleColors = ["#2f2f2f", "#3f3f3f", "#4f4f4f", "#5f5f5f", "#6f6f6f", "#7f7f7f", "#8f8f8f", "#9f9f9f", "#afafaf", "#bfbfbf"];
    let currentIndex = 0;
    function getUniqueRandomColor() {
        if (currentIndex >= grayScaleColors.length) {
            currentIndex = 0;
        }
        const color = grayScaleColors[currentIndex++];
        return color;
    }

    const convertedData = convertData(chartData);
    const keys = [...new Set(chartData.map((d) => d.name))];

    return (
        <Stack height={500} pb={3}>
            <ResponsiveBar
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
                labelTextColor={{ from: "color", modifiers: [["darker", 1.6]] }}
                labelSkipWidth={12}
                labelSkipHeight={12}
                role="application"
                ariaLabel="Bar Chart"
                barAriaLabel={(e) => `${e.id}: ${e.formattedValue} in year: ${e.indexValue}`}
            />
            <Stack flexDirection="row" m="auto">
                <Box height={18} width={18} sx={{ backgroundColor: "#00A9F3" }}></Box>
                <Typography variant="caption" ml={1}>
                    {selectedProjectName}
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
