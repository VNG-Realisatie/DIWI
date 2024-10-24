package nl.vng.diwi.services;

import nl.vng.diwi.dal.AutoCloseTransaction;
import nl.vng.diwi.dal.Dal;
import nl.vng.diwi.dal.DalFactory;
import nl.vng.diwi.dal.GenericRepository;
import nl.vng.diwi.dal.VngRepository;
import nl.vng.diwi.dal.entities.Houseblock;
import nl.vng.diwi.dal.entities.HouseblockDurationChangelog;
import nl.vng.diwi.dal.entities.HouseblockMutatieChangelog;
import nl.vng.diwi.dal.entities.HouseblockNameChangelog;
import nl.vng.diwi.dal.entities.HouseblockState;
import nl.vng.diwi.dal.entities.Milestone;
import nl.vng.diwi.dal.entities.Project;
import nl.vng.diwi.dal.entities.User;
import nl.vng.diwi.dal.entities.enums.MutationType;
import nl.vng.diwi.rest.VngServerErrorException;
import nl.vng.diwi.testutil.TestDb;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class HouseblockServiceTest {

    private static DalFactory dalFactory;
    private static TestDb testDb;

    private Dal dal;
    private VngRepository repo;
    private UUID userUuid;
    private User user;

    private Houseblock houseblock;
    private UUID houseblockUuid;

    private static HouseblockService houseblockService;

    @BeforeAll
    static void beforeAll() throws Exception {
        testDb = new TestDb();
        testDb.reset();
        dalFactory = testDb.getDalFactory();
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
    void beforeEach() {
        dal = dalFactory.constructDal();
        repo = new VngRepository(dal.getSession());

        try (AutoCloseTransaction transaction = repo.beginTransaction()) {
            user = repo.persist(new User());
            userUuid = user.getId();

            Project project = ProjectServiceTest.createProject(repo, user);
            houseblock = new Houseblock();
            houseblock.setProject(project);
            repo.persist(houseblock);
            houseblockUuid = houseblock.getId();

            transaction.commit();
            repo.getSession().clear();
        }
    }

    @AfterEach
    void afterEach() {
        dal.close();
    }

    @Test
    void updateNameTest_futureHouseblock() throws VngServerErrorException {
        final LocalDate startDate = LocalDate.now().minusDays(10);
        final LocalDate middleDate = LocalDate.now().plusDays(5);
        final LocalDate endDate = LocalDate.now().plusDays(10);

        try (AutoCloseTransaction transaction = repo.beginTransaction()) {
            Milestone startMilestone = ProjectServiceTest.createMilestone(repo, houseblock.getProject(), startDate, user);
            Milestone middleMilestone = ProjectServiceTest.createMilestone(repo, houseblock.getProject(), middleDate, user);
            Milestone endMilestone = ProjectServiceTest.createMilestone(repo, houseblock.getProject(), endDate, user);

            ProjectServiceTest.createProjectDurationChangelog(repo, houseblock.getProject(), startMilestone, endMilestone, user);
            createHouseblockDurationAndStateChangelog(repo, houseblock, middleMilestone, endMilestone, user);
            createHouseblockNameChangelog(repo, houseblock, "Name 1", middleMilestone, endMilestone, user);

            transaction.commit();
            repo.getSession().clear();
        }

        try (AutoCloseTransaction transaction = repo.beginTransaction()) {
            houseblockService.updateHouseblockName(repo, repo.findById(Project.class, houseblock.getProject().getId()), repo.findById(Houseblock.class, houseblockUuid),
                "Name 1 - Update Test", userUuid, LocalDate.now());
            transaction.commit();
            repo.getSession().clear();
        }

        repo.getSession().disableFilter(GenericRepository.CURRENT_DATA_FILTER);
        Houseblock updatedHouseblock = repo.findById(Houseblock.class, houseblockUuid);
        List<HouseblockNameChangelog> nameChangelogs = updatedHouseblock.getNames();

        assertThat(nameChangelogs.size()).isEqualTo(2);

        HouseblockNameChangelog oldChangelog = nameChangelogs.stream().filter(c -> c.getName().equals("Name 1") && c.getChangeEndDate() != null).findFirst()
                .orElse(null);
        assertThat(oldChangelog).isNotNull();
        assertThat(oldChangelog.getStartMilestone().getState().get(0).getDate()).isEqualTo(middleDate);
        assertThat(oldChangelog.getEndMilestone().getState().get(0).getDate()).isEqualTo(endDate);

        HouseblockNameChangelog newChangelog = nameChangelogs.stream().filter(c -> c.getName().equals("Name 1 - Update Test") && c.getChangeEndDate() == null)
                .findFirst().orElse(null);
        assertThat(newChangelog).isNotNull();
        assertThat(newChangelog.getStartMilestone().getState().get(0).getDate()).isEqualTo(middleDate);
        assertThat(newChangelog.getEndMilestone().getState().get(0).getDate()).isEqualTo(endDate);

    }

    @Test
    void updateNameTest_pastHouseblock() throws VngServerErrorException {

        final LocalDate startDate = LocalDate.now().minusDays(10);
        final LocalDate middleDate = LocalDate.now().minusDays(5);
        final LocalDate endDate = LocalDate.now().plusDays(10);

        try (AutoCloseTransaction transaction = repo.beginTransaction()) {
            Milestone startMilestone = ProjectServiceTest.createMilestone(repo, houseblock.getProject(), startDate, user);
            Milestone middleMilestone = ProjectServiceTest.createMilestone(repo, houseblock.getProject(), middleDate, user);
            Milestone endMilestone = ProjectServiceTest.createMilestone(repo, houseblock.getProject(), endDate, user);

            ProjectServiceTest.createProjectDurationChangelog(repo, houseblock.getProject(), startMilestone, endMilestone, user);
            createHouseblockDurationAndStateChangelog(repo, houseblock, startMilestone, middleMilestone, user);
            createHouseblockNameChangelog(repo, houseblock, "Name 1", startMilestone, middleMilestone, user);

            transaction.commit();
            repo.getSession().clear();
        }

        try (AutoCloseTransaction transaction = repo.beginTransaction()) {
            houseblockService.updateHouseblockName(repo, repo.findById(Project.class, houseblock.getProject().getId()), repo.findById(Houseblock.class, houseblockUuid),
                "Name 1 - Update Test", userUuid, LocalDate.now());
            transaction.commit();
            repo.getSession().clear();
        }

        repo.getSession().disableFilter(GenericRepository.CURRENT_DATA_FILTER);
        Houseblock updatedHouseblock = repo.findById(Houseblock.class, houseblockUuid);
        List<HouseblockNameChangelog> nameChangelogs = updatedHouseblock.getNames();

        assertThat(nameChangelogs.size()).isEqualTo(2);

        HouseblockNameChangelog oldChangelog = nameChangelogs.stream().filter(c -> c.getName().equals("Name 1") && c.getChangeEndDate() != null).findFirst()
            .orElse(null);
        assertThat(oldChangelog).isNotNull();
        assertThat(oldChangelog.getStartMilestone().getState().get(0).getDate()).isEqualTo(startDate);
        assertThat(oldChangelog.getEndMilestone().getState().get(0).getDate()).isEqualTo(middleDate);

        HouseblockNameChangelog newChangelog = nameChangelogs.stream().filter(c -> c.getName().equals("Name 1 - Update Test") && c.getChangeEndDate() == null)
            .findFirst().orElse(null);
        assertThat(newChangelog).isNotNull();
        assertThat(newChangelog.getStartMilestone().getState().get(0).getDate()).isEqualTo(startDate);
        assertThat(newChangelog.getEndMilestone().getState().get(0).getDate()).isEqualTo(middleDate);

    }


    @Test
    void updateNameTest_currentHouseblock() throws VngServerErrorException {

        final LocalDate startDate = LocalDate.now().minusDays(10);
        final LocalDate endDate = LocalDate.now().plusDays(10);

        try (AutoCloseTransaction transaction = repo.beginTransaction()) {
            Milestone startMilestone = ProjectServiceTest.createMilestone(repo, houseblock.getProject(), startDate, user);
            Milestone endMilestone = ProjectServiceTest.createMilestone(repo, houseblock.getProject(), endDate, user);

            ProjectServiceTest.createProjectDurationChangelog(repo, houseblock.getProject(), startMilestone, endMilestone, user);
            createHouseblockDurationAndStateChangelog(repo, houseblock, startMilestone, endMilestone, user);
            createHouseblockNameChangelog(repo, houseblock, "Name 1", startMilestone, endMilestone, user);

            transaction.commit();
            repo.getSession().clear();
        }

        try (AutoCloseTransaction transaction = repo.beginTransaction()) {
            houseblockService.updateHouseblockName(repo, repo.findById(Project.class, houseblock.getProject().getId()), repo.findById(Houseblock.class, houseblockUuid),
                "Name 1 - Update Test", userUuid, LocalDate.now());
            transaction.commit();
            repo.getSession().clear();
        }

        repo.getSession().disableFilter(GenericRepository.CURRENT_DATA_FILTER);
        Houseblock updatedHouseblock = repo.findById(Houseblock.class, houseblockUuid);
        List<HouseblockNameChangelog> nameChangelogs = updatedHouseblock.getNames();

        assertThat(nameChangelogs.size()).isEqualTo(3);

        HouseblockNameChangelog oldChangelog = nameChangelogs.stream().filter(c -> c.getName().equals("Name 1") && c.getChangeEndDate() == null).findFirst()
            .orElse(null);
        assertThat(oldChangelog).isNotNull();
        assertThat(oldChangelog.getStartMilestone().getState().get(0).getDate()).isEqualTo(startDate);
        assertThat(oldChangelog.getEndMilestone().getState().get(0).getDate()).isEqualTo(LocalDate.now());

        HouseblockNameChangelog oldChangelogAfterUpdate = nameChangelogs.stream().filter(c -> c.getName().equals("Name 1") && c.getChangeEndDate() != null).findFirst()
            .orElse(null);
        assertThat(oldChangelogAfterUpdate).isNotNull();
        assertThat(oldChangelogAfterUpdate.getStartMilestone().getState().get(0).getDate()).isEqualTo(startDate);
        assertThat(oldChangelogAfterUpdate.getEndMilestone().getState().get(0).getDate()).isEqualTo(endDate);

        HouseblockNameChangelog newChangelog = nameChangelogs.stream().filter(c -> c.getName().equals("Name 1 - Update Test") && c.getChangeEndDate() == null)
            .findFirst().orElse(null);
        assertThat(newChangelog).isNotNull();
        assertThat(newChangelog.getStartMilestone().getState().get(0).getDate()).isEqualTo(LocalDate.now());
        assertThat(newChangelog.getEndMilestone().getState().get(0).getDate()).isEqualTo(endDate);

    }

    public static HouseblockDurationChangelog createHouseblockDurationAndStateChangelog(VngRepository repo, Houseblock houseblock,
                                                                                        Milestone startMilestone, Milestone endMilestone, User user) {
        HouseblockDurationChangelog durationChangelog = new HouseblockDurationChangelog();
        durationChangelog.setHouseblock(houseblock);
        durationChangelog.setCreateUser(user);
        durationChangelog.setStartMilestone(startMilestone);
        durationChangelog.setEndMilestone(endMilestone);
        durationChangelog.setChangeStartDate(ZonedDateTime.now());
        repo.persist(durationChangelog);

        HouseblockState state = new HouseblockState();
        state.setHouseblock(houseblock);
        state.setCreateUser(user);
        state.setChangeStartDate(ZonedDateTime.now());
        repo.persist(state);

        return durationChangelog;
    }

    public static HouseblockNameChangelog createHouseblockNameChangelog(VngRepository repo, Houseblock houseblock, String name, Milestone startMilestone,
                                                                     Milestone endMilestone, User user) {
        HouseblockNameChangelog nameChangelog = new HouseblockNameChangelog();
        nameChangelog.setHouseblock(houseblock);
        nameChangelog.setCreateUser(user);
        nameChangelog.setStartMilestone(startMilestone);
        nameChangelog.setEndMilestone(endMilestone);
        nameChangelog.setChangeStartDate(ZonedDateTime.now());
        nameChangelog.setName(name);
        repo.persist(nameChangelog);
        return nameChangelog;
    }

    public static HouseblockMutatieChangelog createHouseblockMutationChangelog(VngRepository repo, Houseblock houseblock, MutationType mutationType, Milestone startMilestone,
                                                                               Milestone endMilestone, User user) {
        HouseblockMutatieChangelog mutationChangelog = new HouseblockMutatieChangelog();
        mutationChangelog.setHouseblock(houseblock);
        mutationChangelog.setCreateUser(user);
        mutationChangelog.setStartMilestone(startMilestone);
        mutationChangelog.setEndMilestone(endMilestone);
        mutationChangelog.setChangeStartDate(ZonedDateTime.now());
        mutationChangelog.setMutationType(mutationType);
        mutationChangelog.setAmount(10);
        repo.persist(mutationChangelog);
        return mutationChangelog;
    }

}
