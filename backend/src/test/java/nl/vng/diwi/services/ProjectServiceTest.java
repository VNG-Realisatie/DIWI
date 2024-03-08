package nl.vng.diwi.services;

import nl.vng.diwi.dal.*;
import nl.vng.diwi.dal.entities.*;
import nl.vng.diwi.dal.entities.enums.Confidentiality;
import nl.vng.diwi.dal.entities.enums.MilestoneStatus;
import nl.vng.diwi.dal.entities.enums.PlanStatus;
import nl.vng.diwi.dal.entities.enums.ProjectPhase;
import nl.vng.diwi.dal.entities.enums.ValueType;
import nl.vng.diwi.rest.VngBadRequestException;
import nl.vng.diwi.rest.VngNotFoundException;
import nl.vng.diwi.rest.VngServerErrorException;
import nl.vng.diwi.testutil.TestDb;

import org.apache.commons.lang3.NotImplementedException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import jakarta.persistence.Tuple;

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
    private UUID userUuid;
    private User user;
    private Project project;
    private UUID projectUuid;

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

        try (AutoCloseTransaction transaction = repo.beginTransaction()) {
            user = repo.persist(new User());
            userUuid = user.getId();

            project = createProject(repo, user);
            projectUuid = project.getId();

            transaction.commit();
            repo.getSession().clear();
        }
    }

    @AfterEach
    void afterEach() {
        dal.close();
    }

    /**
     * Name must be defined for the entire duration of the project Test is for a current project. Initial state: project currently has Name 1 && it will later
     * have Name 2 Project name is updated Expected result: from the beginning of the project until now it will have Name 1, from now until the Name 2 start
     * milestone it will have the updated name, the Name 2 changelog remains unchanged.
     */
    @Test
    void updateProjectNameTest() throws VngServerErrorException, VngBadRequestException, VngNotFoundException {

        UUID projectUuid;

        try (AutoCloseTransaction transaction = repo.beginTransaction()) {
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
            projectService.updateProjectName(repo, repo.findById(Project.class, projectUuid), "Name 1 - Update Test", userUuid, LocalDate.now());
            transaction.commit();
            repo.getSession().clear();
        }

        repo.getSession().disableFilter(GenericRepository.CURRENT_DATA_FILTER);
        Project updatedProject = repo.findById(Project.class, projectUuid);
        List<ProjectNameChangelog> nameChangelogs = updatedProject.getName();

        assertThat(nameChangelogs.size()).isEqualTo(4);

        ProjectNameChangelog oldChangelog = nameChangelogs.stream().filter(c -> c.getName().equals("Name 1") && c.getChangeEndDate() != null).findFirst()
                .orElse(null);
        assertThat(oldChangelog).isNotNull();
        assertThat(oldChangelog.getStartMilestone().getState().get(0).getDate()).isEqualTo(LocalDate.now().minusDays(10));
        assertThat(oldChangelog.getEndMilestone().getState().get(0).getDate()).isEqualTo(LocalDate.now().plusDays(5));

        ProjectNameChangelog oldChangelogV2 = nameChangelogs.stream().filter(c -> c.getName().equals("Name 1") && c.getChangeEndDate() == null).findFirst()
                .orElse(null);
        assertThat(oldChangelogV2).isNotNull();
        assertThat(oldChangelogV2.getStartMilestone().getState().get(0).getDate()).isEqualTo(LocalDate.now().minusDays(10));
        assertThat(oldChangelogV2.getEndMilestone().getState().get(0).getDate()).isEqualTo(LocalDate.now());

        ProjectNameChangelog newChangelog = nameChangelogs.stream().filter(c -> c.getName().equals("Name 1 - Update Test") && c.getChangeEndDate() == null)
                .findFirst().orElse(null);
        assertThat(newChangelog).isNotNull();
        assertThat(newChangelog.getStartMilestone().getState().get(0).getDate()).isEqualTo(LocalDate.now());
        assertThat(newChangelog.getEndMilestone().getState().get(0).getDate()).isEqualTo(LocalDate.now().plusDays(5));

        ProjectNameChangelog futureNameChangelog = nameChangelogs.stream().filter(c -> c.getName().equals("Name 2") && c.getChangeEndDate() == null).findFirst()
                .orElse(null);
        assertThat(futureNameChangelog).isNotNull();
        assertThat(futureNameChangelog.getStartMilestone().getState().get(0).getDate()).isEqualTo(LocalDate.now().plusDays(5));
        assertThat(futureNameChangelog.getEndMilestone().getState().get(0).getDate()).isEqualTo(LocalDate.now().plusDays(10));
    }

    /**
     * ProjectPhase can be null, but must not have gaps during the duration of the project Test is for a current project. Initial state: project currently has
     * no phase defined && later it will have _4_REALISATIEFASE Project phase is updated Expected result: from the beginning of the project until now it will
     * have no phase, from now until the _4_REALISATIEFASE start milestone it will have the updated phase, the _4_REALISATIEFASE changelog remains unchanged.
     */
    @Test
    void updateProjectPhaseTest() throws VngServerErrorException, VngBadRequestException, VngNotFoundException {

        try (AutoCloseTransaction transaction = repo.beginTransaction()) {
            Milestone startMilestone = createMilestone(repo, project, LocalDate.now().minusDays(10), user);
            Milestone middleMilestone = createMilestone(repo, project, LocalDate.now().plusDays(5), user);
            Milestone endMilestone = createMilestone(repo, project, LocalDate.now().plusDays(10), user);
            createProjectDurationChangelog(repo, project, startMilestone, endMilestone, user);
            createProjectFaseChangelog(repo, project, ProjectPhase._4_REALISATIEFASE, middleMilestone, endMilestone, user);
            transaction.commit();
            repo.getSession().clear();
        }

        try (AutoCloseTransaction transaction = repo.beginTransaction()) {
            projectService.updateProjectPhase(repo, repo.findById(Project.class, projectUuid), ProjectPhase._2_PROJECTFASE, userUuid, LocalDate.now());
            transaction.commit();
            repo.getSession().clear();
        }

        repo.getSession().disableFilter(GenericRepository.CURRENT_DATA_FILTER);
        Project updatedProject = repo.findById(Project.class, projectUuid);
        List<ProjectFaseChangelog> faseChangelogs = updatedProject.getPhase();

        assertThat(faseChangelogs.size()).isEqualTo(2);

        ProjectFaseChangelog newChangelog = faseChangelogs.stream()
                .filter(c -> c.getProjectPhase().equals(ProjectPhase._2_PROJECTFASE) && c.getChangeEndDate() == null).findFirst().orElse(null);
        assertThat(newChangelog).isNotNull();
        assertThat(newChangelog.getStartMilestone().getState().get(0).getDate()).isEqualTo(LocalDate.now());
        assertThat(newChangelog.getEndMilestone().getState().get(0).getDate()).isEqualTo(LocalDate.now().plusDays(5));

        ProjectFaseChangelog futureFaseChangelog = faseChangelogs.stream()
                .filter(c -> c.getProjectPhase().equals(ProjectPhase._4_REALISATIEFASE) && c.getChangeEndDate() == null).findFirst().orElse(null);
        assertThat(futureFaseChangelog).isNotNull();
        assertThat(futureFaseChangelog.getStartMilestone().getState().get(0).getDate()).isEqualTo(LocalDate.now().plusDays(5));
        assertThat(futureFaseChangelog.getEndMilestone().getState().get(0).getDate()).isEqualTo(LocalDate.now().plusDays(10));
    }

    /**
     * PlanStatus can be updated to null Test is for a current project. Initial state: project currently has 2 plan status values Plan Status is updated to null
     * Expected result: from the beginning of the project until now it will have 2 plan statuses, from now on it will have none
     */
    @Test
    void updateProjectPlanStatus() throws VngServerErrorException, VngBadRequestException, VngNotFoundException {

        try (AutoCloseTransaction transaction = repo.beginTransaction()) {
            Milestone startMilestone = createMilestone(repo, project, LocalDate.now().minusDays(10), user);
            Milestone endMilestone = createMilestone(repo, project, LocalDate.now().plusDays(10), user);
            createProjectDurationChangelog(repo, project, startMilestone, endMilestone, user);
            createProjectPlanStatusChangelog(repo, project, Set.of(PlanStatus._1A_ONHERROEPELIJK, PlanStatus._2A_VASTGESTELD), startMilestone, endMilestone,
                    user);
            transaction.commit();
            repo.getSession().clear();
        }

        try (AutoCloseTransaction transaction = repo.beginTransaction()) {
            projectService.updateProjectPlanStatus(repo, repo.findById(Project.class, projectUuid), null, userUuid, LocalDate.now());
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
        Set<PlanStatus> oldChangelogPlanStatus = oldChangelogValues.stream().map(ProjectPlanologischePlanstatusChangelogValue::getPlanStatus)
                .collect(Collectors.toSet());
        assertThat(oldChangelogPlanStatus).containsExactlyInAnyOrder(PlanStatus._1A_ONHERROEPELIJK, PlanStatus._2A_VASTGESTELD);

        ProjectPlanologischePlanstatusChangelog oldChangelogV2 = planStatusChangelogs.stream().filter(c -> c.getChangeEndDate() == null).findFirst()
                .orElse(null);
        assertThat(oldChangelogV2).isNotNull();
        assertThat(oldChangelogV2.getStartMilestone().getState().get(0).getDate()).isEqualTo(LocalDate.now().minusDays(10));
        assertThat(oldChangelogV2.getEndMilestone().getState().get(0).getDate()).isEqualTo(LocalDate.now());
        List<ProjectPlanologischePlanstatusChangelogValue> oldChangelogV2Values = oldChangelog.getValue();
        assertThat(oldChangelogV2Values).isNotNull();
        assertThat(oldChangelogV2Values.size()).isEqualTo(2);
        Set<PlanStatus> oldChangelogV2PlanStatus = oldChangelogValues.stream().map(ProjectPlanologischePlanstatusChangelogValue::getPlanStatus)
                .collect(Collectors.toSet());
        assertThat(oldChangelogV2PlanStatus).containsExactlyInAnyOrder(PlanStatus._1A_ONHERROEPELIJK, PlanStatus._2A_VASTGESTELD);
    }

    /**
     * Municipality role can have multiple changelogs active at the same time for a project Test is for a current project. Initial state: project currently has
     * no municipality role A municipality role is added to the project Expected result: from the beginning of the project until now it will have 0 municipality
     * roles, from now on it will have one
     */
    @Test
    void updateProjectMunicipalityRole() throws VngServerErrorException, VngBadRequestException, VngNotFoundException {
        UUID municipalityRoleUuid;

        try (AutoCloseTransaction transaction = repo.beginTransaction()) {
            Milestone startMilestone = createMilestone(repo, project, LocalDate.now().minusDays(10), user);
            Milestone endMilestone = createMilestone(repo, project, LocalDate.now().plusDays(10), user);
            createProjectDurationChangelog(repo, project, startMilestone, endMilestone, user);
            ProjectGemeenteRolValue municipalityRole = new ProjectGemeenteRolValue();
            repo.persist(municipalityRole);
            municipalityRoleUuid = municipalityRole.getId();
            transaction.commit();
            repo.getSession().clear();
        }

        try (AutoCloseTransaction transaction = repo.beginTransaction()) {
            projectService.updateProjectMunicipalityRoles(repo, repo.findById(Project.class, projectUuid), municipalityRoleUuid, null, userUuid,
                    LocalDate.now());
            transaction.commit();
            repo.getSession().clear();
        }

        repo.getSession().disableFilter(GenericRepository.CURRENT_DATA_FILTER);
        Project updatedProject = repo.findById(Project.class, projectUuid);
        List<ProjectGemeenteRolChangelog> municipalitRolesChangelogs = updatedProject.getMunicipalityRole();

        assertThat(municipalitRolesChangelogs.size()).isEqualTo(1);
        ProjectGemeenteRolChangelog newMunicipalityRoleChangelog = municipalitRolesChangelogs.get(0);
        assertThat(newMunicipalityRoleChangelog.getStartMilestone().getState().get(0).getDate()).isEqualTo(LocalDate.now());
        assertThat(newMunicipalityRoleChangelog.getEndMilestone().getState().get(0).getDate()).isEqualTo(LocalDate.now().plusDays(10));
        assertThat(newMunicipalityRoleChangelog.getValue().getId()).isEqualTo(municipalityRoleUuid);
    }

    @ParameterizedTest
    @ValueSource(strings = { "project_state",
//            "project_actor_rol_changelog", Not used yet
            "project_duration_changelog",
            "project_fase_changelog",
            "project_gemeenterol_changelog",
//            "project_maatwerk_boolean_changelog", Not used yet
//            "project_maatwerk_categorie_changelog", Not used yet
//            "project_maatwerk_numeriek_changelog", Not used yet
//            "project_maatwerk_ordinaal_changelog", Not used yet
            "project_name_changelog",
            "project_plan_type_changelog",
            "project_planologische_planstatus_changelog",
            "project_planologische_planstatus_changelog",
            "project_priorisering_changelog"
    })
    void deleteProject(String tableName) throws Exception {
        try (AutoCloseTransaction transaction = repo.beginTransaction()) {
            Milestone startMilestone = createMilestone(repo, project, LocalDate.now().minusDays(10), user);
            Milestone endMilestone = createMilestone(repo, project, LocalDate.now().plusDays(10), user);

            if (tableName.equals("project_name_changelog")) {
                createProjectNameChangelog(repo, project, "", startMilestone, endMilestone, user);
            } else if (tableName.equals("project_duration_changelog")) {
                createProjectDurationChangelog(repo, project, startMilestone, endMilestone, user);
            } else if (tableName.equals("project_fase_changelog")) {
                createProjectFaseChangelog(repo, project, ProjectPhase._1_INITIATIEFFASE, startMilestone, endMilestone, user);
            } else if (tableName.equals("project_plan_type_changelog")) {
                createProjectPlanTypeChangelog(repo, project, startMilestone, endMilestone, user);
            } else if (tableName.equals("project_gemeenterol_changelog")) {
                createProjectGemeenteRolChangelog(repo, project, startMilestone, endMilestone, user);
            } else if (tableName.equals("project_priorisering_changelog")) {
                createPriorityChangelog(repo, project, startMilestone, endMilestone, user);
            } else if (tableName.equals("project_planologische_planstatus_changelog")) {
                createProjectPlanStatusChangelog(repo, project, Set.of(PlanStatus._1A_ONHERROEPELIJK), startMilestone, endMilestone, user);
            }
            else if (tableName.equals("project_gemeenterol_changelog")) {
                createProjectGemeenteRolChangelog(repo, project, startMilestone, endMilestone, user);
            } else if (tableName.equals("project_state")) {
                // No need to create this one. it is created in the before each method
            } else {
                throw new NotImplementedException();
            }
            transaction.commit();
            repo.getSession().clear();
        }

        try (AutoCloseTransaction transaction = repo.beginTransaction()) {
            projectService.deleteProject(repo, projectUuid, userUuid);
            transaction.commit();
            repo.getSession().clear();
        }

        List<Tuple> changelogs = repo
                .getSession()
                .createNativeQuery("SELECT change_end_date, change_user_id FROM diwi_testset." + tableName + " WHERE project_id = :projectUuid",
                        Tuple.class)
                .setParameter("projectUuid", projectUuid)
                .list();

        assertThat(changelogs)
                .hasSize(1); // Only one change log at a time

        assertThat(changelogs)
                .extracting((t) -> t.get("change_end_date"))
                .doesNotContainNull();

        assertThat(changelogs)
                .extracting((t) -> t.get("change_user_id"))
                .containsOnly(userUuid);
    }

    public static Project createProject(VngRepository repo, User user) {
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

    public static Milestone createMilestone(VngRepository repo, Project project, LocalDate date, User user) {

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

    public static ProjectDurationChangelog createProjectDurationChangelog(VngRepository repo, Project project, Milestone startMilestone,
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

    public static ProjectNameChangelog createProjectNameChangelog(VngRepository repo, Project project, String name, Milestone startMilestone,
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

    public static ProjectFaseChangelog createProjectFaseChangelog(VngRepository repo, Project project, ProjectPhase fase, Milestone startMilestone,
            Milestone endMilestone, User user) {
        ProjectFaseChangelog changelog = new ProjectFaseChangelog();
        changelog.setProject(project);
        changelog.setCreateUser(user);
        changelog.setStartMilestone(startMilestone);
        changelog.setEndMilestone(endMilestone);
        changelog.setChangeStartDate(ZonedDateTime.now());
        changelog.setProjectPhase(fase);
        repo.persist(changelog);
        return changelog;
    }

    private ProjectPlanTypeChangelog createProjectPlanTypeChangelog(VngRepository repo, Project project, Milestone startMilestone, Milestone endMilestone,
            User user) {
        var changelog = new ProjectPlanTypeChangelog();
        changelog.setProject(project);
        changelog.setCreateUser(user);
        changelog.setStartMilestone(startMilestone);
        changelog.setEndMilestone(endMilestone);
        changelog.setChangeStartDate(ZonedDateTime.now());
        repo.persist(changelog);
        return changelog;
    }

    private ProjectGemeenteRolChangelog createProjectGemeenteRolChangelog(VngRepository repo, Project project, Milestone startMilestone, Milestone endMilestone,
            User user) {
        var value = new ProjectGemeenteRolValue();
        repo.persist(value);

        var changelog = new ProjectGemeenteRolChangelog();
        changelog.setProject(project);
        changelog.setCreateUser(user);
        changelog.setStartMilestone(startMilestone);
        changelog.setEndMilestone(endMilestone);
        changelog.setChangeStartDate(ZonedDateTime.now());
        changelog.setValue(value);
        repo.persist(changelog);
        return changelog;
    }

    private ProjectPrioriseringChangelog createPriorityChangelog(VngRepository repo, Project project, Milestone startMilestone, Milestone endMilestone,
            User user) {
        var changelogValue = new ProjectPrioriseringValue();
        repo.persist(changelogValue);

        var changelog = new ProjectPrioriseringChangelog();
        changelog.setProject(project);
        changelog.setCreateUser(user);
        changelog.setStartMilestone(startMilestone);
        changelog.setEndMilestone(endMilestone);
        changelog.setChangeStartDate(ZonedDateTime.now());
        changelog.setValue(changelogValue);
        changelog.setValueType(ValueType.SINGLE_VALUE);
        repo.persist(changelog);
        return changelog;
    }

    public static ProjectPlanologischePlanstatusChangelog createProjectPlanStatusChangelog(VngRepository repo, Project project, Set<PlanStatus> planStatuses,
            Milestone startMilestone,
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
