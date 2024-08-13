import { Autocomplete, Stack, TextField } from "@mui/material";
import BreadcrumbBar from "../components/header/BreadcrumbBar";
import * as Paths from "../Paths";
import { useContext, useEffect, useState } from "react";
import ConfigContext from "../context/ConfigContext";
import { useTranslation } from "react-i18next";
import useCustomSearchParams from "../hooks/useCustomSearchParams";
import { Project } from "../api/projectsServices";
import { useNavigate } from "react-router-dom";
import { ChartType } from "./DashboardProject";
import useAllowedActions from "../hooks/useAllowedActions";
import ActionNotAllowed from "./ActionNotAllowed";
import { FileDownload } from "@mui/icons-material";
import { TooltipInfo } from "../widgets/TooltipInfo";
import { DashboardCharts } from "../components/dashboard/DashboardCharts";
import { exportPdf } from "../utils/exportPDF";

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

    useEffect(() => {
        pdfExport &&
            setTimeout(() => {
                exportPdf(t, setPdfExport);
            }, 500);
    }, [pdfExport, setPdfExport, t]);

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
                        sx={{ fill: "#002C64", cursor: "pointer" }}
                    />
                </TooltipInfo>
            </Stack>

            <DashboardCharts isPrintingFullDashboard={true} isPdf={pdfExport} />
        </Stack>
    );
};
