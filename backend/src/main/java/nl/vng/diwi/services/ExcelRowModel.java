package nl.vng.diwi.services;

import lombok.Data;
import nl.vng.diwi.dal.VngRepository;
import nl.vng.diwi.dal.entities.Milestone;
import nl.vng.diwi.dal.entities.MilestoneState;
import nl.vng.diwi.dal.entities.Project;
import nl.vng.diwi.dal.entities.ProjectCategoryPropertyChangelog;
import nl.vng.diwi.dal.entities.ProjectCategoryPropertyChangelogValue;
import nl.vng.diwi.dal.entities.ProjectDurationChangelog;
import nl.vng.diwi.dal.entities.ProjectFaseChangelog;
import nl.vng.diwi.dal.entities.ProjectNameChangelog;
import nl.vng.diwi.dal.entities.ProjectPlanTypeChangelog;
import nl.vng.diwi.dal.entities.ProjectPlanTypeChangelogValue;
import nl.vng.diwi.dal.entities.ProjectPlanologischePlanstatusChangelog;
import nl.vng.diwi.dal.entities.ProjectPlanologischePlanstatusChangelogValue;
import nl.vng.diwi.dal.entities.ProjectState;
import nl.vng.diwi.dal.entities.ProjectTextCustomPropertyChangelog;
import nl.vng.diwi.dal.entities.Property;
import nl.vng.diwi.dal.entities.PropertyCategoryValue;
import nl.vng.diwi.dal.entities.User;
import nl.vng.diwi.dal.entities.enums.Confidentiality;
import nl.vng.diwi.dal.entities.enums.MilestoneStatus;
import nl.vng.diwi.dal.entities.enums.PlanStatus;
import nl.vng.diwi.dal.entities.enums.PlanType;
import nl.vng.diwi.dal.entities.enums.ProjectPhase;
import nl.vng.diwi.dal.entities.enums.ProjectStatus;
import nl.vng.diwi.dal.entities.superclasses.MilestoneChangeDataSuperclass;
import nl.vng.diwi.models.ExcelError;
import nl.vng.diwi.models.SelectModel;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

@Data
public class ExcelRowModel {

    private Integer id;
    private String projectName;
    private PlanType planType;
    private Boolean programming;

    private String priority; //TODO
    private String municipalityRole; //TODO

    private ProjectStatus projectStatus;
    private LocalDate projectStartDate;
    private LocalDate projectEndDate;

    private Map<ProjectPhase, LocalDate> projectPhases = new HashMap<>();
    private Map<PlanStatus, LocalDate> projectPlanStatuses = new HashMap<>();

    private Map<UUID, UUID> projectCategoryProperties = new HashMap<>();
    private Map<UUID, String> projectStringProperties = new HashMap<>();
    //TODO: projectBooleanProperties
    //TODO: projectNumericProperties
    //TODO: projectOrdinalProperties

    public void validate(Integer excelRowNo, List<ExcelError> rowErrors, LocalDate importTime) {
        //business logic validations - all individual values are valid by this point

        if (!projectEndDate.isAfter(projectStartDate)) {
            rowErrors.add(new ExcelError(excelRowNo, ExcelError.ERROR.PROJECT_START_DATE_AFTER_END_DATE));
        }
        switch (projectStatus) {
            case NEW -> {
                if (importTime.isAfter(projectStartDate) || importTime.isAfter(projectEndDate)) {
                    rowErrors.add(new ExcelError(excelRowNo, ExcelError.ERROR.PROJECT_DATES_WRONG_FOR_PROJECT_STATUS));
                }
            }
            case ACTIVE -> {
                if (importTime.isBefore(projectStartDate) || importTime.isAfter(projectEndDate)) {
                    rowErrors.add(new ExcelError(excelRowNo, ExcelError.ERROR.PROJECT_DATES_WRONG_FOR_PROJECT_STATUS));
                }
            }
            case REALIZED, TERMINATED -> {
                if (importTime.isBefore(projectStartDate) || importTime.isBefore(projectEndDate)) {
                    rowErrors.add(new ExcelError(excelRowNo, ExcelError.ERROR.PROJECT_DATES_WRONG_FOR_PROJECT_STATUS));
                }
            }
        }

        //TODO: project phase dates
        //TODO: project plan status dates
        //TODO: municipality / district / neighbourhood

    }


    private MilestoneStatus getMilestoneStatus(LocalDate milestoneTime, ZonedDateTime importTime) {
        if (milestoneTime.isAfter(importTime.toLocalDate())) {
            return MilestoneStatus.GEREALISEERD;
        } else {
            return MilestoneStatus.GEPLAND;
        }
    }

    private Milestone getOrCreateProjectMilestone(VngRepository repo, List<MilestoneState> milestoneStates, Project project, LocalDate milestoneDate,
                                                  MilestoneStatus milestoneStatus, User user, ZonedDateTime importTime) {

        MilestoneState existingMilestone = milestoneStates.stream().filter(ms -> ms.getDate().equals(milestoneDate)).findFirst().orElse(null);
        if (existingMilestone == null) {
            var milestone = new Milestone();
            milestone.setProject(project);
            repo.persist(milestone);

            var milestoneState = new MilestoneState();
            milestoneState.setDate(milestoneDate);
            milestoneState.setMilestone(milestone);
            milestoneState.setCreateUser(user);
            milestoneState.setChangeStartDate(importTime);
            milestoneState.setState(milestoneStatus == null ? getMilestoneStatus(milestoneDate, importTime) : milestoneStatus);
            repo.persist(milestoneState);

            milestoneStates.add(milestoneState);
            return milestone;
        } else {
            return existingMilestone.getMilestone();
        }
    }

    public SelectModel persistProjectAndHouseblocks(VngRepository repo, User user, ZonedDateTime importTime) {

        var project = new Project();
        repo.persist(project);

        List<MilestoneState> projectMilestones = new ArrayList<>();

        var startMilestone = getOrCreateProjectMilestone(repo, projectMilestones, project, projectStartDate, null, user, importTime);

        var endMilestoneStatus = projectStatus == ProjectStatus.TERMINATED ? MilestoneStatus.AFGEBROKEN : getMilestoneStatus(projectEndDate, importTime);
        var endMilestone = getOrCreateProjectMilestone(repo, projectMilestones, project,projectEndDate, endMilestoneStatus, user, importTime);

        Consumer<MilestoneChangeDataSuperclass> setChangelogValues = (MilestoneChangeDataSuperclass entity) -> {
            entity.setStartMilestone(startMilestone);
            entity.setEndMilestone(endMilestone);
            entity.setCreateUser(user);
            entity.setChangeStartDate(importTime);
        };

        var duration = new ProjectDurationChangelog();
        setChangelogValues.accept(duration);
        duration.setProject(project);
        repo.persist(duration);

        var name = new ProjectNameChangelog();
        name.setProject(project);
        name.setName(projectName);
        setChangelogValues.accept(name);
        repo.persist(name);

        var state = new ProjectState();
        state.setProject(project);
        state.setCreateUser(user);
        state.setChangeStartDate(importTime);
        state.setConfidentiality(Confidentiality.PRIVE);
        state.setColor("#000000");
        repo.persist(state);

        if (planType != null) {
            var planTypeChangelog = new ProjectPlanTypeChangelog();
            planTypeChangelog.setProject(project);
            setChangelogValues.accept(planTypeChangelog);
            repo.persist(planTypeChangelog);
            var planTypeValue = new ProjectPlanTypeChangelogValue();
            planTypeValue.setPlanTypeChangelog(planTypeChangelog);
            planTypeValue.setPlanType(planType);
            repo.persist(planTypeValue);
        }

        if (!projectPhases.isEmpty()) {
            Milestone phaseEndMilestone = endMilestone;
            List<ProjectPhase> projectPhasesList = Arrays.stream(ProjectPhase.values()).sorted(Collections.reverseOrder()).toList();
            for (ProjectPhase phase : projectPhasesList) {
                if (projectPhases.containsKey(phase)) {
                    LocalDate phaseStartDate = projectPhases.get(phase);
                    Milestone phaseStartMilestone = getOrCreateProjectMilestone(repo, projectMilestones, project, phaseStartDate, null, user, importTime);

                    var phaseChangelog = new ProjectFaseChangelog();
                    phaseChangelog.setProject(project);
                    phaseChangelog.setStartMilestone(phaseStartMilestone);
                    phaseChangelog.setEndMilestone(phaseEndMilestone);
                    phaseChangelog.setChangeStartDate(importTime);
                    phaseChangelog.setCreateUser(user);
                    phaseChangelog.setProjectPhase(phase);

                    repo.persist(phaseChangelog);
                    phaseEndMilestone = phaseStartMilestone;
                }
            }
        }
        if (!projectPlanStatuses.isEmpty()) {
            Milestone planStatusEndMilestone = endMilestone;
            List<PlanStatus> planStatusesList = Arrays.stream(PlanStatus.values()).sorted(Collections.reverseOrder()).toList();
            for (PlanStatus planStatus : planStatusesList) {
                if (projectPlanStatuses.containsKey(planStatus)) {
                    LocalDate planStatusStartDate = projectPlanStatuses.get(planStatus);
                    Milestone planStatusStartMilestone = getOrCreateProjectMilestone(repo, projectMilestones, project, planStatusStartDate, null, user, importTime);

                    var planStatusChangelog = new ProjectPlanologischePlanstatusChangelog();
                    planStatusChangelog.setProject(project);
                    planStatusChangelog.setStartMilestone(planStatusStartMilestone);
                    planStatusChangelog.setEndMilestone(planStatusEndMilestone);
                    planStatusChangelog.setChangeStartDate(importTime);
                    planStatusChangelog.setCreateUser(user);
                    repo.persist(planStatusChangelog);

                    var planStatusChangelogValue = new ProjectPlanologischePlanstatusChangelogValue();
                    planStatusChangelogValue.setPlanStatusChangelog(planStatusChangelog);
                    planStatusChangelogValue.setPlanStatus(planStatus);
                    repo.persist(planStatusChangelogValue);

                    planStatusEndMilestone = planStatusStartMilestone;
                }
            }
        }

        if (!projectCategoryProperties.isEmpty()) {
            for (Map.Entry<UUID, UUID> categoryEntry : projectCategoryProperties.entrySet()) {
                var projectPropertyChangelog = new ProjectCategoryPropertyChangelog();
                projectPropertyChangelog.setProject(project);
                setChangelogValues.accept(projectPropertyChangelog);
                projectPropertyChangelog.setProperty(repo.getReferenceById(Property.class, categoryEntry.getKey()));
                repo.persist(projectPropertyChangelog);

                var projectPropertyChangelogValue = new ProjectCategoryPropertyChangelogValue();
                projectPropertyChangelogValue.setCategoryChangelog(projectPropertyChangelog);
                projectPropertyChangelogValue.setCategoryValue(repo.getReferenceById(PropertyCategoryValue.class, categoryEntry.getValue()));
                repo.persist(projectPropertyChangelogValue);
            }
        }

        if (!projectStringProperties.isEmpty()) {
            for (Map.Entry<UUID, String> stringEntry : projectStringProperties.entrySet()) {
                var projectTextChangelog = new ProjectTextCustomPropertyChangelog();
                projectTextChangelog.setProject(project);
                setChangelogValues.accept(projectTextChangelog);
                projectTextChangelog.setProperty(repo.getReferenceById(Property.class, stringEntry.getKey()));
                projectTextChangelog.setValue(stringEntry.getValue());
                repo.persist(projectTextChangelog);
            }
        }

        if (programming != null) {
//            TODO
        }
        return new SelectModel(project.getId(), projectName);

    }

}
