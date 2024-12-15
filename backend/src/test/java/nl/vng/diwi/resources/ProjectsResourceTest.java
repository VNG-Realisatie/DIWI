package nl.vng.diwi.resources;

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
import nl.vng.diwi.dal.entities.UserGroupState;
import nl.vng.diwi.dal.entities.UserGroupToProject;
import nl.vng.diwi.dal.entities.UserState;
import nl.vng.diwi.dal.entities.UserToUserGroup;
import nl.vng.diwi.dal.entities.enums.Confidentiality;
import nl.vng.diwi.dal.entities.enums.ProjectPhase;
import nl.vng.diwi.models.HouseblockSnapshotModel;
import nl.vng.diwi.models.MilestoneModel;
import nl.vng.diwi.models.ProjectSnapshotModel;
import nl.vng.diwi.models.UserGroupModel;
import nl.vng.diwi.models.UserGroupUserModel;
import nl.vng.diwi.models.UserModel;
import nl.vng.diwi.models.superclasses.ProjectCreateSnapshotModel;
import nl.vng.diwi.models.superclasses.ProjectMinimalSnapshotModel;
import nl.vng.diwi.rest.VngBadRequestException;
import nl.vng.diwi.rest.VngNotAllowedException;
import nl.vng.diwi.rest.VngNotFoundException;
import nl.vng.diwi.rest.VngServerErrorException;
import nl.vng.diwi.security.LoggedUser;
import nl.vng.diwi.security.UserRole;
import nl.vng.diwi.services.ExcelImportService;
import nl.vng.diwi.services.GeoJsonImportService;
import nl.vng.diwi.services.PropertiesService;
import nl.vng.diwi.services.HouseblockService;
import nl.vng.diwi.services.ProjectService;
import nl.vng.diwi.services.ProjectServiceTest;
import nl.vng.diwi.services.UserGroupService;
import nl.vng.diwi.testutil.TestDb;

import org.assertj.core.api.recursive.comparison.RecursiveComparisonConfiguration;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class ProjectsResourceTest {
    private static ObjectMapper objectMapper = new ObjectMapper();
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
    private ZonedDateTime now = ZonedDateTime.now();
    private LocalDate today = now.toLocalDate();
    private HouseblockResource blockResource;
    private UserState userState;
    private ContainerRequestContext context;

    @BeforeEach
    void beforeEach() {
        dal = dalFactory.constructDal();
        repo = new VngRepository(dal.getSession());
        projectResource = new ProjectsResource(new GenericRepository(dal),
                new ProjectService(), new HouseblockService(), new UserGroupService(null), new PropertiesService(), testDb.projectConfig,
                new ExcelImportService(), new GeoJsonImportService());
        projectResource.getUserGroupService().setUserGroupDAO(repo.getUsergroupDAO());
        blockResource = new HouseblockResource(new GenericRepository(dal), new HouseblockService(), new ProjectService(), new PropertiesService());

        try (AutoCloseTransaction transaction = repo.beginTransaction()) {
            user = new User();
            userGroup = new UserGroup();

            userState = persistUserAndUserGroup(repo, user, userGroup);

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

    @Test
    public void changeProjectEndDate() throws Exception {
        LocalDate startDate = today.minusDays(10);
        LocalDate endDate = today.plusDays(10);

        // Create an end date earlier than today so it will be before the milestones we end up creating
        LocalDate earlierEndDate = today.minusDays(5);

        // Some set-up to get the users matching
        UserGroupModel owner1 = new UserGroupModel(userGroup);
        owner1.setName("UG");
        UserGroupUserModel ugum = new UserGroupUserModel();
        ugum.setFirstName(userState.getFirstName());
        ugum.setInitials("");
        ugum.setLastName(userState.getLastName());
        ugum.setInitials("LF");
        ugum.setUserGroupName("UG");
        owner1.setUsers(List.of(ugum));
        List<UserGroupModel> owners = List.of(owner1);

        // Create the project
        var originalProjectModel = new ProjectCreateSnapshotModel();
        originalProjectModel.setStartDate(startDate);
        originalProjectModel.setEndDate(endDate);
        originalProjectModel.setProjectName("changeEndDate project");
        originalProjectModel.setProjectColor("#abcdef");
        originalProjectModel.setConfidentialityLevel(Confidentiality.EXTERNAL_GOVERNMENTAL);
        originalProjectModel.setProjectPhase(ProjectPhase._5_PREPARATION);
        originalProjectModel.setProjectOwners(owners);

        ProjectMinimalSnapshotModel createdProject = projectResource.createProject(loggedUser, originalProjectModel);
        repo.getSession().clear();

        // Create a block
        var originalBlockModel = new HouseblockSnapshotModel();
        originalBlockModel.setStartDate(startDate);
        originalBlockModel.setEndDate(endDate);
        originalBlockModel.setHouseblockName("changeEndDate block");
        originalBlockModel.setProjectId(createdProject.getProjectId());

        var createdBlock = blockResource.createHouseblock(loggedUser, originalBlockModel);
        repo.getSession().clear();

        // Create model and change everything except the start date, end date and the id to create a lot of milestones on the current day
        // var updateProjectModel = new ProjectSnapshotModel();

        ProjectSnapshotModel updateProjectModel = objectMapper
                .readValue(objectMapper.writeValueAsString(originalProjectModel), ProjectSnapshotModel.class);
        updateProjectModel.setProjectId(createdProject.getProjectId());

        // updateProjectModel.setStartDate(originalProjectModel.getStartDate());
        // updateProjectModel.setEndDate(originalProjectModel.getEndDate());
        updateProjectModel.setProjectOwners(originalProjectModel.getProjectOwners());

        if (false) {
            // Change the following values to create new milestones on the current date
            updateProjectModel.setProjectName("new name");
            updateProjectModel.setProjectColor("#123456");
            updateProjectModel.setConfidentialityLevel(Confidentiality.PUBLIC);
            updateProjectModel.setProjectPhase(ProjectPhase._6_REALIZATION);
            projectResource.updateProjectSnapshot(loggedUser, updateProjectModel);
            repo.getSession().clear();
        }
        // Set the end date to a date before today so it will conflict with the milestones created by the changes above
        updateProjectModel.setEndDate(earlierEndDate);
        projectResource.updateProjectSnapshot(loggedUser, updateProjectModel);
        repo.getSession().clear();

        assertThat(projectResource.getCurrentProjectSnapshot(context, createdProject.getProjectId()))
                .usingRecursiveComparison(RecursiveComparisonConfiguration.builder()
                        .withIgnoredFields("projectStateId", "projectOwners.users.userGroupUuid", "projectOwners.users.uuid")
                        .build())
                .isEqualTo(updateProjectModel);
        repo.getSession().clear();

        // The end date of the houseblock model should have been changed as well.
        HouseblockSnapshotModel expectedHouseblockModel = originalBlockModel.toBuilder()
                .endDate(earlierEndDate)
                .build();
        assertThat(blockResource.getCurrentHouseblockSnapshot(createdBlock.getHouseblockId()))
                .isEqualTo(expectedHouseblockModel);
    }

    private UserState persistUserAndUserGroup(VngRepository repo, User user, UserGroup userGroup) {
        repo.persist(user);

        userGroup.setSingleUser(true);
        repo.persist(userGroup);

        UserState userState = new UserState();
        userState.setChangeStartDate(now);
        userState.setUser(user);
        userState.setFirstName("FN");
        userState.setLastName("LN");
        userState.setCreateUser(user);
        userState.setIdentityProviderId("identityProviderId");
        userState.setUserRole(UserRole.UserPlus);
        repo.persist(userState);

        UserGroupState userGroupState = new UserGroupState();
        userGroupState.setName("UG");
        userGroupState.setUserGroup(userGroup);
        userGroupState.setCreateUser(user);
        userGroupState.setChangeStartDate(now);
        repo.persist(userGroupState);

        UserToUserGroup utug = new UserToUserGroup();
        utug.setUser(user);
        utug.setUserGroup(userGroup);
        utug.setCreateUser(user);
        utug.setChangeStartDate(now);
        repo.persist(utug);

        return userState;
    }
}
