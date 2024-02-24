package nl.vng.diwi.services;

import nl.vng.diwi.dal.*;
import nl.vng.diwi.dal.entities.*;
import nl.vng.diwi.dal.entities.enums.Confidentiality;
import nl.vng.diwi.dal.entities.enums.MilestoneStatus;
import nl.vng.diwi.dal.entities.enums.PlanStatus;
import nl.vng.diwi.dal.entities.enums.ProjectPhase;
import nl.vng.diwi.rest.VngBadRequestException;
import nl.vng.diwi.rest.VngNotFoundException;
import nl.vng.diwi.rest.VngServerErrorException;
import nl.vng.diwi.testutil.TestDb;
import org.junit.jupiter.api.*;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class ProjectServiceTest {

    private static DalFactory dalFactory;
    private static TestDb testDb;
    private Dal dal;
    private VngRepository repo;

    private static ProjectService projectService;

    @BeforeAll
    static void beforeAll() throws Exception {
        testDb = new TestDb();
        dalFactory = testDb.getDalFactory();
        projectService = new ProjectService();
    }

    @AfterAll
    static void afterAll() {
        testDb.close();
    }

    @BeforeEach
    void beforeEach() {
        dal = dalFactory.constructDal();
        repo = new VngRepository(dal.getSession());
    }

    @AfterEach
    void afterEach() {
        dal.close();
    }


    /**
     *  Name must be defined for the entire duration of the project
     *  Test is for a current project. Initial state: project currently has Name 1 && it will later have Name 2
     *  Project name is updated
     *  Expected result: from the beginning of the project until now it will have Name 1, from now until the Name 2 start milestone
     *  it will have the updated name, the Name 2 changelog remains unchanged.
     */
    @Test
    void updateProjectNameTest() throws VngServerErrorException, VngBadRequestException, VngNotFoundException {

        UUID userUuid;
        UUID projectUuid;

        try (AutoCloseTransaction transaction = repo.beginTransaction()) {
            User user = repo.persist(new User());
            userUuid = user.getId();
            Project project = createProject(repo, user);
            projectUuid = project.getId();
            Milestone startMilestone = createMilestone(repo, project, LocalDate.now().minusDays(10), user);
            Milestone middleMilestone = createMilestone(repo, project, LocalDate.now().plusDays(5), user);
            Milestone endMilestone = createMilestone(repo, project, LocalDate.now().plusDays(10), user);
            createProjectDurationChangelog(repo, project, startMilestone, endMilestone, user);
            createProjectNameChangelog(repo, project, "Name 1", startMilestone, middleMilestone, user);
            createProjectNameChangelog(repo, project, "Name 2", middleMilestone, endMilestone, user);
            transaction.commit();
            repo.getSession().clear();
        }

        try (AutoCloseTransaction transaction = repo.beginTransaction()) {
            projectService.updateProjectName(repo, projectUuid, "Name 1 - Update Test", userUuid);
            transaction.commit();
            repo.getSession().clear();
        }

        repo.getSession().disableFilter(GenericRepository.CURRENT_DATA_FILTER);
        Project updatedProject = repo.findById(Project.class, projectUuid);
        List<ProjectNameChangelog> nameChangelogs = updatedProject.getName();

        assertThat(nameChangelogs.size()).isEqualTo(4);

        ProjectNameChangelog oldChangelog = nameChangelogs.stream().filter(c -> c.getName().equals("Name 1") && c.getChangeEndDate() != null).findFirst().orElse(null);
        assertThat(oldChangelog).isNotNull();
        assertThat(oldChangelog.getStartMilestone().getState().get(0).getDate()).isEqualTo(LocalDate.now().minusDays(10));
        assertThat(oldChangelog.getEndMilestone().getState().get(0).getDate()).isEqualTo(LocalDate.now().plusDays(5));

        ProjectNameChangelog oldChangelogV2 = nameChangelogs.stream().filter(c -> c.getName().equals("Name 1") && c.getChangeEndDate() == null).findFirst().orElse(null);
        assertThat(oldChangelogV2).isNotNull();
        assertThat(oldChangelogV2.getStartMilestone().getState().get(0).getDate()).isEqualTo(LocalDate.now().minusDays(10));
        assertThat(oldChangelogV2.getEndMilestone().getState().get(0).getDate()).isEqualTo(LocalDate.now());

        ProjectNameChangelog newChangelog = nameChangelogs.stream().filter(c -> c.getName().equals("Name 1 - Update Test") && c.getChangeEndDate() == null).findFirst().orElse(null);
        assertThat(newChangelog).isNotNull();
        assertThat(newChangelog.getStartMilestone().getState().get(0).getDate()).isEqualTo(LocalDate.now());
        assertThat(newChangelog.getEndMilestone().getState().get(0).getDate()).isEqualTo(LocalDate.now().plusDays(5));

        ProjectNameChangelog futureNameChangelog = nameChangelogs.stream().filter(c -> c.getName().equals("Name 2") && c.getChangeEndDate() == null).findFirst().orElse(null);
        assertThat(futureNameChangelog).isNotNull();
        assertThat(futureNameChangelog.getStartMilestone().getState().get(0).getDate()).isEqualTo(LocalDate.now().plusDays(5));
        assertThat(futureNameChangelog.getEndMilestone().getState().get(0).getDate()).isEqualTo(LocalDate.now().plusDays(10));
    }

    /**
     *  ProjectPhase can be null, but must not have gaps during the duration of the project
     *  Test is for a current project. Initial state: project currently has no phase defined && later it will have _4_REALISATIEFASE
     *  Project phase is updated
     *  Expected result: from the beginning of the project until now it will have no phase, from now until the _4_REALISATIEFASE start milestone
     *  it will have the updated phase, the _4_REALISATIEFASE changelog remains unchanged.
     */
    @Test
    void updateProjectPhaseTest() throws VngServerErrorException, VngBadRequestException, VngNotFoundException {

        UUID userUuid;
        UUID projectUuid;

        try (AutoCloseTransaction transaction = repo.beginTransaction()) {
            User user = repo.persist(new User());
            userUuid = user.getId();
            Project project = createProject(repo, user);
            projectUuid = project.getId();
            Milestone startMilestone = createMilestone(repo, project, LocalDate.now().minusDays(10), user);
            Milestone middleMilestone = createMilestone(repo, project, LocalDate.now().plusDays(5), user);
            Milestone endMilestone = createMilestone(repo, project, LocalDate.now().plusDays(10), user);
            createProjectDurationChangelog(repo, project, startMilestone, endMilestone, user);
            createProjectFaseChangelog(repo, project, ProjectPhase._4_REALISATIEFASE, middleMilestone, endMilestone, user);
            transaction.commit();
            repo.getSession().clear();
        }

        try (AutoCloseTransaction transaction = repo.beginTransaction()) {
            projectService.updateProjectPhase(repo, projectUuid, ProjectPhase._2_PROJECTFASE, userUuid);
            transaction.commit();
            repo.getSession().clear();
        }

        repo.getSession().disableFilter(GenericRepository.CURRENT_DATA_FILTER);
        Project updatedProject = repo.findById(Project.class, projectUuid);
        List<ProjectFaseChangelog> faseChangelogs = updatedProject.getPhase();

        assertThat(faseChangelogs.size()).isEqualTo(2);

        ProjectFaseChangelog newChangelog = faseChangelogs.stream().filter(c -> c.getProjectPhase().equals(ProjectPhase._2_PROJECTFASE) && c.getChangeEndDate() == null).findFirst().orElse(null);
        assertThat(newChangelog).isNotNull();
        assertThat(newChangelog.getStartMilestone().getState().get(0).getDate()).isEqualTo(LocalDate.now());
        assertThat(newChangelog.getEndMilestone().getState().get(0).getDate()).isEqualTo(LocalDate.now().plusDays(5));

        ProjectFaseChangelog futureFaseChangelog = faseChangelogs.stream().filter(c -> c.getProjectPhase().equals(ProjectPhase._4_REALISATIEFASE) && c.getChangeEndDate() == null).findFirst().orElse(null);
        assertThat(futureFaseChangelog).isNotNull();
        assertThat(futureFaseChangelog.getStartMilestone().getState().get(0).getDate()).isEqualTo(LocalDate.now().plusDays(5));
        assertThat(futureFaseChangelog.getEndMilestone().getState().get(0).getDate()).isEqualTo(LocalDate.now().plusDays(10));
    }

    /**
     *  PlanStatus can be updated to null
     *  Test is for a current project. Initial state: project currently has 2 plan status values
     *  Plan Status is updated to null
     *  Expected result: from the beginning of the project until now it will have 2 plan statuses, from now on it will have none
     */
    @Test
    void updateProjectPlanStatus() throws VngServerErrorException, VngBadRequestException, VngNotFoundException {

        UUID userUuid;
        UUID projectUuid;

        try (AutoCloseTransaction transaction = repo.beginTransaction()) {
            User user = repo.persist(new User());
            userUuid = user.getId();
            Project project = createProject(repo, user);
            projectUuid = project.getId();
            Milestone startMilestone = createMilestone(repo, project, LocalDate.now().minusDays(10), user);
            Milestone endMilestone = createMilestone(repo, project, LocalDate.now().plusDays(10), user);
            createProjectDurationChangelog(repo, project, startMilestone, endMilestone, user);
            createProjectPlanStatusChangelog(repo, project, Set.of(PlanStatus._1A_ONHERROEPELIJK, PlanStatus._2A_VASTGESTELD), startMilestone, endMilestone, user);
            transaction.commit();
            repo.getSession().clear();
        }

        try (AutoCloseTransaction transaction = repo.beginTransaction()) {
            projectService.updateProjectPlanStatus(repo, projectUuid, null, userUuid);
            transaction.commit();
            repo.getSession().clear();
        }

        repo.getSession().disableFilter(GenericRepository.CURRENT_DATA_FILTER);
        Project updatedProject = repo.findById(Project.class, projectUuid);
        List<ProjectPlanologischePlanstatusChangelog> planStatusChangelogs = updatedProject.getPlanologischePlanstatus();

        assertThat(planStatusChangelogs.size()).isEqualTo(2);

        ProjectPlanologischePlanstatusChangelog oldChangelog = planStatusChangelogs.stream().filter(c -> c.getChangeEndDate() != null).findFirst().orElse(null);
        assertThat(oldChangelog).isNotNull();
        assertThat(oldChangelog.getStartMilestone().getState().get(0).getDate()).isEqualTo(LocalDate.now().minusDays(10));
        assertThat(oldChangelog.getEndMilestone().getState().get(0).getDate()).isEqualTo(LocalDate.now().plusDays(10));
        List<ProjectPlanologischePlanstatusChangelogValue> oldChangelogValues = oldChangelog.getValue();
        assertThat(oldChangelogValues).isNotNull();
        assertThat(oldChangelogValues.size()).isEqualTo(2);
        Set<PlanStatus> oldChangelogPlanStatus = oldChangelogValues.stream().map(ProjectPlanologischePlanstatusChangelogValue::getPlanStatus).collect(Collectors.toSet());
        assertThat(oldChangelogPlanStatus).containsExactlyInAnyOrder(PlanStatus._1A_ONHERROEPELIJK, PlanStatus._2A_VASTGESTELD);

        ProjectPlanologischePlanstatusChangelog oldChangelogV2 = planStatusChangelogs.stream().filter(c -> c.getChangeEndDate() == null).findFirst().orElse(null);
        assertThat(oldChangelogV2).isNotNull();
        assertThat(oldChangelogV2.getStartMilestone().getState().get(0).getDate()).isEqualTo(LocalDate.now().minusDays(10));
        assertThat(oldChangelogV2.getEndMilestone().getState().get(0).getDate()).isEqualTo(LocalDate.now());
        List<ProjectPlanologischePlanstatusChangelogValue> oldChangelogV2Values = oldChangelog.getValue();
        assertThat(oldChangelogV2Values).isNotNull();
        assertThat(oldChangelogV2Values.size()).isEqualTo(2);
        Set<PlanStatus> oldChangelogV2PlanStatus = oldChangelogValues.stream().map(ProjectPlanologischePlanstatusChangelogValue::getPlanStatus).collect(Collectors.toSet());
        assertThat(oldChangelogV2PlanStatus).containsExactlyInAnyOrder(PlanStatus._1A_ONHERROEPELIJK, PlanStatus._2A_VASTGESTELD);
    }

    private Project createProject(VngRepository repo, User user) {
        Project project = repo.persist(new Project());
        ProjectState projectState = new ProjectState();
        projectState.setProject(project);
        projectState.setCreateUser(user);
        projectState.setConfidentiality(Confidentiality.OPENBAAR);
        projectState.setChangeStartDate(ZonedDateTime.now());
        projectState.setColor("#123456");
        repo.persist(projectState);
        return project;
    }

    private Milestone createMilestone(VngRepository repo, Project project, LocalDate date, User user) {

        Milestone milestone = new Milestone();
        milestone.setProject(project);
        repo.persist(milestone);
        MilestoneState milestoneState = new MilestoneState();
        milestoneState.setMilestone(milestone);
        milestoneState.setCreateUser(user);
        milestoneState.setDate(date);
        milestoneState.setChangeStartDate(ZonedDateTime.now());
        milestoneState.setState(MilestoneStatus.GEPLAND);
        repo.persist(milestoneState);

        return milestone;
    }

    private ProjectDurationChangelog createProjectDurationChangelog(VngRepository repo, Project project, Milestone startMilestone,
                                                                    Milestone endMilestone, User user) {
        ProjectDurationChangelog durationChangelog = new ProjectDurationChangelog();
        durationChangelog.setProject(project);
        durationChangelog.setCreateUser(user);
        durationChangelog.setStartMilestone(startMilestone);
        durationChangelog.setEndMilestone(endMilestone);
        durationChangelog.setChangeStartDate(ZonedDateTime.now());
        repo.persist(durationChangelog);
        return durationChangelog;
    }

    private ProjectNameChangelog createProjectNameChangelog(VngRepository repo, Project project, String name, Milestone startMilestone,
                                                                    Milestone endMilestone, User user) {
        ProjectNameChangelog nameChangelog = new ProjectNameChangelog();
        nameChangelog.setProject(project);
        nameChangelog.setCreateUser(user);
        nameChangelog.setStartMilestone(startMilestone);
        nameChangelog.setEndMilestone(endMilestone);
        nameChangelog.setChangeStartDate(ZonedDateTime.now());
        nameChangelog.setName(name);
        repo.persist(nameChangelog);
        return nameChangelog;
    }

    private ProjectFaseChangelog createProjectFaseChangelog(VngRepository repo, Project project, ProjectPhase fase, Milestone startMilestone,
                                                            Milestone endMilestone, User user) {
        ProjectFaseChangelog faseChangelog = new ProjectFaseChangelog();
        faseChangelog.setProject(project);
        faseChangelog.setCreateUser(user);
        faseChangelog.setStartMilestone(startMilestone);
        faseChangelog.setEndMilestone(endMilestone);
        faseChangelog.setChangeStartDate(ZonedDateTime.now());
        faseChangelog.setProjectPhase(fase);
        repo.persist(faseChangelog);
        return faseChangelog;
    }


    private ProjectPlanologischePlanstatusChangelog createProjectPlanStatusChangelog(VngRepository repo, Project project, Set<PlanStatus> planStatuses, Milestone startMilestone,
                                                  Milestone endMilestone, User user) {

        ProjectPlanologischePlanstatusChangelog planStatusChangelog = new ProjectPlanologischePlanstatusChangelog();
        planStatusChangelog.setProject(project);
        planStatusChangelog.setCreateUser(user);
        planStatusChangelog.setStartMilestone(startMilestone);
        planStatusChangelog.setEndMilestone(endMilestone);
        planStatusChangelog.setChangeStartDate(ZonedDateTime.now());
        repo.persist(planStatusChangelog);

        for (PlanStatus planStatusValue : planStatuses) {
            ProjectPlanologischePlanstatusChangelogValue value = new ProjectPlanologischePlanstatusChangelogValue();
            value.setPlanStatus(planStatusValue);
            value.setPlanStatusChangelog(planStatusChangelog);
            repo.persist(value);
        }

        return planStatusChangelog;
    }
}
