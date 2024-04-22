import { Grid, Typography, TextField } from "@mui/material";
import { t } from "i18next";
import { Organization, PriorityModel, SelectModel } from "../../../api/projectsServices";
import { OrganizationSelect } from "../../../widgets/OrganizationSelect";
import { CellContainer } from "./CellContainer";
import { ConfidentialityLevelEditForm } from "./ConfidentialityLevelEditForm";
import { MunicipalityRoleEditForm } from "./MunicipalityRoleEditForm";
import { PhaseEditForm } from "./PhaseEditForm";
import { PlanStatusEditForm } from "./PlanStatusEditForm";
import { PlanTypeEditForm } from "./PlanTypeEditForm";
import { PriorityEditForm } from "./PriorityEditForm";
import { ProjectNameEditForm } from "./ProjectNameEditForm";
import { columnTitleStyle } from "./ProjectWithHouseBlock";
import { useContext } from "react";
import ProjectContext from "../../../context/ProjectContext";
import { PlanStatusOptions, PlanTypeOptions } from "../../../types/enums";
import dayjs, { Dayjs } from "dayjs";
import { DatePicker } from "@mui/x-date-pickers";
import { convertDayjsToString } from "../../../utils/convertDayjsToString";
import { capitalizeFirstLetters } from "../../../utils/stringFunctions";
import useProperties from "../../../hooks/useProperties";
type Props = {
    readOnly: boolean;
    name: string | null;
    setName: (name: string | null) => void;
    owner: Organization[];
    setOwner: (owner: Organization[]) => void;
    planType: PlanTypeOptions[];
    setPlanType: (planType: PlanTypeOptions[]) => void;
    startDate: Dayjs | null;
    endDate: Dayjs | null;
    setStartDate: (startDate: Dayjs | null) => void;
    setEndDate: (endDate: Dayjs | null) => void;
    projectPhase: string | undefined;
    setProjectPhase: (projectPhase: string | undefined) => void;
    projectPriority: PriorityModel | null;
    setProjectPriority: (projectPriority: PriorityModel | null) => void;
    selectedMunicipalityRole: SelectModel[];
    setSelectedMunicipalityRole: (selectedMunicipalityRole: SelectModel[]) => void;
    confidentialityLevel: string | undefined;
    setConfidentialityLevel: (confidentialityLevel: string | undefined) => void;
    leader: Organization[];
    setLeader: (leader: Organization[]) => void;
    planStatus: PlanStatusOptions[];
    setPlanStatus: (planStatus: PlanStatusOptions[]) => void;
    // selectedMunicipality: SelectModel[];
    // setSelectedMunicipality: (selectedMunicipality: SelectModel[]) => void;
    // selectedNeighbourhood: SelectModel[];
    // setSelectedNeighbourhood: (selectedNeighbourhood: SelectModel[]) => void;
    // selectedWijk: SelectModel[];
    // setSelectedWijk: (selectedWijk: SelectModel[]) => void;
};
type DateDisplayEditorProps = {
    readOnly: boolean;
    date: Dayjs | string | undefined;
    onChange: (newDate: Dayjs | null) => void;
};

const DateDisplayEditor = ({ readOnly, date, onChange }: DateDisplayEditorProps) => {
    const dayjsDate = typeof date === "string" ? dayjs(date) : date;
    if (readOnly) {
        return <CellContainer>{dayjsDate ? convertDayjsToString(dayjsDate) : ""}</CellContainer>;
    } else {
        return <DatePicker sx={{ width: "100%" }} slotProps={{ textField: { size: "small" } }} value={dayjsDate} onChange={onChange} />;
    }
};

export const ProjectProperties = ({
    projectPhase,
    setProjectPhase,
    setStartDate,
    setEndDate,
    readOnly,
    name,
    setName,
    owner,
    setOwner,
    planType,
    setPlanType,
    startDate,
    endDate,
    projectPriority,
    setProjectPriority,
    selectedMunicipalityRole,
    setSelectedMunicipalityRole,
    confidentialityLevel,
    setConfidentialityLevel,
    leader,
    setLeader,
    planStatus,
    setPlanStatus,
    // selectedMunicipality,
    // setSelectedMunicipality,
    // selectedNeighbourhood,
    // setSelectedNeighbourhood,
    // selectedWijk,
    // setSelectedWijk,
}: Props) => {
    const { selectedProject } = useContext(ProjectContext);
    const { priorityOptionList, municipalityRolesOptions } = useProperties();

    const handleStartDateChange = (newValue: Dayjs | null) => {
        if (newValue) {
            const startDate = newValue.endOf("day");
            setStartDate(startDate);
        } else {
            setStartDate(null);
        }
    };

    const handleEndDateChange = (newValue: Dayjs | null) => {
        if (newValue) {
            const endDate = newValue.endOf("day");
            setEndDate(endDate);
        } else {
            setEndDate(null);
        }
    };

    return (
        <Grid container my={2}>
            <Grid container>
                <Grid item xs={12} md={1}>
                    <Typography sx={columnTitleStyle}>{capitalizeFirstLetters(t("projects.tableColumns.projectName"))}</Typography>
                    {readOnly ? (
                        <CellContainer>{name ? name : selectedProject?.projectName}</CellContainer>
                    ) : (
                        <ProjectNameEditForm name={name} setName={setName} />
                    )}
                </Grid>
                <Grid item xs={12} md={1}>
                    <Typography sx={columnTitleStyle}>{capitalizeFirstLetters(t("projects.tableColumns.totalValue"))}</Typography>
                    {readOnly ? (
                        <CellContainer>{selectedProject?.totalValue ? selectedProject.totalValue : 0}</CellContainer>
                    ) : (
                        <TextField fullWidth size="small" disabled value={selectedProject?.totalValue ? selectedProject.totalValue : 0} />
                    )}
                </Grid>
                <Grid item xs={12} md={2}>
                    <Typography sx={columnTitleStyle}>{capitalizeFirstLetters(t("projects.tableColumns.organizationName"))}</Typography>
                    <OrganizationSelect readOnly={readOnly} owner={owner} setOwner={setOwner} />
                </Grid>
                <Grid item xs={12} md={8}>
                    <Typography sx={columnTitleStyle}>{capitalizeFirstLetters(t("projects.tableColumns.planType"))}</Typography>

                    {readOnly ? (
                        <CellContainer>
                            {planType.length > 0
                                ? planType.map((pt: string) => {
                                      return <span key={pt}>{capitalizeFirstLetters(t(`projectTable.planTypeOptions.${pt}`))},</span>;
                                  })
                                : selectedProject?.planType?.map((pt) => {
                                      return <span key={pt}>{capitalizeFirstLetters(t(`projectTable.planTypeOptions.${pt}`))},</span>;
                                  })}
                        </CellContainer>
                    ) : (
                        <PlanTypeEditForm planType={planType} setPlanType={setPlanType} />
                    )}
                </Grid>
            </Grid>
            <Grid container>
                <Grid item xs={12} md={1.1}>
                    <Typography sx={columnTitleStyle}>{capitalizeFirstLetters(t("projects.tableColumns.startDate"))}</Typography>
                    <DateDisplayEditor readOnly={readOnly} date={startDate ? startDate : selectedProject?.startDate} onChange={handleStartDateChange} />
                </Grid>
                <Grid item xs={12} md={1.1}>
                    <Typography sx={columnTitleStyle}>{capitalizeFirstLetters(t("projects.tableColumns.endDate"))}</Typography>
                    <DateDisplayEditor readOnly={readOnly} date={endDate ? endDate : selectedProject?.endDate} onChange={handleEndDateChange} />
                </Grid>
                <Grid item xs={12} md={1.8}>
                    <Typography sx={columnTitleStyle}>{capitalizeFirstLetters(t("projects.tableColumns.priority"))}</Typography>

                    {readOnly ? (
                        <CellContainer>
                            <span key={selectedProject?.priority?.value?.id}>
                                {selectedProject?.priority?.value?.name
                                    ? selectedProject?.priority?.value?.name
                                    : selectedProject?.priority?.min === null && selectedProject?.priority?.max === null
                                      ? ""
                                      : `${selectedProject?.priority?.min?.name}-${selectedProject?.priority?.max?.name}`}
                            </span>
                        </CellContainer>
                    ) : (
                        <PriorityEditForm projectPriority={projectPriority} setProjectPriority={setProjectPriority} options={priorityOptionList ?? []} />
                    )}
                </Grid>
                <Grid item xs={12} md={2}>
                    <Typography sx={columnTitleStyle}>{capitalizeFirstLetters(t("projects.tableColumns.projectPhase"))}</Typography>

                    {readOnly ? (
                        <CellContainer>
                            {capitalizeFirstLetters(t(`projectTable.projectPhaseOptions.${projectPhase ? projectPhase : selectedProject?.projectPhase}`))}
                        </CellContainer>
                    ) : (
                        <PhaseEditForm projectPhase={projectPhase} setProjectPhase={setProjectPhase} />
                    )}
                </Grid>
                <Grid item xs={12} md={2}>
                    <Typography sx={columnTitleStyle}>{capitalizeFirstLetters(t("projects.tableColumns.municipalityRole"))}</Typography>

                    {readOnly ? (
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
                            options={municipalityRolesOptions ?? []}
                        />
                    )}
                </Grid>
                <Grid item xs={12} md={2}>
                    <Typography sx={columnTitleStyle}>{capitalizeFirstLetters(t("projects.tableColumns.confidentialityLevel"))}</Typography>

                    {readOnly ? (
                        <CellContainer>
                            {confidentialityLevel
                                ? t(`projectTable.confidentialityLevelOptions.${confidentialityLevel}`)
                                : t(`projectTable.confidentialityLevelOptions.${selectedProject?.confidentialityLevel}`)}
                        </CellContainer>
                    ) : (
                        <ConfidentialityLevelEditForm confidentialityLevel={confidentialityLevel} setConfidentialityLevel={setConfidentialityLevel} />
                    )}
                </Grid>
                <Grid item xs={12} md={2}>
                    <Typography sx={columnTitleStyle}>{capitalizeFirstLetters(t("projects.tableColumns.projectLeader"))}</Typography>
                    <OrganizationSelect readOnly={readOnly} owner={leader} setOwner={setLeader} isLeader={true} />
                </Grid>
            </Grid>
            <Grid container>
                <Grid item xs={12} md={12}>
                    <Typography sx={columnTitleStyle}>{capitalizeFirstLetters(t("projects.tableColumns.planningPlanStatus"))}</Typography>

                    {readOnly ? (
                        <CellContainer>
                            {planStatus.length > 0
                                ? planStatus.map((pp: string) => {
                                      return <span key={pp}>{capitalizeFirstLetters(t(`projectTable.planningPlanStatus.${pp}`))},</span>;
                                  })
                                : selectedProject?.planningPlanStatus?.map((pp) => {
                                      return <span key={pp}>{capitalizeFirstLetters(t(`projectTable.planningPlanStatus.${pp}`))},</span>;
                                  })}
                        </CellContainer>
                    ) : (
                        <PlanStatusEditForm planStatus={planStatus} setPlanStatus={setPlanStatus} />
                    )}
                </Grid>
            </Grid>
        </Grid>
    );
};
