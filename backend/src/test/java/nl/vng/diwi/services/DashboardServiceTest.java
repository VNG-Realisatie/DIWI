package nl.vng.diwi.services;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.UUID;

import org.assertj.core.api.recursive.comparison.RecursiveComparisonConfiguration;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import nl.vng.diwi.dal.AutoCloseTransaction;
import nl.vng.diwi.dal.Dal;
import nl.vng.diwi.dal.DalFactory;
import nl.vng.diwi.dal.VngRepository;
import nl.vng.diwi.dal.entities.Houseblock;
import nl.vng.diwi.dal.entities.Milestone;
import nl.vng.diwi.dal.entities.User;
import nl.vng.diwi.rest.VngNotFoundException;
import nl.vng.diwi.security.LoggedUser;
import nl.vng.diwi.security.UserRole;
import nl.vng.diwi.testutil.TestDb;

public class DashboardServiceTest {
    private static RecursiveComparisonConfiguration ignoreId = RecursiveComparisonConfiguration.builder()
            .withIgnoredFields("id").build();

    private static DalFactory dalFactory;
    private static TestDb testDb;

    private Dal dal;
    private VngRepository repo;

    private ZonedDateTime now;
    // private UUID userUuid;
    // private User user;
    // private Project project;
    // private UUID projectUuid;
    // private Milestone startMilestone;
    // private Milestone endMilestone;
    // private Milestone middleMilestone;

    private static DashboardService dashboardService;

    @BeforeAll
    static void beforeAll() throws Exception {
        testDb = new TestDb();
        dalFactory = testDb.getDalFactory();
        dashboardService = new DashboardService();
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
    }

    @AfterEach
    void afterEach() {
        dal.close();
    }

    @Test
    void testGetProjectDashboardSnapshot() throws VngNotFoundException {
        // given
        UUID projectUuid;
        LoggedUser loggedUser;
        LocalDate snapshotDate = LocalDate.now();

        try (AutoCloseTransaction transaction = repo.beginTransaction()) {
            // User
            var user = repo.persist(new User());
            var userUuid = user.getId();
            loggedUser = new LoggedUser();
            loggedUser.setUuid(userUuid);
            loggedUser.setRole(UserRole.UserPlus);

            // Project
            var project = ProjectServiceTest.createProject(repo, user);
            final LocalDate startDate = LocalDate.now().minusDays(10);
            final LocalDate endDate = LocalDate.now().plusDays(10);
            Milestone startMilestone = ProjectServiceTest.createMilestone(repo, project, startDate, user);
            Milestone endMilestone = ProjectServiceTest.createMilestone(repo, project, endDate, user);
            ProjectServiceTest.createProjectDurationChangelog(repo, project, startMilestone, endMilestone, user);
            projectUuid = project.getId();

            // Houseblock
            var             houseblock = new Houseblock();
            houseblock.setProject(project);
            repo.persist(houseblock);

            HouseblockServiceTest.createHouseblockDurationAndStateChangelog(repo, houseblock, startMilestone, endMilestone, user);
            HouseblockServiceTest.createHouseblockNameChangelog(repo, houseblock, "Name 1", startMilestone, endMilestone, user);


            transaction.commit();
            repo.getSession().clear();
        }

        // when
        var result = dashboardService.getProjectDashboardSnapshot(repo, projectUuid, snapshotDate, loggedUser);

        // then
        assertThat(result).isNotNull();
    }
}
