import { Box, Popover, Stack, Tooltip } from "@mui/material";
import { useCallback, useContext, useEffect, useState } from "react";
import ProjectContext from "../../../context/ProjectContext";
import EditIcon from "@mui/icons-material/Edit";
import SaveIcon from "@mui/icons-material/Save";
import ClearIcon from "@mui/icons-material/Clear";
import FormatColorFillIcon from "@mui/icons-material/FormatColorFill";
import { useTranslation } from "react-i18next";
import { defaultColors } from "../../ColorSelector";
import { BlockPicker } from "react-color";
import { Project, updateProjectWithCustomProperties } from "../../../api/projectsServices";
import AlertContext from "../../../context/AlertContext";
import { ProjectForm } from "../../ProjectForm";
import useLoading from "../../../hooks/useLoading";
import { useHasEditPermission } from "../../../hooks/useHasEditPermission";
import { checkDeletedCategories, validateForm } from "../../../utils/formValidation";
import UserContext from "../../../context/UserContext";
import { checkIsOwnerValidWithConfidentialityLevel } from "../../../utils/checkIsOwnerValidWithConfidentialityLevel";

export const ProjectsWithHouseBlock = () => {
    const { selectedProject, updateProject, nonFixedCustomDefinitions } = useContext(ProjectContext);
    const [anchorEl, setAnchorEl] = useState<HTMLButtonElement | null>(null);
    const [openColorDialog, setOpenColorDialog] = useState(false);
    const [readOnly, setReadOnly] = useState(true);
    const [projectForm, setProjectForm] = useState<Project | null>(selectedProject);

    const { setAlert } = useContext(AlertContext);
    const { user } = useContext(UserContext);
    const { setLoading } = useLoading();
    const { t } = useTranslation();
    const { getEditPermission } = useHasEditPermission();

    const resetProjectForm = useCallback(() => {
        setProjectForm(selectedProject);
    }, [selectedProject]);

    useEffect(() => {
        resetProjectForm();
    }, [resetProjectForm, selectedProject]);

    const handleCancelChange = () => {
        setReadOnly(true);
        resetProjectForm();
    };

    const handleProjectEdit = () => {
        setReadOnly(false);
    };

    const handleProjectSave = async () => {
        if (projectForm) {
            const updatedCustomProperties = projectForm.customProperties?.map((customProperty) => {
                const customDefinition = nonFixedCustomDefinitions.find((definition) => definition.id === customProperty.customPropertyId);
                const updatedCustomProperty = customDefinition ? checkDeletedCategories(customProperty, customDefinition) : customProperty;
                return updatedCustomProperty;
            });
            const updatedProjectForm = { ...projectForm, customProperties: updatedCustomProperties };
            if (!validateForm(updatedProjectForm, checkIsOwnerValidWithConfidentialityLevel(updatedProjectForm, user), nonFixedCustomDefinitions)) {
                setAlert(t("createProject.hasMissingRequiredAreas.hasmissingProperty"), "warning");
                return;
            }
            try {
                setLoading(true);
                const newProjectForm = await updateProjectWithCustomProperties(updatedProjectForm);
                setAlert(t("createProject.houseBlocksForm.notifications.successfullySaved"), "success");
                setProjectForm(newProjectForm);
                updateProject();
            } catch {
                setAlert(t("createProject.houseBlocksForm.notifications.error"), "error");
            } finally {
                setLoading(false);
                setReadOnly(true);
            }
        }
    };

    const open = Boolean(anchorEl);

    return (
        <Stack mb={10}>
            <Box sx={{ cursor: "pointer" }} position="absolute" right={100} top={17}>
                {getEditPermission() && (
                    <>
                        {!readOnly && (
                            <Tooltip placement="top" title={t("projectDetail.colorEdit")}>
                                <FormatColorFillIcon
                                    sx={{ mr: 2, color: "#FFFFFF" }}
                                    // eslint-disable-next-line @typescript-eslint/no-explicit-any
                                    onClick={(event: any) => {
                                        setOpenColorDialog(true);
                                        setAnchorEl(event.currentTarget);
                                    }}
                                />
                            </Tooltip>
                        )}
                        {readOnly && (
                            <Tooltip placement="top" title={t("generic.edit")}>
                                <EditIcon sx={{ color: "#FFFFFF" }} onClick={handleProjectEdit} />
                            </Tooltip>
                        )}
                        {!readOnly && (
                            <>
                                <Tooltip placement="top" title={t("generic.cancelChanges")}>
                                    <ClearIcon sx={{ mr: 2, color: "#FFFFFF" }} onClick={handleCancelChange} />
                                </Tooltip>
                                <Tooltip placement="top" title={t("generic.saveChanges")}>
                                    <SaveIcon sx={{ color: "#FFFFFF" }} onClick={handleProjectSave} />
                                </Tooltip>
                            </>
                        )}
                    </>
                )}
            </Box>
            <Stack>
                {projectForm && (
                    // this box with padding is to make the layout similar to how houseblocks look
                    <Box padding={2} paddingTop={1}>
                        <ProjectForm
                            checkIsOwnerValidWithConfidentialityLevel={() => checkIsOwnerValidWithConfidentialityLevel(projectForm, user)}
                            project={projectForm}
                            setProject={setProjectForm}
                            readOnly={readOnly}
                            showColorPicker={false}
                        />
                    </Box>
                )}

                {/* Dialog to select color */}
                {openColorDialog && (
                    <Popover
                        open={open}
                        anchorEl={anchorEl}
                        onClose={() => setAnchorEl(null)}
                        anchorOrigin={{
                            vertical: "bottom",
                            horizontal: "center",
                        }}
                    >
                        <BlockPicker
                            colors={defaultColors}
                            color={projectForm?.projectColor}
                            onChange={(newColor) => projectForm && setProjectForm({ ...projectForm, projectColor: newColor.hex })}
                        />
                    </Popover>
                )}
            </Stack>
        </Stack>
    );
};
