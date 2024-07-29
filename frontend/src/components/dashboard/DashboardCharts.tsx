import { Box, Grid, Switch, Typography } from "@mui/material";
import { DashboardPieChart } from "./PieChart";
import { Dispatch, SetStateAction, useEffect, useState } from "react";
import { ChartType } from "../../pages/DashboardProject";
import { MutationCard } from "./MutationCard";
import ProjectOverviewMap from "../map/ProjectOverviewMap";
import { getDashboardProjects, VisibilityElement } from "../../api/dashboardServices";
import { getProjects } from "../../api/projectsServices";
import { getProjectHouseBlocksWithCustomProperties } from "../../api/houseBlockServices";
import { useTranslation } from "react-i18next";
import { chartColors } from "../../utils/dashboardChartColors";
import useAllowedActions from "../../hooks/useAllowedActions";

const chartCardStyling = { backgroundColor: "#F0F0F0", my: 1, p: 2, xs: 12, md: 5.9 };

type DashboardProjects = {
    physicalAppearance: ChartType[];
    targetGroup: ChartType[];
};

type MutationValues = {
    [key: string]: number;
};

export type Visibility = {
    MUTATION: boolean;
    PROJECT_PHASE: boolean;
    TARGET_GROUP: boolean;
    PHYSICAL_APPEARANCE: boolean;
    OWNERSHIP_BUY: boolean;
    OWNERSHIP_RENT: boolean;
    PROJECT_MAP: boolean;
    RESIDENTIAL_PROJECTS: boolean;
    DELIVERABLES: boolean;
    DELAYED_PROJECTS: boolean;
};
type Props = {
    visibility?: Visibility;
    setVisibility?: Dispatch<SetStateAction<Visibility>>;
};
export const DashboardCharts = ({ visibility, setVisibility }: Props) => {
    const [dashboardProjects, setDashboardProjects] = useState<DashboardProjects>();
    const [projectPhaseSums, setProjectPhaseSums] = useState([]);
    const [dashboardMutationValues, setDashboardMutationValues] = useState<MutationValues>();
    const { allowedActions } = useAllowedActions();

    const handleToggleVisibility = (item: VisibilityElement) => {
        if (!setVisibility) return;

        setVisibility((prev) => ({ ...prev, [item]: !prev[item] }));
    };

    const { t } = useTranslation();
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
    return (
        <Grid container border="solid 1px #DDD" justifyContent="space-around" p={1}>
            {(allowedActions.includes("VIEW_ALL_BLUEPRINTS") || visibility?.MUTATION) && (
                <Grid item xs={12}>
                    <MutationCard
                        handleToggle={() => handleToggleVisibility("MUTATION")}
                        visibility={visibility}
                        demolitionAmount={dashboardMutationValues?.DEMOLITION ?? 0}
                        constructionAmount={dashboardMutationValues?.CONSTRUCTION ?? 0}
                    />
                </Grid>
            )}
            {(allowedActions.includes("VIEW_ALL_BLUEPRINTS") || visibility?.PROJECT_PHASE) && (
                <Grid item {...chartCardStyling}>
                    <Box display="flex" justifyContent="space-between" alignItems="center">
                        <Typography variant="h6" fontSize={16}>
                            {t("dashboard.projectPhases")}
                        </Typography>
                        {visibility && (
                            <Switch
                                checked={visibility.PROJECT_PHASE}
                                onChange={() => handleToggleVisibility("PROJECT_PHASE")}
                                inputProps={{ "aria-label": "controlled" }}
                            />
                        )}
                    </Box>
                    <DashboardPieChart chartData={projectPhaseSums || []} colors={chartColors} />
                </Grid>
            )}
            {(allowedActions.includes("VIEW_ALL_BLUEPRINTS") || visibility?.TARGET_GROUP) && (
                <Grid item {...chartCardStyling}>
                    <Box display="flex" justifyContent="space-between" alignItems="center">
                        <Typography variant="h6" fontSize={16}>
                            {t("dashboard.targetAudiences")}
                        </Typography>
                        {visibility && (
                            <Switch
                                checked={visibility.TARGET_GROUP}
                                onChange={() => handleToggleVisibility("TARGET_GROUP")}
                                inputProps={{ "aria-label": "controlled" }}
                            />
                        )}
                    </Box>
                    <DashboardPieChart chartData={dashboardProjects?.targetGroup || []} colors={chartColors} />
                </Grid>
            )}
            {(allowedActions.includes("VIEW_ALL_BLUEPRINTS") || visibility?.PHYSICAL_APPEARANCE) && (
                <Grid item {...chartCardStyling}>
                    <Box display="flex" justifyContent="space-between" alignItems="center">
                        <Typography variant="h6" fontSize={16}>
                            {t("dashboard.residentialFeatures")}
                        </Typography>
                        {visibility && (
                            <Switch
                                checked={visibility.PHYSICAL_APPEARANCE}
                                onChange={() => handleToggleVisibility("PHYSICAL_APPEARANCE")}
                                inputProps={{ "aria-label": "controlled" }}
                            />
                        )}
                    </Box>
                    <DashboardPieChart chartData={dashboardProjects?.physicalAppearance || []} colors={chartColors} />
                </Grid>
            )}
            {(allowedActions.includes("VIEW_ALL_BLUEPRINTS") || visibility?.OWNERSHIP_BUY) && (
                <Grid item {...chartCardStyling}>
                    <Box display="flex" justifyContent="space-between" alignItems="center">
                        <Typography variant="h6" fontSize={16}>
                            {t("dashboard.buy")}
                        </Typography>
                        {visibility && (
                            <Switch
                                checked={visibility.OWNERSHIP_BUY}
                                onChange={() => handleToggleVisibility("OWNERSHIP_BUY")}
                                inputProps={{ "aria-label": "controlled" }}
                            />
                        )}
                    </Box>
                </Grid>
            )}
            {(allowedActions.includes("VIEW_ALL_BLUEPRINTS") || visibility?.OWNERSHIP_RENT) && (
                <Grid item {...chartCardStyling}>
                    <Box display="flex" justifyContent="space-between" alignItems="center">
                        <Typography variant="h6" fontSize={16}>
                            {t("dashboard.rent")}
                        </Typography>
                        {visibility && (
                            <Switch
                                checked={visibility.OWNERSHIP_RENT}
                                onChange={() => handleToggleVisibility("OWNERSHIP_RENT")}
                                inputProps={{ "aria-label": "controlled" }}
                            />
                        )}
                    </Box>
                </Grid>
            )}
            {(allowedActions.includes("VIEW_ALL_BLUEPRINTS") || visibility?.RESIDENTIAL_PROJECTS) && (
                <Grid item {...chartCardStyling}>
                    <Box display="flex" justifyContent="space-between" alignItems="center">
                        <Typography variant="h6" fontSize={16}>
                            {t("dashboard.residentialProjects")}
                        </Typography>
                        {visibility && (
                            <Switch
                                checked={visibility.RESIDENTIAL_PROJECTS}
                                onChange={() => handleToggleVisibility("RESIDENTIAL_PROJECTS")}
                                inputProps={{ "aria-label": "controlled" }}
                            />
                        )}
                    </Box>
                </Grid>
            )}
            {(allowedActions.includes("VIEW_ALL_BLUEPRINTS") || visibility?.DELIVERABLES) && (
                <Grid item {...chartCardStyling}>
                    <Box display="flex" justifyContent="space-between" alignItems="center">
                        <Typography variant="h6" fontSize={16}>
                            {t("dashboard.deliverables")}
                        </Typography>
                        {visibility && (
                            <Switch
                                checked={visibility.DELIVERABLES}
                                onChange={() => handleToggleVisibility("DELIVERABLES")}
                                inputProps={{ "aria-label": "controlled" }}
                            />
                        )}
                    </Box>
                </Grid>
            )}
            {(allowedActions.includes("VIEW_ALL_BLUEPRINTS") || visibility?.DELAYED_PROJECTS) && (
                <Grid item {...chartCardStyling}>
                    <Box display="flex" justifyContent="space-between" alignItems="center">
                        <Typography variant="h6" fontSize={16}>
                            {t("dashboard.delayedProjects")}
                        </Typography>
                        {visibility && (
                            <Switch
                                checked={visibility.DELAYED_PROJECTS}
                                onChange={() => handleToggleVisibility("DELAYED_PROJECTS")}
                                inputProps={{ "aria-label": "controlled" }}
                            />
                        )}
                    </Box>
                </Grid>
            )}
            {(allowedActions.includes("VIEW_ALL_BLUEPRINTS") || visibility?.PROJECT_MAP) && (
                <Grid item {...chartCardStyling}>
                    <Box display="flex" justifyContent="space-between" alignItems="center">
                        <Typography variant="h6" fontSize={16}>
                            {t("dashboard.projects")}
                        </Typography>
                        {visibility && (
                            <Switch
                                checked={visibility.PROJECT_MAP}
                                onChange={() => handleToggleVisibility("PROJECT_MAP")}
                                inputProps={{ "aria-label": "controlled" }}
                            />
                        )}
                    </Box>
                    <ProjectOverviewMap isDashboardMap={true} />
                </Grid>
            )}
        </Grid>
    );
};
