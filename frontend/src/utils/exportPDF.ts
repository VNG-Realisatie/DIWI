import jsPDF from "jspdf";
import html2canvas from "html2canvas";

type Element = {
    id: string;
    width: number;
    height: number;
};

const elements: Element[] = [
    { id: "totalValues", width: 436, height: 25 },
    { id: "projectPhaseChart", width: 205, height: 75 },
    { id: "targetGroupChart", width: 205, height: 75 },
    { id: "physicalAppearanceChart", width: 205, height: 75 },
    // Add the rest of the elements here
];

const h2c = async (element: HTMLElement) => {
    const canvas = await html2canvas(element, { scale: 2.5 });
    return canvas.toDataURL("image/png");
};

const getElementImage = async ({ id, width, height }: Element) => {
    const element = document.getElementById(id);
    if (element) {
        const chart = await h2c(element);
        return { chart, width, height };
    }
    return null;
};

export const exportPdf = async (t: (key: string) => string, setPdfExport: (value: boolean) => void) => {
    const chartsArray = await Promise.all(elements.map(getElementImage));
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

    filteredChartsArray.forEach(({ chart, width, height }) => {
        if (width === 436) {
            pdf.addImage(chart, "PNG", x, y, width, height);
            y += height + 10;
            lineHeight = 0;
            chartsInLine = 0;
        } else {
            pdf.addImage(chart, "PNG", x, y, width, height);
            x += width + 10;
            lineHeight = Math.max(lineHeight, height);
            chartsInLine += 1;

            if (chartsInLine === 2) {
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
