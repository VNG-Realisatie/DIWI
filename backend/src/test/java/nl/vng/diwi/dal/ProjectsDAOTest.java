package nl.vng.diwi.dal;

import nl.vng.diwi.dal.entities.enums.Confidentiality;
import nl.vng.diwi.dal.entities.enums.PlanType;
import nl.vng.diwi.dal.entities.enums.ProjectPhase;
import nl.vng.diwi.models.ProjectListModel;
import nl.vng.diwi.dal.entities.ProjectListSqlModel;
import nl.vng.diwi.security.LoggedUser;
import nl.vng.diwi.security.UserRole;
import nl.vng.diwi.testutil.TestDb;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ProjectsDAOTest {

    private static DalFactory dalFactory;
    private static TestDb testDb;
    private Dal dal;
    private VngRepository repo;

    private static final String PROJECT_TABLE_SCRIPT_PATH = "nl/vng/diwi/ProjectsDAOTest/getProjectsTable.sql";

    @BeforeAll
    static void beforeAll() throws Exception {
        testDb = new TestDb();
        dalFactory = testDb.getDalFactory();
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
    public void getProjectsTableTest() throws IOException {
        FilterPaginationSorting filtering = new FilterPaginationSorting();
        filtering.setPageNumber(1);
        filtering.setPageSize(10);
        filtering.setSortColumn(ProjectListModel.DEFAULT_SORT_COLUMN);

        // There are 5 milestones: M1 - M2 - M3 - M4 - M5. Present timestamp is between M2 and M3.
        // There is 1 past project => between M1 and M2
        // There is 1 current project => between M2 and M4
        // There is 1 future project => M3 and M5

        try (var transaction = dal.beginTransaction()) {
            Path scriptPath = Path.of(ProjectsDAOTest.class.getClassLoader().getResource(PROJECT_TABLE_SCRIPT_PATH).getPath());
            String scriptSql = Files.readString(scriptPath);

            dal.getSession().createNativeMutationQuery(scriptSql).executeUpdate();
            transaction.commit();
        }

        LoggedUser loggedUser = new LoggedUser();
        loggedUser.setRole(UserRole.UserPlus);
        List<ProjectListSqlModel> projects = repo.getProjectsDAO().getProjectsTable(filtering, loggedUser);

        //There are 3 projects. One in the past, one ongoing, one in the future. All are returned.
        assertThat(projects.size()).isEqualTo(3);

        ProjectListSqlModel currentProject = projects.get(1);
        //Test that only the project_state with change_end_date = NULL is taken into account
        assertThat(currentProject.getConfidentialityLevel()).isEqualTo(Confidentiality.PUBLIC);
        assertThat(currentProject.getProjectColor()).isEqualTo("#223344");
        assertThat(currentProject.getStartDate()).isEqualTo(LocalDate.now().minusDays(5));
        assertThat(currentProject.getEndDate()).isEqualTo(LocalDate.now().plusDays(15));
        //Test that the name changelog with the milestones corresponding to the present moment is selected
        assertThat(currentProject.getProjectName()).isEqualTo("Current project Phase 1");
        assertThat(currentProject.getProjectPhase()).isEqualTo(ProjectPhase._1_CONCEPT);
        //Test that only the changelog value with milestones corresponding to the present moment are selected and values are sorted alphabetically
        assertThat(currentProject.getPlanType().size()).isEqualTo(2);
        assertThat(currentProject.getPlanType().get(0)).isEqualTo(PlanType.HERSTRUCTURERING);
        assertThat(currentProject.getPlanType().get(1)).isEqualTo(PlanType.PAND_TRANSFORMATIE);
        assertThat(currentProject.getMunicipality().size()).isEqualTo(2);
        assertThat(currentProject.getMunicipality().get(0).getName()).isEqualTo("Gemeente 1");
        assertThat(currentProject.getMunicipality().get(1).getName()).isEqualTo("Gemeente 2");

        ProjectListSqlModel futureProject = projects.get(2);
        assertThat(futureProject.getConfidentialityLevel()).isEqualTo(Confidentiality.EXTERNAL_REGIONAL);
        assertThat(futureProject.getProjectColor()).isEqualTo("#456456");
        assertThat(futureProject.getStartDate()).isEqualTo(LocalDate.now().plusDays(10));
        assertThat(futureProject.getEndDate()).isEqualTo(LocalDate.now().plusDays(20));
        //Test that the name changelog at the project start milestone
        assertThat(futureProject.getProjectName()).isEqualTo("Future project Phase 1");
        assertThat(futureProject.getProjectPhase()).isNull();
        //Test that only the changelog values with the start milestone at the beginning of the project are selected
        assertThat(futureProject.getPlanType().size()).isEqualTo(1);
        assertThat(futureProject.getPlanType().get(0)).isEqualTo(PlanType.UITBREIDING_OVERIG);
        assertThat(futureProject.getMunicipality().size()).isEqualTo(1);
        assertThat(futureProject.getMunicipality().get(0).getName()).isEqualTo("Gemeente 1");
    }
}
