import { Box, Popover, Stack, SxProps, Theme, Tooltip } from "@mui/material";
import { MouseEvent, useCallback, useContext, useEffect, useState } from "react";
import ProjectContext from "../../../context/ProjectContext";
import EditIcon from "@mui/icons-material/Edit";
import SaveIcon from "@mui/icons-material/Save";
import ClearIcon from "@mui/icons-material/Clear";
import FormatColorFillIcon from "@mui/icons-material/FormatColorFill";
import { useTranslation } from "react-i18next";

import { defaultColors } from "../../ColorSelector";
import { BlockPicker } from "react-color";

import { Project, updateProject as updateProjects } from "../../../api/projectsServices";
import AlertContext from "../../../context/AlertContext";
import { CreateHouseBlockDialog } from "./CreateHouseBlockDialog";
import { HouseBlocksList } from "./HouseBlocksList";
import { CustomPropertyValue, getCustomPropertyValues, putCustomPropertyValue } from "../../../api/customPropServices";
import { ProjectForm } from "../../ProjectForm";

export const columnTitleStyle: SxProps<Theme> = {
    border: "solid 1px #ddd",
    p: 0.6,
    color: "#FFFFFF",
    backgroundColor: "#738092",
};

export const ProjectsWithHouseBlock = () => {
    const { selectedProject, updateProject } = useContext(ProjectContext);
    const [anchorEl, setAnchorEl] = useState<HTMLButtonElement | null>(null);
    const [openColorDialog, setOpenColorDialog] = useState(false);
    const [openHouseBlockDialog, setOpenHouseBlockDialog] = useState(false);
    const [readOnly, setReadOnly] = useState(true);

    const [projectForm, setProjectForm] = useState<Project | null>(selectedProject);
    const [customValues, setCustomValues] = useState<CustomPropertyValue[]>([]);

    const { setAlert } = useContext(AlertContext);
    const { t } = useTranslation();

    useEffect(() => {
        const fetchCustomPropertyValues = async () => {
            if (selectedProject?.projectId) {
                try {
                    const values = await getCustomPropertyValues(selectedProject?.projectId);
                    setCustomValues(values);
                } catch (error) {
                    console.error("Error fetching custom property values:", error);
                }
            }
        };

        fetchCustomPropertyValues();
    }, [selectedProject?.projectId]);

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

    const handleCustomPropertiesSave = () => {
        customValues.forEach((value) => {
            putCustomPropertyValue(selectedProject?.projectId as string, value).catch((error: any) => setAlert(error.message, "error"));
        });
    };

    const handleProjectSave = () => {
        if (projectForm) {
            updateProjects(projectForm)
                .then(() => {
                    setReadOnly(true);
                    updateProject();
                    handleCustomPropertiesSave();
                })
                .catch((error) => {
                    setAlert(error.message, "error");
                });
        }
    };

    const open = Boolean(anchorEl);

    return (
        <Stack my={1} mb={10}>
            <Box sx={{ cursor: "pointer" }} position="absolute" right={100} top={17}>
                {!readOnly && (
                    <Tooltip placement="top" title={t("projectDetail.colorEdit")}>
                        <FormatColorFillIcon
                            sx={{ mr: 2, color: "#FFFFFF" }}
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
            </Box>
            <Stack>
                {projectForm && (
                    // this box with padding is to make the layout similar to how houseblocks look
                    <Box padding={2}>
                        <ProjectForm project={projectForm} setProject={setProjectForm} readOnly={readOnly} showColorPicker={false} />
                    </Box>
                )}

                <HouseBlocksList setOpenHouseBlockDialog={setOpenHouseBlockDialog} />

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

                {/* Dialog to create new houseblock */}
                <CreateHouseBlockDialog openHouseBlockDialog={openHouseBlockDialog} setOpenHouseBlockDialog={setOpenHouseBlockDialog} />
            </Stack>
        </Stack>
    );
};
