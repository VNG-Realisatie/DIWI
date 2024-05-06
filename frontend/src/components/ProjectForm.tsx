import Grid from "@mui/material/Grid";
import { Project } from "../api/projectsServices";
import { Alert, Autocomplete, ListItemText, MenuItem, OutlinedInput, Select, Stack, TextField, Typography } from "@mui/material";
import { t } from "i18next";
import { WizardCard } from "./project-wizard/WizardCard";
import { LabelComponent } from "./project/LabelComponent";
import ColorSelector from "./ColorSelector";
import { MenuProps } from "../utils/menuProps";
import { confidentialityLevelOptions, planTypeOptions, planningPlanStatus, projectPhaseOptions } from "./table/constants";
import { DatePicker } from "@mui/x-date-pickers";
import dayjs, { Dayjs } from "dayjs";
import { dateFormats } from "../localization";
import { ConfidentialityLevelOptions, PlanStatusOptions, ProjectPhaseOptions } from "../types/enums";
import { OrganizationSelect } from "../widgets/OrganizationSelect";
import useProperties from "../hooks/useProperties";
import { CustomPropertiesProject } from "./project/project-with-house-block/CustomPropertiesProject";
import { CellContainer } from "./project/project-with-house-block/CellContainer";
import { useContext } from "react";
import HouseBlockContext from "../context/HouseBlockContext";
import NameInput from "./project/inputs/NameInput";

type Props = {
    readOnly: boolean;
    project: Project;
    setProject: (project: Project) => void;
    showColorPicker?: boolean;
    showAmounts?: boolean;
};

const datePickerStyle = {
    "& .MuiFormHelperText-root": {
        color: "red",
        width: "100%",
    },
    "& .MuiInputBase-input.Mui-disabled": {
        backgroundColor: "#0000", // set 0 opacity when disabled
    },
};

export const ProjectForm = ({ readOnly, project, setProject, showColorPicker = false, showAmounts = true }: Props) => {
    const { priorityOptionList, municipalityRolesOptions } = useProperties();
    const { houseBlocks } = useContext(HouseBlockContext);

    const constructionAmount = houseBlocks
        .filter((hb) => hb.mutation.kind === "CONSTRUCTION")
        .map((hb) => hb.mutation.amount ?? 0)
        .reduce((a, b) => a + b, 0);

    const demolitionAmount = houseBlocks
        .filter((hb) => hb.mutation.kind === "DEMOLITION")
        .map((hb) => hb.mutation.amount ?? 0)
        .reduce((a, b) => a + b, 0);

    const handleNameChange = (event: React.ChangeEvent<HTMLInputElement>) => {
        const newName = event.target.value.trimStart();
        setProject({ ...project, projectName: newName });
    };
    return (
        <Grid container spacing={2} alignItems="stretch">
            <Grid item xs={12}>
                <WizardCard>
                    <Grid container spacing={2} alignItems="stretch">
                        {/* Name */}
                        <Grid item xs={12} md={showColorPicker ? 8 : 12}>
                            <Stack width="100%">
                                <NameInput
                                    readOnly={readOnly}
                                    value={project?.projectName}
                                    setValue={handleNameChange}
                                    mandatory={true}
                                    title={t("createProject.informationForm.nameLabel")}
                                    errorText={t("createProject.hasMissingRequiredAreas.name")}
                                />
                            </Stack>
                        </Grid>
                        {/* Color: on the wizard page include this, on the project details this is excluded */}
                        {showColorPicker && (
                            <Grid item xs={12} md={4}>
                                <LabelComponent required readOnly={readOnly} text="createProject.informationForm.color" />
                                <ColorSelector
                                    selectedColor={project?.projectColor}
                                    defaultColor="#FF5733"
                                    disabled={readOnly}
                                    width={"100%"}
                                    onColorChange={(newColor) =>
                                        setProject({
                                            ...project,
                                            projectColor: newColor,
                                        })
                                    }
                                />
                            </Grid>
                        )}
                        {/* Plan type */}
                        <Grid item xs={12} md={4}>
                            <LabelComponent required={false} readOnly={readOnly} text={t("createProject.informationForm.planType")} />
                            <Select
                                fullWidth
                                disabled={readOnly}
                                sx={{
                                    "& .MuiInputBase-input.Mui-disabled": {
                                        backgroundColor: "#0000", // set 0 opacity when disabled
                                    },
                                }}
                                size="small"
                                labelId="plantype"
                                id="plan-type-checkbox"
                                multiple
                                value={project?.planType ?? []}
                                onChange={(event) => {
                                    const {
                                        target: { value },
                                    } = event;
                                    if (typeof value !== "string") {
                                        setProject({
                                            ...project,
                                            planType: value,
                                        });
                                    }
                                }}
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
                        </Grid>
                        {/* Start date */}
                        <Grid item xs={12} md={4}>
                            <LabelComponent required readOnly={readOnly} text={t("createProject.informationForm.startDate")} />
                            <DatePicker
                                sx={datePickerStyle}
                                format={dateFormats.keyboardDate}
                                disabled={readOnly}
                                slotProps={{
                                    textField: {
                                        size: "small",
                                        fullWidth: true,
                                    },
                                }}
                                value={project?.startDate ? dayjs(project?.startDate) : null}
                                onChange={(newValue: Dayjs | null) =>
                                    setProject({
                                        ...project,
                                        startDate: newValue ? newValue.format("YYYY-MM-DD") : undefined,
                                    })
                                }
                            />
                            {!project.startDate && <Alert severity="warning">{t("createProject.hasMissingRequiredAreas.startDate")}</Alert>}
                        </Grid>
                        {/* End date */}
                        <Grid item xs={12} md={4}>
                            <LabelComponent required readOnly={readOnly} text={t("createProject.informationForm.endDate")} />
                            <DatePicker
                                sx={datePickerStyle}
                                format={dateFormats.keyboardDate}
                                disabled={readOnly}
                                slotProps={{
                                    textField: { size: "small", fullWidth: true },
                                }}
                                value={project?.endDate ? dayjs(project?.endDate) : null}
                                onChange={(newValue: Dayjs | null) =>
                                    setProject({
                                        ...project,
                                        endDate: newValue ? newValue.format("YYYY-MM-DD") : undefined,
                                    })
                                }
                            />
                            {!project.endDate && <Alert severity="warning">{t("createProject.hasMissingRequiredAreas.endDate")}</Alert>}
                        </Grid>

                        {/* Priority */}
                        <Grid item xs={12} md={4}>
                            <LabelComponent required={false} readOnly={readOnly} text={t("createProject.informationForm.priority")} />
                            <Autocomplete
                                id="priority-select"
                                size="small"
                                disabled={readOnly}
                                sx={{
                                    "& .MuiInputBase-input.Mui-disabled": {
                                        backgroundColor: "#0000", // set 0 opacity when disabled
                                    },
                                }}
                                fullWidth
                                options={priorityOptionList ?? []}
                                getOptionLabel={(option) => option.name ?? ""}
                                value={project?.priority?.value ?? null}
                                filterSelectedOptions
                                onChange={(_, newValue) =>
                                    setProject({
                                        ...project,
                                        priority: {
                                            ...project?.priority,
                                            value: newValue || undefined,
                                        },
                                    })
                                }
                                renderInput={(params) => <TextField {...params} />}
                            />
                        </Grid>
                        {/* Phase */}
                        <Grid item xs={12} md={4}>
                            <LabelComponent required readOnly={readOnly} text={t("createProject.informationForm.projectPhase")} />
                            <Select
                                fullWidth
                                size="small"
                                disabled={readOnly}
                                sx={{
                                    "& .MuiInputBase-input.Mui-disabled": {
                                        backgroundColor: "#0000", // set 0 opacity when disabled
                                    },
                                }}
                                labelId="projectPhase"
                                id="project-phase-select"
                                value={project?.projectPhase ?? ""}
                                onChange={(e) =>
                                    setProject({
                                        ...project,
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
                            {!project.projectPhase && <Alert severity="warning">{t("createProject.hasMissingRequiredAreas.projectPhase")}</Alert>}
                        </Grid>
                        {/* Role municipality */}
                        <Grid item xs={12} md={4}>
                            <LabelComponent required={false} readOnly={readOnly} text={t("createProject.informationForm.roleMunicipality")} />
                            <Autocomplete
                                size="small"
                                disabled={readOnly}
                                sx={{
                                    "& .MuiInputBase-input.Mui-disabled": {
                                        backgroundColor: "#0000", // set 0 opacity when disabled
                                    },
                                }}
                                multiple
                                id="tags-outlined"
                                options={municipalityRolesOptions ?? []}
                                getOptionLabel={(option) => option.name}
                                value={project?.municipalityRole ?? []}
                                filterSelectedOptions
                                onChange={(_, newValue) => setProject({ ...project, municipalityRole: newValue })}
                                renderInput={(params) => <TextField {...params} />}
                            />
                        </Grid>

                        {/* Project lead */}
                        <Grid item xs={12} md={4}>
                            <LabelComponent required={false} readOnly={readOnly} text={t("createProject.informationForm.projectLeader")} />
                            <OrganizationSelect
                                readOnly={readOnly}
                                userGroup={project?.projectLeaders ? project.projectLeaders : []}
                                setUserGroup={(e) =>
                                    setProject({
                                        ...project,
                                        projectLeaders: e,
                                    })
                                }
                            />
                        </Grid>
                        {/* Confidentiality */}
                        <Grid item xs={12} md={4}>
                            <LabelComponent required readOnly={readOnly} text={t("createProject.informationForm.confidentialityLevel")} />
                            <Select
                                fullWidth
                                labelId="confidentialityLevel"
                                size="small"
                                disabled={readOnly}
                                sx={{
                                    "& .MuiInputBase-input.Mui-disabled": {
                                        backgroundColor: "#0000", // set 0 opacity when disabled
                                    },
                                }}
                                id="confidentiality-level-select"
                                value={project?.confidentialityLevel ?? ""}
                                onChange={(e) =>
                                    setProject({
                                        ...project,
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
                            {!project.confidentialityLevel && (
                                <Alert severity="warning">{t("createProject.hasMissingRequiredAreas.confidentialityLevel")}</Alert>
                            )}
                        </Grid>
                        {/* Planning plan status */}
                        <Grid item xs={12} md={4}>
                            <LabelComponent required={false} readOnly={readOnly} text={t("createProject.informationForm.planningPlanStatus")} />
                            <Select
                                fullWidth
                                size="small"
                                disabled={readOnly}
                                sx={{
                                    "& .MuiInputBase-input.Mui-disabled": {
                                        backgroundColor: "#0000", // set 0 opacity when disabled
                                    },
                                }}
                                id="plan-status-checkbox"
                                multiple
                                value={project?.planningPlanStatus ?? []}
                                onChange={(event) => {
                                    const {
                                        target: { value },
                                    } = event;
                                    if (typeof value !== "string") {
                                        setProject({
                                            ...project,
                                            planningPlanStatus: value,
                                        });
                                    }
                                }}
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
                        </Grid>

                        {/* Owner */}
                        <Grid item xs={12} md={4}>
                            <LabelComponent required={false} readOnly={readOnly} text={t("createProject.informationForm.owner")} />
                            <OrganizationSelect
                                readOnly={readOnly}
                                userGroup={project?.projectOwners ? project.projectOwners : []}
                                setUserGroup={(e) =>
                                    setProject({
                                        ...project,
                                        projectOwners: e,
                                    })
                                }
                            />
                        </Grid>
                        {/* District */}
                        <Grid item xs={12} md={4}>
                            <LabelComponent required={false} readOnly={readOnly} text={t("createProject.informationForm.district")} />
                            TODO
                        </Grid>
                        {/* Neighbourhood */}
                        <Grid item xs={12} md={4}>
                            <LabelComponent required={false} readOnly={readOnly} text={t("createProject.informationForm.neighbourhood")} />
                            TODO
                        </Grid>
                    </Grid>
                </WizardCard>
            </Grid>
            {/* AMOUNTS BLOCK */}
            {showAmounts && (
                <Grid item xs={12}>
                    <WizardCard>
                        <Grid container spacing={2} alignItems="stretch">
                            {/* Demolition */}
                            <Grid item xs={12} md={4}>
                                <LabelComponent required={false} readOnly={readOnly} text={t("createProject.houseBlocksForm.demolition")} />
                                <CellContainer>
                                    <LabelComponent required={false} readOnly={true} text={demolitionAmount.toString()} />
                                </CellContainer>
                            </Grid>
                            {/* Construction */}
                            <Grid item xs={12} md={4}>
                                <LabelComponent required={false} readOnly={readOnly} text={t("createProject.houseBlocksForm.grossPlanCapacity")} />
                                <CellContainer>
                                    <LabelComponent required={false} readOnly={true} text={constructionAmount.toString()} />
                                </CellContainer>
                            </Grid>
                            {/* Total */}
                            <Grid item xs={12} md={4}>
                                <LabelComponent required={false} readOnly={readOnly} text={t("createProject.houseBlocksForm.netPlanCapacity")} />
                                <CellContainer>
                                    <LabelComponent required={false} readOnly={true} text={(constructionAmount - demolitionAmount).toString()} />
                                </CellContainer>
                            </Grid>
                        </Grid>
                    </WizardCard>
                </Grid>
            )}
            {/* CUSTOM PROPERTIES */}
            <Grid item xs={12}>
                <WizardCard>
                    <Typography fontWeight={600} mb={2}>
                        {t(`customProperties.title`)}
                    </Typography>
                    <Grid container spacing={2} alignItems="stretch">
                        <CustomPropertiesProject
                            readOnly={readOnly}
                            customValues={project.customProperties ?? []}
                            setCustomValues={(newValue) => setProject({ ...project, customProperties: newValue })}
                        />
                    </Grid>
                </WizardCard>
            </Grid>
        </Grid>
    );
};
