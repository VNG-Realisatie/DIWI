import { Alert, Autocomplete, Box, ListItemText, MenuItem, OutlinedInput, Select, SelectChangeEvent, Stack, TextField, Typography } from "@mui/material";
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
import { LabelComponent } from "./LabelComponent";

type Props = {
    setCreateProjectForm: (a: any) => void;
    createProjectForm: any;
    validationError: string;
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
            <LabelComponent required text="createProject.informationForm.nameLabel" />
            <Stack direction="row" alignItems="center" justifyContent="space-between">
                <Stack width="100%">
                    <TextField
                        required
                        id="projectname"
                        size="small"
                        variant="outlined"
                        value={createProjectForm?.projectName ?? ""}
                        onChange={(e) => {
                            setCreateProjectForm({
                                ...createProjectForm,
                                projectName: e.target.value,
                            });
                        }}
                    />
                    {!createProjectForm.projectName && <Alert severity="warning">{t("createProject.hasMissingRequiredAreas.name")}</Alert>}
                </Stack>

                <ColorSelector selectedColor={createProjectForm} defaultColor="#FF5733" onColorChange={handleColorChange} />
            </Stack>
            <Stack direction="row" alignItems="center" spacing={3} mt={2}>
                <Stack flex={6}>
                    <LabelComponent required={false} text={t("createProject.informationForm.planType")} />
                    <Select
                        fullWidth
                        size="small"
                        labelId="plantype"
                        id="plan-type-checkbox"
                        multiple
                        value={createProjectForm?.planType ?? []}
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
                    <LabelComponent required text={t("createProject.informationForm.startDate")} />
                    <DatePicker
                        sx={datePickerStyle}
                        format={dateFormats.keyboardDate}
                        slotProps={{
                            textField: { size: "small" },
                        }}
                        value={createProjectForm?.startDate ?? null}
                        onChange={(newValue: Dayjs | null) =>
                            setCreateProjectForm({
                                ...createProjectForm,
                                startDate: newValue,
                            })
                        }
                    />
                    {!createProjectForm.startDate && <Alert severity="warning">{t("createProject.hasMissingRequiredAreas.startDate")}</Alert>}
                </Stack>
                <Stack flex={2}>
                    <LabelComponent required text={t("createProject.informationForm.endDate")} />
                    <DatePicker
                        sx={datePickerStyle}
                        format={dateFormats.keyboardDate}
                        slotProps={{
                            textField: { size: "small" },
                        }}
                        value={createProjectForm?.endDate ?? null}
                        onChange={(newValue: Dayjs | null) =>
                            setCreateProjectForm({
                                ...createProjectForm,
                                endDate: newValue,
                            })
                        }
                    />
                    {!createProjectForm.endDate && <Alert severity="warning">{t("createProject.hasMissingRequiredAreas.endDate")}</Alert>}
                </Stack>
            </Stack>
            <Stack direction="row" alignItems="center" spacing={3} mt={2}>
                <Stack flex={1}>
                    <LabelComponent required={false} text={t("createProject.informationForm.priority")} />
                    <Autocomplete
                        id="priority-select"
                        size="small"
                        fullWidth
                        options={priorityOptionList ?? []}
                        getOptionLabel={(option) => option.name ?? ""}
                        value={createProjectForm?.priority?.value ?? null}
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
                    <LabelComponent required text={t("createProject.informationForm.projectPhase")} />
                    <Select
                        fullWidth
                        size="small"
                        labelId="projectPhase"
                        id="project-phase-select"
                        value={createProjectForm?.projectPhase ?? ""}
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
                    {!createProjectForm.projectPhase && <Alert severity="warning">{t("createProject.hasMissingRequiredAreas.projectPhase")}</Alert>}
                </Stack>
                <Stack flex={1}>
                    <LabelComponent required={false} text={t("createProject.informationForm.roleMunicipality")} />
                    <Autocomplete
                        size="small"
                        multiple
                        id="tags-outlined"
                        options={municipalityRolesOptions ?? []}
                        getOptionLabel={(option) => option.name ?? ""}
                        value={createProjectForm?.municipalityRole ?? []}
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
                    <LabelComponent required={false} text={t("createProject.informationForm.projectLeader")} />
                    <Select
                        labelId="projectLeader"
                        size="small"
                        id="project-leader"
                        value={createProjectForm?.projectLeaders ?? ""}
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
                                <MenuItem key={lead.ID} value={lead.name ?? ""}>
                                    {lead.name}
                                </MenuItem>
                            );
                        })}
                    </Select>
                </Stack>
                <Stack flex={1}>
                    <LabelComponent required text={t("createProject.informationForm.confidentialityLevel")} />
                    <Select
                        labelId="confidentialityLevel"
                        size="small"
                        id="confidentiality-level-select"
                        value={createProjectForm?.confidentialityLevel ?? ""}
                        onChange={(e) =>
                            setCreateProjectForm({
                                ...createProjectForm,
                                confidentialityLevel: e.target.value,
                            })
                        }
                    >
                        {confidentialityLevelOptions.map((ppo) => {
                            return (
                                <MenuItem key={ppo.id} value={ppo.id ?? ""}>
                                    {t(`projectTable.confidentialityLevelOptions.${ppo.name}`)}
                                </MenuItem>
                            );
                        })}
                    </Select>
                    {!createProjectForm.confidentialityLevel && (
                        <Alert severity="warning">{t("createProject.hasMissingRequiredAreas.confidentialityLevel")}</Alert>
                    )}
                </Stack>
                <Stack flex={1}>
                    <LabelComponent required={false} text={t("createProject.informationForm.planningPlanStatus")} />
                    <Select
                        size="small"
                        id="plan-status-checkbox"
                        sx={{ maxWidth: "538px" }}
                        multiple
                        value={createProjectForm?.planningPlanStatus ?? []}
                        onChange={handlePlanStatusChange}
                        input={<OutlinedInput />}
                        renderValue={(selected) => selected.join(", ")}
                        MenuProps={MenuProps}
                    >
                        {planningPlanStatus.map((pt) => (
                            <MenuItem key={pt.id} value={pt.id ?? null}>
                                <ListItemText primary={t(`projectTable.planningPlanStatus.${pt.name}`)} />
                            </MenuItem>
                        ))}
                    </Select>
                </Stack>
            </Stack>

            <Stack direction="row" alignItems="center" spacing={3} mt={2}>
                <Stack>
                    <LabelComponent required={false} text={t("createProject.informationForm.owner")} />
                    <Select
                        sx={{ width: "370px" }}
                        labelId="leader"
                        id="owner"
                        size="small"
                        value={createProjectForm?.projectOwners ?? ""}
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
                                <MenuItem key={lead.ID} value={lead.naam ?? ""}>
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
