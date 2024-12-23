package nl.vng.diwi.services;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.hypersistence.utils.hibernate.type.range.Range;
import jakarta.annotation.Nullable;
import jakarta.persistence.Table;
import nl.vng.diwi.dal.FilterPaginationSorting;
import nl.vng.diwi.dal.VngRepository;
import nl.vng.diwi.dal.entities.Milestone;
import nl.vng.diwi.dal.entities.MilestoneState;
import nl.vng.diwi.dal.entities.Project;
import nl.vng.diwi.dal.entities.ProjectAuditSqlModel;
import nl.vng.diwi.dal.entities.ProjectBooleanCustomPropertyChangelog;
import nl.vng.diwi.dal.entities.ProjectCategoryPropertyChangelog;
import nl.vng.diwi.dal.entities.ProjectCategoryPropertyChangelogValue;
import nl.vng.diwi.dal.entities.ProjectDurationChangelog;
import nl.vng.diwi.dal.entities.ProjectFaseChangelog;
import nl.vng.diwi.dal.entities.ProjectListSqlModel;
import nl.vng.diwi.dal.entities.ProjectNameChangelog;
import nl.vng.diwi.dal.entities.ProjectNumericCustomPropertyChangelog;
import nl.vng.diwi.dal.entities.ProjectOrdinalPropertyChangelog;
import nl.vng.diwi.dal.entities.ProjectPlanTypeChangelog;
import nl.vng.diwi.dal.entities.ProjectPlanTypeChangelogValue;
import nl.vng.diwi.dal.entities.ProjectPlanologischePlanstatusChangelog;
import nl.vng.diwi.dal.entities.ProjectPlanologischePlanstatusChangelogValue;
import nl.vng.diwi.dal.entities.ProjectRegistryLinkChangelog;
import nl.vng.diwi.dal.entities.ProjectRegistryLinkChangelogValue;
import nl.vng.diwi.dal.entities.ProjectState;
import nl.vng.diwi.dal.entities.ProjectTextPropertyChangelog;
import nl.vng.diwi.dal.entities.Property;
import nl.vng.diwi.dal.entities.PropertyCategoryValue;
import nl.vng.diwi.dal.entities.PropertyOrdinalValue;
import nl.vng.diwi.dal.entities.User;
import nl.vng.diwi.dal.entities.UserGroup;
import nl.vng.diwi.dal.entities.UserGroupToProject;
import nl.vng.diwi.dal.entities.enums.Confidentiality;
import nl.vng.diwi.dal.entities.enums.MilestoneStatus;
import nl.vng.diwi.dal.entities.enums.PlanStatus;
import nl.vng.diwi.dal.entities.enums.PlanType;
import nl.vng.diwi.dal.entities.enums.ProjectPhase;
import nl.vng.diwi.dal.entities.enums.ValueType;
import nl.vng.diwi.dal.entities.superclasses.MilestoneChangeDataSuperclass;
import nl.vng.diwi.models.MilestoneModel;
import nl.vng.diwi.models.PlotModel;
import nl.vng.diwi.models.ProjectAuditModel;
import nl.vng.diwi.models.ProjectHouseblockCustomPropertyModel;
import nl.vng.diwi.models.ProjectListModel;
import nl.vng.diwi.models.ProjectSnapshotModel;
import nl.vng.diwi.models.SingleValueOrRangeModel;
import nl.vng.diwi.models.superclasses.ProjectCreateSnapshotModel;
import nl.vng.diwi.rest.VngBadRequestException;
import nl.vng.diwi.rest.VngNotAllowedException;
import nl.vng.diwi.rest.VngNotFoundException;
import nl.vng.diwi.rest.VngServerErrorException;
import nl.vng.diwi.security.LoggedUser;
import nl.vng.diwi.security.UserAction;

public class ProjectService {
    private static final Logger logger = LogManager.getLogger();

    MilestoneService milestoneService;

    public ProjectService() {
        milestoneService = new MilestoneService();
    }

    public void checkProjectEditPermission(VngRepository repo, UUID projectUuid, LoggedUser loggedUser) throws VngNotFoundException, VngNotAllowedException {
        checkProjectEditPermission(repo, projectUuid, null, loggedUser);
    }

    public void checkProjectEditPermission(VngRepository repo, UUID projectUuid, @Nullable ProjectSnapshotModel projectSnapshot, LoggedUser loggedUser)
            throws VngNotFoundException, VngNotAllowedException {
        if (loggedUser.getRole().allowedActions.contains(UserAction.EDIT_ALL_PROJECTS)) {
            return; // permission is ok;
        } else if (loggedUser.getRole().allowedActions.contains(UserAction.EDIT_OWN_PROJECTS)) {
            if (projectSnapshot == null) {
                projectSnapshot = getProjectSnapshot(repo, projectUuid, loggedUser);
            }
            if (projectSnapshot == null) {
                throw new VngNotFoundException();
            }

            Set<UUID> projectOwners = new HashSet<>();
            projectSnapshot.getProjectOwners().forEach(o -> o.getUsers().forEach(u -> projectOwners.add(u.getUuid())));
            if (!projectOwners.contains(loggedUser.getUuid())) {
                throw new VngNotAllowedException("User is not allowed to edit projects for which he is not an owner.");
            }
        } else {
            throw new VngNotAllowedException("User is not allowed to edit projects.");
        }
    }

    public Project getCurrentProject(VngRepository repo, UUID uuid) {
        return repo.getProjectsDAO().getCurrentProject(uuid);
    }

    public ProjectSnapshotModel getProjectSnapshot(VngRepository repo, UUID projectUuid, LoggedUser loggedUser) throws VngNotFoundException {

        ProjectListSqlModel projectModel = repo.getProjectsDAO().getProjectByUuid(projectUuid, loggedUser);

        if (projectModel == null) {
            logger.error("Project with uuid {} was not found.", projectUuid);
            throw new VngNotFoundException();
        }

        ProjectSnapshotModel snapshotModel = new ProjectSnapshotModel(projectModel);
        snapshotModel.setCustomProperties(getProjectCustomProperties(repo, projectUuid));

        return snapshotModel;
    }

    public List<ProjectListModel> getProjectsTable(VngRepository repo, FilterPaginationSorting filtering, LoggedUser loggedUser) {
        List<ProjectListSqlModel> projectsTable = repo.getProjectsDAO().getProjectsTable(filtering, loggedUser);
        List<ProjectListModel> result = projectsTable.stream().map(ProjectListModel::new).toList();
        return result;
    }

    public List<ProjectHouseblockCustomPropertyModel> getProjectCustomProperties(VngRepository repo, UUID projectUuid) {

        return repo.getProjectsDAO().getProjectCustomProperties(projectUuid).stream()
                .map(ProjectHouseblockCustomPropertyModel::new).toList();

    }

    public List<PlotModel> getCurrentPlots(Project project) {

        var projectStartMilestone = project.getDuration().get(0).getStartMilestone();
        LocalDate projectStartDate = (new MilestoneModel(projectStartMilestone)).getDate();

        var projectEndMilestone = project.getDuration().get(0).getEndMilestone();
        var projectEndDate = (new MilestoneModel(projectEndMilestone)).getDate();

        boolean finalIsCurrentOrFuture;
        LocalDate referenceDate = LocalDate.now();
        if (projectStartDate.isAfter(referenceDate)) {
            referenceDate = projectStartDate;
            finalIsCurrentOrFuture = true;
        } else if (projectEndDate.isBefore(referenceDate)) {
            referenceDate = projectEndDate;
            finalIsCurrentOrFuture = false;
        } else {
            finalIsCurrentOrFuture = true;
        }
        final LocalDate finalReferenceDate = referenceDate;

        List<ProjectRegistryLinkChangelog> registryLinks = project.getRegistryLinks();
        var currentChangelog = registryLinks.stream()
                .filter(pc -> {
                    LocalDate startDate = (new MilestoneModel(pc.getStartMilestone())).getDate();
                    LocalDate endDate = (new MilestoneModel(pc.getEndMilestone())).getDate();
                    return (finalIsCurrentOrFuture && !startDate.isAfter(finalReferenceDate) && endDate.isAfter(finalReferenceDate)) ||
                            (!finalIsCurrentOrFuture && endDate.isEqual(finalReferenceDate));
                })
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

        var oldCl = prepareProjectChangelogValuesToUpdate(repo, project, project.getRegistryLinks(), newCl, oldClAfterUpdate, loggedInUserUuid, currentDate);

        if (newCl != null) {
            repo.persist(newCl);
            for (var plot : plots) {
                repo.persist(ProjectRegistryLinkChangelogValue.builder()
                        .brkGemeenteCode(plot.getBrkGemeenteCode())
                        .brkPerceelNummer(plot.getBrkPerceelNummer())
                        .brkSectie(plot.getBrkSectie())
                        .subselectionGeometry(plot.getSubselectionGeometry())
                        .plotFeature(plot.getPlotFeature())
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
                            .subselectionGeometry(oldClValue.getSubselectionGeometry())
                            .plotFeature(oldClValue.getPlotFeature())
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

        projectData.getProjectOwners().forEach(ug -> {
            UserGroupToProject ugtp = new UserGroupToProject();
            ugtp.setProject(project);
            ugtp.setUserGroup(repo.getReferenceById(UserGroup.class, ug.getUuid()));
            ugtp.setCreateUser(user);
            ugtp.setChangeStartDate(now);
            repo.persist(ugtp);
        });

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

    public void updateProjectUserGroups(VngRepository repo, Project project, UUID userGroupToAdd,
            UUID userGroupToRemove, UUID loggedInUserUuid) {

        UUID projectUuid = project.getId();

        if (userGroupToAdd != null) {
            UUID groupToProjectUuid = repo.getUsergroupDAO().findUserGroupForProject(projectUuid, userGroupToAdd);
            if (groupToProjectUuid != null) {
                logger.info("Trying to add to project {} an usergroup {} which is already associated with this project.", projectUuid,
                        userGroupToAdd);
            } else {
                repo.getUsergroupDAO().addUserGroupToProject(projectUuid, userGroupToAdd, loggedInUserUuid);
            }
        }

        if (userGroupToRemove != null) {
            UUID groupToProjectUuid = repo.getUsergroupDAO().findUserGroupForProject(projectUuid, userGroupToRemove);
            if (groupToProjectUuid == null) {
                logger.info("Trying to remove from project {} an usergroup {} which is not associated with this project.", projectUuid,
                        userGroupToRemove);
            } else {
                repo.getUsergroupDAO().removeUserGroupFromProject(projectUuid, userGroupToRemove, loggedInUserUuid);
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

        ProjectNameChangelog oldProjectNameChangelog = prepareProjectChangelogValuesToUpdate(repo, project, project.getName(), newProjectNameChangelog,
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

    public void updateProjectPlanStatus(VngRepository repo, Project project, @Nonnull Set<PlanStatus> newProjectPlanStatuses, UUID loggedInUserUuid,
            LocalDate updateDate) {

        ProjectPlanologischePlanstatusChangelog oldPlanStatusChangelogAfterUpdate = new ProjectPlanologischePlanstatusChangelog();
        ProjectPlanologischePlanstatusChangelog newPlanStatusChangelog = null;
        if (newProjectPlanStatuses != null && !newProjectPlanStatuses.isEmpty()) {
            newPlanStatusChangelog = new ProjectPlanologischePlanstatusChangelog();
            newPlanStatusChangelog.setProject(project);
        }
        ProjectPlanologischePlanstatusChangelog oldPlanStatusChangelog = prepareProjectChangelogValuesToUpdate(repo, project,
                project.getPlanologischePlanstatus(),
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

    public void updateProjectPlanTypes(VngRepository repo, Project project, @Nonnull Set<PlanType> newProjectPlanTypes, UUID loggedInUserUuid,
            LocalDate updateDate) {

        ProjectPlanTypeChangelog oldPlanTypeChangelogAfterUpdate = new ProjectPlanTypeChangelog();
        ProjectPlanTypeChangelog newPlanTypeChangelog = null;
        if (newProjectPlanTypes != null && !newProjectPlanTypes.isEmpty()) {
            newPlanTypeChangelog = new ProjectPlanTypeChangelog();
            newPlanTypeChangelog.setProject(project);
        }
        ProjectPlanTypeChangelog oldPlanTypeChangelog = prepareProjectChangelogValuesToUpdate(repo, project, project.getPlanType(), newPlanTypeChangelog,
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
            newChangelog.setProperty(repo.getReferenceById(Property.class, customPropertyId));
        }

        List<ProjectBooleanCustomPropertyChangelog> changelogs = project.getBooleanCustomProperties().stream()
                .filter(cp -> cp.getProperty().getId().equals(customPropertyId)).toList();

        ProjectBooleanCustomPropertyChangelog oldChangelog = prepareProjectChangelogValuesToUpdate(repo, project, changelogs, newChangelog,
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
                oldChangelogAfterUpdate.setProperty(oldChangelog.getProperty());
                repo.persist(oldChangelogAfterUpdate);
            }
        }
    }

    public void updateProjectTextCustomProperty(VngRepository repo, Project project, UUID customPropertyId, String newTextValue, UUID loggedInUserUuid,
            LocalDate updateDate) {
        ProjectTextPropertyChangelog oldChangelogAfterUpdate = new ProjectTextPropertyChangelog();
        ProjectTextPropertyChangelog newChangelog = null;
        if (newTextValue != null) {
            newChangelog = new ProjectTextPropertyChangelog();
            newChangelog.setProject(project);
            newChangelog.setValue(newTextValue);
            newChangelog.setProperty(repo.getReferenceById(Property.class, customPropertyId));
        }

        List<ProjectTextPropertyChangelog> changelogs = project.getTextCustomProperties().stream()
                .filter(cp -> cp.getProperty().getId().equals(customPropertyId)).toList();

        ProjectTextPropertyChangelog oldChangelog = prepareProjectChangelogValuesToUpdate(repo, project, changelogs, newChangelog,
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
                oldChangelogAfterUpdate.setProperty(oldChangelog.getProperty());
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
            newChangelog.setProperty(repo.getReferenceById(Property.class, customPropertyId));
        }

        List<ProjectNumericCustomPropertyChangelog> changelogs = project.getNumericCustomProperties().stream()
                .filter(cp -> cp.getProperty().getId().equals(customPropertyId)).toList();

        ProjectNumericCustomPropertyChangelog oldChangelog = prepareProjectChangelogValuesToUpdate(repo, project, changelogs, newChangelog,
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
                oldChangelogAfterUpdate.setProperty(oldChangelog.getProperty());
                repo.persist(oldChangelogAfterUpdate);
            }
        }
    }

    public void updateProjectCategoryProperty(VngRepository repo, Project project, UUID propertyId, Set<UUID> newCategoryValues,
            UUID loggedInUserUuid, LocalDate updateDate) {
        ProjectCategoryPropertyChangelog oldChangelogAfterUpdate = new ProjectCategoryPropertyChangelog();
        ProjectCategoryPropertyChangelog newChangelog = null;
        if (newCategoryValues != null && !newCategoryValues.isEmpty()) {
            newChangelog = new ProjectCategoryPropertyChangelog();
            newChangelog.setProject(project);
            newChangelog.setProperty(repo.getReferenceById(Property.class, propertyId));
        }

        List<ProjectCategoryPropertyChangelog> changelogs = project.getCategoryProperties().stream()
                .filter(cp -> cp.getProperty().getId().equals(propertyId)).toList();

        ProjectCategoryPropertyChangelog oldChangelog = prepareProjectChangelogValuesToUpdate(repo, project, changelogs, newChangelog,
                oldChangelogAfterUpdate, loggedInUserUuid, updateDate);

        if (newChangelog != null) {
            repo.persist(newChangelog);
            for (UUID newCategoryValue : newCategoryValues) {
                ProjectCategoryPropertyChangelogValue newChangelogValue = new ProjectCategoryPropertyChangelogValue();
                newChangelogValue.setCategoryChangelog(newChangelog);
                newChangelogValue.setCategoryValue(repo.getReferenceById(PropertyCategoryValue.class, newCategoryValue));
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
                oldChangelogAfterUpdate.setProperty(oldChangelog.getProperty());
                repo.persist(oldChangelogAfterUpdate);
                for (UUID oldCategoryValue : oldCategoryValues) {
                    ProjectCategoryPropertyChangelogValue oldChangelogValue = new ProjectCategoryPropertyChangelogValue();
                    oldChangelogValue.setCategoryChangelog(oldChangelogAfterUpdate);
                    oldChangelogValue.setCategoryValue(repo.getReferenceById(PropertyCategoryValue.class, oldCategoryValue));
                    repo.persist(oldChangelogValue);
                }
            }
        }
    }

    public void updateProjectOrdinalCustomProperty(VngRepository repo, Project project, UUID customPropertyId, SingleValueOrRangeModel<UUID> newOrdinalValue,
            UUID loggedInUserUuid, LocalDate updateDate) {
        ProjectOrdinalPropertyChangelog oldChangelogAfterUpdate = new ProjectOrdinalPropertyChangelog();
        ProjectOrdinalPropertyChangelog newChangelog = null;
        if (newOrdinalValue.getValue() != null || newOrdinalValue.getMin() != null || newOrdinalValue.getMax() != null) {
            newChangelog = new ProjectOrdinalPropertyChangelog();
            newChangelog.setProject(project);
            if (newOrdinalValue.getValue() != null) {
                newChangelog.setValue(repo.getReferenceById(PropertyOrdinalValue.class, newOrdinalValue.getValue()));
                newChangelog.setValueType(ValueType.SINGLE_VALUE);
            } else {
                newChangelog.setMinValue(repo.getReferenceById(PropertyOrdinalValue.class, newOrdinalValue.getMin()));
                newChangelog.setMaxValue(repo.getReferenceById(PropertyOrdinalValue.class, newOrdinalValue.getMax()));
                newChangelog.setValueType(ValueType.RANGE);
            }
            newChangelog.setProperty(repo.getReferenceById(Property.class, customPropertyId));
        }

        List<ProjectOrdinalPropertyChangelog> changelogs = project.getOrdinalCustomProperties().stream()
                .filter(cp -> cp.getProperty().getId().equals(customPropertyId)).toList();

        ProjectOrdinalPropertyChangelog oldChangelog = prepareProjectChangelogValuesToUpdate(repo, project, changelogs, newChangelog,
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
                oldChangelogAfterUpdate.setProperty(oldChangelog.getProperty());
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
        ProjectFaseChangelog oldProjectFaseChangelog = prepareProjectChangelogValuesToUpdate(repo, project, project.getPhase(), newProjectFaseChangelog,
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

    public void updateProjectDuration(VngRepository repo, Project project, LocalDate newStartDate, LocalDate newEndDate, UUID loggedInUserUuid)
            throws VngServerErrorException, VngBadRequestException {

        ZonedDateTime zdtNow = ZonedDateTime.now();
        User loggedUser = repo.getReferenceById(User.class, loggedInUserUuid);

        MilestoneState oldMilestoneState = null;
        MilestoneState newMilestoneState = new MilestoneState();

        var startMilestone = project.getDuration().get(0).getStartMilestone();

        if (newStartDate != null) {
            MilestoneModel projectStartMilestone = new MilestoneModel(startMilestone);
            LocalDate nextMilestoneDate = project.getMilestones().stream()
                    .map(MilestoneModel::new)
                    .filter(mm -> !Objects.equals(mm.getId(), projectStartMilestone.getId()) && mm.getStateId() != null)
                    .map(MilestoneModel::getDate)
                    .min(LocalDate::compareTo)
                    .orElseThrow(() -> new VngServerErrorException("Project does not have active milestones"));

            if (nextMilestoneDate.isAfter(newStartDate)) {
                oldMilestoneState = repo.findById(MilestoneState.class, projectStartMilestone.getStateId());
                newMilestoneState.setMilestone(startMilestone);
                newMilestoneState.setDate(newStartDate);
                startMilestone.getState().add(newMilestoneState);
            } else {
                throw new VngBadRequestException("Update is not possible because new start date overlaps other existing milestones in this project");
            }
        }

        if (newEndDate != null) {
            Milestone milestone = project.getDuration().get(0).getEndMilestone();
            MilestoneModel projectEndMilestone = new MilestoneModel(milestone);

            List<MilestoneModel> milestones = project.getMilestones()
                    .stream()
                    .map(MilestoneModel::new)
                    .sorted((a, b) -> a.getDate().compareTo(b.getDate()))
                    .toList();

            LocalDate previousMilestoneDate = milestones
                    .stream()
                    .filter(mm -> !Objects.equals(mm.getId(), projectEndMilestone.getId()) && mm.getStateId() != null)
                    .map(MilestoneModel::getDate)
                    .max(LocalDate::compareTo)
                    .orElseThrow(() -> new VngServerErrorException("Project does not have active milestones"));

            oldMilestoneState = repo.findById(MilestoneState.class, projectEndMilestone.getStateId());
            newMilestoneState.setMilestone(milestone);
            newMilestoneState.setDate(newEndDate);
            milestone.getState().add(newMilestoneState);

            if (!previousMilestoneDate.isBefore(newEndDate)) {
                replaceProjectChangelogsForNewEndDate(repo, project, zdtNow, loggedUser, startMilestone, milestone);

                // throw new VngBadRequestException("Update is not possible because new end date overlaps other existing milestones in this project");
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

    private void replaceProjectChangelogsForNewEndDate(VngRepository repo, Project project, ZonedDateTime zdtNow, User loggedUser, Milestone startMilestone,
            Milestone endMilestone) {
        for (var milestoneType : List.of(
                ProjectPlanTypeChangelog.class,
                ProjectBooleanCustomPropertyChangelog.class,
                ProjectNameChangelog.class,
                ProjectCategoryPropertyChangelog.class,
                ProjectPlanologischePlanstatusChangelog.class, // Issue with one to many
                ProjectDurationChangelog.class,
                ProjectRegistryLinkChangelog.class,
                ProjectTextPropertyChangelog.class,
                ProjectFaseChangelog.class,
                ProjectOrdinalPropertyChangelog.class,
                ProjectNumericCustomPropertyChangelog.class)) {

            var className = milestoneType.getSimpleName();

            var changeLogs = repo.getSession()
                    .createQuery("FROM " + className + " cl WHERE cl.project.id = :projectId", milestoneType)
                    .setParameter("projectId", project.getId())
                    .list();

            milestoneService.replaceChangelogsWithSingleChangelog(repo, zdtNow, loggedUser, startMilestone, endMilestone, changeLogs);
        }

    }

    private <T extends MilestoneChangeDataSuperclass> T prepareProjectChangelogValuesToUpdate(VngRepository repo, Project project, List<T> changelogs,
            T newProjectChangelog, T oldProjectChangelogAfterUpdate, UUID loggedInUserUuid, LocalDate updateDate) {

        Milestone projectStartMilestone = project.getDuration().get(0).getStartMilestone();
        Milestone projectEndMilestone = project.getDuration().get(0).getEndMilestone();

        return prepareChangelogValuesToUpdate(repo, project, changelogs, newProjectChangelog, oldProjectChangelogAfterUpdate, loggedInUserUuid,
                projectStartMilestone,
                projectEndMilestone, updateDate);
    }

    public <T extends MilestoneChangeDataSuperclass> T prepareChangelogValuesToUpdate(VngRepository repo, Project project, List<T> changelogs,
            T newChangelog, T oldChangelogAfterUpdate, UUID loggedInUserUuid, Milestone startMilestone, Milestone endMilestone, LocalDate updateDate) {

        LocalDate startDate = (new MilestoneModel(startMilestone)).getDate();
        LocalDate endDate = (new MilestoneModel(endMilestone)).getDate();

        ZonedDateTime zdtNow = ZonedDateTime.now();

        T oldChangelog;
        if (newChangelog != null) {
            newChangelog.setCreateUser(repo.getReferenceById(User.class, loggedInUserUuid));
            newChangelog.setChangeStartDate(zdtNow);
        }

        boolean finalIsCurrentOrFuture;
        LocalDate finalUpdateDate;
        if (startDate.isAfter(updateDate)) {
            finalUpdateDate = startDate;
            finalIsCurrentOrFuture = true;
        } else if (endDate.isBefore(updateDate)) {
            finalUpdateDate = endDate;
            finalIsCurrentOrFuture = false;
        } else {
            finalUpdateDate = updateDate;
            finalIsCurrentOrFuture = true;
        }

        oldChangelog = changelogs.stream()
                .filter(pc -> finalIsCurrentOrFuture ? !(new MilestoneModel(pc.getStartMilestone())).getDate().isAfter(finalUpdateDate)
                        && (new MilestoneModel(pc.getEndMilestone())).getDate().isAfter(finalUpdateDate)
                        : (new MilestoneModel(pc.getEndMilestone())).getDate().equals(finalUpdateDate))
                .findFirst().orElse(null);

        Milestone updateMilestone = getOrCreateMilestoneForProject(repo, project, finalUpdateDate, loggedInUserUuid);

        if (oldChangelog != null && finalIsCurrentOrFuture && !Objects.equals(oldChangelog.getStartMilestone().getId(), updateMilestone.getId())) {
            oldChangelogAfterUpdate.setStartMilestone(oldChangelog.getStartMilestone());
            oldChangelogAfterUpdate.setEndMilestone(updateMilestone);
            oldChangelogAfterUpdate.setCreateUser(oldChangelog.getCreateUser());
            oldChangelogAfterUpdate.setChangeStartDate(zdtNow);
        }

        if (newChangelog != null) {
            if (finalIsCurrentOrFuture) {
                newChangelog.setStartMilestone(updateMilestone);
            } else {
                if (oldChangelog != null) {
                    newChangelog.setStartMilestone(oldChangelog.getStartMilestone());
                } else {
                    newChangelog.setStartMilestone(startMilestone);
                }
            }
        }

        if (oldChangelog != null) {
            oldChangelog.setChangeEndDate(zdtNow);
            oldChangelog.setChangeUser(repo.getReferenceById(User.class, loggedInUserUuid));
        }

        if (newChangelog != null) {
            if (oldChangelog != null) {
                newChangelog.setEndMilestone(oldChangelog.getEndMilestone());
            } else {
                LocalDate currentStartDate = (new MilestoneModel(newChangelog.getStartMilestone())).getDate();
                UUID newEndMilestoneUuid = changelogs.stream().map(MilestoneChangeDataSuperclass::getStartMilestone)
                        .map(MilestoneModel::new)
                        .filter(mm -> mm.getDate().isAfter(currentStartDate))
                        .min(Comparator.comparing(MilestoneModel::getDate))
                        .map(MilestoneModel::getId)
                        .orElse(endMilestone.getId());
                newChangelog.setEndMilestone(repo.getReferenceById(Milestone.class, newEndMilestoneUuid));
            }
        }

        return oldChangelog;
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

        if (project.getState().size() != 1) {
            logger.error("Project with uuid {} has {} state values", projectUuid, project.getState().size());
            throw new VngServerErrorException("Project state is invalid.");
        }

        MilestoneModel projectStartMilestone = new MilestoneModel(project.getDuration().get(0).getStartMilestone());
        MilestoneModel projectEndMilestone = new MilestoneModel(project.getDuration().get(0).getEndMilestone());
        if (projectStartMilestone.getStateId() == null || projectEndMilestone.getStateId() == null) {
            logger.error("Project with uuid {} has start or end milestone with invalid states.", projectUuid);
            throw new VngServerErrorException("Project milestones are invalid.");
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

    public List<ProjectAuditModel> getProjectAuditLog(VngRepository repo, UUID projectId, LocalDateTime startDateTime, LocalDateTime endDateTime,
            LoggedUser loggedUser) {
        List<ProjectAuditSqlModel> sqlAuditLog = repo.getProjectsDAO().getProjectAuditLog(projectId, startDateTime, endDateTime, loggedUser);
        List<ProjectAuditModel> result = sqlAuditLog.stream().map(ProjectAuditModel::new).toList();
        return result;
    }
}
