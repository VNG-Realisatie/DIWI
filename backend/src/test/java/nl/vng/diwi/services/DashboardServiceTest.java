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
import nl.vng.diwi.dal.entities.Project;
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
    private LoggedUser loggedUser;
    private Houseblock houseblock;

    private Project project;

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

        try (AutoCloseTransaction transaction = repo.beginTransaction()) {
            // User
            var user = repo.persist(new User());
            var userUuid = user.getId();
            loggedUser = new LoggedUser();
            loggedUser.setUuid(userUuid);
            loggedUser.setRole(UserRole.UserPlus);

            // Project
            project = ProjectServiceTest.createProject(repo, user);
            final LocalDate startDate = LocalDate.now().minusDays(10);
            final LocalDate endDate = LocalDate.now().plusDays(10);
            Milestone startMilestone = ProjectServiceTest.createMilestone(repo, project, startDate, user);
            Milestone endMilestone = ProjectServiceTest.createMilestone(repo, project, endDate, user);
            ProjectServiceTest.createProjectDurationChangelog(repo, project, startMilestone, endMilestone, user);

            // Houseblock
            houseblock = new Houseblock();
            houseblock.setProject(project);
            repo.persist(houseblock);

            HouseblockServiceTest.createHouseblockDurationAndStateChangelog(repo, houseblock, startMilestone,
                    endMilestone, user);
            HouseblockServiceTest.createHouseblockNameChangelog(repo, houseblock, "Name 1", startMilestone,
                    endMilestone, user);

            transaction.commit();
            repo.getSession().clear();
        }
    }

    @AfterEach
    void afterEach() {
        dal.close();
    }

    @Test
    void getProjectDashboardSnapshot() throws VngNotFoundException {
        // when
        var result = dashboardService.getProjectDashboardSnapshot(repo, project.getId(), now.toLocalDate(), loggedUser);

        // then
        assertThat(result).isNotNull();
    }

    @Test
    void getMultiProjectDashboardSnapshot() throws VngNotFoundException {
        // when
        var result = dashboardService.getMultiProjectDashboardSnapshot(repo, now.toLocalDate(), loggedUser);

        // then
        assertThat(result).isNotNull();
    }
}
