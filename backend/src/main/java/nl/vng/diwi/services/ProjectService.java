package nl.vng.diwi.services;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import io.hypersistence.utils.hibernate.type.range.Range;
import nl.vng.diwi.dal.entities.CustomCategoryValue;
import nl.vng.diwi.dal.entities.CustomOrdinalValue;
import nl.vng.diwi.dal.entities.CustomProperty;
import nl.vng.diwi.dal.entities.ProjectBooleanCustomPropertyChangelog;
import nl.vng.diwi.dal.entities.ProjectCategoryCustomPropertyChangelog;
import nl.vng.diwi.dal.entities.ProjectCategoryCustomPropertyChangelogValue;
import nl.vng.diwi.dal.entities.ProjectNumericCustomPropertyChangelog;
import nl.vng.diwi.dal.entities.ProjectOrdinalCustomPropertyChangelog;
import nl.vng.diwi.dal.entities.ProjectTextCustomPropertyChangelog;
import nl.vng.diwi.models.ProjectHouseblockCustomPropertyModel;
import nl.vng.diwi.models.SingleValueOrRangeModel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import nl.vng.diwi.dal.FilterPaginationSorting;
import nl.vng.diwi.dal.VngRepository;
import nl.vng.diwi.dal.entities.Milestone;
import nl.vng.diwi.dal.entities.MilestoneState;
import nl.vng.diwi.dal.entities.Project;
import nl.vng.diwi.dal.entities.ProjectDurationChangelog;
import nl.vng.diwi.dal.entities.ProjectFaseChangelog;
import nl.vng.diwi.dal.entities.ProjectGemeenteRolChangelog;
import nl.vng.diwi.dal.entities.ProjectGemeenteRolValue;
import nl.vng.diwi.dal.entities.ProjectListSqlModel;
import nl.vng.diwi.dal.entities.ProjectNameChangelog;
import nl.vng.diwi.dal.entities.ProjectPlanTypeChangelog;
import nl.vng.diwi.dal.entities.ProjectPlanTypeChangelogValue;
import nl.vng.diwi.dal.entities.ProjectPlanologischePlanstatusChangelog;
import nl.vng.diwi.dal.entities.ProjectPlanologischePlanstatusChangelogValue;
import nl.vng.diwi.dal.entities.ProjectPrioriseringChangelog;
import nl.vng.diwi.dal.entities.ProjectPrioriseringValue;
import nl.vng.diwi.dal.entities.ProjectRegistryLinkChangelog;
import nl.vng.diwi.dal.entities.ProjectRegistryLinkChangelogValue;
import nl.vng.diwi.dal.entities.ProjectState;
import nl.vng.diwi.dal.entities.User;
import nl.vng.diwi.dal.entities.enums.Confidentiality;
import nl.vng.diwi.dal.entities.enums.MilestoneStatus;
import nl.vng.diwi.dal.entities.enums.PlanStatus;
import nl.vng.diwi.dal.entities.enums.PlanType;
import nl.vng.diwi.dal.entities.enums.ProjectPhase;
import nl.vng.diwi.dal.entities.enums.ProjectRole;
import nl.vng.diwi.dal.entities.enums.ValueType;
import nl.vng.diwi.dal.entities.superclasses.MilestoneChangeDataSuperclass;
import nl.vng.diwi.models.MilestoneModel;
import nl.vng.diwi.models.PlotModel;
import nl.vng.diwi.models.ProjectListModel;
import nl.vng.diwi.models.ProjectSnapshotModel;
import nl.vng.diwi.models.superclasses.ProjectCreateSnapshotModel;
import nl.vng.diwi.rest.VngBadRequestException;
import nl.vng.diwi.rest.VngNotFoundException;
import nl.vng.diwi.rest.VngServerErrorException;

public class ProjectService {
    private static final Logger logger = LogManager.getLogger();

    public ProjectService() {
    }

    public Project getCurrentProject(VngRepository repo, UUID uuid) {
        return repo.getProjectsDAO().getCurrentProject(uuid);
    }

    public ProjectSnapshotModel getProjectSnapshot(VngRepository repo, UUID projectUuid) throws VngNotFoundException {

        ProjectListSqlModel projectModel = repo.getProjectsDAO().getProjectByUuid(projectUuid);

        if (projectModel == null) {
            logger.error("Project with uuid {} was not found.", projectUuid);
            throw new VngNotFoundException();
        }

        ProjectSnapshotModel snapshotModel = new ProjectSnapshotModel(projectModel);
        snapshotModel.setCustomProperties(getProjectCustomProperties(repo, projectUuid));

        return snapshotModel;
    }

    public List<ProjectListModel> getProjectsTable(VngRepository repo, FilterPaginationSorting filtering) {
        List<ProjectListSqlModel> projectsTable = repo.getProjectsDAO().getProjectsTable(filtering);
        List<ProjectListModel> result = projectsTable.stream().map(ProjectListModel::new).toList();
        return result;
    }

    public List<ProjectHouseblockCustomPropertyModel> getProjectCustomProperties(VngRepository repo, UUID projectUuid) {

        return repo.getProjectsDAO().getProjectCustomProperties(projectUuid).stream()
                .map(ProjectHouseblockCustomPropertyModel::new).toList();

    }

    public List<PlotModel> getCurrentPlots(Project project) {

        Milestone projectStartMilestone = project.getDuration().get(0).getStartMilestone();
        LocalDate projectStartDate = (new MilestoneModel(projectStartMilestone)).getDate();

        LocalDate referenceDate = LocalDate.now();
        if (projectStartDate.isAfter(referenceDate)) {
            referenceDate = projectStartDate;
        }
        LocalDate finalReferenceDate = referenceDate;

        var currentChangelog = project.getRegistryLinks().stream()
            .filter(pc -> !(new MilestoneModel(pc.getStartMilestone())).getDate().isAfter(finalReferenceDate)
                && (new MilestoneModel(pc.getEndMilestone())).getDate().isAfter(finalReferenceDate))
            .findFirst().orElse(null);

        if (currentChangelog != null) {
            return currentChangelog.getValues().stream().map(PlotModel::new).toList();
        }

        return new ArrayList<>();
    }

    public void setPlots(VngRepository repo, Project project, List<PlotModel> plots, UUID loggedInUserUuid) {

        var currentDate = LocalDate.now();

        var oldClAfterUpdate = new ProjectRegistryLinkChangelog();
        ProjectRegistryLinkChangelog newCl = null;

        if (!plots.isEmpty()) {
            newCl = new ProjectRegistryLinkChangelog();
            newCl.setProject(project);
        }

        var oldCl = prepareChangelogValuesToUpdate(repo, project, project.getRegistryLinks(), newCl, oldClAfterUpdate, loggedInUserUuid, currentDate);

        if (newCl != null) {
            repo.persist(newCl);
            for (var plot : plots) {
                repo.persist(ProjectRegistryLinkChangelogValue.builder()
                    .brkGemeenteCode(plot.getBrkGemeenteCode())
                    .brkPerceelNummer(plot.getBrkPerceelNummer())
                    .brkSectie(plot.getBrkSectie())
                    .brkSelectie(plot.getBrkSelectie())
                    .geoJson(plot.getGeoJson())
                    .projectRegistryLinkChangelog(newCl)
                    .build());
            }
        }

        if (oldCl != null) {
            repo.persist(oldCl);
            if (oldClAfterUpdate.getStartMilestone() != null) {
                // it is a current project && it had a non-null changelog before the update
                oldClAfterUpdate.setProject(project);
                repo.persist(oldClAfterUpdate);

                for (var oldClValue : oldCl.getValues()) {
                    repo.persist(ProjectRegistryLinkChangelogValue.builder()
                        .brkGemeenteCode(oldClValue.getBrkGemeenteCode())
                        .brkPerceelNummer(oldClValue.getBrkPerceelNummer())
                        .brkSectie(oldClValue.getBrkSectie())
                        .brkSelectie(oldClValue.getBrkSelectie())
                        .geoJson(oldClValue.getGeoJson())
                        .projectRegistryLinkChangelog(oldClAfterUpdate)
                        .build());
                }
            }
        }
    }

    public Project createProject(VngRepository repo, UUID loggedInUserUuid, ProjectCreateSnapshotModel projectData, ZonedDateTime now)
            throws VngServerErrorException {
        var user = repo.getReferenceById(User.class, loggedInUserUuid);

        var project = new Project();
        repo.persist(project);

        var startMilestone = new Milestone();
        startMilestone.setProject(project);
        repo.persist(startMilestone);

        var startMilestoneState = new MilestoneState();
        startMilestoneState.setDate(projectData.getStartDate());
        startMilestoneState.setMilestone(startMilestone);
        startMilestoneState.setCreateUser(user);
        startMilestoneState.setChangeStartDate(now);
        startMilestoneState.setState(MilestoneStatus.GEPLAND);
        repo.persist(startMilestoneState);

        var endMilestone = new Milestone();
        endMilestone.setProject(project);
        repo.persist(endMilestone);

        var endMilestoneState = new MilestoneState();
        endMilestoneState.setDate(projectData.getEndDate());
        endMilestoneState.setMilestone(endMilestone);
        endMilestoneState.setCreateUser(user);
        endMilestoneState.setChangeStartDate(now);
        endMilestoneState.setState(MilestoneStatus.GEPLAND);
        repo.persist(endMilestoneState);

        Consumer<MilestoneChangeDataSuperclass> setChangelogValues = (MilestoneChangeDataSuperclass entity) -> {
            entity.setStartMilestone(startMilestone);
            entity.setEndMilestone(endMilestone);
            entity.setCreateUser(user);
            entity.setChangeStartDate(now);
        };

        var duration = new ProjectDurationChangelog();
        setChangelogValues.accept(duration);
        duration.setProject(project);
        repo.persist(duration);

        var name = new ProjectNameChangelog();
        name.setProject(project);
        name.setName(projectData.getProjectName());
        setChangelogValues.accept(name);
        repo.persist(name);

        var state = new ProjectState();
        state.setProject(project);
        state.setCreateUser(user);
        state.setChangeStartDate(now);
        state.setConfidentiality(projectData.getConfidentialityLevel());
        state.setColor(projectData.getProjectColor());
        repo.persist(state);

        var faseChangelog = new ProjectFaseChangelog();
        faseChangelog.setProject(project);
        setChangelogValues.accept(faseChangelog);
        faseChangelog.setProjectPhase(projectData.getProjectPhase());
        repo.persist(faseChangelog);

        var planStatus = new ProjectPlanologischePlanstatusChangelog();
        planStatus.setProject(project);
        setChangelogValues.accept(planStatus);
        repo.persist(planStatus);

        return project;
    }

    public void deleteProject(VngRepository repo, UUID projectUuid, UUID loggedInUserUuid) throws VngNotFoundException {
        var now = ZonedDateTime.now();
        var user = repo.findById(User.class, loggedInUserUuid);

        var project = repo.getProjectsDAO().getCurrentProject(projectUuid);

        if (project == null) {
            logger.error("Project with uuid {} was not found.", projectUuid);
            throw new VngNotFoundException();
        }

        project.getDuration().stream()
                .filter(cl -> cl.getChangeEndDate() == null)
                .forEach(cl -> {
                    cl.setChangeEndDate(now);
                    cl.setChangeUser(user);
                    repo.persist(cl);
                });
    }

    public void updateProjectLocation(VngRepository repo, Project project, List<Double> location, UUID loggedInUserUuid, ZonedDateTime updateTime)
            throws VngNotFoundException {
        ProjectState oldProjectState = repo.getProjectsDAO().getCurrentProjectState(project.getId());
        if (oldProjectState == null) {
            logger.error("Active projectState was not found for projectUuid {}.", project.getId());
            throw new VngNotFoundException();
        }

        if (!Objects.equals(oldProjectState.getLatitude(), location.get(0)) || !Objects.equals(oldProjectState.getLongitude(), location.get(1))) {

            ProjectState newProjectState;
            if (oldProjectState.getChangeStartDate().equals(updateTime)) {
                // this is a bulk update of all properties, and there already is a new project state created for this update
                newProjectState = oldProjectState;
            } else {
                newProjectState = new ProjectState();
                newProjectState.setProject(oldProjectState.getProject());
                newProjectState.setConfidentiality(oldProjectState.getConfidentiality());
                newProjectState.setColor(oldProjectState.getColor());
                newProjectState.setChangeStartDate(updateTime);
                newProjectState.setCreateUser(repo.findById(User.class, loggedInUserUuid));

                oldProjectState.setChangeEndDate(updateTime);
                oldProjectState.setChangeUser(repo.findById(User.class, loggedInUserUuid));
                repo.persist(oldProjectState);
            }

            newProjectState.setLatitude(location.get(0));
            newProjectState.setLongitude(location.get(1));
            repo.persist(newProjectState);
        }
    }

    public void updateProjectColor(VngRepository repo, Project project, String newColor, UUID loggedInUserUuid, ZonedDateTime updateTime)
            throws VngNotFoundException {

        ProjectState oldProjectState = repo.getProjectsDAO().getCurrentProjectState(project.getId());
        if (oldProjectState == null) {
            logger.error("Active projectState was not found for projectUuid {}.", project.getId());
            throw new VngNotFoundException();
        }

        if (!Objects.equals(oldProjectState.getColor(), newColor)) {

            ProjectState newProjectState;
            if (oldProjectState.getChangeStartDate().equals(updateTime)) {
                // this is a bulk update of all properties, and there already is a new project state created for this update
                newProjectState = oldProjectState;
            } else {
                newProjectState = new ProjectState();
                newProjectState.setProject(oldProjectState.getProject());
                newProjectState.setConfidentiality(oldProjectState.getConfidentiality());
                newProjectState.setChangeStartDate(updateTime);
                newProjectState.setCreateUser(repo.findById(User.class, loggedInUserUuid));
                newProjectState.setLatitude(oldProjectState.getLatitude());
                newProjectState.setLongitude(oldProjectState.getLongitude());

                oldProjectState.setChangeEndDate(updateTime);
                oldProjectState.setChangeUser(repo.findById(User.class, loggedInUserUuid));
                repo.persist(oldProjectState);
            }

            newProjectState.setColor(newColor);
            repo.persist(newProjectState);
        }
    }

    public void updateProjectConfidentialityLevel(VngRepository repo, Project project, Confidentiality newConfidentiality, UUID loggedInUserUuid,
            ZonedDateTime updateTime)
            throws VngNotFoundException {

        ProjectState oldProjectState = repo.getProjectsDAO().getCurrentProjectState(project.getId());
        if (oldProjectState == null) {
            logger.error("Active projectState was not found for projectUuid {}.", project.getId());
            throw new VngNotFoundException();
        }

        if (!Objects.equals(oldProjectState.getConfidentiality(), newConfidentiality)) {
            ProjectState newProjectState;
            if (oldProjectState.getChangeStartDate().equals(updateTime)) {
                // this is a bulk update of all properties, and there already is a new project state created for this update
                newProjectState = oldProjectState;
            } else {
                newProjectState = new ProjectState();
                newProjectState.setProject(oldProjectState.getProject());
                newProjectState.setColor(oldProjectState.getColor());
                newProjectState.setChangeStartDate(updateTime);
                newProjectState.setCreateUser(repo.getReferenceById(User.class, loggedInUserUuid));
                newProjectState.setLatitude(oldProjectState.getLatitude());
                newProjectState.setLongitude(oldProjectState.getLongitude());

                oldProjectState.setChangeEndDate(updateTime);
                oldProjectState.setChangeUser(repo.findById(User.class, loggedInUserUuid));
                repo.persist(oldProjectState);
            }

            newProjectState.setConfidentiality(newConfidentiality);
            repo.persist(newProjectState);
        }
    }

    public void updateProjectOrganizations(VngRepository repo, Project project, ProjectRole projectRole, UUID organizationToAdd,
            UUID organizationToRemove, UUID loggedInUserUuid) {

        UUID projectUuid = project.getId();

        if (organizationToAdd != null) {
            UUID organizationToProjectUuid = repo.getOrganizationDAO().findOrganizationForProject(projectUuid, organizationToAdd, projectRole);
            if (organizationToProjectUuid != null) {
                logger.info("Trying to add to project {} a {} organization {} which is already associated with this project.", projectUuid, projectRole,
                        organizationToAdd);
            } else {
                repo.getOrganizationDAO().addOrganizationToProject(projectUuid, organizationToAdd, projectRole, loggedInUserUuid);
            }
        }

        if (organizationToRemove != null) {
            UUID organizationToProjectUuid = repo.getOrganizationDAO().findOrganizationForProject(projectUuid, organizationToRemove, projectRole);
            if (organizationToProjectUuid == null) {
                logger.info("Trying to remove from project {} a {} organization {} which is not associated with this project.", projectUuid, projectRole,
                        organizationToRemove);
            } else {
                repo.getOrganizationDAO().removeOrganizationFromProject(projectUuid, organizationToRemove, projectRole, loggedInUserUuid);
            }
        }
    }

    public void updateProjectName(VngRepository repo, Project project, String newName, UUID loggedInUserUuid, LocalDate updateDate)
            throws VngServerErrorException {
        // name is mandatory for the entire duration of the project

        ProjectNameChangelog oldProjectNameChangelogAfterUpdate = new ProjectNameChangelog();
        ProjectNameChangelog newProjectNameChangelog = new ProjectNameChangelog();
        newProjectNameChangelog.setProject(project);
        newProjectNameChangelog.setName(newName);

        ProjectNameChangelog oldProjectNameChangelog = prepareChangelogValuesToUpdate(repo, project, project.getName(), newProjectNameChangelog,
                oldProjectNameChangelogAfterUpdate, loggedInUserUuid, updateDate);

        repo.persist(newProjectNameChangelog);
        if (oldProjectNameChangelog == null) {
            logger.error("Project with uuid {} has missing name changelog value", project.getId());
            throw new VngServerErrorException("Project name changelog is invalid.");
        }

        if (Objects.equals(oldProjectNameChangelog.getName(), newName)) {
            logger.info("Trying to update the project {} with the same project phase value that it already has {}.", project.getId(), newName);
            return;
        }
        repo.persist(oldProjectNameChangelog);
        if (oldProjectNameChangelogAfterUpdate.getStartMilestone() != null) {
            // it is a current project && it had a non-null changelog before the update
            oldProjectNameChangelogAfterUpdate.setProject(project);
            oldProjectNameChangelogAfterUpdate.setName(oldProjectNameChangelog.getName());
            repo.persist(oldProjectNameChangelogAfterUpdate);
        }
    }

    public void updateProjectMunicipalityRoles(VngRepository repo, Project project, UUID municipalityRoleToAdd, UUID municipalityRoleToRemove,
            UUID loggedInUserUuid, LocalDate updateDate) {
        // a project can have multiple active changelog entries for municipality roles

        LocalDate projectStartDate = (new MilestoneModel(project.getDuration().get(0).getStartMilestone())).getDate();
        if (projectStartDate.isAfter(updateDate)) {
            updateDate = projectStartDate;
        }
        LocalDate finalUpdateDate = updateDate;

        if (municipalityRoleToAdd != null) {

            List<ProjectGemeenteRolChangelog> changelogs = project.getMunicipalityRole().stream()
                    .filter(mrc -> mrc.getValue().getId().equals(municipalityRoleToAdd)
                            && !(new MilestoneModel(mrc.getStartMilestone())).getDate().isAfter(finalUpdateDate)
                            && (new MilestoneModel(mrc.getEndMilestone())).getDate().isAfter(finalUpdateDate))
                    .toList();
            if (!changelogs.isEmpty()) {
                logger.info("Trying to add to project {} a municipality role {} which is already associated with this project.", project.getId(),
                        municipalityRoleToAdd);
                return;
            } else {
                ProjectGemeenteRolChangelog newChangelog = new ProjectGemeenteRolChangelog();
                newChangelog.setValue(repo.findById(ProjectGemeenteRolValue.class, municipalityRoleToAdd));
                newChangelog.setProject(project);
                newChangelog.setChangeStartDate(ZonedDateTime.now());
                newChangelog.setCreateUser(repo.getReferenceById(User.class, loggedInUserUuid));
                newChangelog.setStartMilestone(getOrCreateMilestoneForProject(repo, project, updateDate, loggedInUserUuid));
                UUID newEndMilestoneUuid = project.getMunicipalityRole().stream().filter(mrc -> mrc.getValue().getId().equals(municipalityRoleToAdd))
                        .map(mr -> new MilestoneModel(mr.getStartMilestone()))
                        .filter(mm -> mm.getDate().isAfter(finalUpdateDate))
                        .min(Comparator.comparing(MilestoneModel::getDate))
                        .map(MilestoneModel::getId)
                        .orElse(project.getDuration().get(0).getEndMilestone().getId());
                newChangelog.setEndMilestone(repo.getReferenceById(Milestone.class, newEndMilestoneUuid));
                repo.persist(newChangelog);
            }
        }

        if (municipalityRoleToRemove != null) {
            List<ProjectGemeenteRolChangelog> changelogs = project.getMunicipalityRole().stream()
                    .filter(mrc -> mrc.getValue().getId().equals(municipalityRoleToRemove)
                            && !(new MilestoneModel(mrc.getStartMilestone())).getDate().isAfter(finalUpdateDate)
                            && (new MilestoneModel(mrc.getEndMilestone())).getDate().isAfter(finalUpdateDate))
                    .toList();
            if (changelogs.isEmpty()) {
                logger.info("Trying to remove from project {} a municipality role {} which is not associated with this project.", project.getId(),
                        municipalityRoleToRemove);
            } else {
                ProjectGemeenteRolChangelog changelog = changelogs.get(0);
                changelog.setChangeEndDate(ZonedDateTime.now());
                changelog.setChangeUser(repo.getReferenceById(User.class, loggedInUserUuid));
                repo.persist(changelog);
            }
        }
    }

    public void updateProjectPlanStatus(VngRepository repo, Project project, Set<PlanStatus> newProjectPlanStatuses, UUID loggedInUserUuid,
            LocalDate updateDate) {

        ProjectPlanologischePlanstatusChangelog oldPlanStatusChangelogAfterUpdate = new ProjectPlanologischePlanstatusChangelog();
        ProjectPlanologischePlanstatusChangelog newPlanStatusChangelog = null;
        if (newProjectPlanStatuses != null && !newProjectPlanStatuses.isEmpty()) {
            newPlanStatusChangelog = new ProjectPlanologischePlanstatusChangelog();
            newPlanStatusChangelog.setProject(project);
        }
        ProjectPlanologischePlanstatusChangelog oldPlanStatusChangelog = prepareChangelogValuesToUpdate(repo, project, project.getPlanologischePlanstatus(),
                newPlanStatusChangelog,
                oldPlanStatusChangelogAfterUpdate, loggedInUserUuid, updateDate);
        if (newPlanStatusChangelog != null) {
            repo.persist(newPlanStatusChangelog);
            for (PlanStatus newPlanStatusValue : newProjectPlanStatuses) {
                ProjectPlanologischePlanstatusChangelogValue newChangelogValue = new ProjectPlanologischePlanstatusChangelogValue();
                newChangelogValue.setPlanStatusChangelog(newPlanStatusChangelog);
                newChangelogValue.setPlanStatus(newPlanStatusValue);
                repo.persist(newChangelogValue);
            }
        }
        if (oldPlanStatusChangelog != null) {
            Set<PlanStatus> oldProjectPlanStatuses = oldPlanStatusChangelog.getValue().stream()
                    .map(ProjectPlanologischePlanstatusChangelogValue::getPlanStatus).collect(Collectors.toSet());
            if (Objects.equals(oldProjectPlanStatuses, newProjectPlanStatuses)) {
                logger.info("Trying to update the project {} with the same plan statuses that it already has {}.", project.getId(), newProjectPlanStatuses);
                return;
            }
            repo.persist(oldPlanStatusChangelog);
            if (oldPlanStatusChangelogAfterUpdate.getStartMilestone() != null) {
                // it is a current project && it had a non-null changelog before the update
                oldPlanStatusChangelogAfterUpdate.setProject(project);
                repo.persist(oldPlanStatusChangelogAfterUpdate);
                for (PlanStatus oldPlanStatusValue : oldProjectPlanStatuses) {
                    ProjectPlanologischePlanstatusChangelogValue oldChangelogValue = new ProjectPlanologischePlanstatusChangelogValue();
                    oldChangelogValue.setPlanStatusChangelog(oldPlanStatusChangelogAfterUpdate);
                    oldChangelogValue.setPlanStatus(oldPlanStatusValue);
                    repo.persist(oldChangelogValue);
                }
            }
        }
    }

    public void updateProjectPlanTypes(VngRepository repo, Project project, Set<PlanType> newProjectPlanTypes, UUID loggedInUserUuid, LocalDate updateDate) {

        ProjectPlanTypeChangelog oldPlanTypeChangelogAfterUpdate = new ProjectPlanTypeChangelog();
        ProjectPlanTypeChangelog newPlanTypeChangelog = null;
        if (newProjectPlanTypes != null && !newProjectPlanTypes.isEmpty()) {
            newPlanTypeChangelog = new ProjectPlanTypeChangelog();
            newPlanTypeChangelog.setProject(project);
        }
        ProjectPlanTypeChangelog oldPlanTypeChangelog = prepareChangelogValuesToUpdate(repo, project, project.getPlanType(), newPlanTypeChangelog,
                oldPlanTypeChangelogAfterUpdate, loggedInUserUuid, updateDate);
        if (newPlanTypeChangelog != null) {
            repo.persist(newPlanTypeChangelog);
            for (PlanType newPlanTypeValue : newProjectPlanTypes) {
                ProjectPlanTypeChangelogValue newChangelogValue = new ProjectPlanTypeChangelogValue();
                newChangelogValue.setPlanTypeChangelog(newPlanTypeChangelog);
                newChangelogValue.setPlanType(newPlanTypeValue);
                repo.persist(newChangelogValue);
            }
        }
        if (oldPlanTypeChangelog != null) {
            Set<PlanType> oldProjectPlanTypes = oldPlanTypeChangelog.getValue().stream()
                    .map(ProjectPlanTypeChangelogValue::getPlanType).collect(Collectors.toSet());
            if (Objects.equals(oldProjectPlanTypes, newProjectPlanTypes)) {
                logger.info("Trying to update the project {} with the same plan types that it already has {}.", project.getId(), newProjectPlanTypes);
                return;
            }
            repo.persist(oldPlanTypeChangelog);
            if (oldPlanTypeChangelogAfterUpdate.getStartMilestone() != null) {
                // it is a current project && it had a non-null changelog before the update
                oldPlanTypeChangelogAfterUpdate.setProject(project);
                repo.persist(oldPlanTypeChangelogAfterUpdate);
                for (PlanType oldPlanTypeValue : oldProjectPlanTypes) {
                    ProjectPlanTypeChangelogValue oldChangelogValue = new ProjectPlanTypeChangelogValue();
                    oldChangelogValue.setPlanTypeChangelog(oldPlanTypeChangelogAfterUpdate);
                    oldChangelogValue.setPlanType(oldPlanTypeValue);
                    repo.persist(oldChangelogValue);
                }
            }
        }
    }

    public void updateProjectBooleanCustomProperty(VngRepository repo, Project project, UUID customPropertyId, Boolean newBooleanValue, UUID loggedInUserUuid,
            LocalDate updateDate) {
        ProjectBooleanCustomPropertyChangelog oldChangelogAfterUpdate = new ProjectBooleanCustomPropertyChangelog();
        ProjectBooleanCustomPropertyChangelog newChangelog = null;
        if (newBooleanValue != null) {
            newChangelog = new ProjectBooleanCustomPropertyChangelog();
            newChangelog.setProject(project);
            newChangelog.setValue(newBooleanValue);
            newChangelog.setCustomProperty(repo.getReferenceById(CustomProperty.class, customPropertyId));
        }

        List<ProjectBooleanCustomPropertyChangelog> changelogs = project.getBooleanCustomProperties().stream()
                .filter(cp -> cp.getCustomProperty().getId().equals(customPropertyId)).toList();

        ProjectBooleanCustomPropertyChangelog oldChangelog = prepareChangelogValuesToUpdate(repo, project, changelogs, newChangelog,
                oldChangelogAfterUpdate, loggedInUserUuid, updateDate);

        if (newChangelog != null) {
            repo.persist(newChangelog);
        }
        if (oldChangelog != null) {
            repo.persist(oldChangelog);
            if (oldChangelogAfterUpdate.getStartMilestone() != null) {
                // it is a current project && it had a non-null changelog before the update
                oldChangelogAfterUpdate.setProject(project);
                oldChangelogAfterUpdate.setValue(oldChangelog.getValue());
                oldChangelogAfterUpdate.setCustomProperty(oldChangelog.getCustomProperty());
                repo.persist(oldChangelogAfterUpdate);
            }
        }
    }

    public void updateProjectTextCustomProperty(VngRepository repo, Project project, UUID customPropertyId, String newTextValue, UUID loggedInUserUuid,
            LocalDate updateDate) {
        ProjectTextCustomPropertyChangelog oldChangelogAfterUpdate = new ProjectTextCustomPropertyChangelog();
        ProjectTextCustomPropertyChangelog newChangelog = null;
        if (newTextValue != null) {
            newChangelog = new ProjectTextCustomPropertyChangelog();
            newChangelog.setProject(project);
            newChangelog.setValue(newTextValue);
            newChangelog.setCustomProperty(repo.getReferenceById(CustomProperty.class, customPropertyId));
        }

        List<ProjectTextCustomPropertyChangelog> changelogs = project.getTextCustomProperties().stream()
                .filter(cp -> cp.getCustomProperty().getId().equals(customPropertyId)).toList();

        ProjectTextCustomPropertyChangelog oldChangelog = prepareChangelogValuesToUpdate(repo, project, changelogs, newChangelog,
                oldChangelogAfterUpdate, loggedInUserUuid, updateDate);

        if (newChangelog != null) {
            repo.persist(newChangelog);
        }
        if (oldChangelog != null) {
            repo.persist(oldChangelog);
            if (oldChangelogAfterUpdate.getStartMilestone() != null) {
                // it is a current project && it had a non-null changelog before the update
                oldChangelogAfterUpdate.setProject(project);
                oldChangelogAfterUpdate.setValue(oldChangelog.getValue());
                oldChangelogAfterUpdate.setCustomProperty(oldChangelog.getCustomProperty());
                repo.persist(oldChangelogAfterUpdate);
            }
        }
    }

    public void updateProjectNumericCustomProperty(VngRepository repo, Project project, UUID customPropertyId,
            SingleValueOrRangeModel<BigDecimal> newNumericValue,
            UUID loggedInUserUuid, LocalDate updateDate) {
        ProjectNumericCustomPropertyChangelog oldChangelogAfterUpdate = new ProjectNumericCustomPropertyChangelog();
        ProjectNumericCustomPropertyChangelog newChangelog = null;
        if (newNumericValue.getValue() != null || newNumericValue.getMin() != null || newNumericValue.getMax() != null) {
            newChangelog = new ProjectNumericCustomPropertyChangelog();
            newChangelog.setProject(project);
            if (newNumericValue.getValue() != null) {
                newChangelog.setValue(newNumericValue.getValue().doubleValue());
                newChangelog.setValueType(ValueType.SINGLE_VALUE);
            } else {
                newChangelog.setValueRange(Range.closed(newNumericValue.getMin(), newNumericValue.getMax()));
                newChangelog.setValueType(ValueType.RANGE);
            }
            newChangelog.setCustomProperty(repo.getReferenceById(CustomProperty.class, customPropertyId));
        }

        List<ProjectNumericCustomPropertyChangelog> changelogs = project.getNumericCustomProperties().stream()
                .filter(cp -> cp.getCustomProperty().getId().equals(customPropertyId)).toList();

        ProjectNumericCustomPropertyChangelog oldChangelog = prepareChangelogValuesToUpdate(repo, project, changelogs, newChangelog,
                oldChangelogAfterUpdate, loggedInUserUuid, updateDate);

        if (newChangelog != null) {
            repo.persist(newChangelog);
        }
        if (oldChangelog != null) {
            repo.persist(oldChangelog);
            if (oldChangelogAfterUpdate.getStartMilestone() != null) {
                // it is a current project && it had a non-null changelog before the update
                oldChangelogAfterUpdate.setProject(project);
                oldChangelogAfterUpdate.setValue(oldChangelog.getValue());
                oldChangelogAfterUpdate.setValueRange(oldChangelog.getValueRange());
                oldChangelogAfterUpdate.setValueType(oldChangelog.getValueType());
                oldChangelogAfterUpdate.setCustomProperty(oldChangelog.getCustomProperty());
                repo.persist(oldChangelogAfterUpdate);
            }
        }
    }

    public void updateProjectCategoryCustomProperty(VngRepository repo, Project project, UUID customPropertyId, Set<UUID> newCategoryValues,
            UUID loggedInUserUuid, LocalDate updateDate) {
        ProjectCategoryCustomPropertyChangelog oldChangelogAfterUpdate = new ProjectCategoryCustomPropertyChangelog();
        ProjectCategoryCustomPropertyChangelog newChangelog = null;
        if (newCategoryValues != null && !newCategoryValues.isEmpty()) {
            newChangelog = new ProjectCategoryCustomPropertyChangelog();
            newChangelog.setProject(project);
            newChangelog.setCustomProperty(repo.getReferenceById(CustomProperty.class, customPropertyId));
        }

        List<ProjectCategoryCustomPropertyChangelog> changelogs = project.getCategoryCustomProperties().stream()
                .filter(cp -> cp.getCustomProperty().getId().equals(customPropertyId)).toList();

        ProjectCategoryCustomPropertyChangelog oldChangelog = prepareChangelogValuesToUpdate(repo, project, changelogs, newChangelog,
                oldChangelogAfterUpdate, loggedInUserUuid, updateDate);

        if (newChangelog != null) {
            repo.persist(newChangelog);
            for (UUID newCategoryValue : newCategoryValues) {
                ProjectCategoryCustomPropertyChangelogValue newChangelogValue = new ProjectCategoryCustomPropertyChangelogValue();
                newChangelogValue.setCategoryChangelog(newChangelog);
                newChangelogValue.setCategoryValue(repo.getReferenceById(CustomCategoryValue.class, newCategoryValue));
                repo.persist(newChangelogValue);
            }
        }
        if (oldChangelog != null) {
            Set<UUID> oldCategoryValues = oldChangelog.getChangelogCategoryValues().stream()
                    .map(cv -> cv.getCategoryValue().getId()).collect(Collectors.toSet());

            repo.persist(oldChangelog);
            if (oldChangelogAfterUpdate.getStartMilestone() != null) {
                // it is a current project && it had a non-null changelog before the update
                oldChangelogAfterUpdate.setProject(project);
                oldChangelogAfterUpdate.setCustomProperty(oldChangelog.getCustomProperty());
                repo.persist(oldChangelogAfterUpdate);
                for (UUID oldCategoryValue : oldCategoryValues) {
                    ProjectCategoryCustomPropertyChangelogValue oldChangelogValue = new ProjectCategoryCustomPropertyChangelogValue();
                    oldChangelogValue.setCategoryChangelog(oldChangelogAfterUpdate);
                    oldChangelogValue.setCategoryValue(repo.getReferenceById(CustomCategoryValue.class, oldCategoryValue));
                    repo.persist(oldChangelogValue);
                }
            }
        }
    }

    public void updateProjectOrdinalCustomProperty(VngRepository repo, Project project, UUID customPropertyId, SingleValueOrRangeModel<UUID> newOrdinalValue,
            UUID loggedInUserUuid, LocalDate updateDate) {
        ProjectOrdinalCustomPropertyChangelog oldChangelogAfterUpdate = new ProjectOrdinalCustomPropertyChangelog();
        ProjectOrdinalCustomPropertyChangelog newChangelog = null;
        if (newOrdinalValue.getValue() != null || newOrdinalValue.getMin() != null || newOrdinalValue.getMax() != null) {
            newChangelog = new ProjectOrdinalCustomPropertyChangelog();
            newChangelog.setProject(project);
            if (newOrdinalValue.getValue() != null) {
                newChangelog.setValue(repo.getReferenceById(CustomOrdinalValue.class, newOrdinalValue.getValue()));
                newChangelog.setValueType(ValueType.SINGLE_VALUE);
            } else {
                newChangelog.setMinValue(repo.getReferenceById(CustomOrdinalValue.class, newOrdinalValue.getMin()));
                newChangelog.setMaxValue(repo.getReferenceById(CustomOrdinalValue.class, newOrdinalValue.getMax()));
                newChangelog.setValueType(ValueType.RANGE);
            }
            newChangelog.setCustomProperty(repo.getReferenceById(CustomProperty.class, customPropertyId));
        }

        List<ProjectOrdinalCustomPropertyChangelog> changelogs = project.getOrdinalCustomProperties().stream()
                .filter(cp -> cp.getCustomProperty().getId().equals(customPropertyId)).toList();

        ProjectOrdinalCustomPropertyChangelog oldChangelog = prepareChangelogValuesToUpdate(repo, project, changelogs, newChangelog,
                oldChangelogAfterUpdate, loggedInUserUuid, updateDate);

        if (newChangelog != null) {
            repo.persist(newChangelog);
        }
        if (oldChangelog != null) {
            repo.persist(oldChangelog);
            if (oldChangelogAfterUpdate.getStartMilestone() != null) {
                // it is a current project && it had a non-null changelog before the update
                oldChangelogAfterUpdate.setProject(project);
                oldChangelogAfterUpdate.setValue(oldChangelog.getValue());
                oldChangelogAfterUpdate.setMinValue(oldChangelog.getMinValue());
                oldChangelogAfterUpdate.setMaxValue(oldChangelog.getMaxValue());
                oldChangelogAfterUpdate.setValueType(oldChangelog.getValueType());
                oldChangelogAfterUpdate.setCustomProperty(oldChangelog.getCustomProperty());
                repo.persist(oldChangelogAfterUpdate);
            }
        }
    }

    public void updateProjectPhase(VngRepository repo, Project project, ProjectPhase newProjectPhase, UUID loggedInUserUuid, LocalDate updateDate) {

        ProjectFaseChangelog oldProjectFaseChangelogAfterUpdate = new ProjectFaseChangelog();
        ProjectFaseChangelog newProjectFaseChangelog = null;
        if (newProjectPhase != null) {
            newProjectFaseChangelog = new ProjectFaseChangelog();
            newProjectFaseChangelog.setProject(project);
            newProjectFaseChangelog.setProjectPhase(newProjectPhase);
        }
        ProjectFaseChangelog oldProjectFaseChangelog = prepareChangelogValuesToUpdate(repo, project, project.getPhase(), newProjectFaseChangelog,
                oldProjectFaseChangelogAfterUpdate, loggedInUserUuid, updateDate);
        if (newProjectFaseChangelog != null) {
            repo.persist(newProjectFaseChangelog);
        }
        if (oldProjectFaseChangelog != null) {
            if (Objects.equals(oldProjectFaseChangelog.getProjectPhase(), newProjectPhase)) {
                logger.info("Trying to update the project {} with the same project phase value that it already has {}.", project.getId(), newProjectPhase);
                return;
            }
            repo.persist(oldProjectFaseChangelog);
            if (oldProjectFaseChangelogAfterUpdate.getStartMilestone() != null) {
                // it is a current project && it had a non-null changelog before the update
                oldProjectFaseChangelogAfterUpdate.setProject(project);
                oldProjectFaseChangelogAfterUpdate.setProjectPhase(oldProjectFaseChangelog.getProjectPhase());
                repo.persist(oldProjectFaseChangelogAfterUpdate);
            }
        }
    }

    public void updateProjectPriority(VngRepository repo, Project project, UUID priorityValue, UUID priorityMin, UUID priorityMax, UUID loggedInUserUuid,
            LocalDate updateDate) {

        ProjectPrioriseringChangelog oldPriorityChangelogAfterUpdate = new ProjectPrioriseringChangelog();
        ProjectPrioriseringChangelog newPriorityChangelog = null;
        if (priorityValue != null || priorityMin != null || priorityMax != null) {
            newPriorityChangelog = new ProjectPrioriseringChangelog();
            newPriorityChangelog.setProject(project);
            newPriorityChangelog.setValue((priorityValue != null) ? repo.getReferenceById(ProjectPrioriseringValue.class, priorityValue) : null);
            newPriorityChangelog.setMinValue((priorityMin != null) ? repo.getReferenceById(ProjectPrioriseringValue.class, priorityMin) : null);
            newPriorityChangelog.setMaxValue((priorityMax != null) ? repo.getReferenceById(ProjectPrioriseringValue.class, priorityMax) : null);
            newPriorityChangelog.setValueType((priorityValue != null) ? ValueType.SINGLE_VALUE : ValueType.RANGE);
        }
        ProjectPrioriseringChangelog oldPriorityChangelog = prepareChangelogValuesToUpdate(repo, project, project.getPriority(), newPriorityChangelog,
                oldPriorityChangelogAfterUpdate, loggedInUserUuid, updateDate);
        if (newPriorityChangelog != null) {
            repo.persist(newPriorityChangelog);
        }
        if (oldPriorityChangelog != null) {
            UUID oldPriorityValue = (oldPriorityChangelog.getValue() == null) ? null : oldPriorityChangelog.getValue().getId();
            UUID oldPriorityMinValue = (oldPriorityChangelog.getMinValue() == null) ? null : oldPriorityChangelog.getMinValue().getId();
            UUID oldPriorityMaxValue = (oldPriorityChangelog.getMaxValue() == null) ? null : oldPriorityChangelog.getMaxValue().getId();
            if (Objects.equals(oldPriorityValue, priorityValue) && Objects.equals(oldPriorityMinValue, priorityMin) &&
                    Objects.equals(oldPriorityMaxValue, priorityMax)) {
                logger.info("Trying to update the project {} with the same project priority value {}, min value {} and max value {} that it already has.",
                        project.getId(), priorityValue, priorityMin, priorityMax);
                return;
            }
            repo.persist(oldPriorityChangelog);
            if (oldPriorityChangelogAfterUpdate.getStartMilestone() != null) {
                // it is a current project && it had a non-null changelog before the update
                oldPriorityChangelogAfterUpdate.setProject(project);
                oldPriorityChangelogAfterUpdate.setValue(oldPriorityChangelog.getValue());
                oldPriorityChangelogAfterUpdate.setMinValue(oldPriorityChangelog.getMinValue());
                oldPriorityChangelogAfterUpdate.setMaxValue(oldPriorityChangelog.getMaxValue());
                oldPriorityChangelogAfterUpdate.setValueType(oldPriorityChangelog.getValueType());
                repo.persist(oldPriorityChangelogAfterUpdate);
            }
        }
    }

    public void updateProjectDuration(VngRepository repo, Project project, LocalDate newStartDate, LocalDate newEndDate, UUID loggedInUserUuid)
            throws VngServerErrorException, VngBadRequestException {

        ZonedDateTime zdtNow = ZonedDateTime.now();
        User loggedUser = repo.getReferenceById(User.class, loggedInUserUuid);

        Milestone milestone = null;
        MilestoneState oldMilestoneState = null;
        MilestoneState newMilestoneState = new MilestoneState();

        if (newStartDate != null) {
            milestone = project.getDuration().get(0).getStartMilestone();
            MilestoneModel projectStartMilestone = new MilestoneModel(milestone);
            LocalDate nextMilestoneDate = project.getMilestones().stream()
                    .map(MilestoneModel::new)
                    .filter(mm -> !Objects.equals(mm.getId(), projectStartMilestone.getId()) && mm.getStateId() != null)
                    .map(MilestoneModel::getDate)
                    .min(LocalDate::compareTo)
                    .orElseThrow(() -> new VngServerErrorException("Project does not have active milestones"));

            if (nextMilestoneDate.isAfter(newStartDate)) {
                oldMilestoneState = repo.findById(MilestoneState.class, projectStartMilestone.getStateId());
                newMilestoneState.setMilestone(milestone);
                newMilestoneState.setDate(newStartDate);
                milestone.getState().add(newMilestoneState);
            } else {
                throw new VngBadRequestException("Update is not possible because new start date overlaps other existing milestones in this project");
            }
        }

        if (newEndDate != null) {
            milestone = project.getDuration().get(0).getEndMilestone();
            MilestoneModel projectEndMilestone = new MilestoneModel(milestone);
            LocalDate previousMilestoneDate = project.getMilestones().stream()
                    .map(MilestoneModel::new)
                    .filter(mm -> !Objects.equals(mm.getId(), projectEndMilestone.getId()) && mm.getStateId() != null)
                    .map(MilestoneModel::getDate)
                    .max(LocalDate::compareTo)
                    .orElseThrow(() -> new VngServerErrorException("Project does not have active milestones"));

            if (previousMilestoneDate.isBefore(newEndDate)) {
                oldMilestoneState = repo.findById(MilestoneState.class, projectEndMilestone.getStateId());
                newMilestoneState.setMilestone(milestone);
                newMilestoneState.setDate(newEndDate);
                milestone.getState().add(newMilestoneState);
            } else {
                throw new VngBadRequestException("Update is not possible because new end date overlaps other existing milestones in this project");
            }
        }

        if (oldMilestoneState != null) {
            oldMilestoneState.setChangeEndDate(zdtNow);
            oldMilestoneState.setChangeUser(loggedUser);
            repo.persist(oldMilestoneState);

            newMilestoneState.setDescription(oldMilestoneState.getDescription());
            newMilestoneState.setState(oldMilestoneState.getState());
            newMilestoneState.setCreateUser(loggedUser);
            newMilestoneState.setChangeStartDate(zdtNow);
            repo.persist(newMilestoneState);
        }
    }

    private <T extends MilestoneChangeDataSuperclass> T prepareChangelogValuesToUpdate(VngRepository repo, Project project, List<T> changelogs,
            T newProjectChangelog, T oldProjectChangelogAfterUpdate, UUID loggedInUserUuid, LocalDate updateDate) {

        Milestone projectStartMilestone = project.getDuration().get(0).getStartMilestone();
        Milestone projectEndMilestone = project.getDuration().get(0).getEndMilestone();

        ZonedDateTime zdtNow = ZonedDateTime.now();
        LocalDate projectStartDate = (new MilestoneModel(projectStartMilestone)).getDate();

        T oldProjectChangelog;
        if (newProjectChangelog != null) {
            newProjectChangelog.setCreateUser(repo.getReferenceById(User.class, loggedInUserUuid));
            newProjectChangelog.setChangeStartDate(zdtNow);
        }

        if (projectStartDate.isAfter(updateDate)) {
            updateDate = projectStartDate;
        }

        LocalDate finalUpdateDate = updateDate;
        oldProjectChangelog = changelogs.stream()
                .filter(pc -> !(new MilestoneModel(pc.getStartMilestone())).getDate().isAfter(finalUpdateDate)
                        && (new MilestoneModel(pc.getEndMilestone())).getDate().isAfter(finalUpdateDate))
                .findFirst().orElse(null);

        Milestone updateMilestone = getOrCreateMilestoneForProject(repo, project, updateDate, loggedInUserUuid);

        if (oldProjectChangelog != null && !Objects.equals(oldProjectChangelog.getStartMilestone().getId(), updateMilestone.getId())) {
            oldProjectChangelogAfterUpdate.setStartMilestone(oldProjectChangelog.getStartMilestone());
            oldProjectChangelogAfterUpdate.setEndMilestone(updateMilestone);
            oldProjectChangelogAfterUpdate.setCreateUser(oldProjectChangelog.getCreateUser());
            oldProjectChangelogAfterUpdate.setChangeStartDate(zdtNow);
        }

        if (newProjectChangelog != null) {
            newProjectChangelog.setStartMilestone(updateMilestone);
        }

        if (oldProjectChangelog != null) {
            oldProjectChangelog.setChangeEndDate(zdtNow);
            oldProjectChangelog.setChangeUser(repo.getReferenceById(User.class, loggedInUserUuid));
        }

        if (newProjectChangelog != null) {
            if (oldProjectChangelog != null) {
                newProjectChangelog.setEndMilestone(oldProjectChangelog.getEndMilestone());
            } else {
                LocalDate currentStartDate = (new MilestoneModel(newProjectChangelog.getStartMilestone())).getDate();
                UUID newEndMilestoneUuid = changelogs.stream().map(MilestoneChangeDataSuperclass::getStartMilestone)
                        .map(MilestoneModel::new)
                        .filter(mm -> mm.getDate().isAfter(currentStartDate))
                        .min(Comparator.comparing(MilestoneModel::getDate))
                        .map(MilestoneModel::getId)
                        .orElse(projectEndMilestone.getId());
                newProjectChangelog.setEndMilestone(repo.getReferenceById(Milestone.class, newEndMilestoneUuid));
            }
        }

        return oldProjectChangelog;
    }

    public Project getCurrentProjectAndPerformPreliminaryUpdateChecks(VngRepository repo, UUID projectUuid)
            throws VngNotFoundException, VngServerErrorException, VngBadRequestException {
        Project project = repo.getProjectsDAO().getCurrentProject(projectUuid);

        if (project == null) {
            logger.error("Project with uuid {} not found.", projectUuid);
            throw new VngNotFoundException("Project not found");
        }

        if (project.getDuration().size() != 1) {
            logger.error("Project with uuid {} has {} duration changelog values", projectUuid, project.getDuration().size());
            throw new VngServerErrorException("Project duration changelog is invalid.");
        }

        MilestoneModel projectStartMilestone = new MilestoneModel(project.getDuration().get(0).getStartMilestone());
        MilestoneModel projectEndMilestone = new MilestoneModel(project.getDuration().get(0).getEndMilestone());
        if (projectStartMilestone.getStateId() == null || projectEndMilestone.getStateId() == null) {
            logger.error("Project with uuid {} has start or end milestone with invalid states.", projectUuid);
            throw new VngServerErrorException("Project milestones are invalid.");
        }

        if (!projectEndMilestone.getDate().isAfter(LocalDate.now())) {
            logger.error("Project with uuid {} is in the past, it cannot be updated.", projectUuid);
            throw new VngBadRequestException("Cannot update past projects");
        }

        return project;
    }

    public Milestone getOrCreateMilestoneForProject(VngRepository repo, Project project, LocalDate milestoneDate, UUID loggedInUserUuid) {

        List<Milestone> projectMilestones = project.getMilestones();

        Milestone milestone = projectMilestones.stream()
                .filter(m -> milestoneDate.equals((new MilestoneModel(m)).getDate()))
                .findFirst().orElse(null);

        if (milestone == null) {
            milestone = new Milestone();
            milestone.setProject(project);

            MilestoneState milestoneState = new MilestoneState();
            milestoneState.setMilestone(milestone);
            milestoneState.setDate(milestoneDate);
            milestoneState.setCreateUser(repo.getReferenceById(User.class, loggedInUserUuid));
            milestoneState.setChangeStartDate(ZonedDateTime.now());
            milestoneState.setState(MilestoneStatus.GEPLAND);
            milestoneState.setDescription(milestoneDate.toString());
            milestone.getState().add(milestoneState);

            repo.persist(milestone);
            repo.persist(milestoneState);
        }

        return milestone;
    }

}
