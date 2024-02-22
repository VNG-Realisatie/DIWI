import { Avatar, AvatarGroup, Box, Grid, Stack, TextField, Typography } from "@mui/material";
import { useContext, useState } from "react";
import ProjectContext from "../../../context/ProjectContext";
import EditIcon from "@mui/icons-material/Edit";
import SaveIcon from "@mui/icons-material/Save";
import { stringAvatar } from "../../../utils/stringAvatar";
import FormatColorFillIcon from "@mui/icons-material/FormatColorFill";
import { useTranslation } from "react-i18next";
import { DatePicker } from "@mui/x-date-pickers";
import dayjs, { Dayjs } from "dayjs";
import { convertDayjsToString } from "../../../utils/convertDayjsToString";
import { formatDate } from "../../../utils/formatDate";
import { ProjectNameEditForm } from "./ProjectNameEditForm";
import { TotalValueEditForm } from "./TotalValueEditForm";
import { PlanTypeEditForm } from "./PlanTypeEditForm";
import { PhaseEditForm } from "./PhaseEditForm";
import { MunicipalityRoleEditForm } from "./MunicipalityRoleEditForm";
import { ConfidentialityLevelEditForm } from "./ConfidentialityLevelEditForm";
import { PlanStatusEditForm } from "./PlanStatusEditForm";
import { MunicipalityEditForm } from "./MunicipalityEditForm";
import { BuurtEditForm } from "./BuurtEditForm";
import { WijkEditForm } from "./WijkEditForm";
// import { ProjectHouseBlockCardItem } from "./ProjectHouseBlockCardItem";

export const columnTitleStyle = {
    border: "solid 1px #ddd",
    p: 0.6,
    color: "#FFFFFF",
    backgroundColor: "#738092",
};

export const ProjectsWithHouseBlock = (props: any) => {
    const { selectedProject } = useContext(ProjectContext);
    const [projectEditable, setProjectEditable] = useState(false);
    const [openColorDialog, setOpenColorDialog] = useState(false);
    const [name, setName] = useState<string | undefined>();
    const [totalValue, setTotalValue] = useState<string | undefined>();
    const [startDate, setStartDate] = useState<Dayjs | null>();
    const [endDate, setEndDate] = useState<Dayjs | null>();
    const [projectPhase, setProjectPhase] = useState<string | undefined>();
    const [confidentialityLevel, setConfidentialityLevel] = useState<string | undefined>();
    const [planType, setPlanType] = useState<string[]>([]);
    const [planStatus, setPlanStatus] = useState<string[]>([]);
    const [selectedMunicipalityRole, setSelectedMunicipalityRole] = useState<string[]>([]);
    const [selectedMunicipality, setSelectedMunicipality] = useState<string[]>([]);
    const [selectedBuurt, setSelectedBuurt] = useState<string[]>([]);
    const [selectedWijk, setSelectedWijk] = useState<string[]>([]);

    const handleStartDateChange = (newValue: Dayjs | null) => setStartDate(newValue);

    const handleEndDateChange = (newValue: Dayjs | null) => setEndDate(newValue);

    const { t } = useTranslation();

    return (
        <Stack my={1} p={1} mb={10}>
            <Box sx={{ cursor: "pointer" }} position="absolute" right={10} top={55} zIndex={9999}>
                <FormatColorFillIcon sx={{ mr: 2, color: "#FFFFFF" }} onClick={() => setOpenColorDialog(true)} />
                {!projectEditable && <EditIcon sx={{ color: "#FFFFFF" }} onClick={() => setProjectEditable(true)} />}
                {projectEditable && <SaveIcon sx={{ color: "#FFFFFF" }} onClick={() => setProjectEditable(false)} />}
            </Box>
            <Stack>
                {/* List project properties */}
                <Grid container my={2}>
                    <Grid item xs={6} md={1}>
                        <Typography sx={columnTitleStyle}>{t("projects.tableColumns.projectName")}</Typography>
                        {!projectEditable ? (
                            <Typography sx={{ border: "solid 1px #ddd", p: 0.5 }}>{name ? name : selectedProject?.projectName}</Typography>
                        ) : (
                            <ProjectNameEditForm name={name} setName={setName} />
                        )}
                    </Grid>
                    <Grid item xs={6} md={1}>
                        <Typography sx={columnTitleStyle}>{t("projects.tableColumns.totalValue")}</Typography>

                        {!projectEditable ? (
                            <Typography sx={{ border: "solid 1px #ddd", p: 0.5 }}>{totalValue ? totalValue : selectedProject?.totalValue}</Typography>
                        ) : (
                            <TotalValueEditForm totalValue={totalValue} setTotalValue={setTotalValue} />
                        )}
                    </Grid>
                    <Grid item xs={6} md={1}>
                        <Typography sx={columnTitleStyle}>{t("projects.tableColumns.organizationName")}</Typography>

                        {!projectEditable ? (
                            <AvatarGroup max={3}>
                                {selectedProject?.projectOwners.map((owner: any[], id: number) => {
                                    return <Avatar key={id} {...stringAvatar(`${owner[2]} ${owner[3]}`)} />;
                                })}
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
                                    : selectedProject?.planType.map((pt: string) => {
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
                            <Typography sx={{ border: "solid 1px #ddd", p: 0.5 }}>
                                {startDate ? convertDayjsToString(startDate) : selectedProject?.startDate}
                            </Typography>
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
                            <Typography sx={{ border: "solid 1px #ddd", p: 0.5 }}>
                                {endDate ? convertDayjsToString(endDate) : selectedProject?.endDate}
                            </Typography>
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
                            <Typography sx={{ border: "solid 1px #ddd", p: 0.5 }}>
                                {selectedProject?.priority.map((p: string) => {
                                    return <span key={p}>{p},</span>;
                                })}
                            </Typography>
                        ) : (
                            // TODO Implement later
                            <TextField size="small" id="outlined-basic" variant="outlined" />
                        )}
                    </Grid>
                    <Grid item xs={12} md={2}>
                        <Typography sx={columnTitleStyle}>{t("projects.tableColumns.projectPhase")}</Typography>

                        {!projectEditable ? (
                            <Typography sx={{ border: "solid 1px #ddd", p: 0.5 }}>
                                {t(`projectTable.projectPhaseOptions.${projectPhase ? projectPhase : selectedProject?.projectPhase}`)}
                            </Typography>
                        ) : (
                            <PhaseEditForm projectPhase={projectPhase} setProjectPhase={setProjectPhase} />
                        )}
                    </Grid>
                    <Grid item xs={12} md={2}>
                        <Typography sx={columnTitleStyle}>{t("projects.tableColumns.municipalityRole")}</Typography>

                        {!projectEditable ? (
                            <Typography sx={{ border: "solid 1px #ddd", p: 0.5 }}>
                                {selectedMunicipalityRole.length > 0
                                    ? selectedMunicipalityRole.map((mr: string) => {
                                          return <span key={mr}>{mr}</span>;
                                      })
                                    : selectedProject?.municipalityRole.map((mr: string) => {
                                          return <span key={mr}>{mr}</span>;
                                      })}
                            </Typography>
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
                            <Typography sx={{ border: "solid 1px #ddd", p: 0.5 }}>
                                {confidentialityLevel
                                    ? t(`projectTable.confidentialityLevelOptions.${confidentialityLevel}`)
                                    : t(`projectTable.confidentialityLevelOptions.${selectedProject?.confidentialityLevel}`)}
                            </Typography>
                        ) : (
                            <ConfidentialityLevelEditForm confidentialityLevel={confidentialityLevel} setConfidentialityLevel={setConfidentialityLevel} />
                        )}
                    </Grid>
                    <Grid item xs={12} md={1}>
                        <Typography sx={columnTitleStyle}>{t("projects.tableColumns.projectLeader")}</Typography>

                        {!projectEditable ? (
                            <Box sx={{ border: "solid 1px #ddd", overflow: "hidden" }}>
                                <AvatarGroup max={3}>
                                    {selectedProject?.projectLeaders.map((leader: any[], id: number) => {
                                        return <Avatar key={id} {...stringAvatar(`${leader[2]} ${leader[3]}`)} />;
                                    })}
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
                                    : selectedProject?.planningPlanStatus.map((pp: string) => {
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
                                    : selectedProject?.municipality?.map((municipality: string) => {
                                          return <span key={municipality}>{municipality},</span>;
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
                                    : selectedProject?.buurt?.map((buurt: string) => {
                                          return <span key={buurt}>{buurt},</span>;
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
                                    : selectedProject?.wijk?.map((wijk: string) => {
                                          return <span key={wijk}>{wijk},</span>;
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
                {openColorDialog && <>Add here later color dialog</>}
            </Stack>
        </Stack>
    );
};
