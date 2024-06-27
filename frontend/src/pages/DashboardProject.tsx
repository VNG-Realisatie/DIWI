import { Grid, Stack, Typography } from "@mui/material";
import BreadcrumbBar from "../components/header/BreadcrumbBar";
import * as Paths from "../Paths";
import { useContext, useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import ProjectContext from "../context/ProjectContext";
import { useParams } from "react-router-dom";
import { CharacteristicTable } from "../components/dashboard/CharacteristicTable";
import { DashboardPieChart } from "../components/dashboard/PieChart";
import { getDashboardProject } from "../api/dashboardServices";
import { chartColors } from "../utils/dashboardChartColors";

export type ChartType = {
    label: string;
    value: number;
};

export const DashboardProject = () => {
    const { t } = useTranslation();
    const { selectedProject } = useContext(ProjectContext);
    const { projectId } = useParams();

    const titleStyling = { fontWeight: "bold", fontSize: 16, my: 1 };
    const chartCardStyling = { backgroundColor: "#F0F0F0", m: 1, p: 2, xs: 12, md: 5.8 };

    const [physicalAppearance, setPhysicalAppearance] = useState<ChartType[]>([]);

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
        <Stack flexDirection="column" width="100%" spacing={2} mb={10}>
            <BreadcrumbBar
                pageTitle={t("dashboard.projectTitle")}
                links={[
                    { title: `${t("dashboard.title")}`, link: Paths.dashboard.path },
                    { title: `${selectedProject?.projectName}`, link: Paths.dashboardProject.toPath({ projectId: projectId || "" }) },
                ]}
            />
            <Grid width="100%" container border="solid 1px #DDD" justifyContent="space-around">
                <Grid item {...chartCardStyling}>
                    <Typography sx={titleStyling}>{t("dashboard.characteristics")}</Typography>
                    <CharacteristicTable />
                </Grid>
                <Grid item {...chartCardStyling}>
                    <Typography sx={titleStyling}>{t("dashboard.priceSegmentsPurchase")}</Typography>

                    {/* ToDo:Add chart here later */}
                </Grid>
                <Grid item {...chartCardStyling}>
                    <Typography sx={titleStyling}>{t("dashboard.priceSegmentsRent")}</Typography>
                    {/* ToDo:Add chart here later */}
                </Grid>
                <Grid item {...chartCardStyling}>
                    <Typography sx={titleStyling}>{t("dashboard.residentialProjects")}%</Typography>
                    <DashboardPieChart chartData={physicalAppearance} colors={chartColors} />
                </Grid>
                <Grid item {...chartCardStyling}>
                    <Typography sx={titleStyling}>{t("dashboard.schedule")}</Typography>
                </Grid>
                <Grid item {...chartCardStyling}>
                    <Typography sx={titleStyling}>{t("dashboard.upcomingMileStones")}</Typography>
                    {/* ToDo:Add chart here later */}
                </Grid>
            </Grid>
        </Stack>
    );
};
