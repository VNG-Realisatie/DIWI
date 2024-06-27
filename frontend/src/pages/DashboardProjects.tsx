import { Autocomplete, Grid, Stack, TextField, Typography } from "@mui/material";
import BreadcrumbBar from "../components/header/BreadcrumbBar";
import * as Paths from "../Paths";
import { useContext, useEffect, useState } from "react";
import ConfigContext from "../context/ConfigContext";
import { useTranslation } from "react-i18next";
import useCustomSearchParams from "../hooks/useCustomSearchParams";
import { Project } from "../api/projectsServices";
import { useNavigate } from "react-router-dom";
import { getDashboardProjects } from "../api/dashboardServices";
import { ChartType } from "./DashboardProject";
import { DashboardPieChart } from "../components/dashboard/PieChart";
import { chartColors } from "../utils/dashboardChartColors";
type DashboardProjects = {
    physicalAppearance: ChartType[];
    targetGroup: ChartType[];
};
export const DashboardProjects = () => {
    const [selectedProject, setSelectedProject] = useState<Project | null>(null);
    const [dashboardProjects, setDashboardProjects] = useState<DashboardProjects>();

    const { municipalityName } = useContext(ConfigContext);
    const { rows } = useCustomSearchParams(undefined, undefined, { page: 1, pageSize: 10000 });
    const { t } = useTranslation();
    const navigate = useNavigate();

    const handleSelectProject = (project: Project | null) => {
        setSelectedProject(project);
        navigate(Paths.dashboardProject.toPath({ projectId: project?.projectId || "" }));
    };

    useEffect(() => {
        getDashboardProjects().then((data) => {
            const convertedPhysicalAppearance = data.physicalAppearance.map((d) => {
                return { label: d.name, value: d.amount };
            });
            const convertedTargetGroup = data.targetGroup.map((d) => {
                return { label: d.name, value: d.amount };
            });
            setDashboardProjects({ physicalAppearance: convertedPhysicalAppearance, targetGroup: convertedTargetGroup });
        });
    }, []);

    return (
        <Stack>
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
            <Grid container border="solid 1px #DDD">
                <Grid item xs={12} md={6}>
                    <Typography variant="h6" fontSize={16}>
                        {t("dashboard.buy")}
                    </Typography>
                    {/* ToDo:Add chart here later */}
                </Grid>
                <Grid item xs={12} md={6}>
                    <Typography variant="h6" fontSize={16}>
                        {t("dashboard.rent")}
                    </Typography>
                    {/* ToDo:Add chart here later */}
                </Grid>
                <Grid item xs={12} md={6}>
                    <Typography variant="h6" fontSize={16}>
                        {t("dashboard.residentialProjects")}
                    </Typography>
                    {/* ToDo:Add chart here later */}
                </Grid>
                <Grid item xs={12} md={6}>
                    <Typography variant="h6" fontSize={16}>
                        {t("dashboard.deliverables")}
                    </Typography>
                    {/* ToDo:Add chart here later */}
                </Grid>
                <Grid item xs={12} md={6}>
                    <Typography variant="h6" fontSize={16}>
                        {t("dashboard.delayedProjects")}
                    </Typography>
                </Grid>
                <Grid item xs={12} md={6}>
                    <Typography variant="h6" fontSize={16}>
                        {t("dashboard.residentialFeautures")}
                    </Typography>
                    <DashboardPieChart chartData={dashboardProjects?.physicalAppearance || []} colors={chartColors} />
                </Grid>
                <Grid item xs={12} md={6}>
                    <Typography variant="h6" fontSize={16}>
                        {t("dashboard.permitsGranted")}
                    </Typography>
                    {/* ToDo:Add chart here later */}
                </Grid>
                <Grid item xs={12} md={6}>
                    <Typography variant="h6" fontSize={16}>
                        {t("dashboard.targetAudiences")}
                    </Typography>
                    <DashboardPieChart chartData={dashboardProjects?.targetGroup || []} colors={chartColors} />
                </Grid>
            </Grid>
        </Stack>
    );
};
