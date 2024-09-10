import { Box, CircularProgress, Grid, Stack, Switch, Typography } from "@mui/material";
import { DashboardPieChart } from "./PieChart";
import { Dispatch, SetStateAction, useEffect, useState } from "react";
import { ChartType } from "../../pages/DashboardProject";
import { MutationCard } from "./MutationCard";
import ProjectOverviewMap from "../map/ProjectOverviewMap";
import { getDashboardProjects, Planning, PolicyGoal, VisibilityElement } from "../../api/dashboardServices";
import { getProjects } from "../../api/projectsServices";
import { getProjectHouseBlocksWithCustomProperties } from "../../api/houseBlockServices";
import { useTranslation } from "react-i18next";
import useAllowedActions from "../../hooks/useAllowedActions";
import { LabelComponent } from "../project/LabelComponent";
import { CellContainer } from "../project/project-with-house-block/CellContainer";
import { MyResponsiveBar } from "./BarChart";
import { PolicyGoalChart } from "./PolicyGoalChart";
import { GoalDirection, GoalType } from "../../api/goalsServices";

const chartCardStyling = { backgroundColor: "#F0F0F0", my: 1, p: 2, xs: 12, md: 5.9 };

type DashboardProjects = {
    physicalAppearance: ChartType[];
    targetGroup: ChartType[];
    priceCategoryOwn: ChartType[];
    priceCategoryRent: ChartType[];
    planning: Planning[];
    policyGoals: PolicyGoal[];
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
    DELIVERABLES: boolean;
    DELAYED_PROJECTS: boolean;
};
type Props = {
    visibility?: Visibility;
    setVisibility?: Dispatch<SetStateAction<Visibility>>;
    isPdf?: boolean;
    isPrintingFullDashboard: boolean;
};

export const DashboardCharts = ({ visibility, setVisibility, isPrintingFullDashboard, isPdf = false }: Props) => {
    const [dashboardProjects, setDashboardProjects] = useState<DashboardProjects>();
    const [projectPhaseSums, setProjectPhaseSums] = useState([]);
    const [dashboardMutationValues, setDashboardMutationValues] = useState<MutationValues>();
    const { allowedActions } = useAllowedActions();

    const handleToggleVisibility = (item: VisibilityElement) => {
        if (!setVisibility) return;

        setVisibility((prev) => ({ ...prev, [item]: !prev[item] }));
    };

    const dummyPolicyGoals = [
        {
            name: "Houtbouw",
            category: "1 category",
            id: "1",
            goal: 100,
            amount: 144,
            percentage: 0,
            totalAmount: 0,
            goalDirection: "MAXIMAL" as GoalDirection,
            goalType: "NUMBER" as GoalType,
        },
        {
            name: "Betonbouw",
            category: "1 category",
            id: "2",
            goal: 50,
            amount: 32,
            percentage: 0,
            totalAmount: 0,
            goalDirection: "MAXIMAL" as GoalDirection,
            goalType: "NUMBER" as GoalType,
        },
        {
            name: "Staalbouw",
            category: "1 category",
            id: "3",
            goal: 70,
            amount: 70,
            percentage: 0,
            totalAmount: 0,
            goalDirection: "MAXIMAL" as GoalDirection,
            goalType: "NUMBER" as GoalType,
        },
        {
            name: "Houtbouw Percentage",
            category: "2 category",
            id: "4",
            goal: 50,
            amount: 0,
            percentage: 22,
            totalAmount: 100,
            goalDirection: "MAXIMAL" as GoalDirection,
            goalType: "PERCENTAGE" as GoalType,
        },
        {
            name: "Betonbouw Percentage",
            category: "1 category",
            id: "5",
            goal: 50,
            amount: 0,
            percentage: 78,
            totalAmount: 100,
            goalDirection: "MAXIMAL" as GoalDirection,
            goalType: "PERCENTAGE" as GoalType,
        },
        {
            name: "Staalbouw Percentage",
            category: "3 category",
            id: "6",
            goal: 50,
            amount: 0,
            percentage: 10,
            totalAmount: 100,
            goalDirection: "MINIMAL" as GoalDirection,
            goalType: "PERCENTAGE" as GoalType,
        },
        {
            name: "Staalbouw Percentage333",
            category: "3 category",
            id: "8",
            goal: 50,
            amount: 0,
            percentage: 99,
            totalAmount: 100,
            goalDirection: "MINIMAL" as GoalDirection,
            goalType: "PERCENTAGE" as GoalType,
        },
        {
            name: "Staalbouw Percentage333",
            category: "3 category",
            id: "9",
            goal: 50,
            amount: 0,
            percentage: 99,
            totalAmount: 100,
            goalDirection: "MINIMAL" as GoalDirection,
            goalType: "PERCENTAGE" as GoalType,
        },
        {
            name: "Staalbouw Percentage333",
            category: "3 category",
            id: "10",
            goal: 50,
            amount: 0,
            percentage: 99,
            totalAmount: 100,
            goalDirection: "MINIMAL" as GoalDirection,
            goalType: "PERCENTAGE" as GoalType,
        },
        {
            name: "Staalbouw Percentage333",
            category: "3 category",
            id: "11",
            goal: 50,
            amount: 0,
            percentage: 99,
            totalAmount: 100,
            goalDirection: "MINIMAL" as GoalDirection,
            goalType: "PERCENTAGE" as GoalType,
        },
        {
            name: "Staalbouw Percentage333",
            category: "3 category",
            id: "12",
            goal: 50,
            amount: 0,
            percentage: 99,
            totalAmount: 100,
            goalDirection: "MINIMAL" as GoalDirection,
            goalType: "PERCENTAGE" as GoalType,
        },
        {
            name: "Staalbouw Percentage333",
            category: "3 category",
            id: "13",
            goal: 50,
            amount: 0,
            percentage: 99,
            totalAmount: 100,
            goalDirection: "MINIMAL" as GoalDirection,
            goalType: "PERCENTAGE" as GoalType,
        },
    ];

    const { t } = useTranslation();
    useEffect(() => {
        getDashboardProjects().then((data) => {
            const convertedPhysicalAppearance = data.physicalAppearance.map((d) => {
                return { label: d.name, value: d.amount };
            });
            const convertedTargetGroup = data.targetGroup.map((d) => {
                return { label: d.name, value: d.amount };
            });
            const convertedPriceCategoryOwn = data.priceCategoryOwn.map((d) => {
                return {
                    label: `${d.name}\n(€${d.min} ${d.max ? `- €${d.max}` : t("generic.andMore")})`,
                    value: d.amount,
                };
            });
            const convertedPriceCategoryRent = data.priceCategoryRent.map((d) => {
                return {
                    label: `${d.name}\n(€${d.min} ${d.max ? `- €${d.max}` : t("generic.andMore")})`,
                    value: d.amount,
                };
            });
            setDashboardProjects({
                physicalAppearance: convertedPhysicalAppearance,
                targetGroup: convertedTargetGroup,
                priceCategoryOwn: convertedPriceCategoryOwn,
                priceCategoryRent: convertedPriceCategoryRent,
                planning: data.planning,
                policyGoals: dummyPolicyGoals,
            });
        });
    }, [t, dummyPolicyGoals]);

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
        <>
            {!isPdf && (
                <>
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
                                    {visibility && allowedActions.includes("EDIT_ALL_BLUEPRINTS") && (
                                        <Switch
                                            checked={visibility.PROJECT_PHASE}
                                            onChange={() => handleToggleVisibility("PROJECT_PHASE")}
                                            inputProps={{ "aria-label": "controlled" }}
                                        />
                                    )}
                                </Box>
                                <DashboardPieChart chartData={projectPhaseSums || []} />
                            </Grid>
                        )}
                        {(allowedActions.includes("VIEW_ALL_BLUEPRINTS") || visibility?.TARGET_GROUP) && (
                            <Grid item {...chartCardStyling}>
                                <Box display="flex" justifyContent="space-between" alignItems="center">
                                    <Typography variant="h6" fontSize={16}>
                                        {t("dashboard.targetAudiences")}
                                    </Typography>
                                    {visibility && allowedActions.includes("EDIT_ALL_BLUEPRINTS") && (
                                        <Switch
                                            checked={visibility.TARGET_GROUP}
                                            onChange={() => handleToggleVisibility("TARGET_GROUP")}
                                            inputProps={{ "aria-label": "controlled" }}
                                        />
                                    )}
                                </Box>
                                <DashboardPieChart chartData={dashboardProjects?.targetGroup || []} />
                            </Grid>
                        )}
                        {(allowedActions.includes("VIEW_ALL_BLUEPRINTS") || visibility?.PHYSICAL_APPEARANCE) && (
                            <Grid item {...chartCardStyling}>
                                <Box display="flex" justifyContent="space-between" alignItems="center">
                                    <Typography variant="h6" fontSize={16}>
                                        {t("dashboard.residentialFeatures")}
                                    </Typography>
                                    {visibility && allowedActions.includes("EDIT_ALL_BLUEPRINTS") && (
                                        <Switch
                                            checked={visibility.PHYSICAL_APPEARANCE}
                                            onChange={() => handleToggleVisibility("PHYSICAL_APPEARANCE")}
                                            inputProps={{ "aria-label": "controlled" }}
                                        />
                                    )}
                                </Box>
                                <DashboardPieChart chartData={dashboardProjects?.physicalAppearance || []} />
                            </Grid>
                        )}
                        {(allowedActions.includes("VIEW_ALL_BLUEPRINTS") || visibility?.OWNERSHIP_BUY) && (
                            <Grid item {...chartCardStyling}>
                                <Box display="flex" justifyContent="space-between" alignItems="center">
                                    <Typography variant="h6" fontSize={16}>
                                        {t("dashboard.buy")}
                                    </Typography>
                                    {visibility && allowedActions.includes("EDIT_ALL_BLUEPRINTS") && (
                                        <Switch
                                            checked={visibility.OWNERSHIP_BUY}
                                            onChange={() => handleToggleVisibility("OWNERSHIP_BUY")}
                                            inputProps={{ "aria-label": "controlled" }}
                                        />
                                    )}
                                </Box>
                                <DashboardPieChart chartData={dashboardProjects?.priceCategoryOwn || []} />
                            </Grid>
                        )}
                        {(allowedActions.includes("VIEW_ALL_BLUEPRINTS") || visibility?.OWNERSHIP_RENT) && (
                            <Grid item {...chartCardStyling}>
                                <Box display="flex" justifyContent="space-between" alignItems="center">
                                    <Typography variant="h6" fontSize={16}>
                                        {t("dashboard.rent")}
                                    </Typography>
                                    {visibility && allowedActions.includes("EDIT_ALL_BLUEPRINTS") && (
                                        <Switch
                                            checked={visibility.OWNERSHIP_RENT}
                                            onChange={() => handleToggleVisibility("OWNERSHIP_RENT")}
                                            inputProps={{ "aria-label": "controlled" }}
                                        />
                                    )}
                                </Box>
                                <DashboardPieChart chartData={dashboardProjects?.priceCategoryRent || []} />
                            </Grid>
                        )}
                        {(allowedActions.includes("VIEW_ALL_BLUEPRINTS") || visibility?.DELIVERABLES) && (
                            <Grid item {...chartCardStyling}>
                                <Box display="flex" justifyContent="space-between" alignItems="center">
                                    <Typography variant="h6" fontSize={16}>
                                        {t("dashboard.deliverables")}
                                    </Typography>
                                    {visibility && allowedActions.includes("EDIT_ALL_BLUEPRINTS") && (
                                        <Switch
                                            checked={visibility.DELIVERABLES}
                                            onChange={() => handleToggleVisibility("DELIVERABLES")}
                                            inputProps={{ "aria-label": "controlled" }}
                                        />
                                    )}
                                </Box>
                                <MyResponsiveBar chartData={dashboardProjects?.planning || []} selectedProject={null} />
                            </Grid>
                        )}
                        {(allowedActions.includes("VIEW_ALL_BLUEPRINTS") || visibility?.DELAYED_PROJECTS) && (
                            <Grid item {...chartCardStyling}>
                                <Box display="flex" justifyContent="space-between" alignItems="center">
                                    <Typography variant="h6" fontSize={16}>
                                        {t("dashboard.delayedProjects")}
                                    </Typography>
                                    {visibility && allowedActions.includes("EDIT_ALL_BLUEPRINTS") && (
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
                                    {visibility && allowedActions.includes("EDIT_ALL_BLUEPRINTS") && (
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
                    {(allowedActions.includes("VIEW_ALL_BLUEPRINTS") || visibility?.DELAYED_PROJECTS) && (
                        <Grid item {...chartCardStyling}>
                            <Box display="flex" justifyContent="space-between" alignItems="center">
                                <Typography variant="h6" fontSize={16}></Typography>
                                {visibility && allowedActions.includes("EDIT_ALL_BLUEPRINTS") && (
                                    // visibility needs to be updated
                                    <Switch
                                        checked={visibility.DELAYED_PROJECTS}
                                        onChange={() => handleToggleVisibility("DELAYED_PROJECTS")}
                                        inputProps={{ "aria-label": "controlled" }}
                                    />
                                )}
                            </Box>
                            {dashboardProjects &&
                                (() => {
                                    const categories = Array.from(new Set(dashboardProjects.policyGoals.map((goal) => goal.category))).sort();

                                    return categories.map((category) => (
                                        <Grid item {...chartCardStyling} key={category}>
                                            <Typography variant="h6" fontSize={16} paddingBottom={2}>
                                                {category ? category : t("goals.dashboard.noCategory")}
                                            </Typography>

                                            {dashboardProjects.policyGoals
                                                .filter((goal) => goal.category === category)
                                                .map((goal) => (
                                                    <PolicyGoalChart key={goal.id} goal={goal} />
                                                ))}
                                        </Grid>
                                    ));
                                })()}
                        </Grid>
                    )}
                </>
            )}

            {isPdf && (
                <Stack width="100%" height="80vh" alignItems="center" justifyContent="center">
                    {" "}
                    <CircularProgress color="inherit" />
                </Stack>
            )}

            {isPdf && (
                <Stack width="1920px" id="export">
                    {(isPrintingFullDashboard || visibility?.MUTATION) && (
                        <Box width="100%" id="totalValues">
                            <Typography variant="h6" fontSize={16}>
                                {t("dashboard.totalValues")}
                            </Typography>
                            {/* Mutation Card Total values */}
                            <Stack flexDirection="row" width="1920px" alignItems="center" justifyContent="space-between">
                                {/* Demolition */}
                                <Box width="600px">
                                    <LabelComponent
                                        required={false}
                                        text={t("createProject.houseBlocksForm.demolition")}
                                        tooltipInfoText={t("tooltipInfo.sloop.title")}
                                    />
                                    <CellContainer>
                                        <LabelComponent required={false} text={dashboardMutationValues?.DEMOLITION?.toString() ?? ""} />
                                    </CellContainer>
                                </Box>
                                {/* Construction */}
                                <Box width="600px">
                                    <LabelComponent
                                        required={false}
                                        text={t("createProject.houseBlocksForm.grossPlanCapacity")}
                                        tooltipInfoText={t("tooltipInfo.brutoPlancapaciteit.title")}
                                    />
                                    <CellContainer>
                                        <LabelComponent required={false} text={dashboardMutationValues?.CONSTRUCTION?.toString() ?? ""} />
                                    </CellContainer>
                                </Box>
                                {/* Total */}
                                <Box width="600px">
                                    <LabelComponent
                                        required={false}
                                        text={t("createProject.houseBlocksForm.netPlanCapacity")}
                                        tooltipInfoText={t("tooltipInfo.nettoPlancapaciteit.title")}
                                    />
                                    <CellContainer>
                                        <LabelComponent
                                            required={false}
                                            text={((dashboardMutationValues?.CONSTRUCTION ?? 0) - (dashboardMutationValues?.DEMOLITION ?? 0)).toString()}
                                        />
                                    </CellContainer>
                                </Box>
                            </Stack>
                        </Box>
                    )}
                    {(isPrintingFullDashboard || visibility?.PROJECT_PHASE) && (
                        <Box width="50%" border="solid 1px #DDD" p={1} id="projectPhaseChart">
                            <Typography variant="h6" fontSize={16}>
                                {t("dashboard.projectPhases")}
                            </Typography>
                            <DashboardPieChart isPdfChart={true} chartData={projectPhaseSums || []} />
                        </Box>
                    )}
                    {(isPrintingFullDashboard || visibility?.TARGET_GROUP) && (
                        <Box width="50%" border="solid 1px #DDD" p={1} id="targetGroupChart">
                            <Typography variant="h6" fontSize={16}>
                                {t("dashboard.targetAudiences")}
                            </Typography>
                            <DashboardPieChart isPdfChart={true} chartData={dashboardProjects?.targetGroup || []} />
                        </Box>
                    )}
                    {(isPrintingFullDashboard || visibility?.PHYSICAL_APPEARANCE) && (
                        <Box width="50%" border="solid 1px #DDD" p={1} id="physicalAppearanceChart">
                            <Typography variant="h6" fontSize={16}>
                                {t("dashboard.residentialFeatures")}
                            </Typography>
                            <DashboardPieChart isPdfChart={true} chartData={dashboardProjects?.physicalAppearance || []} />
                        </Box>
                    )}
                    {(isPrintingFullDashboard || visibility?.OWNERSHIP_BUY) && (
                        <Box width="50%" border="solid 1px #DDD" p={1} id="buy">
                            <Typography variant="h6" fontSize={16}>
                                {t("dashboard.buy")}
                            </Typography>
                            <DashboardPieChart isPdfChart={true} chartData={dashboardProjects?.priceCategoryOwn || []} />
                        </Box>
                    )}
                    {(isPrintingFullDashboard || visibility?.OWNERSHIP_RENT) && (
                        <Box width="50%" border="solid 1px #DDD" p={1} id="rent">
                            <Typography variant="h6" fontSize={16}>
                                {t("dashboard.rent")}
                            </Typography>
                            <DashboardPieChart isPdfChart={true} chartData={dashboardProjects?.priceCategoryRent || []} />
                        </Box>
                    )}
                    {(isPrintingFullDashboard || visibility?.DELIVERABLES) && (
                        <Box width="50%" border="solid 1px #DDD" p={1} id="deliverables">
                            <Typography variant="h6" fontSize={16}>
                                {t("dashboard.deliverables")}
                            </Typography>
                            <MyResponsiveBar chartData={dashboardProjects?.planning || []} selectedProject={null} />
                        </Box>
                    )}
                    {(isPrintingFullDashboard || visibility?.DELAYED_PROJECTS) && (
                        <Box width="50%" border="solid 1px #DDD" p={1} id="delayedProjects">
                            <Typography variant="h6" fontSize={16}>
                                {t("dashboard.delayedProjects")}
                            </Typography>
                        </Box>
                    )}
                    {(isPrintingFullDashboard || visibility?.DELAYED_PROJECTS) && (
                        <Box width="50%" border="solid 1px #DDD" p={1} id="policyGoals">
                            <Typography variant="h6" fontSize={16}></Typography>
                            {dashboardProjects &&
                                (() => {
                                    const categories = Array.from(new Set(dashboardProjects.policyGoals.map((goal) => goal.category))).sort();

                                    return categories.map((category) => (
                                        <Grid item {...chartCardStyling} key={category}>
                                            <Typography variant="h6" fontSize={16} paddingBottom={2}>
                                                {category ? category : t("goals.dashboard.noCategory")}
                                            </Typography>

                                            {dashboardProjects.policyGoals
                                                .filter((goal) => goal.category === category)
                                                .map((goal) => (
                                                    <PolicyGoalChart isPDF={true} key={goal.id} goal={goal} />
                                                ))}
                                        </Grid>
                                    ));
                                })()}
                        </Box>
                    )}
                </Stack>
            )}
        </>
    );
};
