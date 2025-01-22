import { useState, useContext, useEffect } from "react";
import { Grid, Box, Typography, Alert, Stack, Accordion, AccordionSummary, AccordionDetails, List } from "@mui/material";
import { downloadExportData, ExportData, exportProjects, getExportDataById } from "../api/exportServices";
import { t } from "i18next";
import ActionNotAllowed from "./ActionNotAllowed";
import { useNavigate, useParams } from "react-router-dom";
import { configuredExport, exchangeimportdata } from "../Paths";
import UserContext from "../context/UserContext";
import { GridExpandMoreIcon } from "@mui/x-data-grid";
import { PropertyListItem } from "../components/PropertyListItem";
import ProjectsTableWrapper from "../components/project/ProjectTableWrapper";
import { getAllowedConfidentialityLevels } from "../utils/exportUtils";

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
    const { exportId } = useParams();
    const { allowedActions } = useContext(UserContext);
    const navigate = useNavigate();
    const [errors, setErrors] = useState<DownloadError[]>([]);
    const [exportData, setExportData] = useState<ExportData>();

    useEffect(() => {
        if (!exportId) return;
        const fetchData = async () => {
            const exportdata = await getExportDataById(exportId);
            setExportData(exportdata);
        };
        fetchData();
    }, [exportId]);

    if (!allowedActions.includes("VIEW_DATA_EXCHANGES")) {
        return <ActionNotAllowed errorMessage={t("admin.export.actionNotAllowed")} />;
    }

    const handleBack = () => {
        navigate(exchangeimportdata.toPath());
    };

    //this function doesnt do anything at the moment, functionality not implemented
    const handleExportProjects = async () => {
        if (!exportId) return;
        try {
            await exportProjects(exportId, selectedProjects);
            console.log("Export successful");
        } catch (error) {
            console.error("Export failed", error);
        }
    };

    const handleDownload = async () => {
        if (!exportId || !exportData) return;

        try {
            const projectIds = selectedProjects;
            const allowedConfidentialityLevels = exportData.minimumConfidentiality ? getAllowedConfidentialityLevels(exportData.minimumConfidentiality) : [];

            const body = {
                exportDate: new Date().toISOString(),
                ...(projectIds.length > 0 ? { projectIds } : { confidentialityLevels: allowedConfidentialityLevels }),
            };

            await downloadExportData(exportId, body);
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
                    <ProjectsTableWrapper
                        minimumConfidentiality={exportData?.minimumConfidentiality}
                        redirectPath={configuredExport.toPath({ exportId })}
                        setSelectedProjects={setSelectedProjects}
                        selectedProjects={selectedProjects}
                        handleBack={handleBack}
                        exportProjects={handleExportProjects}
                        handleDownload={handleDownload}
                    />
                    {errors.length > 0 && (
                        <Alert severity="error" sx={{ "& .MuiAlert-message": { width: "100%" } }}>
                            <Stack>
                                {errors.map((error) => {
                                    return (
                                        <Accordion defaultExpanded>
                                            <AccordionSummary
                                                expandIcon={<GridExpandMoreIcon />}
                                                sx={{
                                                    backgroundColor: "lightgray",
                                                }}
                                            >
                                                <Typography className="import-error">{t(`exchangeData.downloadErrors.${error.code}`)}</Typography>
                                            </AccordionSummary>
                                            <AccordionDetails>
                                                <>
                                                    <List
                                                        dense
                                                        sx={{
                                                            listStyleType: "disc",
                                                            pl: 1,
                                                            "& .MuiListItem-root": {
                                                                display: "list-item",
                                                                padding: 0,
                                                            },
                                                        }}
                                                    >
                                                        <PropertyListItem label={t("exchangeDatae.downloadErrorProperties.category")} value={error.cat1} />
                                                        <PropertyListItem label={t("exchangeData.downloadErrorProperties.category")} value={error.cat2} />
                                                        <PropertyListItem label={t("exchangeData.downloadErrorProperties.fieldName")} value={error.fieldName} />
                                                        <PropertyListItem
                                                            label={t("exchangeData.downloadErrorProperties.houseblockId")}
                                                            value={error.houseblockId}
                                                        />
                                                        <PropertyListItem
                                                            label={t("exchangeData.downloadErrorProperties.priceValueMax")}
                                                            value={error.priceValueMax}
                                                        />
                                                        <PropertyListItem
                                                            label={t("exchangeData.downloadErrorProperties.priceValueMin")}
                                                            value={error.priceValueMin}
                                                        />
                                                        <PropertyListItem label={t("exchangeData.downloadErrorProperties.projectId")} value={error.projectId} />
                                                    </List>
                                                </>
                                            </AccordionDetails>
                                        </Accordion>
                                    );
                                })}
                            </Stack>
                        </Alert>
                    )}
                </Grid>
            </Grid>
        </Box>
    );
};

export default ExportWizard;
