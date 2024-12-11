import { Box, Button, Stack, Typography } from "@mui/material";
import CategoryInput from "../components/project/inputs/CategoryInput";
import { t } from "i18next";
import { ConfidentialityLevelOptions, confidentialityLevelOptions } from "../components/table/constants";
import { useState } from "react";
import useAlert from "../hooks/useAlert";
import { GenericOptionType, ProjectsTableView } from "../components/project/ProjectsTableView";
import { getProject, updateProject } from "../api/projectsServices";
import { useNavigate, useParams } from "react-router-dom";
import { confidentialityUpdate } from "../Paths";

function ConfidentialityUpdateTable() {
    const [confidentialityLevel, setConfidentialityLevel] = useState<GenericOptionType<ConfidentialityLevelOptions>>(
        confidentialityLevelOptions[confidentialityLevelOptions.length - 1],
    );
    const navigate = useNavigate();
    const { exportId } = useParams();
    const [selectedProjects, setSelectedProjects] = useState<string[]>([]);
    const { setAlert } = useAlert();

    const handleUpdateConfidentiality = async () => {
        try {
            await Promise.all(
                selectedProjects.map(async (projectId) => {
                    const data = await getProject(projectId);
                    const newData = { ...data, confidentialityLevel: confidentialityLevel.id as ConfidentialityLevelOptions };
                    await updateProject(newData);
                }),
            );
            setAlert(t("projects.confidentialityLevelUpdate"), "success");
        } catch (error: unknown) {
            if (error instanceof Error) {
                setAlert(error.message, "warning");
            }
        } finally {
            setSelectedProjects([]);
            navigate(confidentialityUpdate.toPath({ exportId }));
        }
    };

    return (
        <>
            <Stack sx={{ backgroundColor: "#D9D9D9", padding: "15px", width: "100%", gap: "15px", borderRadius: "4px" }}>
                <Typography variant="h6" sx={{ marginBottom: "10px" }}>
                    {t("exchangeData.confidentialityChanger.explanation")}
                </Typography>

                <Box sx={{ display: "flex", alignItems: "center", gap: "10px", width: "60%" }}>
                    <Typography variant="h6" sx={{ whiteSpace: "nowrap" }}>
                        {t("exchangeData.confidentialityChanger.changeTo")}
                    </Typography>
                    <CategoryInput
                        readOnly={false}
                        mandatory={false}
                        options={confidentialityLevelOptions}
                        values={confidentialityLevel}
                        setValue={(_, newValue) => {
                            setConfidentialityLevel(newValue);
                        }}
                        multiple={false}
                        error={t("createProject.hasMissingRequiredAreas.confidentialityLevel")}
                        translationPath="projectTable.confidentialityLevelOptions."
                        tooltipInfoText={"tooltipInfo.vertrouwelijkheidsniveau.title"}
                        hasTooltipOption={true}
                    />
                </Box>

                <Box sx={{ display: "flex", justifyContent: "flex-end" }}>
                    <Button variant="contained" onClick={handleUpdateConfidentiality} disabled={selectedProjects.length === 0 || !confidentialityLevel}>
                        {t("exchangeData.confidentialityChanger.confidentialityUpdate")}
                    </Button>
                </Box>
            </Stack>
            <ProjectsTableView
                redirectPath={confidentialityUpdate.toPath({ exportId })}
                setSelectedProjects={setSelectedProjects}
                selectedProjects={selectedProjects}
            />
        </>
    );
}

export default ConfidentialityUpdateTable;
