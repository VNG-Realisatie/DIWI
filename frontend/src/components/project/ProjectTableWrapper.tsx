import { Dispatch, SetStateAction, useContext, useState } from "react";
import { Box, Button, Dialog, DialogActions, DialogTitle, Stack } from "@mui/material";
import { ProjectsTableView } from "./ProjectsTableView";
import { t } from "i18next";
import { AddProjectButton } from "../PlusButton";
import UserContext from "../../context/UserContext";
import useAlert from "../../hooks/useAlert";
import { confidentialityUpdate, configuredExport } from "../../Paths";
import { useNavigate, useParams } from "react-router-dom";
import ProjectContext from "../../context/ProjectContext";
import { GridPaginationModel } from "@mui/x-data-grid";
import { useArcgisAuth } from "../../hooks/useArcgisAuth";

type Props = {
    redirectPath: string;
    setSelectedProjects?: Dispatch<SetStateAction<string[]>>;
    selectedProjects?: string[];
    handleBack?: () => void;
    exportProjects?: () => void;
    handleDownload?: () => void;
};

const ProjectsTableWrapper = ({
    handleBack = () => {},
    exportProjects = () => {},
    handleDownload = () => {},
    setSelectedProjects = () => {},
    selectedProjects = [],
    redirectPath,
}: Props) => {
    const { allowedActions } = useContext(UserContext);
    const [showDialog, setShowDialog] = useState(false);
    const { setAlert } = useAlert();
    const navigate = useNavigate();
    const { setPaginationInfo } = useContext(ProjectContext) as { setPaginationInfo: Dispatch<SetStateAction<GridPaginationModel>> };
    const { exportId = "defaultExportId" } = useParams<{ exportId?: string }>();

    const configuredExportPath = configuredExport.toPath({ exportId });
    const confidentialityUpdatePath = confidentialityUpdate.toPath({ exportId });

    const arcgisAuth = useArcgisAuth(exportId);
    const login = arcgisAuth?.login;
    const token = arcgisAuth?.token;

    const handleProjectsExport = () => {
        exportProjects();
        setShowDialog(false);
    };

    const handleNavigate = (path: string) => {
        navigate(path);
        setPaginationInfo({ page: 1, pageSize: 10 });
    };

    return (
        <Stack
            width="100%"
            sx={{
                margin: "0 auto",
                overflowX: "auto",
            }}
        >
            <ProjectsTableView
                setSelectedProjects={setSelectedProjects}
                selectedProjects={selectedProjects}
                redirectPath={redirectPath}
                setPaginationInfo={setPaginationInfo}
            />
            <Dialog open={showDialog} onClose={() => setShowDialog(false)} aria-labelledby="alert-dialog-title" aria-describedby="alert-dialog-description">
                <DialogTitle id="alert-dialog-title">{t("projects.confirmExport")}</DialogTitle>
                <DialogActions sx={{ px: 5, py: 3, ml: 15 }}>
                    <Button onClick={() => setShowDialog(false)}>{t("projects.cancelExport")}</Button>
                    <Button
                        variant="contained"
                        onClick={() => {
                            handleProjectsExport();
                            setAlert(t("projects.successExport"), "success");
                        }}
                        autoFocus
                    >
                        {t("projects.exportIt")}
                    </Button>
                </DialogActions>
            </Dialog>
            <Box sx={{ display: "flex", justifyContent: "right", gap: "3px" }}>
                {redirectPath === configuredExportPath && (
                    <Button sx={{ width: "130px", my: 2 }} variant="contained" color="primary" onClick={handleBack}>
                        {t("generic.previousStep")}
                    </Button>
                )}
                {redirectPath === configuredExportPath && allowedActions.includes("EXPORT_PROJECTS") && (
                    <>
                        <Button
                            sx={{ my: 2 }}
                            variant="outlined"
                            onClick={() => {
                                handleNavigate(confidentialityUpdate.toPath({ exportId }));
                            }}
                        >
                            {t("projects.confidentialityChange")}
                        </Button>
                        <Button
                            disabled={true}
                            sx={{ width: "130px", my: 2 }}
                            variant="contained"
                            onClick={() => {
                                setShowDialog(true);
                            }}
                        >
                            {t("projects.export")}
                        </Button>
                        <Button sx={{ width: "130px", my: 2 }} variant="contained" onClick={handleDownload}>
                            {t("projects.download")}
                        </Button>
                        <Button
                            sx={{ width: "130px", my: 2 }}
                            variant="outlined"
                            color="secondary"
                            onClick={() => login(exportId)}
                            disabled={token ? true : false}
                        >
                            {t("projects.authenticate")}
                        </Button>
                    </>
                )}
                {redirectPath === confidentialityUpdatePath && (
                    <Button
                        sx={{ my: 2 }}
                        variant="outlined"
                        onClick={() => {
                            handleNavigate(configuredExport.toPath({ exportId }));
                        }}
                    >
                        {t("projects.backToExport")}
                    </Button>
                )}
            </Box>
            <Box sx={{ height: 100 }}></Box>
            {redirectPath != confidentialityUpdatePath && redirectPath != configuredExportPath && <AddProjectButton />}
        </Stack>
    );
};

export default ProjectsTableWrapper;
