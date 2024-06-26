import { Grid, Stack, Typography } from "@mui/material";
import BreadcrumbBar from "../components/header/BreadcrumbBar";
import * as Paths from "../Paths";
import { useContext } from "react";
import { useTranslation } from "react-i18next";
import ProjectContext from "../context/ProjectContext";
import { useParams } from "react-router-dom";
import { CharacteristicTable } from "../components/dashboard/CharacteristicTable";

export const DashboardProject = () => {
    const { t } = useTranslation();
    const { selectedProject } = useContext(ProjectContext);
    const { projectId } = useParams();
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
                    <CharacteristicTable />
                </Grid>
                <Grid item xs={12} md={6}>
                    <Typography variant="h6" fontSize={16}>
                        {t("dashboard.priceSegmentsPurchase")}
                    </Typography>
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
