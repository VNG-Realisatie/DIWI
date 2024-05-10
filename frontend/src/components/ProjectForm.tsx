import Grid from "@mui/material/Grid";
import { Project } from "../api/projectsServices";
import { Alert, ListItemText, MenuItem, OutlinedInput, Select, Stack, TextField, Typography } from "@mui/material";
import { t } from "i18next";
import { WizardCard } from "./project-wizard/WizardCard";
import { LabelComponent } from "./project/LabelComponent";
import ColorSelector from "./ColorSelector";
import { MenuProps } from "../utils/menuProps";
import { confidentialityLevelOptions, planTypeOptions, planningPlanStatus, projectPhaseOptions } from "./table/constants";
import { Dayjs } from "dayjs";
import { ConfidentialityLevelOptions, PlanStatusOptions } from "../types/enums";
import { OrganizationSelect } from "../widgets/OrganizationSelect";
import useProperties from "../hooks/useProperties";
import { CustomPropertiesProject } from "./project/project-with-house-block/CustomPropertiesProject";
import { CellContainer } from "./project/project-with-house-block/CellContainer";
import { useContext } from "react";
import HouseBlockContext from "../context/HouseBlockContext";
import TextInput from "./project/inputs/TextInput";
import CategoryInput from "./project/inputs/CategoryInput";
import DateInput from "./project/inputs/DateInput";

type Props = {
    readOnly: boolean;
    project: Project;
    setProject: (project: Project) => void;
    showColorPicker?: boolean;
    showAmounts?: boolean;
};

export const ProjectForm = ({ readOnly, project, setProject, showColorPicker = false, showAmounts = true }: Props) => {
    const { priorityOptionList, municipalityRolesOptions, districtOptions, neighbourhoodOptions, municipalityOptions } = useProperties();
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
                <Typography ml={2} mt={2} mb={2}>
                    <strong>{t(`projectDetail.explanation`)}</strong>
                </Typography>
                <WizardCard>
                    <Grid container spacing={2} alignItems="stretch">
                        {/* Name */}
                        <Grid item xs={12} md={showColorPicker ? 8 : 12}>
                            <Stack width="100%">
                                <TextInput
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
                            <DateInput
                                readOnly={readOnly}
                                value={project?.startDate ? project?.startDate : null}
                                setValue={(e: Dayjs | null) => {
                                    const newStartDate = e ? e.format("YYYY-MM-DD") : undefined;
                                    setProject({ ...project, startDate: newStartDate });
                                }}
                                mandatory={true}
                                title={t("createProject.informationForm.startDate")}
                                errorText={t("createProject.hasMissingRequiredAreas.startDate")}
                            />
                        </Grid>

                        {/* End date */}
                        <Grid item xs={12} md={4}>
                            <DateInput
                                readOnly={readOnly}
                                value={project?.endDate ? project?.endDate : null}
                                setValue={(e: Dayjs | null) => {
                                    const newEndDate = e ? e.format("YYYY-MM-DD") : undefined;
                                    setProject({ ...project, endDate: newEndDate });
                                }}
                                mandatory={true}
                                title={t("createProject.informationForm.endDate")}
                                errorText={t("createProject.hasMissingRequiredAreas.endDate")}
                            />
                        </Grid>

                        {/* Priority */}
                        <Grid item xs={12} md={4}>
                            <CategoryInput
                                readOnly={readOnly}
                                values={project?.priority?.value ?? null}
                                setValue={(_: any, newValue: any) =>
                                    setProject({
                                        ...project,
                                        priority: {
                                            ...project?.priority,
                                            value: newValue || undefined,
                                        },
                                    })
                                }
                                mandatory={false}
                                title={t("createProject.informationForm.priority")}
                                options={priorityOptionList ?? []}
                                multiple={false}
                            />
                        </Grid>

                        {/* Phase */}
                        <Grid item xs={12} md={4}>
                            <CategoryInput
                                readOnly={readOnly}
                                mandatory={true}
                                title={t("createProject.informationForm.projectPhase")}
                                options={projectPhaseOptions}
                                values={project?.projectPhase ? projectPhaseOptions.find((p) => p.id === project.projectPhase) : null}
                                setValue={(_, newValue) => {
                                    if (newValue && newValue.id) {
                                        setProject({
                                            ...project,
                                            projectPhase: newValue.id,
                                        });
                                    }
                                }}
                                multiple={false}
                                error={t("createProject.hasMissingRequiredAreas.projectPhase")}
                            />
                        </Grid>

                        {/* Role municipality */}
                        <Grid item xs={12} md={4}>
                            <CategoryInput
                                readOnly={readOnly}
                                values={project?.municipalityRole ?? []}
                                setValue={(_: any, newValue: any) => setProject({ ...project, municipalityRole: newValue })}
                                mandatory={false}
                                title={t("createProject.informationForm.roleMunicipality")}
                                options={municipalityRolesOptions ?? []}
                                multiple={true}
                            />
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

                        {/* Municipality */}
                        <Grid item xs={12} md={4}>
                            <CategoryInput
                                readOnly={readOnly}
                                values={project?.municipality ?? []}
                                setValue={(_: any, newValue: any) => setProject({ ...project, municipality: newValue })}
                                mandatory={false}
                                title={t("createProject.informationForm.municipality")}
                                options={municipalityOptions ?? []}
                                multiple={true}
                            />
                        </Grid>

                        {/* District */}
                        <Grid item xs={12} md={4}>
                            <CategoryInput
                                readOnly={readOnly}
                                values={project?.district ?? []}
                                setValue={(_: any, newValue: any) => setProject({ ...project, district: newValue })}
                                mandatory={false}
                                title={t("createProject.informationForm.district")}
                                options={districtOptions ?? []}
                                multiple={true}
                            />
                        </Grid>

                        {/* Neighbourhood */}
                        <Grid item xs={12} md={4}>
                            <CategoryInput
                                readOnly={readOnly}
                                values={project?.neighbourhood ?? []}
                                setValue={(_: any, newValue: any) => setProject({ ...project, neighbourhood: newValue })}
                                mandatory={false}
                                title={t("createProject.informationForm.neighbourhood")}
                                options={neighbourhoodOptions ?? []}
                                multiple={true}
                            />
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
