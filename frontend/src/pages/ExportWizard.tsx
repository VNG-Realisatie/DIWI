import { useState, useEffect } from "react";
import { Grid, Button, Box, Typography } from "@mui/material";
import { ExportData, exportProjects, getExportData } from "../api/exportServices";
import ExportTable from "../components/export/ExportTable";
import { t } from "i18next";
import { ProjectsTableView } from "../components/project/ProjectsTableView";
import useAllowedActions from "../hooks/useAllowedActions";
import ActionNotAllowed from "./ActionNotAllowed";

const ExportWizard = () => {
    const [step, setStep] = useState(1);
    const [exportData, setExportData] = useState<ExportData[]>([]);
    const [selectedExport, setSelectedExport] = useState<ExportData | null>(null);
    const [selectedProjects, setSelectedProjects] = useState<string[]>([]);
    const { allowedActions } = useAllowedActions();

    useEffect(() => {
        const fetchData = async () => {
            const exportdata = await getExportData();
            setExportData(exportdata);
        };
        fetchData();
    }, []);

    if (!allowedActions.includes("VIEW_DATA_EXCHANGES")) {
        return <ActionNotAllowed errorMessage={t("admin.export.actionNotAllowed")} />;
    }

    const handleNext = () => {
        if (step === 1 && selectedExport) {
            setStep(2);
        }
    };

    const handleBack = () => {
        setStep(1);
    };

    //this function doesnt do anything at the moment
    const handleExportProjects = async () => {
        if (!selectedExport) return;
        try {
            await exportProjects(selectedExport.id, selectedProjects);
            console.log("Export successful");
        } catch (error) {
            console.error("Export failed", error);
        }
    };
    const handleProjectSelection = (projectId: string | null | string[]) => {
        if (projectId === null) {
            setSelectedProjects([]);
        } else if (Array.isArray(projectId)) {
            setSelectedProjects(projectId);
        } else {
            setSelectedProjects((prevSelected) =>
                prevSelected.includes(projectId) ? prevSelected.filter((id) => id !== projectId) : [...prevSelected, projectId],
            );
        }
    };

    return (
        <Box p={2}>
            {step === 1 && (
                <Grid container spacing={2}>
                    <Grid item xs={12}>
                        <Typography variant="h6">{t("admin.export.title.selectExport")}</Typography>
                    </Grid>
                    <Grid item xs={12}>
                        <ExportTable
                            exportData={exportData}
                            selectedExport={selectedExport}
                            setSelectedExport={setSelectedExport}
                            setExportData={setExportData}
                        />
                        <Box sx={{ display: "flex", justifyContent: "left" }}>
                            <Button
                                sx={{ width: "130px", my: 2, ml: "auto" }}
                                variant="contained"
                                color="primary"
                                onClick={handleNext}
                                disabled={!selectedExport}
                            >
                                {t("generic.nextStep")}
                            </Button>
                        </Box>
                    </Grid>
                </Grid>
            )}
            {step === 2 && (
                <Grid container spacing={2}>
                    <Grid item xs={12}>
                        <Typography variant="h6">{t("admin.export.title.selectProject")}</Typography>
                    </Grid>
                    <Grid item xs={12}>
                        <ProjectsTableView
                            isExportPage={true}
                            handleProjectSelection={handleProjectSelection}
                            selectedProjects={selectedProjects}
                            handleBack={handleBack}
                            exportProjects={handleExportProjects}
                        />
                    </Grid>
                </Grid>
            )}
        </Box>
    );
};

export default ExportWizard;
