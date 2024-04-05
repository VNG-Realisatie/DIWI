package nl.vng.diwi.resources;

import nl.vng.diwi.dal.AutoCloseTransaction;
import nl.vng.diwi.dal.Dal;
import nl.vng.diwi.dal.DalFactory;
import nl.vng.diwi.dal.GenericRepository;
import nl.vng.diwi.dal.VngRepository;
import nl.vng.diwi.dal.entities.Houseblock;
import nl.vng.diwi.dal.entities.HouseblockNameChangelog;
import nl.vng.diwi.dal.entities.Milestone;
import nl.vng.diwi.dal.entities.Project;
import nl.vng.diwi.dal.entities.User;
import nl.vng.diwi.models.HouseblockSnapshotModel;
import nl.vng.diwi.models.MilestoneModel;
import nl.vng.diwi.rest.VngBadRequestException;
import nl.vng.diwi.rest.VngNotFoundException;
import nl.vng.diwi.rest.VngServerErrorException;
import nl.vng.diwi.security.LoggedUser;
import nl.vng.diwi.services.PropertiesService;
import nl.vng.diwi.services.HouseblockService;
import nl.vng.diwi.services.HouseblockServiceTest;
import nl.vng.diwi.services.ProjectService;
import nl.vng.diwi.services.ProjectServiceTest;
import nl.vng.diwi.testutil.TestDb;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class HouseblockResourceTest {

    private static DalFactory dalFactory;
    private static TestDb testDb;
    private Dal dal;
    private VngRepository repo;
    private static HouseblockResource houseblockResource;

    @BeforeAll
    static void beforeAll() throws Exception {
        testDb = new TestDb();
        dalFactory = testDb.getDalFactory();
        houseblockResource = new HouseblockResource(new GenericRepository(dalFactory.constructDal()),
            new HouseblockService(), new ProjectService(), new PropertiesService());
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
     *  Updating a houseblock property while updating the start and/or end date at the same time is a complex operation (done in 3 different transactions)
     *  This test is for the most complex scenario: updating name, start date and end date for an ongoing houseblock.
     */
    @Test
    void updateHouseblockTest_currentHouseblock() throws VngNotFoundException, VngServerErrorException, VngBadRequestException {

        UUID userUuid;
        UUID houseblockUuid;

        //prepare houseblock with name and duration changelog
        try (AutoCloseTransaction transaction = repo.beginTransaction()) {
            User user = repo.persist(new User());
            userUuid = user.getId();
            Project project = ProjectServiceTest.createProject(repo, user);
            Milestone startMilestone = ProjectServiceTest.createMilestone(repo, project, LocalDate.now().minusDays(10), user);
            Milestone endMilestone = ProjectServiceTest.createMilestone(repo, project, LocalDate.now().plusDays(10), user);
            ProjectServiceTest.createProjectDurationChangelog(repo, project, startMilestone, endMilestone, user);
            ProjectServiceTest.createProjectNameChangelog(repo, project, "Project 1", startMilestone, endMilestone, user);

            Houseblock houseblock = new Houseblock();
            houseblock.setProject(project);
            repo.persist(houseblock);
            HouseblockServiceTest.createHouseblockDurationAndStateChangelog(repo, houseblock, startMilestone, endMilestone, user);
            HouseblockServiceTest.createHouseblockNameChangelog(repo, houseblock, "HB 1", startMilestone, endMilestone, user);
            houseblockUuid = houseblock.getId();

            transaction.commit();
            repo.getSession().clear();
        }

        //prepare update model with modified name and start date
        HouseblockSnapshotModel houseblockSnapshot = houseblockResource.getCurrentHouseblockSnapshot(houseblockUuid);
        houseblockSnapshot.setHouseblockName("HB 1 updated");
        houseblockSnapshot.setStartDate(LocalDate.now().minusDays(9));
        houseblockSnapshot.setEndDate(LocalDate.now().plusDays(9));

        //call update endpoint
        LoggedUser loggedUser = new LoggedUser();
        loggedUser.setUuid(userUuid);
        houseblockResource.updateHouseblock(loggedUser, houseblockSnapshot);
        repo.getSession().clear();

        //assert
        repo.getSession().disableFilter(GenericRepository.CURRENT_DATA_FILTER);
        Houseblock updatedHouseblock = repo.findById(Houseblock.class, houseblockUuid);
        List<HouseblockNameChangelog> nameChangelogs = updatedHouseblock.getNames();

        assertThat(nameChangelogs.size()).isEqualTo(5);

        HouseblockNameChangelog initialChangelog = nameChangelogs.stream().filter(c -> c.getName().equals("HB 1") && c.getChangeEndDate() != null &&
            (new MilestoneModel(c.getStartMilestone()).getDate()).equals(LocalDate.now().minusDays(10)) &&
            (new MilestoneModel(c.getEndMilestone()).getDate()).equals(LocalDate.now().plusDays(10))).findFirst().orElse(null);
        assertThat(initialChangelog).isNotNull();

        HouseblockNameChangelog oldChangelogV1 = nameChangelogs.stream().filter(c -> c.getName().equals("HB 1") && c.getChangeEndDate() != null &&
            (new MilestoneModel(c.getStartMilestone()).getDate()).equals(LocalDate.now().minusDays(10)) &&
            (new MilestoneModel(c.getEndMilestone()).getDate()).equals(LocalDate.now())).findFirst().orElse(null);
        assertThat(oldChangelogV1).isNotNull();

        HouseblockNameChangelog oldChangelogV2 = nameChangelogs.stream().filter(c -> c.getName().equals("HB 1") && c.getChangeEndDate() == null &&
            (new MilestoneModel(c.getStartMilestone()).getDate()).equals(LocalDate.now().minusDays(9)) &&
            (new MilestoneModel(c.getEndMilestone()).getDate()).equals(LocalDate.now())).findFirst().orElse(null);
        assertThat(oldChangelogV2).isNotNull();

        HouseblockNameChangelog newChangelogV1 = nameChangelogs.stream().filter(c -> c.getName().equals("HB 1 updated") && c.getChangeEndDate() != null &&
            (new MilestoneModel(c.getStartMilestone()).getDate()).equals(LocalDate.now()) &&
            (new MilestoneModel(c.getEndMilestone()).getDate()).equals(LocalDate.now().plusDays(10))).findFirst().orElse(null);
        assertThat(newChangelogV1).isNotNull();

        HouseblockNameChangelog newChangelogV2 = nameChangelogs.stream().filter(c -> c.getName().equals("HB 1 updated") && c.getChangeEndDate() == null &&
            (new MilestoneModel(c.getStartMilestone()).getDate()).equals(LocalDate.now()) &&
            (new MilestoneModel(c.getEndMilestone()).getDate()).equals(LocalDate.now().plusDays(9))).findFirst().orElse(null);
        assertThat(newChangelogV2).isNotNull();

    }

}
