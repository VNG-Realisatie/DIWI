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

    const titleStyling = { fontWeight: "bold", fontSize: 16, my: 1 };

    return (
        <Stack flexDirection="column" width="100%" spacing={2}>
            <BreadcrumbBar
                pageTitle={t("dashboard.projectTitle")}
                links={[
                    { title: `${t("dashboard.title")}`, link: Paths.dashboard.path },
                    { title: `${selectedProject?.projectName}`, link: Paths.dashboardProject.toPath({ projectId: projectId || "" }) },
                ]}
            />
            <Grid container border="solid 1px #DDD" rowSpacing={2} columnSpacing={4} width="100%">
                <Grid item xs={12} md={6}>
                    <Typography sx={titleStyling}>{t("dashboard.characteristics")}</Typography>
                    <CharacteristicTable />
                </Grid>
                <Grid item xs={12} md={6}>
                    <Typography sx={titleStyling}>{t("dashboard.priceSegmentsPurchase")}</Typography>
                    {/* ToDo:Add chart here later */}
                </Grid>
                <Grid item xs={12} md={6}>
                    <Typography sx={titleStyling}>{t("dashboard.priceSegmentsRent")}</Typography>
                    {/* ToDo:Add chart here later */}
                </Grid>
                <Grid item xs={12} md={6}>
                    <Typography sx={titleStyling}>{t("dashboard.residentialProjects")}%</Typography>
                    {/* ToDo:Add chart here later */}
                </Grid>
                <Grid item xs={12} md={6}>
                    <Typography sx={titleStyling}>{t("dashboard.schedule")}</Typography>
                </Grid>
                <Grid item xs={12} md={6}>
                    <Typography sx={titleStyling}>{t("dashboard.upcomingMileStones")}</Typography>
                    {/* ToDo:Add chart here later */}
                </Grid>
            </Grid>
        </Stack>
    );
};
