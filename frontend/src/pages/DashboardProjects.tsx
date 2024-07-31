import { Autocomplete, Stack, TextField } from "@mui/material";
import BreadcrumbBar from "../components/header/BreadcrumbBar";
import * as Paths from "../Paths";
import { useContext, useState } from "react";
import ConfigContext from "../context/ConfigContext";
import { useTranslation } from "react-i18next";
import useCustomSearchParams from "../hooks/useCustomSearchParams";
import { Project } from "../api/projectsServices";
import { useNavigate } from "react-router-dom";
import { ChartType } from "./DashboardProject";
import { DashboardCharts } from "../components/dashboard/DashboardCharts";
type DashboardProjects = {
    physicalAppearance: ChartType[];
    targetGroup: ChartType[];
};
export const DashboardProjects = () => {
    const [selectedProject, setSelectedProject] = useState<Project | null>(null);

    const { municipalityName } = useContext(ConfigContext);
    const { rows } = useCustomSearchParams(undefined, undefined, { page: 1, pageSize: 10000 });
    const { t } = useTranslation();
    const navigate = useNavigate();

    const handleSelectProject = (project: Project | null) => {
        setSelectedProject(project);
        navigate(Paths.dashboardProject.toPath({ projectId: project?.projectId || "" }));
    };

    return (
        <Stack mb={8} >
            <BreadcrumbBar
                pageTitle={t("dashboard.title")}
                links={[{ title: `${t("dashboard.municipalityProgram")} ${municipalityName}`, link: Paths.dashboard.path }]}
            />
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
            <DashboardCharts />
        </Stack>
    );
};
