import { Autocomplete, Grid, Stack, TextField, Typography } from "@mui/material";
import BreadcrumbBar from "../components/header/BreadcrumbBar";
import * as Paths from "../Paths";
import { useContext, useEffect, useState } from "react";
import ConfigContext from "../context/ConfigContext";
import { useTranslation } from "react-i18next";
import useCustomSearchParams from "../hooks/useCustomSearchParams";
import { Project, getProjects } from "../api/projectsServices";
import { useNavigate } from "react-router-dom";
import { getDashboardProjects } from "../api/dashboardServices";
import { ChartType } from "./DashboardProject";
import { DashboardPieChart } from "../components/dashboard/PieChart";
import { chartColors } from "../utils/dashboardChartColors";
import { getProjectHouseBlocksWithCustomProperties } from "../api/houseBlockServices";
import { MutationCard } from "../components/dashboard/MutationCard";
type DashboardProjects = {
    physicalAppearance: ChartType[];
    targetGroup: ChartType[];
};
type MutationValues = {
    [key: string]: number;
};
export const DashboardProjects = () => {
    const [selectedProject, setSelectedProject] = useState<Project | null>(null);
    const [dashboardProjects, setDashboardProjects] = useState<DashboardProjects>();
    const [projectPhaseSums, setProjectPhaseSums] = useState([]);
    const [dashboardMutationValues, setDashboardMutationValues] = useState<MutationValues>();

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

    //This is for calculate the number of projects in each phase in ui update it later with endpoint
    useEffect(() => {
        getProjects(1, 1000).then((projects) => {
            const phaseCounts = projects.reduce((acc, project) => {
                const { projectPhase } = project;
                //@ts-expect-error type error
                if (!acc[projectPhase]) {
                    //@ts-expect-error type error
                    acc[projectPhase] = 0;
                }
                //@ts-expect-error type error
                acc[projectPhase] += 1;
                return acc;
            }, {});

            const phaseCountsArray = Object.entries(phaseCounts).map(([label, value]) => ({
                label: t(`dashboard.projectPhaseOptions.${label}`),
                value,
            }));
            //@ts-expect-error type error
            setProjectPhaseSums(phaseCountsArray);
        });
    }, [t]);

    //This is for calculate the number of mutations in each kind in ui update it later with endpoint
    useEffect(() => {
        getProjects(1, 1000).then(async (projects) => {
            const allHouseBlocks = await Promise.all(
                projects.map(async (project) => {
                    const houseBlocks = await getProjectHouseBlocksWithCustomProperties(project.projectId);
                    return houseBlocks;
                }),
            );

            const flattenedHouseBlocks = allHouseBlocks.flat();

            const list = flattenedHouseBlocks.reduce((acc, houseBlock) => {
                const kind = houseBlock.mutation.kind;
                const amount = houseBlock.mutation.amount;
                //@ts-expect-error type error
                if (acc[kind]) {
                    //@ts-expect-error type error
                    acc[kind] += amount;
                } else {
                    //@ts-expect-error type error
                    acc[kind] = amount;
                }
                return acc;
            }, {});
            setDashboardMutationValues(list);
        });
    }, [t]);

    const chartCardStyling = { backgroundColor: "#F0F0F0", my: 1, p: 2, xs: 12, md: 5.9 };

    return (
        <Stack mb={8}>
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
            <Grid container border="solid 1px #DDD" justifyContent="space-around" p={1}>
                <Grid item xs={12}>
                    <MutationCard demolitionAmount={dashboardMutationValues?.DEMOLITION ?? 0} constructionAmount={dashboardMutationValues?.CONSTRUCTION ?? 0} />
                </Grid>
                <Grid item {...chartCardStyling}>
                    <Typography variant="h6" fontSize={16}>
                        {t("dashboard.projectPhases")}
                    </Typography>
                    <DashboardPieChart chartData={projectPhaseSums || []} colors={chartColors} />
                </Grid>
                <Grid item {...chartCardStyling}>
                    <Typography variant="h6" fontSize={16}>
                        {t("dashboard.targetAudiences")}
                    </Typography>
                    <DashboardPieChart chartData={dashboardProjects?.targetGroup || []} colors={chartColors} />
                </Grid>
                <Grid item {...chartCardStyling}>
                    <Typography variant="h6" fontSize={16}>
                        {t("dashboard.residentialFeautures")}
                    </Typography>
                    <DashboardPieChart chartData={dashboardProjects?.physicalAppearance || []} colors={chartColors} />
                </Grid>
                <Grid item {...chartCardStyling}>
                    <Typography variant="h6" fontSize={16}>
                        {t("dashboard.buy")}
                    </Typography>
                    {/* ToDo:Add chart here later */}
                </Grid>
                <Grid item {...chartCardStyling}>
                    <Typography variant="h6" fontSize={16}>
                        {t("dashboard.rent")}
                    </Typography>
                    {/* ToDo:Add chart here later */}
                </Grid>
                <Grid item {...chartCardStyling}>
                    <Typography variant="h6" fontSize={16}>
                        {t("dashboard.residentialProjects")}
                    </Typography>
                    {/* ToDo:Add chart here later */}
                </Grid>
                <Grid item {...chartCardStyling}>
                    <Typography variant="h6" fontSize={16}>
                        {t("dashboard.deliverables")}
                    </Typography>
                    {/* ToDo:Add chart here later */}
                </Grid>
                <Grid item {...chartCardStyling}>
                    <Typography variant="h6" fontSize={16}>
                        {t("dashboard.delayedProjects")}
                    </Typography>
                </Grid>
            </Grid>
        </Stack>
    );
};
