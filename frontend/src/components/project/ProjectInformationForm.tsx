import {
    Alert,
    Autocomplete,
    Box,
    InputLabel,
    ListItemText,
    MenuItem,
    OutlinedInput,
    Select,
    SelectChangeEvent,
    Stack,
    TextField,
    Typography,
} from "@mui/material";
import ColorSelector from "../ColorSelector";
import { DatePicker } from "@mui/x-date-pickers";

import projectLead from "../../api/json/projectleider.json";
import eigenaarOption from "../../api/json/eigenaar.json";
import { useTranslation } from "react-i18next";
import { MenuProps } from "../../utils/menuProps";
import { confidentialityLevelOptions, planTypeOptions, planningPlanStatus, projectPhaseOptions } from "../table/constants";
import { Dayjs } from "dayjs";
import { useEffect, useState } from "react";
import { getMunicipalityRoleList, getPriorityList } from "../../api/projectsTableServices";
import { SelectModel } from "../../api/projectsServices";
import { dateFormats } from "../../localization";

type Props = {
    setCreateProjectForm: (a: any) => void;
    createProjectForm: any;
    validationError: any;
};

export const ProjectInformationForm = ({ setCreateProjectForm, createProjectForm, validationError }: Props) => {
    const { t } = useTranslation();
    const [priorityOptionList, setPriorityOptionList] = useState<SelectModel[]>();
    const [municipalityRolesOptions, setMunicipalityRolesOptions] = useState<SelectModel[]>();

    useEffect(() => {
        getPriorityList().then((priorityList) => setPriorityOptionList(priorityList));
    }, []);
    useEffect(() => {
        getMunicipalityRoleList().then((roles) => setMunicipalityRolesOptions(roles));
    }, []);
    //ToDo add props later
    const handleColorChange = (newColor: string) => {
        setCreateProjectForm({
            ...createProjectForm,
            projectColor: newColor,
        });
    };
    const handlePlanTypeChange = (event: SelectChangeEvent<any>) => {
        const {
            target: { value },
        } = event;
        if (typeof value !== "string") {
            setCreateProjectForm({
                ...createProjectForm,
                planType: value,
            });
        }
    };
    const handlePlanStatusChange = (event: SelectChangeEvent<any>) => {
        const {
            target: { value },
        } = event;
        if (typeof value !== "string") {
            setCreateProjectForm({
                ...createProjectForm,
                planningPlanStatus: value,
            });
        }
    };
    const datePickerStyle = {
        "& .MuiFormHelperText-root": {
            color: "red", // Change this to your desired color
        },
    };
    return (
        <Box mt={4}>
            <Typography variant="h6" fontWeight="600">
                {t("createProject.informationForm.title")}
            </Typography>
            <Typography variant="subtitle1" fontWeight="500">
                {t("createProject.informationForm.nameLabel")}
            </Typography>
            <Stack direction="row" alignItems="center" justifyContent="space-between">
                <Stack width="100%">
                    <TextField
                        required
                        id="projectname"
                        size="small"
                        variant="outlined"
                        value={createProjectForm ? createProjectForm.projectName : ""}
                        onChange={(e) => {
                            setCreateProjectForm({
                                ...createProjectForm,
                                projectName: e.target.value,
                            });
                        }}
                    />
                    {validationError === "name" && <Alert severity="warning">{t("createProject.hasMissingRequiredAreas.name")}</Alert>}
                </Stack>

                <ColorSelector selectedColor={createProjectForm} defaultColor="#FF5733" onColorChange={handleColorChange} />
            </Stack>
            <Stack direction="row" alignItems="center" spacing={3} mt={2}>
                <Stack flex={6}>
                    <InputLabel id="plantype">{t("createProject.informationForm.planType")}</InputLabel>
                    <Select
                        fullWidth
                        size="small"
                        labelId="plantype"
                        id="plan-type-checkbox"
                        multiple
                        value={createProjectForm?.planType ? createProjectForm?.planType : []}
                        onChange={handlePlanTypeChange}
                        input={<OutlinedInput />}
                        renderValue={(selected) => selected.join(", ")}
                        MenuProps={MenuProps}
                    >
                        {planTypeOptions.map((pt) => (
                            <MenuItem key={pt.id} value={pt.id}>
                                <ListItemText primary={t(`projectTable.planTypeOptions.${pt.name}`)} />
                            </MenuItem>
                        ))}
                    </Select>
                </Stack>
                <Stack flex={2}>
                    <InputLabel id="startDate">{t("createProject.informationForm.startDate")}</InputLabel>
                    <DatePicker
                        sx={datePickerStyle}
                        format={dateFormats.keyboardDate}
                        slotProps={{
                            textField: { size: "small" },
                        }}
                        value={createProjectForm?.startDate}
                        onChange={(newValue: Dayjs | null) =>
                            setCreateProjectForm({
                                ...createProjectForm,
                                startDate: newValue,
                            })
                        }
                    />
                    {validationError === "startDate" && <Alert severity="warning">{t("createProject.hasMissingRequiredAreas.startDate")}</Alert>}
                </Stack>
                <Stack flex={2}>
                    <InputLabel id="enddate">{t("createProject.informationForm.endDate")} </InputLabel>
                    <DatePicker
                        sx={datePickerStyle}
                        format={dateFormats.keyboardDate}
                        slotProps={{
                            textField: { size: "small" },
                        }}
                        value={createProjectForm?.endDate}
                        onChange={(newValue: Dayjs | null) =>
                            setCreateProjectForm({
                                ...createProjectForm,
                                endDate: newValue,
                            })
                        }
                    />
                    {validationError === "endDate" && <Alert severity="warning">{t("createProject.hasMissingRequiredAreas.endDate")}</Alert>}
                </Stack>
            </Stack>
            <Stack direction="row" alignItems="center" spacing={3} mt={2}>
                <Stack flex={1}>
                    <InputLabel id="priority">{t("createProject.informationForm.priority")}</InputLabel>

                    <Autocomplete
                        id="priority-select"
                        size="small"
                        fullWidth
                        options={priorityOptionList ? priorityOptionList : []}
                        getOptionLabel={(option) => option.name}
                        value={createProjectForm?.priority?.value}
                        filterSelectedOptions
                        onChange={(_: any, newValue: SelectModel | null) =>
                            setCreateProjectForm({
                                ...createProjectForm,
                                priority: {
                                    ...createProjectForm?.priority,
                                    value: newValue,
                                },
                            })
                        }
                        renderInput={(params) => <TextField {...params} />}
                    />
                </Stack>
                <Stack flex={1}>
                    <InputLabel id="projectPhase">{t("createProject.informationForm.projectPhase")}</InputLabel>
                    <Select
                        fullWidth
                        size="small"
                        labelId="projectPhase"
                        id="project-phase-select"
                        value={createProjectForm?.projectPhase}
                        onChange={(e) =>
                            setCreateProjectForm({
                                ...createProjectForm,
                                projectPhase: e.target.value,
                            })
                        }
                    >
                        {projectPhaseOptions.map((ppo) => {
                            return (
                                <MenuItem key={ppo.id} value={ppo.id}>
                                    {t(`projectTable.projectPhaseOptions.${ppo.name}`)}
                                </MenuItem>
                            );
                        })}
                    </Select>
                    {validationError === "projectPhase" && <Alert severity="warning">{t("createProject.hasMissingRequiredAreas.projectPhase")}</Alert>}
                </Stack>
                <Stack flex={1}>
                    <InputLabel id="role">{t("createProject.informationForm.roleMunicipality")}</InputLabel>
                    <Autocomplete
                        size="small"
                        multiple
                        id="tags-outlined"
                        options={municipalityRolesOptions ? municipalityRolesOptions : []}
                        getOptionLabel={(option) => option.name}
                        value={createProjectForm?.municipalityRole}
                        filterSelectedOptions
                        onChange={(_: any, newValue: SelectModel[]) =>
                            setCreateProjectForm({
                                ...createProjectForm,
                                municipalityRole: newValue,
                            })
                        }
                        renderInput={(params) => <TextField {...params} />}
                    />
                </Stack>
            </Stack>
            <Stack direction="row" alignItems="center" spacing={3} mt={2}>
                <Stack flex={1}>
                    <InputLabel id="projectLeader">{t("createProject.informationForm.projectLeader")}</InputLabel>
                    <Select
                        labelId="projectLeader"
                        size="small"
                        id="project-leader"
                        value={createProjectForm ? createProjectForm.projectLeaders : ""}
                        label={t("createProject.informationForm.projectLeader")}
                        onChange={(
                            e, //TODO later
                        ) =>
                            setCreateProjectForm({
                                ...createProjectForm,
                                projectLeaders: [e.target.value],
                            })
                        }
                    >
                        {projectLead.map((lead) => {
                            return (
                                <MenuItem key={lead.ID} value={lead.name}>
                                    {lead.name}
                                </MenuItem>
                            );
                        })}
                    </Select>
                </Stack>
                <Stack flex={1}>
                    <InputLabel id="confidentialityLevel">{t("createProject.informationForm.confidentialityLevel")}</InputLabel>
                    <Select
                        labelId="confidentialityLevel"
                        size="small"
                        id="confidentiality-level-select"
                        value={createProjectForm?.confidentialityLevel}
                        onChange={(e) =>
                            setCreateProjectForm({
                                ...createProjectForm,
                                confidentialityLevel: e.target.value,
                            })
                        }
                    >
                        {confidentialityLevelOptions.map((ppo) => {
                            return (
                                <MenuItem key={ppo.id} value={ppo.id}>
                                    {t(`projectTable.confidentialityLevelOptions.${ppo.name}`)}
                                </MenuItem>
                            );
                        })}
                    </Select>
                    {validationError === "confidentialityLevel" && (
                        <Alert severity="warning">{t("createProject.hasMissingRequiredAreas.confidentialityLevel")}</Alert>
                    )}
                </Stack>
                <Stack flex={1}>
                    <InputLabel id="planningPlanStatus">{t("createProject.informationForm.planningPlanStatus")}</InputLabel>
                    <Select
                        size="small"
                        id="plan-status-checkbox"
                        sx={{ maxWidth: "538px" }}
                        multiple
                        value={createProjectForm?.planningPlanStatus ? createProjectForm?.planningPlanStatus : []}
                        onChange={handlePlanStatusChange}
                        input={<OutlinedInput />}
                        renderValue={(selected) => selected.join(", ")}
                        MenuProps={MenuProps}
                    >
                        {planningPlanStatus.map((pt) => (
                            <MenuItem key={pt.id} value={pt.id}>
                                <ListItemText primary={t(`projectTable.planningPlanStatus.${pt.name}`)} />
                            </MenuItem>
                        ))}
                    </Select>
                </Stack>
            </Stack>

            <Stack direction="row" alignItems="center" spacing={3} mt={2}>
                <Stack>
                    <InputLabel id="leader">{t("createProject.informationForm.owner")}</InputLabel>
                    <Select
                        sx={{ width: "370px" }}
                        labelId="leader"
                        id="owner"
                        size="small"
                        value={createProjectForm ? createProjectForm.projectOwners : ""}
                        label={t("createProject.informationForm.owner")}
                        onChange={(e) => {
                            //todo later
                            setCreateProjectForm({
                                ...createProjectForm,
                                projectOwners: [e.target.value],
                            });
                        }}
                    >
                        {eigenaarOption.map((lead) => {
                            return (
                                <MenuItem key={lead.ID} value={lead.naam}>
                                    {lead.naam}
                                </MenuItem>
                            );
                        })}
                    </Select>
                </Stack>
            </Stack>
        </Box>
    );
};
