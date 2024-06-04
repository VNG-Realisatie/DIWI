import { Stack, Typography } from "@mui/material";
import Grid from "@mui/material/Grid";
import { Dayjs } from "dayjs";
import { t } from "i18next";
import { useContext } from "react";
import { Project } from "../api/projectsServices";
import HouseBlockContext from "../context/HouseBlockContext";
import useProperties from "../hooks/useProperties";
import { UserGroupSelect } from "../widgets/UserGroupSelect";
import ColorSelector from "./ColorSelector";
import { WizardCard } from "./project-wizard/WizardCard";
import { LabelComponent } from "./project/LabelComponent";
import CategoryInput from "./project/inputs/CategoryInput";
import DateInput from "./project/inputs/DateInput";
import TextInput from "./project/inputs/TextInput";
import { CellContainer } from "./project/project-with-house-block/CellContainer";
import { CustomPropertiesProject } from "./project/project-with-house-block/CustomPropertiesProject";
import { confidentialityLevelOptions, planTypeOptions, planningPlanStatus, projectPhaseOptions } from "./table/constants";

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
                                    setValue={(event: React.ChangeEvent<HTMLInputElement>) => {
                                        const newName = event.target.value.trimStart();
                                        setProject({ ...project, projectName: newName });
                                    }}
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
                            <CategoryInput
                                readOnly={readOnly}
                                mandatory={false}
                                title={t("createProject.informationForm.planType")}
                                options={planTypeOptions}
                                values={planTypeOptions.filter((option) => (project.planType || []).includes(option.id))}
                                setValue={(_, newValue) => {
                                    setProject({
                                        ...project,
                                        planType: newValue.map((option: any) => option.id),
                                    });
                                }}
                                multiple={true}
                                translationPath="projectTable.planTypeOptions."
                            />
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
                                values={(project?.projectPhase && projectPhaseOptions.find((p) => p.id === project.projectPhase)) || null}
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
                                translationPath="projectTable.projectPhaseOptions."
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
                            <LabelComponent required={true} readOnly={readOnly} text={t("createProject.informationForm.owner")} />
                            <UserGroupSelect
                                readOnly={readOnly}
                                userGroup={project?.projectOwners ? project.projectOwners : []}
                                setUserGroup={(e) =>
                                    setProject({
                                        ...project,
                                        projectOwners: e,
                                    })
                                }
                                mandatory={true}
                                errorText={t("createProject.hasMissingRequiredAreas.owner")}
                            />
                        </Grid>

                        {/* Confidentiality */}
                        <Grid item xs={12} md={4}>
                            <CategoryInput
                                readOnly={readOnly}
                                mandatory={true}
                                title={t("createProject.informationForm.confidentialityLevel")}
                                options={confidentialityLevelOptions}
                                values={
                                    (project?.confidentialityLevel && confidentialityLevelOptions.find((cl) => cl.id === project.confidentialityLevel)) || null
                                }
                                setValue={(_, newValue) => {
                                    if (newValue && newValue.id) {
                                        setProject({
                                            ...project,
                                            confidentialityLevel: newValue.id,
                                        });
                                    }
                                }}
                                multiple={false}
                                error={t("createProject.hasMissingRequiredAreas.confidentialityLevel")}
                                translationPath="projectTable.confidentialityLevelOptions."
                            />
                        </Grid>
                        {/* Planning plan status */}
                        <Grid item xs={12} md={4}>
                            <CategoryInput
                                readOnly={readOnly}
                                mandatory={false}
                                title={t("createProject.informationForm.planningPlanStatus")}
                                options={planningPlanStatus}
                                values={planningPlanStatus.filter((option) => (project.planningPlanStatus || []).includes(option.id))}
                                setValue={(_, newValue) => {
                                    setProject({
                                        ...project,
                                        planningPlanStatus: newValue.map((option: any) => option.id),
                                    });
                                }}
                                multiple={true}
                                translationPath="projectTable.planningPlanStatus."
                            />
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
