import { useState, useEffect } from "react";
import { Grid, Button, Box, Typography } from "@mui/material";
import { ExportData, getExportData } from "../api/exportServices";
import ExportTable from "../components/export/ExportTable";
import { t } from "i18next";
import { ProjectsTableView } from "../components/project/ProjectsTableView";

const ExportWizard = () => {
    const [step, setStep] = useState(1);
    const [exportData, setExportData] = useState<ExportData[]>([]);
    const [selectedExport, setSelectedExport] = useState<ExportData | null>(null); //update type
    const [selectedProjects, setSelectedProjects] = useState<string[]>([]);

    useEffect(() => {
        const fetchData = async () => {
            const dummyData: ExportData[] = [
                {
                    id: "1",
                    name: "ESRI export 1",
                    type: "ESRI_ZUID_HOLLAND",
                    projectUrl: "http://example.com/project1",
                    projectdetailUrl: "http://example.com/project1/details",
                },
                {
                    id: "2",
                    name: "ESRI export 2",
                    type: "ESRI_ZUID_HOLLAND",
                    projectUrl: "http://example.com/project2",
                    projectdetailUrl: "http://example.com/project2/details",
                },
                {
                    id: "3",
                    name: "ESRI export 3",
                    type: "ESRI_ZUID_HOLLAND",
                    projectUrl: "http://example.com/project3",
                    projectdetailUrl: "http://example.com/project3/details",
                },
            ];
            setExportData(dummyData);
        };
        fetchData();
    }, []);

    const handleNext = () => {
        if (step === 1 && selectedExport) {
            setStep(2);
        }
    };

    const handleBack = () => {
        setStep(1);
    };
    const handleExport = () => {
        console.log("Exporting projects:", selectedProjects);
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
                        <Typography variant="h6">{t("admin.export.title")}</Typography>
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
                        <Typography variant="h6">Select Projects to Export</Typography>
                    </Grid>
                    <ProjectsTableView
                        isExportPage={true}
                        handleProjectSelection={handleProjectSelection}
                        selectedProjects={selectedProjects}
                        handleBack={handleBack}
                    />
                </Grid>
            )}
        </Box>
    );
};

export default ExportWizard;
