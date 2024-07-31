import { Autocomplete, Stack, TextField } from "@mui/material";
import BreadcrumbBar from "../components/header/BreadcrumbBar";
import * as Paths from "../Paths";
import { useCallback, useContext, useEffect, useState } from "react";
import ConfigContext from "../context/ConfigContext";
import { useTranslation } from "react-i18next";
import useCustomSearchParams from "../hooks/useCustomSearchParams";
import { Project } from "../api/projectsServices";
import { useNavigate } from "react-router-dom";
import { ChartType } from "./DashboardProject";
import useAllowedActions from "../hooks/useAllowedActions";
import ActionNotAllowed from "./ActionNotAllowed";
import html2canvas from "html2canvas";
import jsPDF from "jspdf";
import { FileDownload } from "@mui/icons-material";
import { TooltipInfo } from "../widgets/TooltipInfo";
import { DashboardCharts } from "../components/dashboard/DashboardCharts";

type DashboardProjects = {
    physicalAppearance: ChartType[];
    targetGroup: ChartType[];
};

export const DashboardProjects = () => {
    const [selectedProject, setSelectedProject] = useState<Project | null>(null);
    const [pdfExport, setPdfExport] = useState(false);

    const { municipalityName } = useContext(ConfigContext);
    const { rows } = useCustomSearchParams(undefined, undefined, { page: 1, pageSize: 10000 });
    const { t } = useTranslation();
    const navigate = useNavigate();

    const { allowedActions } = useAllowedActions();

    const handleSelectProject = (project: Project | null) => {
        setSelectedProject(project);
        navigate(Paths.dashboardProject.toPath({ projectId: project?.projectId || "" }));
    };
    const exportPDF = useCallback(async () => {
        const totalValues = document.getElementById("totalValues");
        const projectPhase = document.getElementById("projectPhaseChart");
        const targetGroup = document.getElementById("targetGroupChart");
        const physicalAppearance = document.getElementById("physicalAppearanceChart");
        const buy = document.getElementById("buy");
        const rent = document.getElementById("rent");
        const residentialProjects = document.getElementById("residentialProjects");
        const deliverables = document.getElementById("deliverables");
        const delayedProjects = document.getElementById("delayedProjects");

        if (!totalValues) {
            console.error("no exportEmployment");
            return;
        }

        if (!projectPhase) {
            console.error("no valueAdded");
            return;
        }

        if (!targetGroup) {
            console.error("no communityGiving");
            return;
        }

        if (!physicalAppearance) {
            console.error("no employment");
            return;
        }
        if (!buy) {
            console.error("no buy");
            return;
        }
        if (!rent) {
            console.error("no rent");
            return;
        }
        if (!residentialProjects) {
            console.error("no residentialProjects");
            return;
        }
        if (!deliverables) {
            console.error("no deliverables");
            return;
        }
        if (!delayedProjects) {
            console.error("no delayedProjects");
            return;
        }

        const h2c = async (element: HTMLElement) => {
            const canvas = await html2canvas(element, { scale: 2.5 });
            return canvas.toDataURL("image/png");
        };
        const totalValueChart = await h2c(totalValues);
        const projectPhaseChart = await h2c(projectPhase);
        const targetGroupChart = await h2c(targetGroup);
        const physicalAppearanceChart = await h2c(physicalAppearance);
        //Add the rest of the charts here
        const pdf = new jsPDF("p", "px", "a4");
        pdf.setFontSize(14);
        pdf.text(t(`dashboard.exportTitle`), 5, 20);
        pdf.addImage(totalValueChart, "PNG", 5, 30, 436, 25);
        pdf.addImage(projectPhaseChart, "PNG", 5, 65, 215, 75);
        pdf.addImage(targetGroupChart, "PNG", 225, 65, 215, 75);
        pdf.addImage(physicalAppearanceChart, "PNG", 5, 145, 215, 75);
        // Add the rest of the charts here
        pdf.save("dashboardProjects.pdf");
        setPdfExport(false);
    }, [t]);

    useEffect(() => {
        pdfExport &&
            setTimeout(() => {
                exportPDF();
            }, 500);
    }, [exportPDF, pdfExport]);

    if (!allowedActions.includes("VIEW_ALL_BLUEPRINTS")) {
        return <ActionNotAllowed errorMessage={t("dashboard.forbidden")} />;
    }

    return (
        <Stack mb={8}>
            <BreadcrumbBar
                pageTitle={t("dashboard.title")}
                links={[{ title: `${t("dashboard.municipalityProgram")} ${municipalityName}`, link: Paths.dashboard.path }]}
            />
            <Stack direction="row" alignItems="center" justifyContent="space-between" sx={{ cursor: "pointer" }}>
                <Autocomplete
                    sx={{ my: 1 }}
                    id="dashboard-projects"
                    size="small"
                    options={rows || []}
                    getOptionLabel={(option) => option?.projectName || ""}
                    value={selectedProject || null}
                    onChange={(_, newValue) => handleSelectProject(newValue)}
                    renderInput={(params) => <TextField {...params} size="small" sx={{ minWidth: "200px" }} placeholder={t("dashboard.selectProject")} />}
                />
                <TooltipInfo text={t("dashboard.exportpdf")}>
                    <FileDownload
                        onClick={() => {
                            setPdfExport(true);
                        }}
                        sx={{ fill: "#002C64" }}
                    />
                </TooltipInfo>
            </Stack>

            {!pdfExport && <DashboardCharts />}
            {pdfExport && <DashboardCharts isPdf={true} />}
        </Stack>
    );
};
