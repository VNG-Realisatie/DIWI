import { Autocomplete, Box, CircularProgress, Grid, Stack, TextField, Typography } from "@mui/material";
import BreadcrumbBar from "../components/header/BreadcrumbBar";
import * as Paths from "../Paths";
import { useCallback, useContext, useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import ProjectContext from "../context/ProjectContext";
import { useNavigate, useParams } from "react-router-dom";
import { CharacteristicTable } from "../components/dashboard/CharacteristicTable";
import { DashboardPieChart } from "../components/dashboard/PieChart";
import { getDashboardProject } from "../api/dashboardServices";
import { chartColors } from "../utils/dashboardChartColors";
import { Project } from "../api/projectsServices";
import useCustomSearchParams from "../hooks/useCustomSearchParams";
import { TooltipInfo } from "../widgets/TooltipInfo";
import { FileDownload } from "@mui/icons-material";
import html2canvas from "html2canvas";
import jsPDF from "jspdf";

export type ChartType = {
    label: string;
    value: number;
};

export const DashboardProject = () => {
    const { t } = useTranslation();
    const { selectedProject } = useContext(ProjectContext);
    const { rows } = useCustomSearchParams(undefined, undefined, { page: 1, pageSize: 10000 });
    const { projectId } = useParams();
    const navigate = useNavigate();

    const [selectedDashboardProject, setSelectedDashboardProject] = useState<Project | null>(null);
    const [pdfExport, setPdfExport] = useState(false);

    const chartCardStyling = { backgroundColor: "#F0F0F0", my: 1, p: 2, xs: 12, md: 5.9 };

    const [physicalAppearance, setPhysicalAppearance] = useState<ChartType[]>([]);

    const handleSelectProject = (project: Project | null) => {
        setSelectedDashboardProject(project);
        navigate(Paths.dashboardProject.toPath({ projectId: project?.projectId || "" }));
    };

    const exportPDF = useCallback(async () => {
        const projectSum = document.getElementById("projectSum");
        const appearance = document.getElementById("physicalAppearanceChart");
        const upcomingMileStones = document.getElementById("upcomingMileStones");
        const priceSegmentsPurchase = document.getElementById("priceSegmentsPurchase");
        const priceSegmentsRent = document.getElementById("priceSegmentsRent");
        const schedule = document.getElementById("schedule");

        if (!projectSum) {
            console.error("no projectSum");
            return;
        }

        if (!appearance) {
            console.error("no appearance");
            return;
        }

        if (!upcomingMileStones) {
            console.error("no upcomingMileStones");
            return;
        }

        if (!priceSegmentsPurchase) {
            console.error("no priceSegmentsPurchase");
            return;
        }

        if (!priceSegmentsRent) {
            console.error("no priceSegmentsRent");
            return;
        }

        if (!schedule) {
            console.error("no schedule");
            return;
        }

        const h2c = async (element: HTMLElement) => {
            const canvas = await html2canvas(element, { scale: 2.5 });
            return canvas.toDataURL("image/png");
        };
        const projectSumChart = await h2c(projectSum);
        const appearanceChart = await h2c(appearance);
        //Add the rest of the charts here

        const pdf = new jsPDF("p", "px", "a4");
        pdf.setFontSize(14);
        pdf.text(selectedProject?.projectName ?? "", 5, 20);
        pdf.addImage(projectSumChart, "PNG", 5, 35, 215, 78);
        pdf.addImage(appearanceChart, "PNG", 225, 35, 215, 78);
        // Add the rest of the charts here

        pdf.save(`${selectedProject?.projectName}.pdf`);
        setPdfExport(false);
    }, [selectedProject]);

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

    useEffect(() => {
        pdfExport &&
            setTimeout(() => {
                exportPDF();
            }, 500);
    }, [exportPDF, pdfExport]);

    return (
        <Stack flexDirection="column" width="100%" spacing={2} mb={10}>
            <BreadcrumbBar
                pageTitle={t("dashboard.projectTitle")}
                links={[
                    { title: `${t("dashboard.title")}`, link: Paths.dashboard.path },
                    { title: `${selectedProject?.projectName}`, link: Paths.dashboardProject.toPath({ projectId: projectId || "" }) },
                ]}
            />
            <Stack direction="row" alignItems="center" justifyContent="space-between" sx={{ cursor: "pointer" }}>
                <Autocomplete
                    sx={{ my: 1 }}
                    id="dashboard-projects"
                    size="small"
                    options={rows || []}
                    getOptionLabel={(option) => option?.projectName || ""}
                    value={selectedDashboardProject || null}
                    onChange={(_, newValue) => handleSelectProject(newValue)}
                    renderInput={(params) => <TextField {...params} size="small" sx={{ minWidth: "200px" }} placeholder={t("dashboard.selectProject")} />}
                />
                <TooltipInfo text={t("dashboard.exportpdf")}>
                    <FileDownload onClick={() => setPdfExport(true)} sx={{ fill: "#002C64" }} />
                </TooltipInfo>
            </Stack>
            {!pdfExport && (
                <Grid width="100%" container border="solid 1px #DDD" justifyContent="space-around" p={1}>
                    <Grid item {...chartCardStyling}>
                        <Typography variant="h6" fontSize={16}>
                            {t("dashboard.characteristics")}
                        </Typography>
                        <CharacteristicTable />
                    </Grid>
                    <Grid item {...chartCardStyling}>
                        <Typography variant="h6" fontSize={16}>
                            {t("dashboard.residentialProjects")}
                        </Typography>
                        <DashboardPieChart chartData={physicalAppearance} colors={chartColors} />
                    </Grid>
                    <Grid item {...chartCardStyling}>
                        <Typography variant="h6" fontSize={16}>
                            {t("dashboard.priceSegmentsPurchase")}
                        </Typography>

                        {/* ToDo:Add chart here later */}
                    </Grid>
                    <Grid item {...chartCardStyling}>
                        <Typography variant="h6" fontSize={16}>
                            {t("dashboard.priceSegmentsRent")}
                        </Typography>
                        {/* ToDo:Add chart here later */}
                    </Grid>
                    <Grid item {...chartCardStyling}>
                        <Typography variant="h6" fontSize={16}>
                            {t("dashboard.schedule")}
                        </Typography>
                    </Grid>
                    <Grid item {...chartCardStyling}>
                        <Typography variant="h6" fontSize={16}>
                            {t("dashboard.upcomingMileStones")}
                        </Typography>
                        {/* ToDo:Add chart here later */}
                    </Grid>
                </Grid>
            )}
            {pdfExport && (
                <Stack width="100%" height="80vh" alignItems="center" justifyContent="center">
                    {" "}
                    <CircularProgress color="inherit" />
                </Stack>
            )}
            {pdfExport && (
                <Stack width="1920px">
                    <Box width="50%" border="solid 1px #DDD" p={1} id="projectSum">
                        <Typography variant="h6" fontSize={16}>
                            {t("dashboard.characteristics")}
                        </Typography>
                        <CharacteristicTable />
                    </Box>
                    <Box width="50%" border="solid 1px #DDD" p={1} id="physicalAppearanceChart">
                        <Typography variant="h6" fontSize={16}>
                            {t("dashboard.residentialProjects")}
                        </Typography>
                        <DashboardPieChart isPdfChart={true} chartData={physicalAppearance} colors={chartColors} />
                    </Box>
                    <Box width="50%" border="solid 1px #DDD" p={1} id="priceSegmentsPurchase">
                        <Typography variant="h6" fontSize={16}>
                            {t("dashboard.priceSegmentsPurchase")}
                        </Typography>

                        {/* ToDo:Add chart here later */}
                    </Box>
                    <Box width="50%" border="solid 1px #DDD" p={1} id="priceSegmentsRent">
                        <Typography variant="h6" fontSize={16}>
                            {t("dashboard.priceSegmentsRent")}
                        </Typography>
                        {/* ToDo:Add chart here later */}
                    </Box>
                    <Box width="50%" border="solid 1px #DDD" p={1} id="schedule">
                        <Typography variant="h6" fontSize={16}>
                            {t("dashboard.schedule")}
                        </Typography>
                    </Box>
                    <Box width="50%" border="solid 1px #DDD" p={1} id="upcomingMileStones">
                        <Typography variant="h6" fontSize={16}>
                            {t("dashboard.upcomingMileStones")}
                        </Typography>
                        {/* ToDo:Add chart here later */}
                    </Box>
                </Stack>
            )}
        </Stack>
    );
};
