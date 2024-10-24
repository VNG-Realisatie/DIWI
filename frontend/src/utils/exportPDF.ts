import jsPDF from "jspdf";
import html2canvas from "html2canvas";
import { getPolicyDashboardProjects } from "../api/dashboardServices";

type Element = {
    id: string;
    width: number;
    height?: number;
};

const pieChartWidth = 205;

const otherChartWidth = 436;

const elements: Element[] = [
    { id: "totalValues", width: otherChartWidth, height: 25 },
    { id: "projectPhaseChart", width: pieChartWidth, height: 75 },
    { id: "targetGroupChart", width: pieChartWidth, height: 75 },
    { id: "physicalAppearanceChart", width: pieChartWidth, height: 75 },
    { id: "buy", width: pieChartWidth, height: 75 },
    { id: "rent", width: pieChartWidth, height: 75 },
    { id: "deliverables", width: pieChartWidth, height: 75 },

    //add more elements here
];

const h2c = async (element: HTMLElement) => {
    const canvas = await html2canvas(element, { scale: 2.5 });
    return canvas.toDataURL("image/png");
};

export const exportPdf = async (t: (key: string) => string, setPdfExport: (value: boolean) => void) => {
    const policyDashboardProjects = await getPolicyDashboardProjects();

    const newElements = policyDashboardProjects.map((project: { id: string }) => ({
        id: project.id,
        width: otherChartWidth,
        height: 35,
    }));

    const allElements: Element[] = [...elements, ...newElements];

    const getElementImage = async (element: Element) => {
        const el = document.getElementById(element.id);
        if (!el) return null;
        const imgData = await h2c(el);
        return { chart: imgData, width: element.width, height: element.height || 0 };
    };

    const chartsArray = await Promise.all(allElements.map(getElementImage));
    const filteredChartsArray = chartsArray.filter((chart) => chart !== null);

    if (filteredChartsArray.length === 0) {
        return;
    }

    const pdf = new jsPDF("p", "px", "a4");
    pdf.setFontSize(14);
    pdf.text(t(`dashboard.exportTitle`), 5, 20);

    let x = 5;
    let y = 30;

    let lineHeight = 0;
    let chartsInLine = 0;
    const pageHeight = pdf.internal.pageSize.height;

    const lastIndexSmallChart = filteredChartsArray.map((chart) => chart.width).lastIndexOf(pieChartWidth);

    filteredChartsArray.forEach(({ chart, width, height }, index) => {

        if (width === 436) {
            if (y + height > pageHeight) {
                pdf.addPage();
                x = 5;
                y = 30;
            }
            pdf.addImage(chart, "PNG", x, y, width, height);
            y += height + 10;
            lineHeight = 0;
            chartsInLine = 0;
        } else {
            if (x + width > pdf.internal.pageSize.width) {
                x = 5;
                y += lineHeight + 10;
                lineHeight = 0;
                chartsInLine = 0;
            }
            if (y + height > pageHeight) {
                pdf.addPage();
                x = 5;
                y = 30;
                lineHeight = 0;
                chartsInLine = 0;
            }
            pdf.addImage(chart, "PNG", x, y, width, height);
            x += width + 10;
            lineHeight = Math.max(lineHeight, height);
            chartsInLine += 1;

            if (chartsInLine === 2 || index === lastIndexSmallChart) {
                x = 5;
                y += lineHeight + 10;
                lineHeight = 0;
                chartsInLine = 0;
            }
        }
    });

    pdf.save("dashboardProjects.pdf");
    setPdfExport(false);
};
