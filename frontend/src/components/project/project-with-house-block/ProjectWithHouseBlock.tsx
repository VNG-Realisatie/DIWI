import { AvatarGroup, Box, Grid, Popover, Stack, TextField, Tooltip, Typography } from "@mui/material";
import { MouseEvent, useContext, useState } from "react";
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
import { BuurtEditForm } from "./BuurtEditForm";
import { WijkEditForm } from "./WijkEditForm";
import { defaultColors } from "../../ColorSelector";
import { BlockPicker, ColorResult } from "react-color";
import { CellContainer } from "./CellContainer";
import { OrganizationUserAvatars } from "../../OrganizationUserAvatars";
import { PlanStatusOptions, PlanTypeOptions } from "../../../types/enums";
// import { ProjectHouseBlockCardItem } from "./ProjectHouseBlockCardItem";

export const columnTitleStyle = {
    border: "solid 1px #ddd",
    p: 0.6,
    color: "#FFFFFF",
    backgroundColor: "#738092",
};

export const ProjectsWithHouseBlock = () => {
    const { selectedProject } = useContext(ProjectContext);
    const { selectedProjectColor, setSelectedProjectColor } = useContext(ProjectColorContext);
    const [projectEditable, setProjectEditable] = useState(false);
    const [openColorDialog, setOpenColorDialog] = useState(false);
    const [name, setName] = useState<string | null>();
    const [startDate, setStartDate] = useState<Dayjs | null>();
    const [endDate, setEndDate] = useState<Dayjs | null>();
    const [projectPhase, setProjectPhase] = useState<string | undefined>();
    const [confidentialityLevel, setConfidentialityLevel] = useState<string | undefined>();
    const [planType, setPlanType] = useState<PlanTypeOptions[]>([]);
    const [planStatus, setPlanStatus] = useState<PlanStatusOptions[]>([]);
    const [selectedMunicipalityRole, setSelectedMunicipalityRole] = useState<string[]>([]);
    const [selectedMunicipality, setSelectedMunicipality] = useState<string[]>([]);
    const [selectedBuurt, setSelectedBuurt] = useState<string[]>([]);
    const [selectedWijk, setSelectedWijk] = useState<string[]>([]);

    const [anchorEl, setAnchorEl] = useState<HTMLButtonElement | null>(null);
    const handleStartDateChange = (newValue: Dayjs | null) => setStartDate(newValue);

    const handleEndDateChange = (newValue: Dayjs | null) => setEndDate(newValue);

    const handleCancelChange = () => {
        setProjectEditable(false);
        setName(selectedProject?.projectName);
        //todo owner will be added later
        setPlanType([]);
        setStartDate(null);
        setEndDate(null);
        //todo priority will be added later
        setProjectPhase(undefined);
        setSelectedMunicipalityRole([]);
        setConfidentialityLevel(undefined);
        //todo add projectleader later
        setPlanStatus([]);
        setSelectedMunicipality([]);
        setSelectedBuurt([]);
        setSelectedWijk([]);
        selectedProject?.projectColor && setSelectedProjectColor(selectedProject?.projectColor);
    };

    const { t } = useTranslation();

    const handleColorChange = (newColor: ColorResult) => {
        const newColorString = `rgba(${newColor.rgb.r}, ${newColor.rgb.g}, ${newColor.rgb.b}, ${newColor.rgb.a})`;
        setSelectedProjectColor(newColorString);
    };

    const handleButtonClick = (event: MouseEvent<HTMLButtonElement>) => {
        setAnchorEl(event.currentTarget);
    };

    const handleClose = () => {
        setAnchorEl(null);
    };

    const open = Boolean(anchorEl);
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
                        <EditIcon sx={{ color: "#FFFFFF" }} onClick={() => setProjectEditable(true)} />
                    </Tooltip>
                )}
                {projectEditable && (
                    <>
                        <Tooltip placement="top" title={t("generic.cancelChanges")}>
                            <ClearIcon sx={{ mr: 2, color: "#FFFFFF" }} onClick={handleCancelChange} />
                        </Tooltip>
                        <Tooltip placement="top" title={t("generic.saveChanges")}>
                            <SaveIcon sx={{ color: "#FFFFFF" }} onClick={() => setProjectEditable(false)} />
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

                        <CellContainer>{selectedProject?.totalValue}</CellContainer>
                    </Grid>
                    <Grid item xs={6} md={1}>
                        <Typography sx={columnTitleStyle}>{t("projects.tableColumns.organizationName")}</Typography>

                        {!projectEditable ? (
                            <AvatarGroup max={3}>
                                <OrganizationUserAvatars organizations={selectedProject?.projectOwners} />
                            </AvatarGroup>
                        ) : (
                            // TODO implement later
                            <TextField size="small" id="organizationName" variant="outlined" />
                        )}
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
                            <DatePicker
                                format="DD-MM-YYYY"
                                slotProps={{ textField: { size: "small" } }}
                                value={startDate ? startDate : dayjs(formatDate(selectedProject?.startDate))}
                                onChange={handleStartDateChange}
                            />
                        )}
                    </Grid>
                    <Grid item xs={6} md={1.1}>
                        <Typography sx={columnTitleStyle}>{t("projects.tableColumns.endDate")}</Typography>

                        {!projectEditable ? (
                            <CellContainer>{endDate ? convertDayjsToString(endDate) : selectedProject?.endDate}</CellContainer>
                        ) : (
                            <DatePicker
                                format="DD-MM-YYYY"
                                slotProps={{ textField: { size: "small" } }}
                                value={endDate ? endDate : dayjs(formatDate(selectedProject?.endDate))}
                                onChange={handleEndDateChange}
                            />
                        )}
                    </Grid>
                    <Grid item xs={12} md={2}>
                        <Typography sx={columnTitleStyle}>{t("projects.tableColumns.priority")}</Typography>

                        {!projectEditable ? (
                            <CellContainer>
                                <span key={selectedProject?.priority?.value?.id}>{selectedProject?.priority?.value?.name},</span>
                            </CellContainer>
                        ) : (
                            // TODO Implement later
                            <TextField size="small" id="outlined-basic" variant="outlined" />
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
                                    ? selectedMunicipalityRole.map((mr: string) => {
                                          return <span key={mr}>{mr}</span>;
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
                    <Grid item xs={12} md={1}>
                        <Typography sx={columnTitleStyle}>{t("projects.tableColumns.projectLeader")}</Typography>

                        {!projectEditable ? (
                            <Box sx={{ border: "solid 1px #ddd", overflow: "hidden" }}>
                                <AvatarGroup max={3}>
                                    <OrganizationUserAvatars organizations={selectedProject?.projectLeaders} />
                                </AvatarGroup>
                            </Box>
                        ) : (
                            // TODO LATER
                            <TextField size="small" id="outlined-basic" variant="outlined" />
                        )}
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
                                    ? selectedMunicipality.map((municipality: string) => {
                                          return <span key={municipality}>{municipality},</span>;
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
                        <Typography sx={columnTitleStyle}>{t("projects.tableColumns.buurt")}</Typography>

                        {!projectEditable ? (
                            <Typography sx={{ border: "solid 1px #ddd", p: 0.5, overflow: "hidden" }}>
                                {selectedBuurt.length > 0
                                    ? selectedBuurt.map((buurt: string) => {
                                          return <span key={buurt}>{buurt},</span>;
                                      })
                                    : selectedProject?.buurt?.map((buurt) => {
                                          return <span key={buurt.id}>{buurt.name},</span>;
                                      })}
                            </Typography>
                        ) : (
                            <BuurtEditForm selectedBuurt={selectedBuurt} setSelectedBuurt={setSelectedBuurt} />
                        )}
                    </Grid>
                    <Grid item xs={12} md={2}>
                        <Typography sx={columnTitleStyle}>{t("projects.tableColumns.wijk")}</Typography>

                        {!projectEditable ? (
                            <Typography sx={{ border: "solid 1px #ddd", p: 0.5, overflow: "hidden" }}>
                                {selectedWijk.length > 0
                                    ? selectedWijk.map((wijk: string) => {
                                          return <span key={wijk}>{wijk},</span>;
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
                {/* <Grid container my={2}>
                    {houseblocks.map((hb: any, i: number) => {
                        return <ProjectHouseBlockCardItem hb={hb} key={i} />;
                    })}
                </Grid> */}
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
            </Stack>
        </Stack>
    );
};
