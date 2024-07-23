import { Autocomplete, Grid, Stack, TextField, Typography } from "@mui/material";
import BreadcrumbBar from "../components/header/BreadcrumbBar";
import * as Paths from "../Paths";
import { useContext, useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import ProjectContext from "../context/ProjectContext";
import { useNavigate, useParams } from "react-router-dom";
import { CharacteristicTable } from "../components/dashboard/CharacteristicTable";
import { DashboardPieChart } from "../components/dashboard/PieChart";
import { getDashboardProject, Planning } from "../api/dashboardServices";
import { chartColors } from "../utils/dashboardChartColors";
import { Project } from "../api/projectsServices";
import useCustomSearchParams from "../hooks/useCustomSearchParams";
import { TooltipInfo } from "../widgets/TooltipInfo";
import { FileDownload } from "@mui/icons-material";
import html2canvas from "html2canvas";
import jsPDF from "jspdf";
import { MyResponsiveBar } from "../components/dashboard/BarChart";

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

    const titleStyling = { fontWeight: "bold", fontSize: 16, my: 1 };
    const chartCardStyling = { backgroundColor: "#F0F0F0", my: 1, p: 2, xs: 12, md: 5.9 };

    const [physicalAppearance, setPhysicalAppearance] = useState<ChartType[]>([]);
    const [planning, setPlanning] = useState<Planning[]>([]);
    const handleSelectProject = (project: Project | null) => {
        setSelectedDashboardProject(project);
        navigate(Paths.dashboardProject.toPath({ projectId: project?.projectId || "" }));
    };

    const exportPDF = async () => {
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
        const planningChart = await h2c(schedule);
        //Add the rest of the charts here

        const pdf = new jsPDF("p", "px", "a4");
        pdf.setFontSize(14);
        pdf.text(selectedProject?.projectName ?? "", 5, 20);
        pdf.addImage(projectSumChart, "PNG", 5, 35, 215, 90);
        pdf.addImage(appearanceChart, "PNG", 225, 35, 215, 90);
        pdf.addImage(planningChart, "PNG", 5, 130, 215, 90);
        // Add the rest of the charts here

        pdf.save(`${selectedProject?.projectName}.pdf`);
    };

    useEffect(() => {
        if (projectId) {
            getDashboardProject(projectId).then((data) => {
                const convertedData = data.physicalAppearance.map((d) => {
                    return { label: d.name, value: d.amount };
                });
                //Set here later planning chart data
                setPhysicalAppearance(convertedData);
                setPlanning(data.planning);
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
                    <FileDownload onClick={exportPDF} sx={{ fill: "#002C64" }} />
                </TooltipInfo>
            </Stack>
            <Grid width="100%" container border="solid 1px #DDD" justifyContent="space-around" p={1}>
                <Grid item {...chartCardStyling} id="projectSum">
                    <Typography sx={titleStyling}>{t("dashboard.characteristics")}</Typography>
                    <CharacteristicTable />
                </Grid>
                <Grid item {...chartCardStyling} id="physicalAppearanceChart">
                    <Typography sx={titleStyling}>{t("dashboard.residentialProjects")}</Typography>
                    <DashboardPieChart chartData={physicalAppearance} colors={chartColors} />
                </Grid>
                <Grid item {...chartCardStyling} id="priceSegmentsPurchase">
                    <Typography sx={titleStyling}>{t("dashboard.priceSegmentsPurchase")}</Typography>

                    {/* ToDo:Add chart here later */}
                </Grid>
                <Grid item {...chartCardStyling} id="priceSegmentsRent">
                    <Typography sx={titleStyling}>{t("dashboard.priceSegmentsRent")}</Typography>
                    {/* ToDo:Add chart here later */}
                </Grid>
                <Grid item {...chartCardStyling} id="schedule">
                    <Typography sx={titleStyling}>{t("dashboard.schedule")}</Typography>
                    <MyResponsiveBar chartData={planning} selectedProjectName={selectedProject?.projectName} />
                </Grid>
                <Grid item {...chartCardStyling} id="upcomingMileStones">
                    <Typography sx={titleStyling}>{t("dashboard.upcomingMileStones")}</Typography>
                    {/* ToDo:Add chart here later */}
                </Grid>
            </Grid>
        </Stack>
    );
};
