import { Alert, Autocomplete, Box, ListItemText, MenuItem, OutlinedInput, Select, SelectChangeEvent, Stack, TextField, Typography } from "@mui/material";
import { DatePicker } from "@mui/x-date-pickers";
import ColorSelector from "../ColorSelector";

import dayjs, { Dayjs } from "dayjs";
import { useTranslation } from "react-i18next";
import { Project } from "../../api/projectsServices";
import { dateFormats } from "../../localization";
import { ConfidentialityLevelOptions, PlanStatusOptions, ProjectPhaseOptions } from "../../types/enums";
import { MenuProps } from "../../utils/menuProps";
import { OrganizationSelect } from "../../widgets/OrganizationSelect";
import { confidentialityLevelOptions, planTypeOptions, planningPlanStatus, projectPhaseOptions } from "../table/constants";
import { LabelComponent } from "./LabelComponent";
import { MunicipalityRoleEditForm } from "./project-with-house-block/MunicipalityRoleEditForm";
import useProperties from "../../hooks/useProperties";

type Props = {
    setCreateProjectForm: (a: Project) => void;
    createProjectForm: Project;
};
export const ProjectInformationForm = ({ setCreateProjectForm, createProjectForm }: Props) => {
    const { t } = useTranslation();

    const { priorityOptionList, municipalityRolesOptions } = useProperties();

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
            color: "red",
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

                <ColorSelector selectedColor={createProjectForm?.projectColor} defaultColor="#FF5733" onColorChange={handleColorChange} />
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
                        renderValue={(selected) => selected.map((s) => t(`projectTable.planTypeOptions.${s}`)).join(", ")}
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
                        value={createProjectForm?.startDate ? dayjs(createProjectForm?.startDate) : null}
                        onChange={(newValue: Dayjs | null) =>
                            setCreateProjectForm({
                                ...createProjectForm,
                                startDate: newValue ? newValue.format("YYYY-MM-DD") : undefined,
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
                        value={createProjectForm?.endDate ? dayjs(createProjectForm?.endDate) : null}
                        onChange={(newValue: Dayjs | null) =>
                            setCreateProjectForm({
                                ...createProjectForm,
                                endDate: newValue ? newValue.format("YYYY-MM-DD") : undefined,
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
                        onChange={(_, newValue) =>
                            setCreateProjectForm({
                                ...createProjectForm,
                                priority: {
                                    ...createProjectForm?.priority,
                                    value: newValue || undefined,
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
                                projectPhase: e.target.value as ProjectPhaseOptions,
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
                    <MunicipalityRoleEditForm
                        selectedMunicipalityRole={createProjectForm?.municipalityRole ?? []}
                        setSelectedMunicipalityRole={(newValue) => setCreateProjectForm({ ...createProjectForm, municipalityRole: newValue })}
                        options={municipalityRolesOptions ?? []}
                    />
                </Stack>
            </Stack>
            <Stack direction="row" alignItems="center" spacing={3} mt={2}>
                <Stack flex={1}>
                    <LabelComponent required={false} text={t("createProject.informationForm.projectLeader")} />
                    <OrganizationSelect
                        readOnly={false}
                        isLeader={true}
                        owner={createProjectForm?.projectLeaders ? createProjectForm.projectLeaders : []}
                        setOwner={(e) =>
                            setCreateProjectForm({
                                ...createProjectForm,
                                projectLeaders: e,
                            })
                        }
                    />
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
                                confidentialityLevel: e.target.value as ConfidentialityLevelOptions,
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
                        renderValue={(selected) => selected.map((s: PlanStatusOptions) => t(`projectTable.planningPlanStatus.${s}`)).join(", ")}
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
                <Stack flex={1}>
                    <LabelComponent required={false} text={t("createProject.informationForm.owner")} />
                    <OrganizationSelect
                        readOnly={false}
                        owner={createProjectForm?.projectOwners ? createProjectForm.projectOwners : []}
                        setOwner={(e) =>
                            setCreateProjectForm({
                                ...createProjectForm,
                                projectOwners: e,
                            })
                        }
                    />
                </Stack>
            </Stack>
        </Box>
    );
};
