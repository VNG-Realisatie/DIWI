import { useState, useContext } from "react";
import { Grid, Box, Typography, Alert } from "@mui/material";
import { downloadExportData, exportProjects } from "../api/exportServices";
import { t } from "i18next";
import { ProjectsTableView } from "../components/project/ProjectsTableView";
import ActionNotAllowed from "./ActionNotAllowed";
import { useNavigate, useParams } from "react-router-dom";
import { exchangeimportdata } from "../Paths";
import UserContext from "../context/UserContext";
import { ConfidentialityLevel } from "../types/enums";

type DownloadError = {
    cat1?: string;
    cat2?: string;
    code: string;
    fieldName?: string;
    houseblockId?: string;
    message?: string;
    priceValueMax?: number;
    priceValueMin?: number;
    projectId?: string;
};
const ExportWizard = () => {
    const [selectedProjects, setSelectedProjects] = useState<string[]>([]);
    const { id: selectedExportId } = useParams();
    const { allowedActions } = useContext(UserContext);
    const navigate = useNavigate();
    const [errors, setErrors] = useState<DownloadError[]>([]);

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
    const handleDownload = async () => {
        if (!selectedExportId) return;

        try {
            const projectIds = selectedProjects;
            const confidentialityLevels = ["PUBLIC", "EXTERNAL_GOVERNMENTAL"] as ConfidentialityLevel[];

            const body = {
                exportDate: new Date().toISOString(),
                ...(projectIds.length > 0 ? { projectIds } : { confidentialityLevels }),
            };

            await downloadExportData(selectedExportId, body);
            setErrors([]);
        } catch (error: unknown) {
            if (Array.isArray(error)) {
                setErrors(error);
            } else {
                setErrors([{ code: "generic_error" }]);
            }
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
                        setSelectedProjects={setSelectedProjects}
                        selectedProjects={selectedProjects}
                        handleBack={handleBack}
                        exportProjects={handleExportProjects}
                        handleDownload={handleDownload}
                    />
                </Grid>
                {errors.map((error, index) => (
                    <Grid item xs={12} key={index}>
                        <Alert severity="warning">
                            {t(`exchangeData.downloadErrors.${error.code}`)}
                            {error.fieldName && `: ${error.fieldName}`}
                        </Alert>
                    </Grid>
                ))}
            </Grid>
        </Box>
    );
};

export default ExportWizard;
