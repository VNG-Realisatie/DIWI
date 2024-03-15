import { AvatarGroup, Box, Grid, Popover, Stack, TextField, Tooltip, Typography } from "@mui/material";
import { MouseEvent, useCallback, useContext, useEffect, useState } from "react";
import ProjectContext from "../../../context/ProjectContext";
import ProjectColorContext from "../../../pages/ProjectDetail";
import EditIcon from "@mui/icons-material/Edit";
import SaveIcon from "@mui/icons-material/Save";
import ClearIcon from "@mui/icons-material/Clear";
import FormatColorFillIcon from "@mui/icons-material/FormatColorFill";
import { useTranslation } from "react-i18next";
import { DatePicker } from "@mui/x-date-pickers";
import dayjs, { Dayjs } from "dayjs";
import { convertDayjsToString } from "../../../utils/convertDayjsToString";
import { formatDate } from "../../../utils/formatDate";
import { ProjectNameEditForm } from "./ProjectNameEditForm";
import { PlanTypeEditForm } from "./PlanTypeEditForm";
import { PhaseEditForm } from "./PhaseEditForm";
import { MunicipalityRoleEditForm } from "./MunicipalityRoleEditForm";
import { ConfidentialityLevelEditForm } from "./ConfidentialityLevelEditForm";
import { PlanStatusEditForm } from "./PlanStatusEditForm";
import { MunicipalityEditForm } from "./MunicipalityEditForm";
import { NeighbourhoodEditForm } from "./NeighbourhoodEditForm";
import { WijkEditForm } from "./WijkEditForm";
import { defaultColors } from "../../ColorSelector";
import { BlockPicker, ColorResult } from "react-color";
import { CellContainer } from "./CellContainer";
import { OrganizationSelect } from "../../../widgets/OrganizationSelect";
import { PlanStatusOptions, PlanTypeOptions } from "../../../types/enums";
import { PriorityEditForm } from "./PriorityEditForm";
import { Organization, SelectModel, getProjectHouseBlocks, updateProjects } from "../../../api/projectsServices";
import { HouseBlock } from "../../project-wizard/house-blocks/types";
import { CreateHouseBlockDialog } from "./CreateHouseBlockDialog";
import { HouseBlocksList } from "./HouseBlocksList";
export const columnTitleStyle = {
    border: "solid 1px #ddd",
    p: 0.6,
    color: "#FFFFFF",
    backgroundColor: "#738092",
};

export const ProjectsWithHouseBlock = () => {
    const { selectedProject, updateProject } = useContext(ProjectContext);
    const { selectedProjectColor, setSelectedProjectColor } = useContext(ProjectColorContext);
    const [projectEditable, setProjectEditable] = useState(false);
    const [openColorDialog, setOpenColorDialog] = useState(false);
    const [name, setName] = useState<string | null>();
    const [owner, setOwner] = useState<Organization[]>([]);
    const [leader, setLeader] = useState<Organization[]>([]);
    const [startDate, setStartDate] = useState<Dayjs | null>();
    const [endDate, setEndDate] = useState<Dayjs | null>();
    const [projectPhase, setProjectPhase] = useState<string | undefined>();
    const [confidentialityLevel, setConfidentialityLevel] = useState<string | undefined>();
    const [planType, setPlanType] = useState<PlanTypeOptions[]>([]);
    const [planStatus, setPlanStatus] = useState<PlanStatusOptions[]>([]);
    const [selectedMunicipalityRole, setSelectedMunicipalityRole] = useState<SelectModel[]>([]);
    const [selectedMunicipality, setSelectedMunicipality] = useState<SelectModel[]>([]);
    const [selectedNeighbourhood, setSelectedNeighbourhood] = useState<SelectModel[]>([]);
    const [selectedWijk, setSelectedWijk] = useState<SelectModel[]>([]);
    const [projectPriority, setProjectPriority] = useState<SelectModel | null>();
    const [houseBlocks, setHouseBlocks] = useState<HouseBlock[]>();
    const [openHouseBlockDialog, setOpenHouseBlockDialog] = useState(false);
    const [anchorEl, setAnchorEl] = useState<HTMLButtonElement | null>(null);
    const handleStartDateChange = (newValue: Dayjs | null) => setStartDate(newValue);

    const handleEndDateChange = (newValue: Dayjs | null) => setEndDate(newValue);

    const handleCancelChange = () => {
        setProjectEditable(false);
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
        setName(selectedProject?.projectName);
        setOwner(selectedProject?.projectOwners ?? []);
        setStartDate(dayjs(formatDate(selectedProject?.startDate)));
        setEndDate(dayjs(formatDate(selectedProject?.endDate)));
        setProjectPriority(selectedProject?.priority?.value); //ToDo Fix later when decided range select
        setProjectPhase(selectedProject?.projectPhase);
        setSelectedNeighbourhood(selectedProject?.buurt ?? []);
        setSelectedMunicipality(selectedProject?.municipality ?? []);
        setSelectedMunicipalityRole(selectedProject?.municipalityRole ?? []);
        setConfidentialityLevel(selectedProject?.confidentialityLevel);
        setLeader(selectedProject?.projectLeaders ?? []);
        setPlanType(selectedProject?.planType?.map((type) => type) ?? []);
        setPlanStatus(selectedProject?.planningPlanStatus?.map((type) => type) ?? []);
        setSelectedWijk(selectedProject?.wijk ?? []);
        setSelectedProjectColor(selectedProject?.projectColor ?? "");
    }, [
        selectedProject?.projectOwners,
        selectedProject?.buurt,
        selectedProject?.confidentialityLevel,
        selectedProject?.endDate,
        selectedProject?.municipality,
        selectedProject?.municipalityRole,
        selectedProject?.planType,
        selectedProject?.projectLeaders,
        selectedProject?.planningPlanStatus,
        selectedProject?.priority?.value,
        selectedProject?.projectColor,
        selectedProject?.projectName,
        selectedProject?.projectPhase,
        selectedProject?.startDate,
        selectedProject?.wijk,
        setSelectedProjectColor,
    ]);

    const open = Boolean(anchorEl);

    const handleProjectEdit = () => {
        setProjectEditable(true);
        updateChanges();
    };

    const updatedProjectForm = {
        startDate: startDate,
        endDate: endDate,
        projectId: selectedProject?.projectId,
        projectName: name,
        projectColor: selectedProjectColor,
        confidentialityLevel: confidentialityLevel,
        planType: planType,
        priority: {
            value: projectPriority,
            //Will be updated after multi select added
            // min: {
            //     id: selectedProject?.projectId,
            //     name: null,
            // },
            //Will be updated after multi select added
            // max: {
            //     id: selectedProject?.projectId,
            //     name: null,
            // },
        },
        projectPhase: projectPhase,
        planningPlanStatus: planStatus,
        municipalityRole: selectedMunicipalityRole,
        projectOwner: owner,
        projectLeader: leader,
        //Will be implemented later
        // projectOwners: [
        //     {
        //         uuid: selectedProject?.projectId,
        //         name: "string",
        //         users: [
        //             {
        //                 uuid: selectedProject?.projectId,
        //                 firstName: "firtname",
        //                 lastName: "lastname",
        //                 initials: "fl",
        //             },
        //         ],
        //     },
        // ],
        //Will be implemented later
        // projectLeaders: [
        //     {
        //         uuid: selectedProject?.projectId,
        //         name: "string",
        //         users: [
        //             {
        //                 uuid: selectedProject?.projectId,
        //                 firstName: "firtname",
        //                 lastName: "lastname",
        //                 initials: "fl",
        //             },
        //         ],
        //     },
        // ],
        totalValue: selectedProject?.totalValue,
        municipality: selectedMunicipality,
        wijk: selectedWijk,
        buurt: selectedNeighbourhood,
    };

    const handleProjectSave = () => {
        updateProjects(updatedProjectForm).then((res) => {
            setProjectEditable(false);
            updateProject();
        });
    };
    const { id } = useContext(ProjectContext);
    useEffect(() => {
        id && getProjectHouseBlocks(id).then((res) => setHouseBlocks(res));
    }, [id]);

    return (
        <Stack my={1} p={1} mb={10}>
            <Box sx={{ cursor: "pointer" }} position="absolute" right={10} top={55} zIndex={9999}>
                <Tooltip placement="top" title={t("projectDetail.colorEdit")}>
                    <FormatColorFillIcon
                        sx={{ mr: 2, color: "#FFFFFF" }}
                        onClick={(event: any) => {
                            setOpenColorDialog(true);
                            handleButtonClick(event);
                        }}
                    />
                </Tooltip>
                {!projectEditable && (
                    <Tooltip placement="top" title={t("generic.edit")}>
                        <EditIcon sx={{ color: "#FFFFFF" }} onClick={handleProjectEdit} />
                    </Tooltip>
                )}
                {projectEditable && (
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
                {/* List project properties */}
                <Grid container my={2}>
                    <Grid item xs={6} md={1}>
                        <Typography sx={columnTitleStyle}>{t("projects.tableColumns.projectName")}</Typography>
                        {!projectEditable ? (
                            <CellContainer>{name ? name : selectedProject?.projectName}</CellContainer>
                        ) : (
                            <ProjectNameEditForm name={name} setName={setName} />
                        )}
                    </Grid>
                    <Grid item xs={6} md={1}>
                        <Typography sx={columnTitleStyle}>{t("projects.tableColumns.totalValue")}</Typography>
                        {!projectEditable ? (
                            <CellContainer>{selectedProject?.totalValue}</CellContainer>
                        ) : (
                            <TextField size="small" disabled value={selectedProject?.totalValue} />
                        )}
                    </Grid>
                    <Grid item sm={2}>
                        <Typography sx={columnTitleStyle}>{t("projects.tableColumns.organizationName")}</Typography>

                        {/* {!projectEditable ? (
                            <AvatarGroup max={3}>
                                <OrganizationUserAvatars organizations={selectedProject?.projectOwners} />
                            </AvatarGroup>
                        ) : (
                            // TODO implement later
                            <TextField disabled size="small" id="organizationName" variant="outlined" />
                        )} */}
                        <OrganizationSelect projectEditable={projectEditable} owner={owner} setOwner={setOwner} />
                    </Grid>
                    <Grid item sm={4}>
                        <Typography sx={columnTitleStyle}>{t("projects.tableColumns.planType")}</Typography>

                        {!projectEditable ? (
                            <Typography sx={{ border: "solid 1px #ddd", p: 0.5, overflow: "hidden" }}>
                                {planType.length > 0
                                    ? planType.map((pt: string) => {
                                          return <span key={pt}>{t(`projectTable.planTypeOptions.${pt}`)},</span>;
                                      })
                                    : selectedProject?.planType?.map((pt) => {
                                          return <span key={pt}>{t(`projectTable.planTypeOptions.${pt}`)},</span>;
                                      })}
                            </Typography>
                        ) : (
                            <PlanTypeEditForm planType={planType} setPlanType={setPlanType} />
                        )}
                    </Grid>
                    <Grid item xs={6} md={1.1}>
                        <Typography sx={columnTitleStyle}>{t("projects.tableColumns.startDate")}</Typography>

                        {!projectEditable ? (
                            <CellContainer>{startDate ? convertDayjsToString(startDate) : selectedProject?.startDate}</CellContainer>
                        ) : (
                            <DatePicker format="DD-MM-YYYY" slotProps={{ textField: { size: "small" } }} value={startDate} onChange={handleStartDateChange} />
                        )}
                    </Grid>
                    <Grid item xs={6} md={1.1}>
                        <Typography sx={columnTitleStyle}>{t("projects.tableColumns.endDate")}</Typography>

                        {!projectEditable ? (
                            <CellContainer>{endDate ? convertDayjsToString(endDate) : selectedProject?.endDate}</CellContainer>
                        ) : (
                            <DatePicker format="DD-MM-YYYY" slotProps={{ textField: { size: "small" } }} value={endDate} onChange={handleEndDateChange} />
                        )}
                    </Grid>
                    <Grid item xs={12} md={2}>
                        <Typography sx={columnTitleStyle}>{t("projects.tableColumns.priority")}</Typography>

                        {!projectEditable ? (
                            <CellContainer>
                                <span key={selectedProject?.priority?.value?.id}>
                                    {selectedProject?.priority?.value?.name ??
                                        `${selectedProject?.priority?.min?.name}-${selectedProject?.priority?.max?.name}`}
                                </span>
                            </CellContainer>
                        ) : (
                            // TODO Implement later
                            <PriorityEditForm projectPriority={projectPriority} setProjectPriority={setProjectPriority} />
                        )}
                    </Grid>
                    <Grid item xs={12} md={2}>
                        <Typography sx={columnTitleStyle}>{t("projects.tableColumns.projectPhase")}</Typography>

                        {!projectEditable ? (
                            <CellContainer>
                                {t(`projectTable.projectPhaseOptions.${projectPhase ? projectPhase : selectedProject?.projectPhase}`)}
                            </CellContainer>
                        ) : (
                            <PhaseEditForm projectPhase={projectPhase} setProjectPhase={setProjectPhase} />
                        )}
                    </Grid>
                    <Grid item xs={12} md={2}>
                        <Typography sx={columnTitleStyle}>{t("projects.tableColumns.municipalityRole")}</Typography>

                        {!projectEditable ? (
                            <CellContainer>
                                {selectedMunicipalityRole.length > 0
                                    ? selectedMunicipalityRole.map((mr: SelectModel) => {
                                          return <span key={mr.id}>{mr.name}</span>;
                                      })
                                    : selectedProject?.municipalityRole?.map((mr) => {
                                          return <span key={mr.id}>{mr.name}</span>;
                                      })}
                            </CellContainer>
                        ) : (
                            <MunicipalityRoleEditForm
                                selectedMunicipalityRole={selectedMunicipalityRole}
                                setSelectedMunicipalityRole={setSelectedMunicipalityRole}
                            />
                        )}
                    </Grid>
                    <Grid item xs={12} md={2}>
                        <Typography sx={columnTitleStyle}>{t("projects.tableColumns.confidentialityLevel")}</Typography>

                        {!projectEditable ? (
                            <CellContainer>
                                {confidentialityLevel
                                    ? t(`projectTable.confidentialityLevelOptions.${confidentialityLevel}`)
                                    : t(`projectTable.confidentialityLevelOptions.${selectedProject?.confidentialityLevel}`)}
                            </CellContainer>
                        ) : (
                            <ConfidentialityLevelEditForm confidentialityLevel={confidentialityLevel} setConfidentialityLevel={setConfidentialityLevel} />
                        )}
                    </Grid>
                    <Grid item sm={2}>
                        <Typography sx={columnTitleStyle}>{t("projects.tableColumns.projectLeader")}</Typography>

                        <OrganizationSelect projectEditable={projectEditable} owner={leader} setOwner={setLeader} isLeader={true} />

                        {/* {!projectEditable ? (
                            <Box sx={{ border: "solid 1px #ddd", overflow: "hidden" }}>
                                <AvatarGroup max={3}>
                                    <OrganizationUserAvatars organizations={selectedProject?.projectLeaders} />
                                </AvatarGroup>
                            </Box>
                        ) : (
                            // TODO LATER
                            <TextField disabled size="small" id="outlined-basic" variant="outlined" />
                        )} */}
                    </Grid>
                    <Grid item xs={12} md={4}>
                        <Typography sx={columnTitleStyle}>{t("projects.tableColumns.planningPlanStatus")}</Typography>

                        {!projectEditable ? (
                            <Typography sx={{ border: "solid 1px #ddd", p: 0.5, overflow: "hidden" }}>
                                {planStatus.length > 0
                                    ? planStatus.map((pp: string) => {
                                          return <span key={pp}>{t(`projectTable.planningPlanStatus.${pp}`)}</span>;
                                      })
                                    : selectedProject?.planningPlanStatus?.map((pp) => {
                                          return <span key={pp}>{t(`projectTable.planningPlanStatus.${pp}`)}</span>;
                                      })}
                            </Typography>
                        ) : (
                            <PlanStatusEditForm planStatus={planStatus} setPlanStatus={setPlanStatus} />
                        )}
                    </Grid>
                    <Grid item xs={12} md={2}>
                        <Typography sx={columnTitleStyle}>{t("projects.tableColumns.municipality")}</Typography>

                        {!projectEditable ? (
                            <Typography sx={{ border: "solid 1px #ddd", p: 0.5, overflow: "hidden" }}>
                                {selectedMunicipality.length > 0
                                    ? selectedMunicipality.map((municipality: SelectModel) => {
                                          return <span key={municipality.id}>{municipality.name},</span>;
                                      })
                                    : selectedProject?.municipality?.map((municipality) => {
                                          return <span key={municipality.id}>{municipality.name},</span>;
                                      })}
                            </Typography>
                        ) : (
                            <MunicipalityEditForm selectedMunicipality={selectedMunicipality} setSelectedMunicipality={setSelectedMunicipality} />
                        )}
                    </Grid>
                    <Grid item xs={12} md={2}>
                        <Typography sx={columnTitleStyle}>{t("projects.tableColumns.neighbourhood")}</Typography>

                        {!projectEditable ? (
                            <Typography sx={{ border: "solid 1px #ddd", p: 0.5, overflow: "hidden" }}>
                                {selectedNeighbourhood.length > 0
                                    ? selectedNeighbourhood.map((neighbourhood: SelectModel) => {
                                          return <span key={neighbourhood.id}>{neighbourhood.name},</span>;
                                      })
                                    : selectedProject?.buurt?.map((neighbourhood) => {
                                          return <span key={neighbourhood.id}>{neighbourhood.name},</span>;
                                      })}
                            </Typography>
                        ) : (
                            <NeighbourhoodEditForm selectedNeighbourhood={selectedNeighbourhood} setSelectedNeighbourhood={setSelectedNeighbourhood} />
                        )}
                    </Grid>
                    <Grid item xs={12} md={2}>
                        <Typography sx={columnTitleStyle}>{t("projects.tableColumns.wijk")}</Typography>

                        {!projectEditable ? (
                            <Typography sx={{ border: "solid 1px #ddd", p: 0.5, overflow: "hidden" }}>
                                {selectedWijk.length > 0
                                    ? selectedWijk.map((wijk: SelectModel) => {
                                          return <span key={wijk.id}>{wijk.name},</span>;
                                      })
                                    : selectedProject?.wijk?.map((wijk) => {
                                          return <span key={wijk.id}>{wijk.name},</span>;
                                      })}
                            </Typography>
                        ) : (
                            <WijkEditForm selectedWijk={selectedWijk} setSelectedWijk={setSelectedWijk} />
                        )}
                    </Grid>
                </Grid>
                {/* List huizen blok cards */}
                <HouseBlocksList houseBlocks={houseBlocks} setOpenHouseBlockDialog={setOpenHouseBlockDialog} />
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
