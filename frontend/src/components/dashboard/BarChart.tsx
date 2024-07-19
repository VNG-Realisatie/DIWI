import { Box, Stack, Typography } from "@mui/material";
import { ResponsiveBar } from "@nivo/bar";
import { t } from "i18next";

type Props = {
    chartData: ChartData;
    selectedProjectId: string;
};

type ChartData = {
    year: number;
    projectId: string;
    amount: number;
}[];

type OutputData = {
    year: number;
    [key: string]: number;
}[];

function convertData(input: ChartData): OutputData {
    const groupedByYear: { [year: number]: { [projectId: string]: number } } = {};

    input.forEach(({ year, projectId, amount }) => {
        if (!groupedByYear[year]) {
            groupedByYear[year] = {};
        }
        groupedByYear[year][projectId] = amount;
    });

    const result: OutputData = Object.keys(groupedByYear).map((year) => ({
        year: parseInt(year),
        //@ts-expect-error expected
        ...groupedByYear[year],
    }));

    return result;
}
export const MyResponsiveBar = ({ chartData, selectedProjectId }: Props) => {
    const grayScaleColors = ["#2f2f2f", "#3f3f3f", "#4f4f4f", "#5f5f5f", "#6f6f6f", "#7f7f7f", "#8f8f8f", "#9f9f9f", "#afafaf", "#bfbfbf"];

    // Function to get a unique random color
    let currentIndex = 0;
    function getUniqueRandomColor() {
        if (currentIndex >= grayScaleColors.length) {
            currentIndex = 1;
            return null;
        }
        const color = grayScaleColors[currentIndex];
        currentIndex++;
        return color;
    }

    const convertedData = convertData(chartData);
    const keys = chartData.map((d) => d.projectId);
    return (
        <Stack height={500} pb={3}>
            <ResponsiveBar
                data={convertedData}
                keys={keys}
                indexBy="year"
                margin={{ top: 50, right: 10, bottom: 80, left: 60 }}
                padding={0.3}
                valueScale={{ type: "linear" }}
                indexScale={{ type: "band", round: true }}
                //@ts-expect-error expected
                colors={(e) => (e.id === selectedProjectId ? "#00A9F3" : getUniqueRandomColor())}
                axisTop={null}
                axisRight={null}
                axisBottom={{
                    tickSize: 5,
                    tickPadding: 5,
                    tickRotation: 0,
                    legendPosition: "middle",
                    legendOffset: 32,
                    truncateTickAt: 0,
                }}
                axisLeft={{
                    tickSize: 5,
                    tickPadding: 5,
                    tickRotation: 0,
                    legendPosition: "middle",
                    legendOffset: -40,
                    truncateTickAt: 0,
                }}
                labelTextColor="#ffffff"
                labelSkipWidth={12}
                labelSkipHeight={12}
                role="application"
                ariaLabel="Nivo bar chart demo"
                barAriaLabel={(e) => e.id + ": " + e.formattedValue + " in country: " + e.indexValue}
            />
            <Stack flexDirection="row" m="auto">
                <Stack flexDirection="row" alignItems="center">
                    <Box height={18} width={18} sx={{ backgroundColor: "#00A9F3" }}></Box>
                    <Typography variant="caption" ml={1}>
                        {selectedProjectId}
                    </Typography>
                </Stack>
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
