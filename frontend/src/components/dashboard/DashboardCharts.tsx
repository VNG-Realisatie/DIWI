import { Grid, Typography } from "@mui/material";
import { DashboardPieChart } from "./PieChart";
import { useEffect, useState } from "react";
import { ChartType } from "../../pages/DashboardProject";
import { MutationCard } from "./MutationCard";
import ProjectOverviewMap from "../map/ProjectOverviewMap";
import { getDashboardProjects, VisibilityElement } from "../../api/dashboardServices";
import { getProjects } from "../../api/projectsServices";
import { getProjectHouseBlocksWithCustomProperties } from "../../api/houseBlockServices";
import { useTranslation } from "react-i18next";
import { chartColors } from "../../utils/dashboardChartColors";
import { RemoveElementButton } from "./RemoveElementButton";

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
    customizable?: boolean;
    visibility: Visibility;
    setVisibility: React.Dispatch<React.SetStateAction<Visibility>>;
};
export const DashboardCharts = ({ customizable = false, visibility, setVisibility }: Props) => {
    const [dashboardProjects, setDashboardProjects] = useState<DashboardProjects>();
    const [projectPhaseSums, setProjectPhaseSums] = useState([]);
    const [dashboardMutationValues, setDashboardMutationValues] = useState<MutationValues>();

    const handleHide = (item: VisibilityElement) => {
        setVisibility((prev) => ({ ...prev, [item]: false }));
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
        <>
            <Grid container border="solid 1px #DDD" justifyContent="space-around" p={1}>
                {visibility.MUTATION && (
                    <Grid item xs={12}>
                        {customizable && <RemoveElementButton handleHide={() => handleHide("MUTATION")} />}
                        <MutationCard
                            demolitionAmount={dashboardMutationValues?.DEMOLITION ?? 0}
                            constructionAmount={dashboardMutationValues?.CONSTRUCTION ?? 0}
                        />
                    </Grid>
                )}
                {visibility.PROJECT_PHASE && (
                    <Grid item {...chartCardStyling}>
                        <Typography variant="h6" fontSize={16}>
                            {t("dashboard.projectPhases")}
                            {customizable && <RemoveElementButton handleHide={() => handleHide("PROJECT_PHASE")} />}
                        </Typography>
                        <DashboardPieChart chartData={projectPhaseSums || []} colors={chartColors} />
                    </Grid>
                )}
                {visibility.TARGET_GROUP && (
                    <Grid item {...chartCardStyling}>
                        <Typography variant="h6" fontSize={16}>
                            {t("dashboard.targetAudiences")}
                            {customizable && <RemoveElementButton handleHide={() => handleHide("TARGET_GROUP")} />}
                        </Typography>
                        <DashboardPieChart chartData={dashboardProjects?.targetGroup || []} colors={chartColors} />
                    </Grid>
                )}
                {visibility.PHYSICAL_APPEARANCE && (
                    <Grid item {...chartCardStyling}>
                        <Typography variant="h6" fontSize={16}>
                            {t("dashboard.residentialFeatures")}
                            {customizable && <RemoveElementButton handleHide={() => handleHide("PHYSICAL_APPEARANCE")} />}
                        </Typography>
                        <DashboardPieChart chartData={dashboardProjects?.physicalAppearance || []} colors={chartColors} />
                    </Grid>
                )}
                {visibility.OWNERSHIP_BUY && (
                    <Grid item {...chartCardStyling}>
                        <Typography variant="h6" fontSize={16}>
                            {t("dashboard.buy")}
                            {customizable && <RemoveElementButton handleHide={() => handleHide("OWNERSHIP_BUY")} />}
                        </Typography>
                    </Grid>
                )}
                {visibility.OWNERSHIP_RENT && (
                    <Grid item {...chartCardStyling}>
                        <Typography variant="h6" fontSize={16}>
                            {t("dashboard.rent")}
                            {customizable && <RemoveElementButton handleHide={() => handleHide("OWNERSHIP_RENT")} />}
                        </Typography>
                    </Grid>
                )}
                {visibility.RESIDENTIAL_PROJECTS && (
                    <Grid item {...chartCardStyling}>
                        <Typography variant="h6" fontSize={16}>
                            {t("dashboard.residentialProjects")}
                            {customizable && <RemoveElementButton handleHide={() => handleHide("RESIDENTIAL_PROJECTS")} />}
                        </Typography>
                    </Grid>
                )}
                {visibility.DELIVERABLES && (
                    <Grid item {...chartCardStyling}>
                        <Typography variant="h6" fontSize={16}>
                            {t("dashboard.deliverables")}
                            {customizable && <RemoveElementButton handleHide={() => handleHide("DELIVERABLES")} />}
                        </Typography>
                    </Grid>
                )}
                {visibility.DELAYED_PROJECTS && (
                    <Grid item {...chartCardStyling}>
                        <Typography variant="h6" fontSize={16}>
                            {t("dashboard.delayedProjects")}
                            {customizable && <RemoveElementButton handleHide={() => handleHide("DELAYED_PROJECTS")} />}
                        </Typography>
                    </Grid>
                )}
                {visibility.PROJECT_MAP && (
                    <Grid item xs={12} sx={{ backgroundColor: "#F0F0F0", m: 1, p: 2 }}>
                        <Typography variant="h6" fontSize={16}>
                            {t("dashboard.projects")}
                            {customizable && <RemoveElementButton handleHide={() => handleHide("PROJECT_MAP")} />}
                        </Typography>
                        <ProjectOverviewMap isDashboardMap={true} />
                    </Grid>
                )}
            </Grid>
        </>
    );
};
