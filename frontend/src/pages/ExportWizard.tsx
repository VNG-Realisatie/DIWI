import { useState, useContext } from "react";
import { Grid, Box, Typography } from "@mui/material";
import { downloadExportData, exportProjects } from "../api/exportServices";
import { t } from "i18next";
import { ProjectsTableView } from "../components/project/ProjectsTableView";
import useAllowedActions from "../hooks/useAllowedActions";
import ActionNotAllowed from "./ActionNotAllowed";
import AlertContext from "../context/AlertContext";
import { useNavigate, useParams } from "react-router-dom";
import { exchangeimportdata } from "../Paths";

const ExportWizard = () => {
    const [selectedProjects, setSelectedProjects] = useState<string[]>([]);
    const { id: selectedExportId } = useParams();
    const { allowedActions } = useAllowedActions();
    const { setAlert } = useContext(AlertContext);
    const navigate = useNavigate();

    if (!allowedActions.includes("VIEW_DATA_EXCHANGES")) {
        return <ActionNotAllowed errorMessage={t("admin.export.actionNotAllowed")} />;
    }

    const handleBack = () => {
        navigate(exchangeimportdata.toPath());
    };

    //this function doesnt do anything at the moment, functionality not implemented
    const handleExportProjects = async () => {
        if (!selectedExportId) return;
        try {
            await exportProjects(selectedExportId, selectedProjects);
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

    const handleDownload = async () => {
        if (!selectedExportId) return;
        try {
            await downloadExportData(selectedExportId);
        } catch (error: unknown) {
            if (error instanceof Error) setAlert(error.message, "warning");
        }
    };

    return (
        <Box p={2}>
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
                        handleDownload={handleDownload}
                    />
                </Grid>
            </Grid>
        </Box>
    );
};

export default ExportWizard;
