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
import nl.vng.diwi.models.MilestoneModel;
import nl.vng.diwi.models.ProjectSnapshotModel;
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
import nl.vng.diwi.testutil.TestDb;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class ProjectsResourceTest {

    private static DalFactory dalFactory;
    private static TestDb testDb;
    private Dal dal;
    private VngRepository repo;
    private static ProjectsResource projectResource;

    @BeforeAll
    static void beforeAll() throws Exception {
        testDb = new TestDb();
        dalFactory = testDb.getDalFactory();
        projectResource = new ProjectsResource(new GenericRepository(dalFactory.constructDal()),
            new ProjectService(), new HouseblockService(), new PropertiesService(), testDb.projectConfig, new ExcelImportService(), new GeoJsonImportService());
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

    @Test
    void updateProjectSnapshotTest_currentProject() throws VngNotFoundException, VngServerErrorException, VngBadRequestException, VngNotAllowedException {

        UUID userUuid;
        UUID projectUuid;

        //prepare project with name and duration changelog
        try (AutoCloseTransaction transaction = repo.beginTransaction()) {
            User user = new User();
            UserGroup userGroup = new UserGroup();
            persistUserAndUserGroup(repo, user, userGroup);
            userUuid = user.getId();

            Project project = ProjectServiceTest.createProject(repo, user);
            projectUuid = project.getId();
            Milestone startMilestone = ProjectServiceTest.createMilestone(repo, project, LocalDate.now().minusDays(10), user);
            Milestone middleMilestone = ProjectServiceTest.createMilestone(repo, project, LocalDate.now().plusDays(5), user);
            Milestone endMilestone = ProjectServiceTest.createMilestone(repo, project, LocalDate.now().plusDays(10), user);
            ProjectServiceTest.createProjectDurationChangelog(repo, project, startMilestone, endMilestone, user);
            ProjectServiceTest.createProjectNameChangelog(repo, project, "Name 1", startMilestone, middleMilestone, user);
            ProjectServiceTest.createProjectNameChangelog(repo, project, "Name 2", middleMilestone, endMilestone, user);
            UserGroupToProject ugtp = new UserGroupToProject();
            ugtp.setUserGroup(userGroup);
            ugtp.setProject(project);
            ugtp.setCreateUser(user);
            ugtp.setChangeStartDate(ZonedDateTime.now());
            repo.persist(ugtp);

            transaction.commit();
            repo.getSession().clear();
        }

        LoggedUser loggedUser = new LoggedUser();
        loggedUser.setRole(UserRole.UserPlus);
        loggedUser.setUuid(userUuid);

        ContainerRequestContext requestContext = Mockito.mock(ContainerRequestContext.class);
        Mockito.when(requestContext.getProperty("loggedUser")).thenReturn(loggedUser);

        //prepare update model with modified name and start date
        ProjectSnapshotModel projectSnapshot = projectResource.getCurrentProjectSnapshot(requestContext, projectUuid);
        projectSnapshot.setProjectName("Name 1 updated");
        projectSnapshot.setStartDate(LocalDate.now().minusDays(15));

        //call update endpoint
        projectResource.updateProjectSnapshot(loggedUser, projectSnapshot);
        repo.getSession().clear();

        //assert
        repo.getSession().disableFilter(GenericRepository.CURRENT_DATA_FILTER);
        Project updatedProject = repo.findById(Project.class, projectUuid);
        List<ProjectNameChangelog> nameChangelogs = updatedProject.getName();

        assertThat(nameChangelogs.size()).isEqualTo(4);

        ProjectNameChangelog oldChangelog = nameChangelogs.stream().filter(c -> c.getName().equals("Name 1") && c.getChangeEndDate() != null).findFirst().orElse(null);
        assertThat(oldChangelog).isNotNull();
        assertThat(new MilestoneModel(oldChangelog.getStartMilestone()).getDate()).isEqualTo(LocalDate.now().minusDays(15));
        assertThat(new MilestoneModel(oldChangelog.getEndMilestone()).getDate()).isEqualTo(LocalDate.now().plusDays(5));

        ProjectNameChangelog oldChangelogV2 = nameChangelogs.stream().filter(c -> c.getName().equals("Name 1") && c.getChangeEndDate() == null).findFirst().orElse(null);
        assertThat(oldChangelog).isNotNull();
        assertThat(new MilestoneModel(oldChangelogV2.getStartMilestone()).getDate()).isEqualTo(LocalDate.now().minusDays(15));
        assertThat(new MilestoneModel(oldChangelogV2.getEndMilestone()).getDate()).isEqualTo(LocalDate.now());

        ProjectNameChangelog newChangelog = nameChangelogs.stream().filter(c -> c.getName().equals("Name 1 updated") && c.getChangeEndDate() == null).findFirst().orElse(null);
        assertThat(newChangelog).isNotNull();
        assertThat(new MilestoneModel(newChangelog.getStartMilestone()).getDate()).isEqualTo(LocalDate.now());
        assertThat(new MilestoneModel(newChangelog.getEndMilestone()).getDate()).isEqualTo(LocalDate.now().plusDays(5));

        ProjectNameChangelog futureNameChangelog = nameChangelogs.stream().filter(c -> c.getName().equals("Name 2") && c.getChangeEndDate() == null).findFirst().orElse(null);
        assertThat(futureNameChangelog).isNotNull();
        assertThat(new MilestoneModel(futureNameChangelog.getStartMilestone()).getDate()).isEqualTo(LocalDate.now().plusDays(5));
        assertThat(new MilestoneModel(futureNameChangelog.getEndMilestone()).getDate()).isEqualTo(LocalDate.now().plusDays(10));
    }


    @Test
    void updateProjectSnapshotTest_futureProject() throws VngNotFoundException, VngServerErrorException, VngBadRequestException, VngNotAllowedException {

        UUID userUuid;
        UUID projectUuid;

        //prepare project with name and duration changelog
        try (AutoCloseTransaction transaction = repo.beginTransaction()) {
            User user = new User();
            UserGroup userGroup = new UserGroup();
            persistUserAndUserGroup(repo, user, userGroup);
            userUuid = user.getId();

            Project project = ProjectServiceTest.createProject(repo, user);
            projectUuid = project.getId();
            Milestone startMilestone = ProjectServiceTest.createMilestone(repo, project, LocalDate.now().plusDays(5), user);
            Milestone middleMilestone = ProjectServiceTest.createMilestone(repo, project, LocalDate.now().plusDays(10), user);
            Milestone endMilestone = ProjectServiceTest.createMilestone(repo, project, LocalDate.now().plusDays(15), user);
            ProjectServiceTest.createProjectDurationChangelog(repo, project, startMilestone, endMilestone, user);
            ProjectServiceTest.createProjectNameChangelog(repo, project, "Name 1", startMilestone, middleMilestone, user);
            ProjectServiceTest.createProjectNameChangelog(repo, project, "Name 2", middleMilestone, endMilestone, user);

            UserGroupToProject ugtp = new UserGroupToProject();
            ugtp.setUserGroup(userGroup);
            ugtp.setProject(project);
            ugtp.setCreateUser(user);
            ugtp.setChangeStartDate(ZonedDateTime.now());
            repo.persist(ugtp);

            transaction.commit();
            repo.getSession().clear();
        }

        LoggedUser loggedUser = new LoggedUser();
        loggedUser.setUuid(userUuid);
        loggedUser.setRole(UserRole.UserPlus);

        ContainerRequestContext requestContext = Mockito.mock(ContainerRequestContext.class);
        Mockito.when(requestContext.getProperty("loggedUser")).thenReturn(loggedUser);

        //prepare update model with modified name and start date
        ProjectSnapshotModel projectSnapshot = projectResource.getCurrentProjectSnapshot(requestContext, projectUuid);
        projectSnapshot.setProjectName("Name 1 updated");
        projectSnapshot.setStartDate(LocalDate.now().minusDays(1));

        //call update endpoint
        projectResource.updateProjectSnapshot(loggedUser, projectSnapshot);
        repo.getSession().clear();

        //assert
        repo.getSession().disableFilter(GenericRepository.CURRENT_DATA_FILTER);
        Project updatedProject = repo.findById(Project.class, projectUuid);
        List<ProjectNameChangelog> nameChangelogs = updatedProject.getName();

        assertThat(nameChangelogs.size()).isEqualTo(3);

        ProjectNameChangelog oldChangelog = nameChangelogs.stream().filter(c -> c.getName().equals("Name 1") && c.getChangeEndDate() != null).findFirst().orElse(null);
        assertThat(oldChangelog).isNotNull();
        assertThat(new MilestoneModel(oldChangelog.getStartMilestone()).getDate()).isEqualTo(LocalDate.now().minusDays(1));
        assertThat(new MilestoneModel(oldChangelog.getEndMilestone()).getDate()).isEqualTo(LocalDate.now().plusDays(10));

        ProjectNameChangelog newChangelog = nameChangelogs.stream().filter(c -> c.getName().equals("Name 1 updated") && c.getChangeEndDate() == null).findFirst().orElse(null);
        assertThat(newChangelog).isNotNull();
        assertThat(new MilestoneModel(newChangelog.getStartMilestone()).getDate()).isEqualTo(LocalDate.now().minusDays(1));
        assertThat(new MilestoneModel(newChangelog.getEndMilestone()).getDate()).isEqualTo(LocalDate.now().plusDays(10));

        ProjectNameChangelog futureNameChangelog = nameChangelogs.stream().filter(c -> c.getName().equals("Name 2") && c.getChangeEndDate() == null).findFirst().orElse(null);
        assertThat(futureNameChangelog).isNotNull();
        assertThat(new MilestoneModel(futureNameChangelog.getStartMilestone()).getDate()).isEqualTo(LocalDate.now().plusDays(10));
        assertThat(new MilestoneModel(futureNameChangelog.getEndMilestone()).getDate()).isEqualTo(LocalDate.now().plusDays(15));
    }


    private void persistUserAndUserGroup(VngRepository repo, User user, UserGroup userGroup) {
        repo.persist(user);

        userGroup.setSingleUser(true);
        repo.persist(userGroup);

        UserState userState = new UserState();
        userState.setChangeStartDate(ZonedDateTime.now());
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
        userGroupState.setChangeStartDate(ZonedDateTime.now());
        repo.persist(userGroupState);

        UserToUserGroup utug = new UserToUserGroup();
        utug.setUser(user);
        utug.setUserGroup(userGroup);
        utug.setCreateUser(user);
        utug.setChangeStartDate(ZonedDateTime.now());
        repo.persist(utug);
    }
}
