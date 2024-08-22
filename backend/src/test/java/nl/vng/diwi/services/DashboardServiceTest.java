package nl.vng.diwi.services;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;

import nl.vng.diwi.models.PlanningModel;
import org.assertj.core.api.recursive.comparison.RecursiveComparisonConfiguration;
import org.hibernate.Session;
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
import nl.vng.diwi.dal.entities.HouseblockAppearanceAndTypeChangelog;
import nl.vng.diwi.dal.entities.HouseblockPhysicalAppearanceChangelogValue;
import nl.vng.diwi.dal.entities.HouseblockTargetGroupChangelog;
import nl.vng.diwi.dal.entities.HouseblockTargetGroupChangelogValue;
import nl.vng.diwi.dal.entities.Milestone;
import nl.vng.diwi.dal.entities.Project;
import nl.vng.diwi.dal.entities.Property;
import nl.vng.diwi.dal.entities.PropertyCategoryValue;
import nl.vng.diwi.dal.entities.PropertyCategoryValueState;
import nl.vng.diwi.dal.entities.User;
import nl.vng.diwi.models.MultiProjectDashboardModel;
import nl.vng.diwi.models.PieChartModel;
import nl.vng.diwi.models.ProjectDashboardModel;
import nl.vng.diwi.rest.VngNotFoundException;
import nl.vng.diwi.security.LoggedUser;
import nl.vng.diwi.security.UserRole;
import nl.vng.diwi.testutil.TestDb;

public class DashboardServiceTest {
    private static TestDb testDb;

    private DalFactory dalFactory;
    private Dal dal;
    private VngRepository repo;

    private ZonedDateTime now;
    private LoggedUser loggedUser;
    private Houseblock houseblock;

    private Project project;

    private static DashboardService dashboardService;
    private static PropertiesService propertiesService;

    private static HouseblockService houseblockService;

    @BeforeAll
    static void beforeAll() throws Exception {
        testDb = new TestDb();
        dashboardService = new DashboardService();
        propertiesService = new PropertiesService();
        houseblockService = new HouseblockService();
        houseblockService.setProjectService(new ProjectService());
    }

    @AfterAll
    static void afterAll() {
        if (testDb != null) {
            testDb.close();
        }
    }

    @BeforeEach
    void beforeEach() throws Exception {
        testDb.reset();
        dalFactory = testDb.getDalFactory();
        now = ZonedDateTime.now();
        dal = dalFactory.constructDal();
        Session session = dal.getSession();
        repo = new VngRepository(session);

        Milestone startMilestone;
        Milestone endMilestone;
        User user;
        try (AutoCloseTransaction transaction = repo.beginTransaction()) {
            // User
            user = repo.persist(new User());
            var userUuid = user.getId();
            loggedUser = new LoggedUser();
            loggedUser.setUuid(userUuid);
            loggedUser.setRole(UserRole.UserPlus);

            // Project
            project = ProjectServiceTest.createProject(repo, user);
            final LocalDate startDate = LocalDate.now().minusDays(10);
            final LocalDate endDate = LocalDate.now().plusDays(10);
            startMilestone = ProjectServiceTest.createMilestone(repo, project, startDate, user);
            endMilestone = ProjectServiceTest.createMilestone(repo, project, endDate, user);
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

        try (AutoCloseTransaction transaction = repo.beginTransaction()) {
            project = repo.getProjectsDAO().getCurrentProject(project.getId());
            houseblock = repo.getHouseblockDAO().getCurrentHouseblock(houseblock.getId());

            addAppearanceChangelogValue(session, startMilestone, endMilestone, user);
            addTargetGroupValue(session, startMilestone, endMilestone, user);

            transaction.commit();
            repo.getSession().clear();
        }
    }

    private void addAppearanceChangelogValue(Session session, Milestone startMilestone, Milestone endMilestone,
            User user) {
        var physAppValue = addPropOption(session, startMilestone, endMilestone, user, "physicalAppearance",
                "AppearanceOption");

        var blockAppearance = new HouseblockAppearanceAndTypeChangelog();
        blockAppearance.setHouseblock(houseblock);

        blockAppearance.setStartMilestone(startMilestone);
        blockAppearance.setEndMilestone(endMilestone);
        blockAppearance.setChangeStartDate(now);
        blockAppearance.setCreateUser(user);
        repo.persist(blockAppearance);

        var appearanceChangelogValue = new HouseblockPhysicalAppearanceChangelogValue();
        appearanceChangelogValue.setAppearanceAndTypeChangelog(blockAppearance);
        appearanceChangelogValue.setCategoryValue(physAppValue);
        appearanceChangelogValue.setAmount(1);
        repo.persist(appearanceChangelogValue);
    }

    private void addTargetGroupValue(Session session, Milestone startMilestone, Milestone endMilestone, User user) {
        var tarGroupValue = addPropOption(session, startMilestone, endMilestone, user, "targetGroup",
                "GroupOption");

        var targetGroup = new HouseblockTargetGroupChangelog();
        targetGroup.setHouseblock(houseblock);
        targetGroup.setStartMilestone(startMilestone);
        targetGroup.setEndMilestone(endMilestone);
        targetGroup.setChangeStartDate(now);
        targetGroup.setCreateUser(user);
        repo.persist(targetGroup);

        var targetGroupValue = new HouseblockTargetGroupChangelogValue();
        targetGroupValue.setTargetGroupChangelog(targetGroup);
        targetGroupValue.setCategoryValue(tarGroupValue);
        targetGroupValue.setAmount(2);
        repo.persist(targetGroupValue);
    }

    private PropertyCategoryValue addPropOption(Session session, Milestone startMilestone, Milestone endMilestone,
            User user, String propertyName, String label) {
        PropertyCategoryValueState valueState;

        var propId = propertiesService.getPropertyUuid(repo, propertyName);
        var prop = session.get(Property.class, propId);

        var value = new PropertyCategoryValue();
        value.setProperty(prop);
        repo.persist(value);

        valueState = new PropertyCategoryValueState();
        valueState.setCategoryValue(value);
        valueState.setLabel(label);
        valueState.setCreateUser(user);
        valueState.setChangeStartDate(now);
        session.persist(valueState);

        return value;
    }

    @AfterEach
    void afterEach() {
        dal.close();
    }

    @Test
    void getProjectDashboardSnapshot() throws VngNotFoundException {
        // when
        var result = dashboardService.getProjectDashboardSnapshot(repo, project.getId(), now.toLocalDate(),
                loggedUser);

        var expected = new ProjectDashboardModel();
        expected.setPhysicalAppearance(List.of(new PieChartModel("AppearanceOption", 1)));

        // then
        assertThat(result.getPhysicalAppearance()).usingRecursiveComparison()
                .isEqualTo(expected.getPhysicalAppearance());
    }

    @Test
    void getMultiProjectDashboardSnapshot() throws VngNotFoundException {
        // when
        var result = dashboardService.getMultiProjectDashboardSnapshot(repo, now.toLocalDate(), loggedUser);

        var expected = new MultiProjectDashboardModel();
        expected.setPhysicalAppearance(List.of(new PieChartModel("AppearanceOption", 1)));
        expected.setTargetGroup(List.of(new PieChartModel("GroupOption", 2)));
        expected.setPlanning(List.of(new PlanningModel(project.getId(), null, 0, now.getYear())));

        // then
        assertThat(result).usingRecursiveComparison()
                .isEqualTo(expected);
    }
}
