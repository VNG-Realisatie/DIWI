package nl.vng.diwi.services;

import java.time.LocalDate;
import java.util.*;

import nl.vng.diwi.dal.AutoCloseTransaction;
import nl.vng.diwi.dal.VngRepository;
import nl.vng.diwi.dal.entities.*;
import nl.vng.diwi.dal.entities.enums.*;
import nl.vng.diwi.dal.entities.superclasses.MilestoneChangeDataSuperclass;
import nl.vng.diwi.rest.VngBadRequestException;
import nl.vng.diwi.rest.VngNotFoundException;

import nl.vng.diwi.rest.VngServerErrorException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.ZonedDateTime;
import java.util.UUID;
import java.util.stream.Collectors;

public class ProjectService {
    private static final Logger logger = LogManager.getLogger();

    public ProjectService() {
    }

    public Project getCurrentProject(VngRepository repo, UUID uuid) {
        return repo.getProjectsDAO().getCurrentProject(uuid);
    }

    public void updateProjectColor(VngRepository repo, UUID projectUuid, String newColor, UUID loggedInUserUuid)
        throws VngNotFoundException {

        try (AutoCloseTransaction transaction = repo.beginTransaction()) {
            ProjectState oldProjectState = repo.getProjectsDAO().getCurrentProjectState(projectUuid);
            if (oldProjectState == null) {
                logger.error("Active projectState was not found for projectUuid {}.", projectUuid);
                throw new VngNotFoundException();
            }

            if (!Objects.equals(oldProjectState.getColor(), newColor)) {
                ZonedDateTime now = ZonedDateTime.now();
                oldProjectState.setChangeEndDate(now);

                ProjectState newProjectState = new ProjectState();
                newProjectState.setProject(oldProjectState.getProject());
                newProjectState.setConfidentiality(oldProjectState.getConfidentiality());
                newProjectState.setColor(newColor);
                newProjectState.setChangeStartDate(now);
                newProjectState.setChangeUser(repo.findById(User.class, loggedInUserUuid));

                repo.persist(oldProjectState);
                repo.persist(newProjectState);
                transaction.commit();
            }
        }
    }

    public void updateProjectConfidentialityLevel(VngRepository repo, UUID projectUuid, Confidentiality newConfidentiality, UUID loggedInUserUuid)
        throws VngNotFoundException {

        try (AutoCloseTransaction transaction = repo.beginTransaction()) {
            ProjectState oldProjectState = repo.getProjectsDAO().getCurrentProjectState(projectUuid);
            if (oldProjectState == null) {
                logger.error("Active projectState was not found for projectUuid {}.", projectUuid);
                throw new VngNotFoundException();
            }

            if (!Objects.equals(oldProjectState.getConfidentiality(), newConfidentiality)) {
                ZonedDateTime now = ZonedDateTime.now();
                oldProjectState.setChangeEndDate(now);

                ProjectState newProjectState = new ProjectState();
                newProjectState.setProject(oldProjectState.getProject());
                newProjectState.setConfidentiality(newConfidentiality);
                newProjectState.setColor(oldProjectState.getColor());
                newProjectState.setChangeStartDate(now);
                newProjectState.setChangeUser(repo.getReferenceById(User.class, loggedInUserUuid));

                repo.persist(oldProjectState);
                repo.persist(newProjectState);
                transaction.commit();
            }
        }
    }

    public void updateProjectName(VngRepository repo, UUID projectUuid, String newName, UUID loggedInUserUuid) throws VngNotFoundException, VngServerErrorException, VngBadRequestException {
        //name is mandatory for the entire duration of the project

        Project project = getCurrentProjectAndPerformPreliminaryUpdateChecks(repo, projectUuid);

        try (AutoCloseTransaction transaction = repo.beginTransaction()) {
            ProjectNameChangelog oldProjectNameChangelogAfterUpdate = new ProjectNameChangelog();
            ProjectNameChangelog newProjectNameChangelog = new ProjectNameChangelog();
            newProjectNameChangelog.setProject(project);
            newProjectNameChangelog.setName(newName);

            ProjectNameChangelog oldProjectNameChangelog = prepareChangelogValuesToUpdate(repo, project, project.getName(), newProjectNameChangelog,
                oldProjectNameChangelogAfterUpdate, loggedInUserUuid);

            repo.persist(newProjectNameChangelog);
            if (oldProjectNameChangelog == null) {
                logger.error("Project with uuid {} has missing name changelog value", projectUuid);
                throw new VngServerErrorException("Project name changelog is invalid.");
            }

            if (Objects.equals(oldProjectNameChangelog.getName(), newName)) {
                logger.info("Trying to update the project {} with the same project phase value that it already has {}.", projectUuid, newName);
                return;
            }
            repo.persist(oldProjectNameChangelog);
            if (oldProjectNameChangelogAfterUpdate.getStartMilestone() != null) {
                //it is a current project && it had a non-null changelog before the update
                oldProjectNameChangelogAfterUpdate.setProject(project);
                oldProjectNameChangelogAfterUpdate.setName(oldProjectNameChangelog.getName());
                repo.persist(oldProjectNameChangelogAfterUpdate);
            }
            transaction.commit();
        }
    }

    public void updateProjectPlanStatus(VngRepository repo, UUID projectUuid, Set<PlanStatus> newProjectPlanStatuses, UUID loggedInUserUuid)
        throws VngServerErrorException, VngNotFoundException, VngBadRequestException {

        Project project = getCurrentProjectAndPerformPreliminaryUpdateChecks(repo, projectUuid);

        try (AutoCloseTransaction transaction = repo.beginTransaction()) {
            ProjectPlanologischePlanstatusChangelog oldPlanStatusChangelogAfterUpdate = new ProjectPlanologischePlanstatusChangelog();
            ProjectPlanologischePlanstatusChangelog newPlanStatusChangelog = null;
            if (newProjectPlanStatuses != null && !newProjectPlanStatuses.isEmpty()) {
                newPlanStatusChangelog = new ProjectPlanologischePlanstatusChangelog();
                newPlanStatusChangelog.setProject(project);
            }
            ProjectPlanologischePlanstatusChangelog oldPlanStatusChangelog = prepareChangelogValuesToUpdate(repo, project, project.getPlanologischePlanstatus(), newPlanStatusChangelog,
                oldPlanStatusChangelogAfterUpdate, loggedInUserUuid);
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
                    logger.info("Trying to update the project {} with the same plan statuses that it already has {}.", projectUuid, newProjectPlanStatuses);
                    return;
                }
                repo.persist(oldPlanStatusChangelog);
                if (oldPlanStatusChangelogAfterUpdate.getStartMilestone() != null) {
                    //it is a current project && it had a non-null changelog before the update
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
            transaction.commit();
        }
    }

    public void updateProjectPlanTypes(VngRepository repo, UUID projectUuid, Set<PlanType> newProjectPlanTypes, UUID loggedInUserUuid)
        throws VngServerErrorException, VngNotFoundException, VngBadRequestException {

        Project project = getCurrentProjectAndPerformPreliminaryUpdateChecks(repo, projectUuid);

        try (AutoCloseTransaction transaction = repo.beginTransaction()) {
            ProjectPlanTypeChangelog oldPlanTypeChangelogAfterUpdate = new ProjectPlanTypeChangelog();
            ProjectPlanTypeChangelog newPlanTypeChangelog = null;
            if (newProjectPlanTypes != null && !newProjectPlanTypes.isEmpty()) {
                newPlanTypeChangelog = new ProjectPlanTypeChangelog();
                newPlanTypeChangelog.setProject(project);
            }
            ProjectPlanTypeChangelog oldPlanTypeChangelog = prepareChangelogValuesToUpdate(repo, project, project.getPlanType(), newPlanTypeChangelog,
                oldPlanTypeChangelogAfterUpdate, loggedInUserUuid);
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
                    logger.info("Trying to update the project {} with the same plan types that it already has {}.", projectUuid, newProjectPlanTypes);
                    return;
                }
                repo.persist(oldPlanTypeChangelog);
                if (oldPlanTypeChangelogAfterUpdate.getStartMilestone() != null) {
                    //it is a current project && it had a non-null changelog before the update
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
            transaction.commit();
        }
    }

    public void updateProjectPhase(VngRepository repo, UUID projectUuid, ProjectPhase newProjectPhase, UUID loggedInUserUuid)
        throws VngServerErrorException, VngNotFoundException, VngBadRequestException {

        Project project = getCurrentProjectAndPerformPreliminaryUpdateChecks(repo, projectUuid);

        try (AutoCloseTransaction transaction = repo.beginTransaction()) {
            ProjectFaseChangelog oldProjectFaseChangelogAfterUpdate = new ProjectFaseChangelog();
            ProjectFaseChangelog newProjectFaseChangelog = null;
            if (newProjectPhase != null) {
                newProjectFaseChangelog = new ProjectFaseChangelog();
                newProjectFaseChangelog.setProject(project);
                newProjectFaseChangelog.setProjectPhase(newProjectPhase);
            }
            ProjectFaseChangelog oldProjectFaseChangelog = prepareChangelogValuesToUpdate(repo, project, project.getPhase(), newProjectFaseChangelog,
                oldProjectFaseChangelogAfterUpdate, loggedInUserUuid);
            if (newProjectFaseChangelog != null) {
                repo.persist(newProjectFaseChangelog);
            }
            if (oldProjectFaseChangelog != null) {
                if (Objects.equals(oldProjectFaseChangelog.getProjectPhase(), newProjectPhase)) {
                    logger.info("Trying to update the project {} with the same project phase value that it already has {}.", projectUuid, newProjectPhase);
                    return;
                }
                repo.persist(oldProjectFaseChangelog);
                if (oldProjectFaseChangelogAfterUpdate.getStartMilestone() != null) {
                    //it is a current project && it had a non-null changelog before the update
                    oldProjectFaseChangelogAfterUpdate.setProject(project);
                    oldProjectFaseChangelogAfterUpdate.setProjectPhase(oldProjectFaseChangelog.getProjectPhase());
                    repo.persist(oldProjectFaseChangelogAfterUpdate);
                }
            }
            transaction.commit();
        }
    }

    private <T extends MilestoneChangeDataSuperclass> T prepareChangelogValuesToUpdate(VngRepository repo, Project project, List<T> changelogs, T newProjectChangelog,
                                                                                       T oldProjectChangelogAfterUpdate, UUID loggedInUserUuid) {

        Milestone projectStartMilestone = project.getDuration().get(0).getStartMilestone();
        Milestone projectEndMilestone = project.getDuration().get(0).getEndMilestone();

        ZonedDateTime zdtNow = ZonedDateTime.now();
        LocalDate now = LocalDate.now();
        LocalDate projectStartDate = projectStartMilestone.getState().get(0).getDate();

        T oldProjectChangelog;
        if (newProjectChangelog != null) {
            newProjectChangelog.setChangeUser(repo.getReferenceById(User.class, loggedInUserUuid));
            newProjectChangelog.setChangeStartDate(zdtNow);
        }

        if (projectStartDate.isAfter(now)) {
            //this is a future project - if there is a changelog, a new version of the changelog with the same milestones is created
            //otherwise, a new one is created with start milestone as start of project
            oldProjectChangelog = changelogs.stream().filter(fc -> fc.getStartMilestone().getId().equals(projectStartMilestone.getId()))
                .findFirst().orElse(null);
            if (newProjectChangelog != null) {
                newProjectChangelog.setStartMilestone(projectStartMilestone);
            }
        } else {
            //this is a current project - a new version of the old changelog is created with end milestone today
            // and a new changelog with the new name is created with start milestone today
            // start milestone must be before or equal to today, end milestone must be after today
            oldProjectChangelog = changelogs.stream()
                .filter(pc -> !pc.getStartMilestone().getState().get(0).getDate().isAfter(now) && pc.getEndMilestone().getState().get(0).getDate().isAfter(now))
                .findFirst().orElse(null);

            Milestone todayMilestone = getOrCreateMilestoneForProject(repo, project, now, loggedInUserUuid);

            if (oldProjectChangelog != null && !Objects.equals(oldProjectChangelog.getStartMilestone().getId(), todayMilestone.getId())) {
                oldProjectChangelogAfterUpdate.setStartMilestone(oldProjectChangelog.getStartMilestone());
                oldProjectChangelogAfterUpdate.setEndMilestone(todayMilestone);
                oldProjectChangelogAfterUpdate.setChangeUser(oldProjectChangelog.getChangeUser());
                oldProjectChangelogAfterUpdate.setChangeStartDate(zdtNow);
            }

            if (newProjectChangelog != null) {
                newProjectChangelog.setStartMilestone(todayMilestone);
            }
        }

        if (oldProjectChangelog != null) {
            oldProjectChangelog.setChangeEndDate(zdtNow);
        }

        if (newProjectChangelog != null) {
            if (oldProjectChangelog != null) {
                newProjectChangelog.setEndMilestone(oldProjectChangelog.getEndMilestone());
            } else {
                LocalDate currentStartDate = newProjectChangelog.getStartMilestone().getState().get(0).getDate();
                Milestone newEndMilestone = changelogs.stream().map(MilestoneChangeDataSuperclass::getStartMilestone)
                    .filter(sm -> sm.getState().get(0).getDate().isAfter(currentStartDate))
                    .min(Comparator.comparing(m -> m.getState().get(0).getDate()))
                    .orElse(projectEndMilestone);
                newProjectChangelog.setEndMilestone(newEndMilestone);
            }
        }

        return oldProjectChangelog;
    }

    private Project getCurrentProjectAndPerformPreliminaryUpdateChecks(VngRepository repo, UUID projectUuid) throws VngNotFoundException, VngServerErrorException, VngBadRequestException {
        Project project = repo.getProjectsDAO().getCurrentProject(projectUuid);

        if (project == null) {
            logger.error("Project with uuid {} not found.", projectUuid);
            throw new VngNotFoundException("Project not found");
        }

        if (project.getDuration().size() != 1) {
            logger.error("Project with uuid {} has {} duration changelog values", projectUuid, project.getDuration().size());
            throw new VngServerErrorException("Project duration changelog is invalid.");
        }

        if (project.getDuration().get(0).getStartMilestone().getState().size() != 1 || project.getDuration().get(0).getEndMilestone().getState().size() != 1) {
            logger.error("Project with uuid {} has start or end milestone with invalid states.", projectUuid);
            throw new VngServerErrorException("Project milestones are invalid.");
        }

        if (!project.getDuration().get(0).getEndMilestone().getState().get(0).getDate().isAfter(LocalDate.now())) {
            logger.error("Project with uuid {} is in the past, it cannot be updated.", projectUuid);
            throw new VngBadRequestException("Cannot update past projects");
        }

        return project;
    }

    private Milestone getOrCreateMilestoneForProject(VngRepository repo, Project project, LocalDate milestoneDate, UUID loggedInUserUuid) {

        List<Milestone> projectMilestones = project.getMilestones();

        Milestone milestone = projectMilestones.stream()
            .filter(m -> {
                for (MilestoneState ms : m.getState()) {
                    if (ms.getChangeEndDate() == null && milestoneDate.equals(ms.getDate())) {
                        return true;
                    }
                }
                return false;
            })
            .findFirst().orElse(null);

        if (milestone == null) {
            milestone = new Milestone();
            milestone.setProject(project);
            repo.persist(milestone);

            MilestoneState milestoneState = new MilestoneState();
            milestoneState.setMilestone(milestone);
            milestoneState.setDate(milestoneDate);
            milestoneState.setChangeUser(repo.getReferenceById(User.class, loggedInUserUuid));
            milestoneState.setChangeStartDate(ZonedDateTime.now());
            milestoneState.setState(MilestoneStatus.GEPLAND);
            milestoneState.setDescription(milestoneDate.toString());

            repo.persist(milestoneState);
        }

        return milestone;
    }
}
