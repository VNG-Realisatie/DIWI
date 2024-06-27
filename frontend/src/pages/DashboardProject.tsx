import { Grid, Stack, Typography } from "@mui/material";
import BreadcrumbBar from "../components/header/BreadcrumbBar";
import * as Paths from "../Paths";
import { useContext, useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import ProjectContext from "../context/ProjectContext";
import { useParams } from "react-router-dom";
import { DashboardPieChart } from "../components/dashboard/PieChart";
import { getDashboardProject } from "../api/dashboardServices";

export type ChartType = {
    label: string;
    value: number;
};

export const DashboardProject = () => {
    const { t } = useTranslation();
    const { selectedProject } = useContext(ProjectContext);
    const { projectId } = useParams();
    const [physicalAppearance, setPhysicalAppearance] = useState<ChartType[]>([]);

    const physicalAppearanceColor = ["#0D3B66", "#145DA0", "#1E7AC9", "#2A9DF4", "#63B2F5", "#8CC6F5", "#B5DAF7", "#D6EAF8", "#E9F5FB", "#F7FBFD"];

    useEffect(() => {
        if (projectId) {
            getDashboardProject(projectId).then((data) => {
                const convertedData = data.physicalAppearance.map((d) => {
                    return { label: d.name, value: d.amount };
                });
                setPhysicalAppearance(convertedData);
            });
        }
    }, [projectId]);
    return (
        <Stack>
            <BreadcrumbBar
                pageTitle={t("dashboard.projectTitle")}
                links={[
                    { title: `${t("dashboard.title")}`, link: Paths.dashboard.path },
                    { title: `${selectedProject?.projectName}`, link: Paths.dashboardProject.toPath({ projectId: projectId || "" }) },
                ]}
            />
            <Grid container border="solid 1px #DDD">
                <Grid item xs={12} md={6}>
                    <Typography variant="h6" fontSize={16}>
                        {t("dashboard.characteristics")}
                    </Typography>
                    {/* ToDo:Add chart here later */}
                </Grid>
                <Grid item xs={12} md={6}>
                    <Typography variant="h6" fontSize={16}>
                        {t("dashboard.priceSegmentsPurchase")}
                    </Typography>
                    <DashboardPieChart chartData={physicalAppearance} colors={physicalAppearanceColor} />
                    {/* ToDo:Add chart here later */}
                </Grid>
                <Grid item xs={12} md={6}>
                    <Typography variant="h6" fontSize={16}>
                        {t("dashboard.priceSegmentsRent")}
                    </Typography>
                    {/* ToDo:Add chart here later */}
                </Grid>
                <Grid item xs={12} md={6}>
                    <Typography variant="h6" fontSize={16}>
                        {t("dashboard.residentialProjects")}%
                    </Typography>
                    {/* ToDo:Add chart here later */}
                </Grid>
                <Grid item xs={12} md={6}>
                    <Typography variant="h6" fontSize={16}>
                        {t("dashboard.schedule")}
                    </Typography>
                </Grid>
                <Grid item xs={12} md={6}>
                    <Typography variant="h6" fontSize={16}>
                        {t("dashboard.upcomingMileStones")}
                    </Typography>
                    {/* ToDo:Add chart here later */}
                </Grid>
            </Grid>
        </Stack>
    );
};
