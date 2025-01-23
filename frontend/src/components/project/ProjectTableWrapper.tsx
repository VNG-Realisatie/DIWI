import { Dispatch, SetStateAction, useContext, useEffect, useState } from "react";
import { Box, Button, Dialog, DialogActions, DialogTitle, Stack, Typography } from "@mui/material";
import { GenericOptionType, ProjectsTableView } from "./ProjectsTableView";
import { t } from "i18next";
import { AddProjectButton } from "../PlusButton";
import UserContext from "../../context/UserContext";
import useAlert from "../../hooks/useAlert";
import { confidentialityUpdate, configuredExport } from "../../Paths";
import { useNavigate, useParams } from "react-router-dom";
import CategoryInput from "./inputs/CategoryInput";
import { ConfidentialityLevelOptionsType, confidentialityLevelOptions } from "../table/constants";
import { getAllowedConfidentialityLevels } from "../../utils/exportUtils";

type Props = {
    redirectPath: string;
    setSelectedProjects?: Dispatch<SetStateAction<string[]>>;
    selectedProjects?: string[];
    handleBack?: () => void;
    exportProjects?: () => void;
    handleDownload?: () => void;
    setConfidentialityLevel?: Dispatch<SetStateAction<GenericOptionType<ConfidentialityLevelOptionsType>>>;
    selectedConfidentialityLevel?: GenericOptionType<ConfidentialityLevelOptionsType>;
    minimumConfidentiality?: ConfidentialityLevelOptionsType;
};

const ProjectsTableWrapper = ({
    handleBack = () => {},
    exportProjects = () => {},
    handleDownload = () => {},
    setSelectedProjects = () => {},
    selectedProjects = [],
    redirectPath,
    setConfidentialityLevel,
    selectedConfidentialityLevel,
    minimumConfidentiality,
}: Props) => {
    const { allowedActions } = useContext(UserContext);
    const [showDialog, setShowDialog] = useState(false);
    const { setAlert } = useAlert();
    const navigate = useNavigate();
    const { exportId = "defaultExportId" } = useParams<{ exportId?: string }>();

    const [filteredConfidentialityOptions, setFilteredConfidentialityOptions] = useState<GenericOptionType<ConfidentialityLevelOptionsType>[]>([]);

    const configuredExportPath = configuredExport.toPath({ exportId });
    const confidentialityUpdatePath = confidentialityUpdate.toPath({ exportId });

    useEffect(() => {
        if (!exportId || !minimumConfidentiality) return;

        const allowedLevels = getAllowedConfidentialityLevels(minimumConfidentiality);
        const filteredOptions = confidentialityLevelOptions.filter((option) => allowedLevels.includes(option.id));
        setFilteredConfidentialityOptions(filteredOptions);
    }, [exportId, minimumConfidentiality]);

    const handleProjectsExport = () => {
        exportProjects();
        setShowDialog(false);
    };

    const handleNavigate = (path: string) => {
        navigate(path);
    };

    return (
        <Stack
            width="100%"
            sx={{
                margin: "0 auto",
                overflowX: "auto",
            }}
        >
            {redirectPath === configuredExportPath && (
                <Stack sx={{ backgroundColor: "#D9D9D9", padding: "15px", width: "100%", gap: "15px", borderRadius: "4px", marginY: "10px" }}>
                    <Box sx={{ display: "flex", alignItems: "center", gap: "10px", width: "60%" }}>
                        <Typography variant="h6" sx={{ whiteSpace: "nowrap" }}>
                            {t("exchangeData.confidentialityChanger.selectConfidentialityLevel")}
                        </Typography>
                        <CategoryInput
                            readOnly={false}
                            mandatory={false}
                            options={filteredConfidentialityOptions}
                            values={selectedConfidentialityLevel}
                            setValue={(_, newValue) => {
                                if (!setConfidentialityLevel) return;
                                setConfidentialityLevel(newValue);
                            }}
                            multiple={false}
                            error={t("createProject.hasMissingRequiredAreas.confidentialityLevel")}
                            translationPath="projectTable.confidentialityLevelOptions."
                            tooltipInfoText={"tooltipInfo.vertrouwelijkheidsniveau.title"}
                            hasTooltipOption={true}
                        />
                    </Box>
                </Stack>
            )}
            <ProjectsTableView
                minimumConfidentiality={minimumConfidentiality}
                selectedConfidentialityLevel={selectedConfidentialityLevel}
                setSelectedProjects={setSelectedProjects}
                selectedProjects={selectedProjects}
                redirectPath={redirectPath}
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
