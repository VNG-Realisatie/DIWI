package nl.vng.diwi.services;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.assertj.core.api.recursive.comparison.RecursiveComparisonConfiguration;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import jakarta.persistence.Tuple;
import nl.vng.diwi.dal.AutoCloseTransaction;
import nl.vng.diwi.dal.Dal;
import nl.vng.diwi.dal.DalFactory;
import nl.vng.diwi.dal.GenericRepository;
import nl.vng.diwi.dal.VngRepository;
import nl.vng.diwi.dal.entities.Milestone;
import nl.vng.diwi.dal.entities.MilestoneState;
import nl.vng.diwi.dal.entities.Project;
import nl.vng.diwi.dal.entities.ProjectDurationChangelog;
import nl.vng.diwi.dal.entities.ProjectFaseChangelog;
import nl.vng.diwi.dal.entities.ProjectGemeenteRolChangelog;
import nl.vng.diwi.dal.entities.ProjectGemeenteRolValue;
import nl.vng.diwi.dal.entities.ProjectNameChangelog;
import nl.vng.diwi.dal.entities.ProjectPlanologischePlanstatusChangelog;
import nl.vng.diwi.dal.entities.ProjectPlanologischePlanstatusChangelogValue;
import nl.vng.diwi.dal.entities.ProjectRegistryLinkChangelog;
import nl.vng.diwi.dal.entities.ProjectRegistryLinkChangelogValue;
import nl.vng.diwi.dal.entities.ProjectState;
import nl.vng.diwi.dal.entities.User;
import nl.vng.diwi.dal.entities.enums.Confidentiality;
import nl.vng.diwi.dal.entities.enums.MilestoneStatus;
import nl.vng.diwi.dal.entities.enums.PlanStatus;
import nl.vng.diwi.dal.entities.enums.ProjectPhase;
import nl.vng.diwi.models.PlotModel;
import nl.vng.diwi.models.ProjectSnapshotModel;
import nl.vng.diwi.models.superclasses.ProjectMinimalSnapshotModel;
import nl.vng.diwi.rest.VngBadRequestException;
import nl.vng.diwi.rest.VngNotFoundException;
import nl.vng.diwi.rest.VngServerErrorException;
import nl.vng.diwi.testutil.TestDb;

public class ProjectServiceTest {
    private static RecursiveComparisonConfiguration ignoreId = RecursiveComparisonConfiguration.builder().withIgnoredFields("id").build();

    private static DalFactory dalFactory;
    private static TestDb testDb;

    private Dal dal;
    private VngRepository repo;

    private ZonedDateTime now;
    private UUID userUuid;
    private User user;
    private Project project;
    private UUID projectUuid;
    private Milestone startMilestone;
    private Milestone endMilestone;
    private Milestone middleMilestone;

    private static ProjectService projectService;

    @BeforeAll
    static void beforeAll() throws Exception {
        testDb = new TestDb();
        dalFactory = testDb.getDalFactory();
        projectService = new ProjectService();
    }

    @AfterAll
    static void afterAll() {
        if (testDb != null) {
            testDb.close();
        }
    }

    @BeforeEach
    void beforeEach() {
        now = ZonedDateTime.now();
        dal = dalFactory.constructDal();
        repo = new VngRepository(dal.getSession());

        try (AutoCloseTransaction transaction = repo.beginTransaction()) {
            user = repo.persist(new User());
            userUuid = user.getId();

            project = createProject(repo, user);
            projectUuid = project.getId();

            startMilestone = createMilestone(repo, project, LocalDate.now().minusDays(10), user);
            endMilestone = createMilestone(repo, project, LocalDate.now().plusDays(10), user);
            createProjectDurationChangelog(repo, project, startMilestone, endMilestone, user);

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

    @Test
    void createProject() throws Exception {
        ZonedDateTime now = ZonedDateTime.now();
        LocalDate today = LocalDate.now();

        ProjectMinimalSnapshotModel projectData = new ProjectMinimalSnapshotModel();
        projectData.setProjectName("name");
        projectData.setStartDate(today.plusDays(10));
        projectData.setEndDate(today.plusDays(20));
        projectData.setProjectColor("abcdef");
        projectData.setConfidentialityLevel(Confidentiality.OPENBAAR);
        projectData.setProjectPhase(ProjectPhase._3_VERGUNNINGSFASE);

        Project project;
        try (AutoCloseTransaction transaction = repo.beginTransaction()) {
            project = projectService.createProject(repo, userUuid, projectData, now);

            transaction.commit();
            repo.getSession().clear();
        }

        try (var transaction = repo.beginTransaction()) {
            var actual = projectService.getProjectSnapshot(repo, project.getId());

            assertThat(actual.getProjectId()).isNotNull();
            assertThat(actual.getProjectStateId()).isNotNull();

            // Creates a partial copy of the project snapshot using only the fields in the minimal snapshot
            ProjectMinimalSnapshotModel minimalSnapshotActual = partialCopy(actual, ProjectMinimalSnapshotModel.class);

            assertThat(minimalSnapshotActual)
                    .usingRecursiveComparison(RecursiveComparisonConfiguration.builder()
                            .withIgnoredFields("projectId", "projectStateId") // These were not set in the original object, so they won't match
                            .build())
                    .isEqualTo(projectData);
        }
    }

    @Test
    void deleteProject() throws Exception {
        try (AutoCloseTransaction transaction = repo.beginTransaction()) {
            projectService.deleteProject(repo, projectUuid, userUuid);
            transaction.commit();
            repo.getSession().clear();
        }

        List<Tuple> changelogs = repo
            .getSession()
            .createNativeQuery("SELECT change_end_date, change_user_id FROM diwi_testset.project_duration_changelog WHERE project_id = :projectUuid",
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

    @Test
    void getPlots() throws Exception {
        ObjectNode geoJson = JsonNodeFactory.instance.objectNode().put("geojson", "ish");
        String gemeenteCode = "gemeente";
        long brkPerceelNummer = 1234l;
        String brkSectie = "sectie";
        ObjectNode subselectionGeometry = JsonNodeFactory.instance.objectNode().put("subselectionGeometry", "geometry");

        try (var transaction = repo.beginTransaction()) {
            createProjectDurationChangelog(repo, project, startMilestone, endMilestone, user);

            var oldChangelog = createPlot(geoJson, gemeenteCode + "old", brkPerceelNummer, brkSectie, subselectionGeometry);
            oldChangelog.setChangeEndDate(now);
            oldChangelog.setChangeUser(user);
            repo.persist(oldChangelog);

            createPlot(geoJson, gemeenteCode, brkPerceelNummer, brkSectie, subselectionGeometry);
            transaction.commit();
            repo.getSession().clear();
        }

        try (AutoCloseTransaction transaction = repo.beginTransaction()) {
            project = projectService.getCurrentProject(repo, projectUuid);
            List<PlotModel> plots = projectService.getCurrentPlots(project);
            var expected = List.of(
                    PlotModel.builder()
                            .brkGemeenteCode(gemeenteCode)
                            .brkPerceelNummer(brkPerceelNummer)
                            .brkSectie(brkSectie)
                            .subselectionGeometry(subselectionGeometry)
                            .plotFeature(geoJson)
                            .build());

            assertThat(plots).usingRecursiveAssertion().isEqualTo(expected);
        }
    }

    @Test
    void setPlots() throws Exception {

        ObjectNode geoJson = JsonNodeFactory.instance.objectNode().put("geojson", "ish");
        String gemeenteCode = "gemeente";
        long brkPerceelNummer = 1234l;
        String brkSectie = "sectie";
        ObjectNode subselectionGeometry = JsonNodeFactory.instance.objectNode().put("subselectionGeometry", "geometry");

        // Create an old and a current changelog already so we can test these are replaced.
        try (var transaction = repo.beginTransaction()) {
            createProjectDurationChangelog(repo, project, startMilestone, endMilestone, user);
            var oldChangelog = createPlot(geoJson, gemeenteCode + "old", brkPerceelNummer, brkSectie, subselectionGeometry);
            oldChangelog.setChangeEndDate(now);
            oldChangelog.setChangeUser(user);
            repo.persist(oldChangelog);

            createPlot(geoJson, gemeenteCode, brkPerceelNummer, brkSectie, subselectionGeometry);

            transaction.commit();
            repo.getSession().clear();
        }

        // Call the endpoints with the new plot
        try (AutoCloseTransaction transaction = repo.beginTransaction()) {
            project = projectService.getCurrentProject(repo, projectUuid);
            var plots = List.of(
                    PlotModel.builder()
                            .brkGemeenteCode(gemeenteCode + "new")
                            .brkPerceelNummer(brkPerceelNummer)
                            .brkSectie(brkSectie)
                            .subselectionGeometry(subselectionGeometry)
                            .plotFeature(geoJson)
                            .build());
            projectService.setPlots(repo, project, plots, user.getId());

            transaction.commit();
            repo.getSession().clear();
        }

        try (var transaction = repo.beginTransaction()) {
            var actualCls = repo.getSession()
                    .createQuery("FROM ProjectRegistryLinkChangelog cl WHERE cl.changeEndDate is null", ProjectRegistryLinkChangelog.class)
                    .list();

            assertThat(actualCls).hasSize(2);

            List<ProjectRegistryLinkChangelogValue> expected = List.of(ProjectRegistryLinkChangelogValue.builder()
                    .brkGemeenteCode(gemeenteCode + "new")
                    .brkPerceelNummer(brkPerceelNummer)
                    .brkSectie(brkSectie)
                    .subselectionGeometry(subselectionGeometry)
                    .plotFeature(geoJson)
                    .projectRegistryLinkChangelog(actualCls.get(0))
                    .build());

            var newChangelog = actualCls.stream().filter(c -> c.getStartMilestone().getState().get(0).getDate().equals(LocalDate.now())).findAny().get();
            assertThat(newChangelog.getValues())
                    .usingRecursiveComparison(ignoreId)
                    .isEqualTo(expected);
        }

    }


    @Test
    void prepareChangelogValuesToUpdate_FutureProject() {

        try (AutoCloseTransaction transaction = repo.beginTransaction()) {
            project = createProject(repo, user);
            projectUuid = project.getId();
            startMilestone = createMilestone(repo, project, LocalDate.now().plusDays(10), user);
            middleMilestone = createMilestone(repo, project, LocalDate.now().plusDays(25), user);
            endMilestone = createMilestone(repo, project, LocalDate.now().plusDays(50), user);
            createProjectDurationChangelog(repo, project, startMilestone, endMilestone, user);
            createProjectNameChangelog(repo, project, "Name 1", startMilestone, middleMilestone, user);
            createProjectNameChangelog(repo, project, "Name 2", middleMilestone, endMilestone, user);
            transaction.commit();
            repo.getSession().clear();
        }

        try (AutoCloseTransaction transaction = repo.beginTransaction()) {

            project = projectService.getCurrentProject(repo, projectUuid);
            startMilestone = project.getDuration().get(0).getStartMilestone();
            endMilestone = project.getDuration().get(0).getEndMilestone();
            ProjectNameChangelog newChangelog = new ProjectNameChangelog();
            ProjectNameChangelog oldChangelogAfterUpdate = new ProjectNameChangelog();

            ProjectNameChangelog oldChangelog = projectService.prepareChangelogValuesToUpdate(repo, project, project.getName(), newChangelog, oldChangelogAfterUpdate,
                user.getId(), startMilestone, endMilestone, LocalDate.now());

            //the existing changelog is not split into past and future segments when updating a future project
            assertThat(oldChangelogAfterUpdate.getStartMilestone()).isNull();

            //the existing changelog has the same start milestone, but non-null change-end-date and change-user
            assertThat(oldChangelog).isNotNull();
            assertThat(oldChangelog.getStartMilestone().getId()).isEqualTo(startMilestone.getId());
            assertThat(oldChangelog.getEndMilestone().getId()).isEqualTo(middleMilestone.getId());
            assertThat(oldChangelog.getChangeEndDate()).isNotNull();
            assertThat(oldChangelog.getChangeUser()).isNotNull();

            //the new changelog is configured with the correct values
            assertThat(newChangelog.getStartMilestone().getId()).isEqualTo(startMilestone.getId());
            assertThat(newChangelog.getEndMilestone().getId()).isEqualTo(middleMilestone.getId());
            assertThat(newChangelog.getCreateUser()).isNotNull();
            assertThat(newChangelog.getChangeStartDate()).isNotNull();
        }
    }

    @Test
    void prepareChangelogValuesToUpdate_PastProject() {

        try (AutoCloseTransaction transaction = repo.beginTransaction()) {
            project = createProject(repo, user);
            projectUuid = project.getId();
            startMilestone = createMilestone(repo, project, LocalDate.now().minusDays(50), user);
            middleMilestone = createMilestone(repo, project, LocalDate.now().minusDays(25), user);
            endMilestone = createMilestone(repo, project, LocalDate.now().minusDays(10), user);
            createProjectDurationChangelog(repo, project, startMilestone, endMilestone, user);
            createProjectNameChangelog(repo, project, "Name 1", startMilestone, middleMilestone, user);
            createProjectNameChangelog(repo, project, "Name 2", middleMilestone, endMilestone, user);
            transaction.commit();
            repo.getSession().clear();
        }

        try (AutoCloseTransaction transaction = repo.beginTransaction()) {

            project = projectService.getCurrentProject(repo, projectUuid);
            startMilestone = project.getDuration().get(0).getStartMilestone();
            endMilestone = project.getDuration().get(0).getEndMilestone();
            ProjectNameChangelog newChangelog = new ProjectNameChangelog();
            ProjectNameChangelog oldChangelogAfterUpdate = new ProjectNameChangelog();

            ProjectNameChangelog oldChangelog = projectService.prepareChangelogValuesToUpdate(repo, project, project.getName(), newChangelog, oldChangelogAfterUpdate,
                user.getId(), startMilestone, endMilestone, LocalDate.now());

            //the existing changelog is not split into past and future segments when updating a past project
            assertThat(oldChangelogAfterUpdate.getStartMilestone()).isNull();

            //the existing changelog has the same end milestone, but non-null change-end-date and change-user
            assertThat(oldChangelog).isNotNull();
            assertThat(oldChangelog.getStartMilestone().getId()).isEqualTo(middleMilestone.getId());
            assertThat(oldChangelog.getEndMilestone().getId()).isEqualTo(endMilestone.getId());
            assertThat(oldChangelog.getChangeEndDate()).isNotNull();
            assertThat(oldChangelog.getChangeUser()).isNotNull();

            //the new changelog is configured with the correct values
            assertThat(newChangelog.getStartMilestone().getId()).isEqualTo(middleMilestone.getId());
            assertThat(newChangelog.getEndMilestone().getId()).isEqualTo(endMilestone.getId());
            assertThat(newChangelog.getCreateUser()).isNotNull();
            assertThat(newChangelog.getChangeStartDate()).isNotNull();
        }
    }

    @Test
    void prepareChangelogValuesToUpdate_CurrentProject() {

        try (AutoCloseTransaction transaction = repo.beginTransaction()) {
            project = createProject(repo, user);
            projectUuid = project.getId();
            startMilestone = createMilestone(repo, project, LocalDate.now().minusDays(10), user);
            middleMilestone = createMilestone(repo, project, LocalDate.now(), user);
            endMilestone = createMilestone(repo, project, LocalDate.now().plusDays(10), user);
            createProjectDurationChangelog(repo, project, startMilestone, endMilestone, user);
            createProjectNameChangelog(repo, project, "Name", startMilestone, endMilestone, user);
            transaction.commit();
            repo.getSession().clear();
        }

        try (AutoCloseTransaction transaction = repo.beginTransaction()) {

            project = projectService.getCurrentProject(repo, projectUuid);
            startMilestone = project.getDuration().get(0).getStartMilestone();
            endMilestone = project.getDuration().get(0).getEndMilestone();
            ProjectNameChangelog newChangelog = new ProjectNameChangelog();
            ProjectNameChangelog oldChangelogAfterUpdate = new ProjectNameChangelog();

            ProjectNameChangelog oldChangelog = projectService.prepareChangelogValuesToUpdate(repo, project, project.getName(), newChangelog, oldChangelogAfterUpdate,
                user.getId(), startMilestone, endMilestone, LocalDate.now());

            //the existing changelog is split into past and future segments when updating a current project
            assertThat(oldChangelogAfterUpdate.getStartMilestone()).isNotNull();
            assertThat(oldChangelogAfterUpdate.getStartMilestone().getId()).isEqualTo(startMilestone.getId());
            assertThat(oldChangelogAfterUpdate.getEndMilestone().getId()).isEqualTo(middleMilestone.getId());
            assertThat(oldChangelogAfterUpdate.getCreateUser()).isNotNull();
            assertThat(oldChangelogAfterUpdate.getChangeStartDate()).isNotNull();

            //the existing changelog has the same end milestone, but non-null change-end-date and change-user
            assertThat(oldChangelog).isNotNull();
            assertThat(oldChangelog.getStartMilestone().getId()).isEqualTo(startMilestone.getId());
            assertThat(oldChangelog.getEndMilestone().getId()).isEqualTo(endMilestone.getId());
            assertThat(oldChangelog.getChangeEndDate()).isNotNull();
            assertThat(oldChangelog.getChangeUser()).isNotNull();

            //the new changelog is configured with the correct values
            assertThat(newChangelog.getStartMilestone().getId()).isEqualTo(middleMilestone.getId());
            assertThat(newChangelog.getEndMilestone().getId()).isEqualTo(endMilestone.getId());
            assertThat(newChangelog.getCreateUser()).isNotNull();
            assertThat(newChangelog.getChangeStartDate()).isNotNull();
        }
    }

    private ProjectRegistryLinkChangelog createPlot(ObjectNode geoJson, String gemeenteCode, long brkPerceelNummer, String brkSectie, ObjectNode subselectionGeometry) {
        ProjectRegistryLinkChangelog changelog = ProjectRegistryLinkChangelog.builder()
                .project(project)
                .build();

        setCreateUserAndDate(changelog);
        changelog.setStartMilestone(startMilestone);
        changelog.setEndMilestone(endMilestone);
        repo.persist(changelog);

        var val = ProjectRegistryLinkChangelogValue.builder()
                .brkGemeenteCode(gemeenteCode)
                .brkPerceelNummer(brkPerceelNummer)
                .brkSectie(brkSectie)
                .subselectionGeometry(subselectionGeometry)
                .plotFeature(geoJson)
                .projectRegistryLinkChangelog(changelog)
                .build();
        repo.persist(val);
        return changelog;
    }

    /**
     * Use json serialization to only copy part of the properties of a class into e.g. a base class.
     *
     * @param <T>
     * @param actual
     * @param targetClass
     * @return
     * @throws JsonProcessingException
     * @throws JsonMappingException
     */
    private static <T> T partialCopy(ProjectSnapshotModel actual, Class<T> targetClass) throws JsonProcessingException, JsonMappingException {
        ObjectMapper objectMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return objectMapper.readValue(objectMapper.writeValueAsString(actual), targetClass);
    }

    private void setCreateUserAndDate(ProjectRegistryLinkChangelog entity) {
        entity.setChangeStartDate(now);
        entity.setCreateUser(user);
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
