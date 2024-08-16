import { ChartData, OutputData, OutputDataItem, selectedProjectColor } from "../components/dashboard/BarChart";

export const convertData = (input: ChartData, selectedProjectId: string): OutputData => {
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
