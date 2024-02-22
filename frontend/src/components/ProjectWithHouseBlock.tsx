import {
    Avatar,
    AvatarGroup,
    Box,
    Checkbox,
    Grid,
    ListItemText,
    MenuItem,
    OutlinedInput,
    Select,
    SelectChangeEvent,
    Stack,
    TextField,
    Typography,
} from "@mui/material";
import { ChangeEvent, useContext, useEffect, useState } from "react";
import ProjectContext from "../context/ProjectContext";
import EditIcon from "@mui/icons-material/Edit";
import SaveIcon from "@mui/icons-material/Save";
import { stringAvatar } from "../utils/stringAvatar";
import FormatColorFillIcon from "@mui/icons-material/FormatColorFill";
import { useTranslation } from "react-i18next";
import { DatePicker } from "@mui/x-date-pickers";
import dayjs, { Dayjs } from "dayjs";
import { convertDayjsToString } from "../utils/convertDayjsToString";
import { formatDate } from "../utils/formatDate";
import { confidentialityLevelOptions, planTypeOptions, planningPlanStatus, projectPhaseOptions } from "./table/constants";
import { getBuurtList, getMunicipalityList, getMunicipalityRoleList } from "../api/projectsTableServices";
import { OptionType } from "./ProjectsTableView";
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
    const [projectPhase, setProjectPhase] = useState<string>();
    const [confidentialityLevel, setConfidentialityLevel] = useState<string>();
    const [planType, setPlanType] = useState<string[]>([]);
    const [planStatus, setPlanStatus] = useState<string[]>([]);
    const [municipalityRolesOptions, setMunicipalityRolesOptions] = useState<OptionType[]>();
    const [municipalityOptions, setMunicipalityOptions] = useState<OptionType[]>();
    const [buurtOptions, setBuurtOptions] = useState<OptionType[]>();
    const [selectedMunicipalityRole, setSelectedMunicipalityRole] = useState<string[]>([]);
    const [selectedMunicipality, setSelectedMunicipality] = useState<string[]>([]);
    const [selectedBuurt, setSelectedBuurt] = useState<string[]>([]);
    const handleNameChange = (event: ChangeEvent<HTMLInputElement>) => {
        setName(event.target.value);
    };

    const handleStartDateChange = (newValue: Dayjs | null) => setStartDate(newValue);
    const handleEndDateChange = (newValue: Dayjs | null) => setEndDate(newValue);
    const handleProjectPhaseChange = (event: SelectChangeEvent) => {
        setProjectPhase(event.target.value as string);
    };
    const handleConfidentialityLevelChange = (event: SelectChangeEvent) => {
        setConfidentialityLevel(event.target.value as string);
    };
    const handlePlanTypeChange = (event: SelectChangeEvent<typeof planType>) => {
        const {
            target: { value },
        } = event;
        setPlanType(
            // On autofill we get a stringified value.
            typeof value === "string" ? value.split(",") : value,
        );
    };
    const handlePlanStatusChange = (event: SelectChangeEvent<typeof planStatus>) => {
        const {
            target: { value },
        } = event;
        setPlanStatus(
            // On autofill we get a stringified value.
            typeof value === "string" ? value.split(",") : value,
        );
    };
    const handleMunicipalityRoleChange = (event: SelectChangeEvent<typeof selectedMunicipalityRole>) => {
        const {
            target: { value },
        } = event;
        setSelectedMunicipalityRole(
            // On autofill we get a stringified value.
            typeof value === "string" ? value.split(",") : value,
        );
    };
    const handleMunicipalityChange = (event: SelectChangeEvent<typeof selectedMunicipality>) => {
        const {
            target: { value },
        } = event;
        setSelectedMunicipality(
            // On autofill we get a stringified value.
            typeof value === "string" ? value.split(",") : value,
        );
    };
    const handleBuurtChange = (event: SelectChangeEvent<typeof selectedBuurt>) => {
        const {
            target: { value },
        } = event;
        setSelectedBuurt(
            // On autofill we get a stringified value.
            typeof value === "string" ? value.split(",") : value,
        );
    };

    const { t } = useTranslation();

    const handleTotalValueChange = (event: ChangeEvent<HTMLInputElement>) => {
        setTotalValue(event.target.value);
    };

    const ITEM_HEIGHT = 48;
    const ITEM_PADDING_TOP = 8;
    const MenuProps = {
        PaperProps: {
            style: {
                maxHeight: ITEM_HEIGHT * 4.5 + ITEM_PADDING_TOP,
                width: 250,
            },
        },
    };

    useEffect(() => {
        getMunicipalityRoleList().then((roles) => setMunicipalityRolesOptions(roles));
    }, []);
    useEffect(() => {
        getMunicipalityList().then((municipalities) => setMunicipalityOptions(municipalities));
    }, []);
    useEffect(() => {
        getBuurtList().then((buurten) => setBuurtOptions(buurten));
    }, []);

    return (
        <Stack my={1} p={1} mb={10}>
            <Stack>
                {/* List project properties */}
                <Grid container my={2}>
                    <Grid
                        item
                        xs={12}
                        sx={{
                            backgroundColor: selectedProject?.projectColor,
                            color: "#FFFFFF",
                            p: 1,
                        }}
                        display="flex"
                        justifyContent="space-between"
                        alignItems="center"
                    >
                        {!projectEditable ? (
                            <Typography>
                                {t("projects.tableColumns.projectName")}: {name ? name : selectedProject?.projectName}
                            </Typography>
                        ) : (
                            <Stack direction="row" alignItems="center" spacing={1}>
                                <TextField
                                    size="small"
                                    sx={{ border: "solid 1px white" }}
                                    label={t("projects.tableColumns.projectName")}
                                    value={name ? name : selectedProject?.projectName}
                                    onChange={handleNameChange}
                                />
                            </Stack>
                        )}
                        <Box sx={{ cursor: "pointer" }}>
                            <FormatColorFillIcon sx={{ mr: 2 }} onClick={() => setOpenColorDialog(true)} />
                            {!projectEditable && <EditIcon onClick={() => setProjectEditable(true)} />}
                            {projectEditable && <SaveIcon onClick={() => setProjectEditable(false)} />}
                        </Box>
                    </Grid>
                    <Grid item xs={6} md={1}>
                        <Typography sx={columnTitleStyle}>{t("projects.tableColumns.totalValue")}</Typography>

                        {!projectEditable ? (
                            <Typography sx={{ border: "solid 1px #ddd", p: 0.5 }}>{totalValue ? totalValue : selectedProject?.totalValue}</Typography>
                        ) : (
                            <TextField
                                value={totalValue ? totalValue : selectedProject?.totalValue}
                                size="small"
                                id="total-value"
                                variant="outlined"
                                onChange={handleTotalValueChange}
                            />
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
                            <TextField size="small" id="outlined-basic" variant="outlined" />
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
                            <Select
                                fullWidth
                                size="small"
                                id="plan-type-checkbox"
                                multiple
                                value={planType.length > 0 ? planType : selectedProject?.planType}
                                onChange={handlePlanTypeChange}
                                input={<OutlinedInput />}
                                renderValue={(selected) => selected.join(", ")}
                                MenuProps={MenuProps}
                            >
                                {planTypeOptions.map((pt) => (
                                    <MenuItem key={pt.id} value={pt.id}>
                                        <Checkbox
                                            checked={
                                                planType.length > 0
                                                    ? planType.indexOf(pt.id) > -1
                                                    : selectedProject?.planType && selectedProject.planType.indexOf(pt.id) > -1
                                            }
                                        />
                                        <ListItemText primary={t(`projectTable.planTypeOptions.${pt.name}`)} />
                                    </MenuItem>
                                ))}
                            </Select>
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
                            <Select
                                fullWidth
                                size="small"
                                id="project-phase-select"
                                value={projectPhase ? projectPhase : selectedProject?.projectPhase}
                                onChange={handleProjectPhaseChange}
                            >
                                {projectPhaseOptions.map((ppo) => {
                                    return (
                                        <MenuItem key={ppo.id} value={ppo.id}>
                                            {t(`projectTable.projectPhaseOptions.${ppo.name}`)}
                                        </MenuItem>
                                    );
                                })}
                            </Select>
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
                            <Select
                                fullWidth
                                size="small"
                                id="plan-type-checkbox"
                                multiple
                                value={selectedMunicipalityRole.length > 0 ? selectedMunicipalityRole : selectedProject?.municipalityRole}
                                onChange={handleMunicipalityRoleChange}
                                input={<OutlinedInput />}
                                renderValue={(selected) => selected.join(", ")}
                                MenuProps={MenuProps}
                            >
                                {municipalityRolesOptions?.map((municipality) => (
                                    <MenuItem key={municipality.id} value={municipality.name}>
                                        <Checkbox
                                            checked={
                                                selectedMunicipalityRole.length > 0
                                                    ? selectedMunicipalityRole.indexOf(municipality.name) > -1
                                                    : selectedProject?.municipalityRole && selectedProject.municipalityRole.indexOf(municipality.name) > -1
                                            }
                                        />
                                        <ListItemText primary={municipality.name} />
                                    </MenuItem>
                                ))}
                            </Select>
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
                            <Select
                                fullWidth
                                size="small"
                                id="project-phase-select"
                                value={confidentialityLevel ? confidentialityLevel : selectedProject?.confidentialityLevel}
                                onChange={handleConfidentialityLevelChange}
                            >
                                {confidentialityLevelOptions.map((ppo) => {
                                    return (
                                        <MenuItem key={ppo.id} value={ppo.id}>
                                            {t(`projectTable.confidentialityLevelOptions.${ppo.name}`)}
                                        </MenuItem>
                                    );
                                })}
                            </Select>
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
                            <Select
                                fullWidth
                                size="small"
                                id="plan-status-checkbox"
                                multiple
                                value={planStatus.length > 0 ? planStatus : selectedProject?.planningPlanStatus}
                                onChange={handlePlanStatusChange}
                                input={<OutlinedInput />}
                                renderValue={(selected) => selected.join(", ")}
                                MenuProps={MenuProps}
                            >
                                {planningPlanStatus.map((pt) => (
                                    <MenuItem key={pt.id} value={pt.id}>
                                        <Checkbox
                                            checked={
                                                planStatus.length > 0
                                                    ? planStatus.indexOf(pt.id) > -1
                                                    : selectedProject?.planningPlanStatus && selectedProject.planningPlanStatus.indexOf(pt.id) > -1
                                            }
                                        />
                                        <ListItemText primary={t(`projectTable.planningPlanStatus.${pt.name}`)} />
                                    </MenuItem>
                                ))}
                            </Select>
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
                            <Select
                                fullWidth
                                size="small"
                                id="municipality-checkbox"
                                multiple
                                value={
                                    selectedMunicipality.length > 0 ? selectedMunicipality : selectedProject?.municipality ? selectedProject?.municipality : []
                                }
                                onChange={handleMunicipalityChange}
                                input={<OutlinedInput />}
                                renderValue={(selected) => selected.join(", ")}
                                MenuProps={MenuProps}
                            >
                                {municipalityOptions?.map((municipality) => (
                                    <MenuItem key={municipality.id} value={municipality.name}>
                                        <Checkbox
                                            checked={
                                                selectedMunicipality.length > 0
                                                    ? selectedMunicipality.indexOf(municipality.name) > -1
                                                    : selectedProject?.municipality && selectedProject.municipality.indexOf(municipality.name) > -1
                                            }
                                        />
                                        <ListItemText primary={municipality.name} />
                                    </MenuItem>
                                ))}
                            </Select>
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
                            <Select
                                fullWidth
                                size="small"
                                id="buurt-checkbox"
                                multiple
                                value={selectedBuurt.length > 0 ? selectedBuurt : selectedProject?.buurt ? selectedProject?.buurt : []}
                                onChange={handleBuurtChange}
                                input={<OutlinedInput />}
                                renderValue={(selected) => selected.join(", ")}
                                MenuProps={MenuProps}
                            >
                                {buurtOptions?.map((buurt) => (
                                    <MenuItem key={buurt.id} value={buurt.name}>
                                        <Checkbox
                                            checked={
                                                selectedBuurt.length > 0
                                                    ? selectedBuurt.indexOf(buurt.name) > -1
                                                    : selectedProject?.buurt && selectedProject.buurt.indexOf(buurt.name) > -1
                                            }
                                        />
                                        <ListItemText primary={buurt.name} />
                                    </MenuItem>
                                ))}
                            </Select>
                        )}
                    </Grid>
                    <Grid item xs={12} md={2}>
                        <Typography sx={columnTitleStyle}>{t("projects.tableColumns.wijk")}</Typography>

                        {!projectEditable ? (
                            <Typography sx={{ border: "solid 1px #ddd", p: 0.5, overflow: "hidden" }}>
                                {selectedProject?.wijk?.map((wijk: string) => {
                                    return <span key={wijk}>{wijk},</span>;
                                })}
                            </Typography>
                        ) : (
                            <TextField size="small" id="outlined-basic" variant="outlined" />
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
