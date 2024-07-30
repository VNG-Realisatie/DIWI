import { Autocomplete, Box, CircularProgress, Grid, Stack, TextField, Typography } from "@mui/material";
import BreadcrumbBar from "../components/header/BreadcrumbBar";
import * as Paths from "../Paths";
import { useCallback, useContext, useEffect, useState } from "react";
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
import { LabelComponent } from "../components/project/LabelComponent";
import { CellContainer } from "../components/project/project-with-house-block/CellContainer";

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
    const [pdfExport, setPdfExport] = useState(false);

    const { municipalityName } = useContext(ConfigContext);
    const { rows } = useCustomSearchParams(undefined, undefined, { page: 1, pageSize: 10000 });
    const { t } = useTranslation();
    const navigate = useNavigate();

    const allowedActions = useAllowedActions();

    const handleSelectProject = (project: Project | null) => {
        setSelectedProject(project);
        navigate(Paths.dashboardProject.toPath({ projectId: project?.projectId || "" }));
    };
    const exportPDF = useCallback(async () => {

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
        pdf.addImage(totalValueChart, "PNG", 5, 30, 436, 25);
        pdf.addImage(projectPhaseChart, "PNG", 5, 65, 215, 75);
        pdf.addImage(targetGroupChart, "PNG", 225, 65, 215, 75);
        pdf.addImage(physicalAppearanceChart, "PNG", 5, 145, 215, 75);
        // Add the rest of the charts here
        pdf.save("dashboardProjects.pdf");
        setPdfExport(false);
    },[t]);

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

    useEffect(() => {
       pdfExport&&setTimeout(() => {
        exportPDF()
       }, 500);

    }, [exportPDF, pdfExport]);

    if (!allowedActions.includes("VIEW_DASHBOARDS")) {
        return <ActionNotAllowed errorMessage={t("dashboard.forbidden")} />;
    }

    const chartCardStyling = { backgroundColor: "#F0F0F0", my: 1, p: 2, xs: 12, md: 5.9 };

    return (
        <Stack mb={8} >
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
                    <FileDownload onClick={()=>{
                        setPdfExport(true)
                        }} sx={{ fill: "#002C64" }} />
                </TooltipInfo>
            </Stack>
         { !pdfExport&&  <Grid container border="solid 1px #DDD" p={1} justifyContent="space-around"  id="export">
                <Grid item xs={12} >
                    <MutationCard demolitionAmount={dashboardMutationValues?.DEMOLITION ?? 0} constructionAmount={dashboardMutationValues?.CONSTRUCTION ?? 0} />
                </Grid>
                <Grid item {...chartCardStyling} >
                    <Typography variant="h6" fontSize={16}>
                        {t("dashboard.projectPhases")}
                    </Typography>
                    <DashboardPieChart chartData={projectPhaseSums || []} colors={chartColors} />
                </Grid>
                <Grid item {...chartCardStyling} >
                    <Typography variant="h6" fontSize={16}>
                        {t("dashboard.targetAudiences")}
                    </Typography>
                    <DashboardPieChart chartData={dashboardProjects?.targetGroup || []} colors={chartColors} />
                </Grid>
                <Grid item {...chartCardStyling} >
                    <Typography variant="h6" fontSize={16}>
                        {t("dashboard.residentialFeatures")}
                    </Typography>
                    <DashboardPieChart chartData={dashboardProjects?.physicalAppearance || []} colors={chartColors} />
                </Grid>
                <Grid item {...chartCardStyling} >
                    <Typography variant="h6" fontSize={16}>
                        {t("dashboard.buy")}
                    </Typography>
                    {/* ToDo:Add chart here later */}
                </Grid>
                <Grid item {...chartCardStyling} >
                    <Typography variant="h6" fontSize={16}>
                        {t("dashboard.rent")}
                    </Typography>
                    {/* ToDo:Add chart here later */}
                </Grid>
                <Grid item {...chartCardStyling} >
                    <Typography variant="h6" fontSize={16}>
                        {t("dashboard.residentialProjects")}
                    </Typography>
                    {/* ToDo:Add chart here later */}
                </Grid>
                <Grid item {...chartCardStyling} >
                    <Typography variant="h6" fontSize={16}>
                        {t("dashboard.deliverables")}
                    </Typography>
                    {/* ToDo:Add chart here later */}
                </Grid>
                <Grid item {...chartCardStyling} >
                    <Typography variant="h6" fontSize={16}>
                        {t("dashboard.delayedProjects")}
                    </Typography>
                </Grid>

                <Grid item xs={12} sx={{ backgroundColor: "#F0F0F0", m: 1, p: 2 }} >
                    <Typography variant="h6" fontSize={16}>
                        {t("dashboard.projects")}
                    </Typography>
                    <ProjectOverviewMap isDashboardMap={true} />
                </Grid>
            </Grid>}

            {pdfExport&&<Stack width="100%" height="80vh" alignItems="center" justifyContent="center"> <CircularProgress color="inherit" /></Stack>}
            { pdfExport&&  <Stack width="1920px"  id="export">
                <Box width="100%"  id="totalValues">
                <Typography variant="h6" fontSize={16}>
                    {t("dashboard.totalValues")}
                </Typography>
                {/* Mutation Card Total values */}
                <Stack flexDirection="row"  width="1920px" alignItems="center" justifyContent="space-between">
                    {/* Demolition */}
                    <Box width="600px">
                        <LabelComponent
                            required={false}
                            text={t("createProject.houseBlocksForm.demolition")}
                            tooltipInfoText={t("tooltipInfo.sloop.title")}
                        />
                        <CellContainer>
                            <LabelComponent required={false}  text={dashboardMutationValues?.DEMOLITION?.toString()??""} />
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
                            <LabelComponent required={false}  text={dashboardMutationValues?.CONSTRUCTION?.toString()??""} />
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
                            <LabelComponent required={false}  text={((dashboardMutationValues?.CONSTRUCTION??0) - (dashboardMutationValues?.DEMOLITION??0)).toString()} />
                        </CellContainer>
                    </Box>
                </Stack>
                </Box>
                <Box width="50%" border="solid 1px #DDD" p={1} id="projectPhaseChart">
                    <Typography variant="h6" fontSize={16}>
                        {t("dashboard.projectPhases")}
                    </Typography>
                    <DashboardPieChart isPdfChart={true} chartData={projectPhaseSums || []} colors={chartColors} />
                </Box>
                <Box width="50%" border="solid 1px #DDD" p={1}  id="targetGroupChart">
                    <Typography variant="h6" fontSize={16}>
                        {t("dashboard.targetAudiences")}
                    </Typography>
                    <DashboardPieChart isPdfChart={true} chartData={dashboardProjects?.targetGroup || []} colors={chartColors} />
                </Box>
                <Box width="50%" border="solid 1px #DDD" p={1} id="physicalAppearanceChart">
                    <Typography variant="h6" fontSize={16}>
                        {t("dashboard.residentialFeatures")}
                    </Typography>
                    <DashboardPieChart isPdfChart={true} chartData={dashboardProjects?.physicalAppearance || []} colors={chartColors} />
                </Box>
                <Box width="50%" border="solid 1px #DDD" p={1} id="buy">
                    <Typography variant="h6" fontSize={16}>
                        {t("dashboard.buy")}
                    </Typography>
                    {/* ToDo:Add chart here later */}
                </Box>
                <Box width="50%" border="solid 1px #DDD" p={1} id="rent">
                    <Typography variant="h6" fontSize={16}>
                        {t("dashboard.rent")}
                    </Typography>
                    {/* ToDo:Add chart here later */}
                </Box>
                <Box width="50%" border="solid 1px #DDD" p={1} id="residentialProjects">
                    <Typography variant="h6" fontSize={16}>
                        {t("dashboard.residentialProjects")}
                    </Typography>
                    {/* ToDo:Add chart here later */}
                </Box>
                <Box width="50%" border="solid 1px #DDD" p={1} id="deliverables">
                    <Typography variant="h6" fontSize={16}>
                        {t("dashboard.deliverables")}
                    </Typography>
                    {/* ToDo:Add chart here later */}
                </Box>
                <Box width="50%" border="solid 1px #DDD" p={1} id="delayedProjects">
                    <Typography variant="h6" fontSize={16}>
                        {t("dashboard.delayedProjects")}
                    </Typography>
                </Box>

            </Stack>}
        </Stack>
    );
};
