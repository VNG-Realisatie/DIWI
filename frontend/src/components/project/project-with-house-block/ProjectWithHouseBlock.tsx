import { Box, Popover, Stack, SxProps, Theme, Tooltip } from "@mui/material";
import { MouseEvent, useCallback, useContext, useEffect, useState } from "react";
import ProjectContext from "../../../context/ProjectContext";
import ProjectColorContext from "../../../pages/ProjectDetail";
import EditIcon from "@mui/icons-material/Edit";
import SaveIcon from "@mui/icons-material/Save";
import ClearIcon from "@mui/icons-material/Clear";
import FormatColorFillIcon from "@mui/icons-material/FormatColorFill";
import { useTranslation } from "react-i18next";
import dayjs, { Dayjs } from "dayjs";

import { defaultColors } from "../../ColorSelector";
import { BlockPicker, ColorResult } from "react-color";

import { PlanStatusOptions, PlanTypeOptions } from "../../../types/enums";
import { Organization, PriorityModel, Project, SelectModel, updateProject as updateProjects } from "../../../api/projectsServices";
import AlertContext from "../../../context/AlertContext";
import { CreateHouseBlockDialog } from "./CreateHouseBlockDialog";
import { HouseBlocksList } from "./HouseBlocksList";
import { CustomerPropertiesProjectBlock } from "./CustomerPropertiesProjectBlock";
import { CustomPropertyValue, getCustomPropertyValues, putCustomPropertyValue } from "../../../api/customPropServices";
import { ProjectProperties } from "./ProjectProperties";

export const columnTitleStyle: SxProps<Theme> = {
    border: "solid 1px #ddd",
    p: 0.6,
    color: "#FFFFFF",
    backgroundColor: "#738092",
};

export const ProjectsWithHouseBlock = () => {
    const { selectedProject, updateProject } = useContext(ProjectContext);
    const { selectedProjectColor, setSelectedProjectColor } = useContext(ProjectColorContext);
    const [readOnly, setReadOnly] = useState(true);
    const [openColorDialog, setOpenColorDialog] = useState(false);
    const [name, setName] = useState<string | null>("");
    const [owner, setOwner] = useState<Organization[]>([]);
    const [leader, setLeader] = useState<Organization[]>([]);
    const [startDate, setStartDate] = useState<Dayjs | null>(null);
    const [endDate, setEndDate] = useState<Dayjs | null>(null);
    const [projectPhase, setProjectPhase] = useState<string | undefined>();
    const [confidentialityLevel, setConfidentialityLevel] = useState<string | undefined>();
    const [planType, setPlanType] = useState<PlanTypeOptions[]>([]);
    const [planStatus, setPlanStatus] = useState<PlanStatusOptions[]>([]);
    const [selectedMunicipalityRole, setSelectedMunicipalityRole] = useState<SelectModel[]>([]);
    const [selectedDistrict, setSelectedDistrict] = useState<SelectModel[]>([]);
    const [selectedMunicipality, setSelectedMunicipality] = useState<SelectModel[]>([]);
    const [selectedNeighbourhood, setSelectedNeighbourhood] = useState<SelectModel[]>([]);
    const [projectPriority, setProjectPriority] = useState<PriorityModel | null>(null);
    const [customValues, setCustomValues] = useState<CustomPropertyValue[]>([]);

    const { setAlert } = useContext(AlertContext);

    const [openHouseBlockDialog, setOpenHouseBlockDialog] = useState(false);
    const [anchorEl, setAnchorEl] = useState<HTMLButtonElement | null>(null);

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

    const handleCancelChange = () => {
        setReadOnly(true);
        updateChanges();
    };

    const { t } = useTranslation();

    const handleColorChange = (newColor: ColorResult) => {
        setSelectedProjectColor(newColor.hex);
    };

    const handleButtonClick = (event: MouseEvent<HTMLButtonElement>) => {
        setAnchorEl(event.currentTarget);
    };

    const handleClose = () => {
        setAnchorEl(null);
    };

    const updateChanges = useCallback(() => {
        setName(selectedProject?.projectName ?? "");
        setOwner(selectedProject?.projectOwners ?? []);
        setStartDate(selectedProject?.startDate ? dayjs(selectedProject.startDate) : null);
        setEndDate(selectedProject?.endDate ? dayjs(selectedProject.endDate) : null);
        setProjectPriority(selectedProject?.priority || {}); //ToDo Fix later when decided range select
        setProjectPhase(selectedProject?.projectPhase);
        setSelectedMunicipalityRole(selectedProject?.municipalityRole ?? []);
        setSelectedMunicipality(selectedProject?.municipality ?? []);
        setSelectedDistrict(selectedProject?.district ?? []);
        setSelectedNeighbourhood(selectedProject?.neighbourhood ?? []);
        setConfidentialityLevel(selectedProject?.confidentialityLevel);
        setLeader(selectedProject?.projectLeaders ?? []);
        setPlanType(selectedProject?.planType?.map((type) => type) ?? []);
        setPlanStatus(selectedProject?.planningPlanStatus?.map((type) => type) ?? []);
        setSelectedProjectColor(selectedProject?.projectColor ?? "");
    }, [
        selectedProject?.projectOwners,
        selectedProject?.confidentialityLevel,
        selectedProject?.endDate,
        selectedProject?.municipalityRole,
        selectedProject?.planType,
        selectedProject?.projectLeaders,
        selectedProject?.planningPlanStatus,
        selectedProject?.priority,
        selectedProject?.projectColor,
        selectedProject?.projectName,
        selectedProject?.projectPhase,
        selectedProject?.startDate,
        selectedProject?.municipality,
        selectedProject?.neighbourhood,
        selectedProject?.district,
        setSelectedProjectColor,
    ]);

    const open = Boolean(anchorEl);

    const handleProjectEdit = () => {
        setReadOnly(false);
        updateChanges();
    };

    const handleCustomPropertiesSave = () => {
        customValues.forEach((value) => {
            putCustomPropertyValue(selectedProject?.projectId as string, value).catch((error: any) => setAlert(error.message, "error"));
        });
    };

    const handleProjectSave = () => {
        const updatedProjectForm = {
            startDate: startDate?.format("YYYY-MM-DD"),
            endDate: endDate?.format("YYYY-MM-DD"),
            projectId: selectedProject?.projectId,
            projectName: name,
            projectColor: selectedProjectColor,
            confidentialityLevel: confidentialityLevel,
            planType: planType,
            priority: projectPriority,
            projectPhase: projectPhase,
            planningPlanStatus: planStatus,
            municipalityRole: selectedMunicipalityRole,
            projectOwners: owner,
            projectLeaders: leader,
            totalValue: selectedProject?.totalValue,
        };

        updateProjects(updatedProjectForm as Project)
            .then(() => {
                setReadOnly(true);
                updateProject();
                handleCustomPropertiesSave();
            })
            .catch((error) => {
                setAlert(error.message, "error");
            });
    };

    return (
        <Stack my={1} p={1} mb={10}>
            <Box sx={{ cursor: "pointer" }} position="absolute" right={10} top={55}>
                <Tooltip placement="top" title={t("projectDetail.colorEdit")}>
                    <FormatColorFillIcon
                        sx={{ mr: 2, color: "#FFFFFF" }}
                        onClick={(event: any) => {
                            setOpenColorDialog(true);
                            handleButtonClick(event);
                        }}
                    />
                </Tooltip>
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
                <ProjectProperties
                    projectPhase={projectPhase}
                    setProjectPhase={setProjectPhase}
                    setStartDate={setStartDate}
                    setEndDate={setEndDate}
                    readOnly={readOnly}
                    name={name}
                    setName={setName}
                    owner={owner}
                    setOwner={setOwner}
                    planType={planType}
                    setPlanType={setPlanType}
                    startDate={startDate}
                    endDate={endDate}
                    projectPriority={projectPriority}
                    setProjectPriority={setProjectPriority}
                    selectedMunicipalityRole={selectedMunicipalityRole}
                    setSelectedMunicipalityRole={setSelectedMunicipalityRole}
                    confidentialityLevel={confidentialityLevel}
                    setConfidentialityLevel={setConfidentialityLevel}
                    leader={leader}
                    setLeader={setLeader}
                    planStatus={planStatus}
                    setPlanStatus={setPlanStatus}
                    selectedDistrict={selectedDistrict}
                    selectedNeighbourhood={selectedNeighbourhood}
                    selectedMunicipality={selectedMunicipality}
                />

                <CustomerPropertiesProjectBlock {...{ readOnly, customValues, setCustomValues, columnTitleStyle }} />

                <HouseBlocksList setOpenHouseBlockDialog={setOpenHouseBlockDialog} />
                {openColorDialog && (
                    <Popover
                        open={open}
                        anchorEl={anchorEl}
                        onClose={handleClose}
                        anchorOrigin={{
                            vertical: "bottom",
                            horizontal: "center",
                        }}
                    >
                        <BlockPicker colors={defaultColors} color={selectedProjectColor} onChange={handleColorChange} />
                    </Popover>
                )}
                <CreateHouseBlockDialog openHouseBlockDialog={openHouseBlockDialog} setOpenHouseBlockDialog={setOpenHouseBlockDialog} />
            </Stack>
        </Stack>
    );
};
