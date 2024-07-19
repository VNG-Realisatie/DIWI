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
import ProjectOverviewMap from "../components/map/ProjectOverviewMap";

import useAllowedActions from "../hooks/useAllowedActions";
import ActionNotAllowed from "./ActionNotAllowed";

import html2canvas from "html2canvas";
import jsPDF from "jspdf";
import { FileDownload } from "@mui/icons-material";
import { TooltipInfo } from "../widgets/TooltipInfo";

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

    const allowedActions = useAllowedActions();


    const handleSelectProject = (project: Project | null) => {
        setSelectedProject(project);
        navigate(Paths.dashboardProject.toPath({ projectId: project?.projectId || "" }));
    };
    const exportPDF = async () => {
        const totalValues = document.getElementById("totalValues");
        const projectPhase = document.getElementById("projectPhaseChart");
        const targetGroup = document.getElementById("targetGroupChart");
        const physicalAppearance = document.getElementById("physicalAppearanceChart");
        const buy = document.getElementById("buy");
        const rent = document.getElementById("rent");
        const residentialProjects = document.getElementById("residentialProjects");
        const deliverables = document.getElementById("deliverables");
        const delayedProjects = document.getElementById("delayedProjects");

        if (!totalValues) {
            console.error("no exportEmployment");
            return;
        }

        if (!projectPhase) {
            console.error("no valueAdded");
            return;
        }

        if (!targetGroup) {
            console.error("no communityGiving");
            return;
        }

        if (!physicalAppearance) {
            console.error("no employment");
            return;
        }
        if (!buy) {
            console.error("no buy");
            return;
        }
        if (!rent) {
            console.error("no rent");
            return;
        }
        if (!residentialProjects) {
            console.error("no residentialProjects");
            return;
        }
        if (!deliverables) {
            console.error("no deliverables");
            return;
        }
        if (!delayedProjects) {
            console.error("no delayedProjects");
            return;
        }

        const h2c = async (element: HTMLElement) => {
            const canvas = await html2canvas(element, { scale: 2.5 });
            return canvas.toDataURL("image/png");
        };
        const totalValueChart = await h2c(totalValues);
        const projectPhaseChart = await h2c(projectPhase);
        const targetGroupChart = await h2c(targetGroup);
        const physicalAppearanceChart = await h2c(physicalAppearance);
        //Add the rest of the charts here
        const pdf = new jsPDF("p", "px", "a4");
        pdf.setFontSize(14);
        pdf.text(t(`dashboard.exportTitle`), 5, 20);
        pdf.addImage(totalValueChart, "PNG", 5, 30, 436, 40);
        pdf.addImage(projectPhaseChart, "PNG", 5, 80, 215, 90);
        pdf.addImage(targetGroupChart, "PNG", 225, 80, 215, 90);
        pdf.addImage(physicalAppearanceChart, "PNG", 5, 180, 215, 90);
        // Add the rest of the charts here
        pdf.save("dashboardProjects.pdf");
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

    if (!allowedActions.includes("VIEW_DASHBOARDS")) {
        return <ActionNotAllowed errorMessage={t("dashboard.forbidden")} />;
    }

    const handleSelectProject = (project: Project | null) => {
        setSelectedProject(project);
        navigate(Paths.dashboardProject.toPath({ projectId: project?.projectId || "" }));
    };

    const chartCardStyling = { backgroundColor: "#F0F0F0", my: 1, p: 2, xs: 12, md: 5.9 };

    return (
        <Stack mb={8}>
            <BreadcrumbBar
                pageTitle={t("dashboard.title")}
                links={[{ title: `${t("dashboard.municipalityProgram")} ${municipalityName}`, link: Paths.dashboard.path }]}
            />
            <Stack direction="row" alignItems="center" justifyContent="space-between" sx={{ cursor: "pointer" }}>
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
                <TooltipInfo text={t("dashboard.exportpdf")}>
                    <FileDownload onClick={exportPDF} sx={{ fill: "#002C64" }} />
                </TooltipInfo>
            </Stack>
            <Grid container border="solid 1px #DDD" justifyContent="space-around" p={1} id="export">
                <Grid item xs={12} id="totalValues">
                    <MutationCard demolitionAmount={dashboardMutationValues?.DEMOLITION ?? 0} constructionAmount={dashboardMutationValues?.CONSTRUCTION ?? 0} />
                </Grid>
                <Grid item {...chartCardStyling} id="projectPhaseChart">
                    <Typography variant="h6" fontSize={16}>
                        {t("dashboard.projectPhases")}
                    </Typography>
                    <DashboardPieChart chartData={projectPhaseSums || []} colors={chartColors} />
                </Grid>
                <Grid item {...chartCardStyling} id="targetGroupChart">
                    <Typography variant="h6" fontSize={16}>
                        {t("dashboard.targetAudiences")}
                    </Typography>
                    <DashboardPieChart chartData={dashboardProjects?.targetGroup || []} colors={chartColors} />
                </Grid>
                <Grid item {...chartCardStyling} id="physicalAppearanceChart">
                    <Typography variant="h6" fontSize={16}>
                        {t("dashboard.residentialFeatures")}
                    </Typography>
                    <DashboardPieChart chartData={dashboardProjects?.physicalAppearance || []} colors={chartColors} />
                </Grid>
                <Grid item {...chartCardStyling} id="buy">
                    <Typography variant="h6" fontSize={16}>
                        {t("dashboard.buy")}
                    </Typography>
                    {/* ToDo:Add chart here later */}
                </Grid>
                <Grid item {...chartCardStyling} id="rent">
                    <Typography variant="h6" fontSize={16}>
                        {t("dashboard.rent")}
                    </Typography>
                    {/* ToDo:Add chart here later */}
                </Grid>
                <Grid item {...chartCardStyling} id="residentialProjects">
                    <Typography variant="h6" fontSize={16}>
                        {t("dashboard.residentialProjects")}
                    </Typography>
                    {/* ToDo:Add chart here later */}
                </Grid>
                <Grid item {...chartCardStyling} id="deliverables">
                    <Typography variant="h6" fontSize={16}>
                        {t("dashboard.deliverables")}
                    </Typography>
                    {/* ToDo:Add chart here later */}
                </Grid>
                <Grid item {...chartCardStyling} id="delayedProjects">
                    <Typography variant="h6" fontSize={16}>
                        {t("dashboard.delayedProjects")}
                    </Typography>
                </Grid>

                <Grid item xs={12} sx={{ backgroundColor: "#F0F0F0", m: 1, p: 2 }} id="map">
                    <Typography variant="h6" fontSize={16}>
                        {t("dashboard.projects")}
                    </Typography>
                    <ProjectOverviewMap isDashboardMap={true} />
                </Grid>
            </Grid>
        </Stack>
    );
};
