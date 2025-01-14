package nl.vng.diwi.resources;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

import org.assertj.core.api.recursive.comparison.RecursiveComparisonConfiguration;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.persistence.Tuple;
import jakarta.ws.rs.container.ContainerRequestContext;
import nl.vng.diwi.dal.AutoCloseTransaction;
import nl.vng.diwi.dal.Dal;
import nl.vng.diwi.dal.DalFactory;
import nl.vng.diwi.dal.GenericRepository;
import nl.vng.diwi.dal.VngRepository;
import nl.vng.diwi.dal.entities.Milestone;
import nl.vng.diwi.dal.entities.Project;
import nl.vng.diwi.dal.entities.ProjectNameChangelog;
import nl.vng.diwi.dal.entities.User;
import nl.vng.diwi.dal.entities.UserGroup;
import nl.vng.diwi.dal.entities.UserGroupToProject;
import nl.vng.diwi.dal.entities.UserState;
import nl.vng.diwi.dal.entities.enums.Confidentiality;
import nl.vng.diwi.dal.entities.enums.MutationType;
import nl.vng.diwi.dal.entities.enums.OwnershipType;
import nl.vng.diwi.dal.entities.enums.PlanStatus;
import nl.vng.diwi.dal.entities.enums.PlanType;
import nl.vng.diwi.dal.entities.enums.ProjectPhase;
import nl.vng.diwi.generic.Constants;
import nl.vng.diwi.models.AmountModel;
import nl.vng.diwi.models.DatedDataModel;
import nl.vng.diwi.models.HouseblockSnapshotModel;
import nl.vng.diwi.models.HouseblockSnapshotModel.HouseType;
import nl.vng.diwi.models.HouseblockSnapshotModel.Mutation;
import nl.vng.diwi.models.HouseblockSnapshotModel.OwnershipValue;
import nl.vng.diwi.models.LocationModel;
import nl.vng.diwi.models.MilestoneModel;
import nl.vng.diwi.models.ProjectSnapshotModel;
import nl.vng.diwi.models.ProjectTimelineModel;
import nl.vng.diwi.models.SingleValueOrRangeModel;
import nl.vng.diwi.rest.VngBadRequestException;
import nl.vng.diwi.rest.VngNotAllowedException;
import nl.vng.diwi.rest.VngNotFoundException;
import nl.vng.diwi.rest.VngServerErrorException;
import nl.vng.diwi.security.LoggedUser;
import nl.vng.diwi.security.UserRole;
import nl.vng.diwi.services.ExcelImportService;
import nl.vng.diwi.services.GeoJsonImportService;
import nl.vng.diwi.services.HouseblockService;
import nl.vng.diwi.services.ProjectService;
import nl.vng.diwi.services.ProjectServiceTest;
import nl.vng.diwi.services.PropertiesService;
import nl.vng.diwi.services.UserGroupService;
import nl.vng.diwi.testutil.ProjectsUtil;
import nl.vng.diwi.testutil.TestDb;

public class ProjectsResourceTest {
    public static ObjectMapper objectMapper = new ObjectMapper();
    private static DalFactory dalFactory;
    private static TestDb testDb;

    @BeforeAll
    static void beforeAll() throws Exception {
        testDb = new TestDb();
        testDb.reset();
        dalFactory = testDb.getDalFactory();
    }

    @AfterAll
    static void afterAll() {
        testDb.close();
    }

    private Dal dal;
    private ProjectsResource projectResource;
    private VngRepository repo;
    private User user;
    private UserGroup userGroup;
    private LoggedUser loggedUser;
    public ZonedDateTime now = ZonedDateTime.now();
    private LocalDate today = now.toLocalDate();
    private HouseblockResource blockResource;
    private UserState userState;
    private ContainerRequestContext context;
    private PropertiesService propertiesService;

    @BeforeEach
    void beforeEach() {
        dal = dalFactory.constructDal();
        repo = new VngRepository(dal.getSession());
        projectResource = new ProjectsResource(new GenericRepository(dal),
                new ProjectService(), new HouseblockService(), new UserGroupService(null), new PropertiesService(), testDb.projectConfig,
                new ExcelImportService(), new GeoJsonImportService());
        projectResource.getUserGroupService().setUserGroupDAO(repo.getUsergroupDAO());
        blockResource = new HouseblockResource(new GenericRepository(dal), new HouseblockService(), new ProjectService(), new PropertiesService());
        propertiesService = new PropertiesService();

        try (AutoCloseTransaction transaction = repo.beginTransaction()) {
            user = new User();
            userGroup = new UserGroup();

            userState = ProjectsUtil.persistUserAndUserGroup(repo, user, userGroup, now);

            transaction.commit();
            repo.getSession().clear();
        }

        loggedUser = new LoggedUser();
        loggedUser.setUuid(user.getId());
        loggedUser.setRole(UserRole.UserPlus);
        context = new LoggedUserContainerRequestContext(loggedUser);
    }

    @AfterEach
    void afterEach() {
        dal.close();
    }

    @Test
    void updateProjectSnapshotTest_currentProject() throws VngNotFoundException, VngServerErrorException, VngBadRequestException, VngNotAllowedException {
        UUID projectUuid;

        // prepare project with name and duration changelog
        try (AutoCloseTransaction transaction = repo.beginTransaction()) {
            Project project = ProjectServiceTest.createProject(repo, user);
            projectUuid = project.getId();
            Milestone startMilestone = ProjectServiceTest.createMilestone(repo, project, today.minusDays(10), user);
            Milestone middleMilestone = ProjectServiceTest.createMilestone(repo, project, today.plusDays(5), user);
            Milestone endMilestone = ProjectServiceTest.createMilestone(repo, project, today.plusDays(10), user);
            ProjectServiceTest.createProjectDurationChangelog(repo, project, startMilestone, endMilestone, user);
            ProjectServiceTest.createProjectNameChangelog(repo, project, "Name 1", startMilestone, middleMilestone, user);
            ProjectServiceTest.createProjectNameChangelog(repo, project, "Name 2", middleMilestone, endMilestone, user);
            UserGroupToProject ugtp = new UserGroupToProject();
            ugtp.setUserGroup(userGroup);
            ugtp.setProject(project);
            ugtp.setCreateUser(user);
            ugtp.setChangeStartDate(now);
            repo.persist(ugtp);

            transaction.commit();
            repo.getSession().clear();
        }

        ContainerRequestContext requestContext = Mockito.mock(ContainerRequestContext.class);
        Mockito.when(requestContext.getProperty("loggedUser")).thenReturn(loggedUser);

        // prepare update model with modified name and start date
        ProjectSnapshotModel projectSnapshot = projectResource.getCurrentProjectSnapshot(requestContext, projectUuid);
        projectSnapshot.setProjectName("Name 1 updated");
        projectSnapshot.setStartDate(today.minusDays(15));

        // call update endpoint
        projectResource.updateProjectSnapshot(loggedUser, projectSnapshot);
        repo.getSession().clear();

        // assert
        repo.getSession().disableFilter(GenericRepository.CURRENT_DATA_FILTER);
        Project updatedProject = repo.findById(Project.class, projectUuid);
        List<ProjectNameChangelog> nameChangelogs = updatedProject.getName();

        assertThat(nameChangelogs.size()).isEqualTo(4);

        ProjectNameChangelog oldChangelog = nameChangelogs.stream().filter(c -> c.getName().equals("Name 1") && c.getChangeEndDate() != null).findFirst()
                .orElse(null);
        assertThat(oldChangelog).isNotNull();
        assertThat(new MilestoneModel(oldChangelog.getStartMilestone()).getDate()).isEqualTo(today.minusDays(15));
        assertThat(new MilestoneModel(oldChangelog.getEndMilestone()).getDate()).isEqualTo(today.plusDays(5));

        ProjectNameChangelog oldChangelogV2 = nameChangelogs.stream().filter(c -> c.getName().equals("Name 1") && c.getChangeEndDate() == null).findFirst()
                .orElse(null);
        assertThat(oldChangelog).isNotNull();
        assertThat(new MilestoneModel(oldChangelogV2.getStartMilestone()).getDate()).isEqualTo(today.minusDays(15));
        assertThat(new MilestoneModel(oldChangelogV2.getEndMilestone()).getDate()).isEqualTo(today);

        ProjectNameChangelog newChangelog = nameChangelogs.stream().filter(c -> c.getName().equals("Name 1 updated") && c.getChangeEndDate() == null)
                .findFirst().orElse(null);
        assertThat(newChangelog).isNotNull();
        assertThat(new MilestoneModel(newChangelog.getStartMilestone()).getDate()).isEqualTo(today);
        assertThat(new MilestoneModel(newChangelog.getEndMilestone()).getDate()).isEqualTo(today.plusDays(5));

        ProjectNameChangelog futureNameChangelog = nameChangelogs.stream().filter(c -> c.getName().equals("Name 2") && c.getChangeEndDate() == null).findFirst()
                .orElse(null);
        assertThat(futureNameChangelog).isNotNull();
        assertThat(new MilestoneModel(futureNameChangelog.getStartMilestone()).getDate()).isEqualTo(today.plusDays(5));
        assertThat(new MilestoneModel(futureNameChangelog.getEndMilestone()).getDate()).isEqualTo(today.plusDays(10));
    }

    @Test
    void updateProjectSnapshotTest_futureProject() throws VngNotFoundException, VngServerErrorException, VngBadRequestException, VngNotAllowedException {
        UUID projectUuid;

        // prepare project with name and duration changelog
        try (AutoCloseTransaction transaction = repo.beginTransaction()) {
            Project project = ProjectServiceTest.createProject(repo, user);
            projectUuid = project.getId();
            Milestone startMilestone = ProjectServiceTest.createMilestone(repo, project, today.plusDays(5), user);
            Milestone middleMilestone = ProjectServiceTest.createMilestone(repo, project, today.plusDays(10), user);
            Milestone endMilestone = ProjectServiceTest.createMilestone(repo, project, today.plusDays(15), user);
            ProjectServiceTest.createProjectDurationChangelog(repo, project, startMilestone, endMilestone, user);
            ProjectServiceTest.createProjectNameChangelog(repo, project, "Name 1", startMilestone, middleMilestone, user);
            ProjectServiceTest.createProjectNameChangelog(repo, project, "Name 2", middleMilestone, endMilestone, user);

            UserGroupToProject ugtp = new UserGroupToProject();
            ugtp.setUserGroup(userGroup);
            ugtp.setProject(project);
            ugtp.setCreateUser(user);
            ugtp.setChangeStartDate(now);
            repo.persist(ugtp);

            transaction.commit();
            repo.getSession().clear();
        }

        ContainerRequestContext requestContext = Mockito.mock(ContainerRequestContext.class);
        Mockito.when(requestContext.getProperty("loggedUser")).thenReturn(loggedUser);

        // prepare update model with modified name and start date
        ProjectSnapshotModel projectSnapshot = projectResource.getCurrentProjectSnapshot(requestContext, projectUuid);
        projectSnapshot.setProjectName("Name 1 updated");
        projectSnapshot.setStartDate(today.minusDays(1));

        // call update endpoint
        projectResource.updateProjectSnapshot(loggedUser, projectSnapshot);
        repo.getSession().clear();

        // assert
        repo.getSession().disableFilter(GenericRepository.CURRENT_DATA_FILTER);
        Project updatedProject = repo.findById(Project.class, projectUuid);
        List<ProjectNameChangelog> nameChangelogs = updatedProject.getName();

        assertThat(nameChangelogs.size()).isEqualTo(3);

        ProjectNameChangelog oldChangelog = nameChangelogs.stream().filter(c -> c.getName().equals("Name 1") && c.getChangeEndDate() != null).findFirst()
                .orElse(null);
        assertThat(oldChangelog).isNotNull();
        assertThat(new MilestoneModel(oldChangelog.getStartMilestone()).getDate()).isEqualTo(today.minusDays(1));
        assertThat(new MilestoneModel(oldChangelog.getEndMilestone()).getDate()).isEqualTo(today.plusDays(10));

        ProjectNameChangelog newChangelog = nameChangelogs.stream().filter(c -> c.getName().equals("Name 1 updated") && c.getChangeEndDate() == null)
                .findFirst().orElse(null);
        assertThat(newChangelog).isNotNull();
        assertThat(new MilestoneModel(newChangelog.getStartMilestone()).getDate()).isEqualTo(today.minusDays(1));
        assertThat(new MilestoneModel(newChangelog.getEndMilestone()).getDate()).isEqualTo(today.plusDays(10));

        ProjectNameChangelog futureNameChangelog = nameChangelogs.stream().filter(c -> c.getName().equals("Name 2") && c.getChangeEndDate() == null).findFirst()
                .orElse(null);
        assertThat(futureNameChangelog).isNotNull();
        assertThat(new MilestoneModel(futureNameChangelog.getStartMilestone()).getDate()).isEqualTo(today.plusDays(10));
        assertThat(new MilestoneModel(futureNameChangelog.getEndMilestone()).getDate()).isEqualTo(today.plusDays(15));
    }

    /**
     * See trello https://trello.com/c/LXTzHbSF/487-changing-the-end-date-for-a-project-to-before-the-end-date-of-a-block-gives-a-confusing-error-message
     */
    @Test
    public void changeProjectEndDate() throws Exception {
        LocalDate startDate = today.minusDays(10);
        LocalDate endDate = today.plusDays(10);

        // Create an end date earlier than today so it will be before the milestones we end up creating
        LocalDate newEndDate = today.minusDays(5);

        // Get fixed properties
        var physicalAppearance = propertiesService.getCategoryStatesByPropertyName(repo, Constants.FIXED_PROPERTY_PHYSICAL_APPEARANCE);
        var targetGroup = propertiesService.getCategoryStatesByPropertyName(repo, Constants.FIXED_PROPERTY_TARGET_GROUP);

        // Create the initial project and block
        var fixture = ProjectsUtil.createTestProject(
                userGroup,
                userState,
                startDate,
                endDate,
                dal,
                testDb.projectConfig,
                repo,
                loggedUser);
        var originalProjectModel = fixture.getProject();
        var projectId = originalProjectModel.getProjectId();
        var originalBlockModel = fixture.getBlocks().get(0);
        var houseblockId = originalBlockModel.getHouseblockId();
        var owners = fixture.getOwners();

        // Create model and change everything except the start date, end date and the id to create a lot of milestones on the current day
        var updateProjectModel = nl.vng.diwi.generic.Json.jsonCopy(originalProjectModel, ProjectSnapshotModel.class);
        updateProjectModel.setProjectId(projectId);
        updateProjectModel.setProjectOwners(originalProjectModel.getProjectOwners());

        // Change the following values from their original values to create new milestones on the current date
        updateProjectModel.setProjectName("new name");
        updateProjectModel.setProjectColor("#123456");
        updateProjectModel.setConfidentialityLevel(Confidentiality.PUBLIC);
        updateProjectModel.setProjectPhase(ProjectPhase._6_REALIZATION);
        updateProjectModel.setPlanningPlanStatus(List.of(PlanStatus._4B_NIET_OPGENOMEN_IN_VISIE));
        updateProjectModel.setPlanType(List.of(PlanType.HERSTRUCTURERING));
        projectResource.updateProjectSnapshot(loggedUser, updateProjectModel);
        repo.getSession().clear();

        HouseblockSnapshotModel.GroundPosition groundPosition = HouseblockSnapshotModel.GroundPosition.builder()
                .formalPermissionOwner(1)
                .intentionPermissionOwner(2)
                .noPermissionOwner(3)
                .build();
        var updatedBlockModel = new HouseblockSnapshotModel();
        updatedBlockModel.setProjectId(projectId);
        updatedBlockModel.setHouseblockId(houseblockId);
        updatedBlockModel.setStartDate(startDate);
        updatedBlockModel.setEndDate(endDate);
        updatedBlockModel.setHouseblockName("changeEndDate block new name");
        updatedBlockModel.setGroundPosition(groundPosition);
        updatedBlockModel.setTargetGroup(List.of(AmountModel.builder().amount(17).id(targetGroup.get(0).getCategoryValue().getId()).build()));
        updatedBlockModel.setPhysicalAppearance(List.of(AmountModel.builder().amount(34).id(physicalAppearance.get(0).getCategoryValue().getId()).build()));
        updatedBlockModel.setHouseType(HouseType.builder().eengezinswoning(1).meergezinswoning(2).build());
        updatedBlockModel.setMutation(Mutation.builder().amount(33).build());
        updatedBlockModel.setSize(new SingleValueOrRangeModel<BigDecimal>(BigDecimal.valueOf(1), null, null));
        updatedBlockModel.setOwnershipValue(List.of(OwnershipValue.builder()
                .amount(1)
                .type(OwnershipType.KOOPWONING)
                .value(new SingleValueOrRangeModel<Long>(1l, null))
                .build()));
        updatedBlockModel.setMutation(Mutation.builder().amount(1).kind(MutationType.CONSTRUCTION).build());
        updatedBlockModel.setProgramming(true);
        blockResource.updateHouseblock(loggedUser, updatedBlockModel);
        repo.getSession().clear();

        //
        // Set the end date to a date before today so it will conflict with the milestones created by the changes above
        updateProjectModel.setEndDate(newEndDate);
        projectResource.updateProjectSnapshot(loggedUser, updateProjectModel);
        repo.getSession().clear();

        updateProjectModel.setTotalValue(1l);
        assertThat(projectResource.getCurrentProjectSnapshot(context, projectId))
                .usingRecursiveComparison(RecursiveComparisonConfiguration.builder()
                        .withIgnoredFields( // Ignore some things that are irrelevant/hard to check
                                "projectStateId",
                                "projectOwners.users.userGroupUuid",
                                "projectOwners.users.uuid",
                                "planType")
                        .build())
                .isEqualTo(updateProjectModel);
        repo.getSession().clear();

        //
        // Check if the house block end date has changed as well
        HouseblockSnapshotModel expectedHouseblockModel = nl.vng.diwi.generic.Json.jsonCopy(originalBlockModel, HouseblockSnapshotModel.class);
        expectedHouseblockModel.setEndDate(newEndDate);
        assertThat(blockResource.getCurrentHouseblockSnapshot(houseblockId))
                .isEqualTo(expectedHouseblockModel);

        //
        // Check if the expected entries are in the project timeline model
        var expectedTimeline = new ProjectTimelineModel();
        expectedTimeline.setConfidentialityLevel(Confidentiality.PUBLIC);
        expectedTimeline.setEndDate(newEndDate);
        expectedTimeline.setLocation(new LocationModel());
        expectedTimeline
                .setPlanningPlanStatus(List.of(new DatedDataModel<List<PlanStatus>>(List.of(PlanStatus._4B_NIET_OPGENOMEN_IN_VISIE), startDate, newEndDate)));
        expectedTimeline.setProjectColor("#123456");
        expectedTimeline.setProjectId(projectId);
        expectedTimeline.setProjectName(List.of(new DatedDataModel<String>("new name", startDate, newEndDate)));
        expectedTimeline.setProjectOwners(owners);
        expectedTimeline.setProjectPhase(List.of(new DatedDataModel<ProjectPhase>(ProjectPhase._6_REALIZATION, startDate, newEndDate)));
        expectedTimeline.setStartDate(startDate);

        var actualTimeline = projectResource.getCurrentProjectTimeline(projectId);
        assertThat(actualTimeline)
                .usingRecursiveComparison(RecursiveComparisonConfiguration.builder()
                        .withIgnoredFields(// Ignore some things that are irrelevant/hard to check
                                "projectOwners.name",
                                "projectOwners.users",
                                "planType")
                        .build())
                .isEqualTo(expectedTimeline);

        var expectedMilestones = repo.getSession()
                .createNativeQuery("""
                        SELECT start_milestone_id, end_milestone_id
                        FROM diwi.project_duration_changelog
                        WHERE change_end_date IS NULL
                          AND project_id = :projectId
                        """, Tuple.class)
                .setParameter("projectId", projectId)
                .getSingleResult();

        // Check if there is only one entry for all changelogs and it has the correct start and end milestones
        for (var tableName : List.of(
                // "project_category_changelog", // This one is not exposed in the resource
                "project_duration_changelog",
                "project_fase_changelog",
                // "project_maatwerk_boolean_changelog", // custom properties are excluded
                // "project_maatwerk_numeriek_changelog",
                // "project_ordinal_changelog",
                // "project_text_changelog"
                "project_name_changelog",
                "project_plan_type_changelog",
                // "project_registry_link_changelog", // Only used when importing
                "project_planologische_planstatus_changelog")) {
            var tuple = repo.getSession()
                    .createNativeQuery("""
                            SELECT start_milestone_id, end_milestone_id
                            FROM diwi.%s
                            WHERE change_end_date IS NULL
                              AND project_id = :projectId
                            """.formatted(tableName), Tuple.class)
                    .setParameter("projectId", projectId)
                    .getSingleResult();
            assertThat(tuple.toArray()).containsExactly(expectedMilestones.toArray());
        }

        for (var tableName : List.of(
                "woningblok_deliverydate_changelog",
                "woningblok_doelgroep_changelog",
                "woningblok_duration_changelog",
                "woningblok_eigendom_en_waarde_changelog",
                "woningblok_grondpositie_changelog",
                "woningblok_grootte_changelog",
                // "woningblok_maatwerk_boolean_changelog", // custom properties are excluded
                // "woningblok_maatwerk_categorie_changelog",
                // "woningblok_maatwerk_numeriek_changelog",
                // "woningblok_maatwerk_ordinaal_changelog",
                // "woningblok_maatwerk_text_changelog",
                "woningblok_mutatie_changelog",
                "woningblok_naam_changelog",
                "woningblok_programmering_changelog",
                "woningblok_type_en_fysiek_changelog")) {

            var tuple = repo.getSession()
                    .createNativeQuery("""
                            SELECT start_milestone_id, end_milestone_id
                            FROM diwi.%s
                            WHERE change_end_date IS NULL
                              AND woningblok_id = :woningblokId
                            """.formatted(tableName), Tuple.class)
                    .setParameter("woningblokId", houseblockId)
                    .getSingleResult();
            assertThat(tuple.toArray()).containsExactly(expectedMilestones.toArray());
        }
    }
}
