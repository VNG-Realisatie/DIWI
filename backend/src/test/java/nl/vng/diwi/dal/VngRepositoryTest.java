package nl.vng.diwi.dal;

import nl.vng.diwi.dal.entities.enums.Confidentiality;
import nl.vng.diwi.dal.entities.enums.PlanType;
import nl.vng.diwi.dal.entities.enums.ProjectPhase;
import nl.vng.diwi.models.ProjectListModel;
import nl.vng.diwi.testutil.TestDb;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class VngRepositoryTest {

    private static DalFactory dalFactory;
    private static TestDb testDb;
    private Dal dal;
    private VngRepository repo;

    private static final String PROJECT_TABLE_SCRIPT_PATH = "nl/vng/diwi/VngRepositoryTest/getProjectsTable.sql";
    private static final DateTimeFormatter DAY_MONTH_YEAR_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    @BeforeAll
    static void beforeAll() throws Exception {
        testDb = new TestDb();
        dalFactory = testDb.getDalFactory();
    }

    @AfterAll
    static void afterAll() throws Exception {
        testDb.close();
    }

    @BeforeEach
    void beforeEach() throws Exception {
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
            Path scriptPath = Path.of(VngRepositoryTest.class.getClassLoader().getResource(PROJECT_TABLE_SCRIPT_PATH).getPath());
            String scriptSql = Files.readString(scriptPath);

            dal.getSession().createNativeMutationQuery(scriptSql).executeUpdate();
            transaction.commit();
        }

        List<ProjectListModel> projects = repo.getProjectsDAO().getProjectsTable(filtering);

        //There are 3 projects. One in the past, one ongoing, one in the future. Only the ongoing and future ones are returned.
        assertThat(projects.size()).isEqualTo(2);

        ProjectListModel currentProject = projects.get(0);
        //Test that only the project_state with change_end_date = NULL is taken into account
        assertThat(currentProject.getConfidentialityLevel()).isEqualTo(Confidentiality.OPENBAAR);
        assertThat(currentProject.getProjectColor()).isEqualTo("#223344");
        assertThat(currentProject.getStartDate()).isEqualTo(LocalDate.now().minusDays(5).format(DAY_MONTH_YEAR_FORMATTER));
        assertThat(currentProject.getEndDate()).isEqualTo(LocalDate.now().plusDays(15).format(DAY_MONTH_YEAR_FORMATTER));
        //Test that the name changelog with the milestones corresponding to the present moment is selected
        assertThat(currentProject.getProjectName()).isEqualTo("Current project Phase 1");
        //Test that only the changelog value with milestones corresponding to the present moment are selected and the correct gemeenterol version is used
        assertThat(currentProject.getMunicipalityRole().size()).isEqualTo(2);
        assertThat(currentProject.getMunicipalityRole().get(0)).isEqualTo("Role 1 new");
        assertThat(currentProject.getMunicipalityRole().get(1)).isEqualTo("Role 3");
        assertThat(currentProject.getProjectPhase()).isEqualTo(ProjectPhase._1_INITIATIEFFASE.name());
        //Test that only the changelog value with milestones corresponding to the present moment are selected and values are sorted alphabetically
        assertThat(currentProject.getPlanType().size()).isEqualTo(2);
        assertThat(currentProject.getPlanType().get(0)).isEqualTo(PlanType.HERSTRUCTURERING.name());
        assertThat(currentProject.getPlanType().get(1)).isEqualTo(PlanType.PAND_TRANSFORMATIE.name());
        assertThat(currentProject.getMunicipality().size()).isEqualTo(2);
        assertThat(currentProject.getMunicipality().get(0)).isEqualTo("Gemeente 1");
        assertThat(currentProject.getMunicipality().get(1)).isEqualTo("Gemeente 2");

        ProjectListModel futureProject = projects.get(1);
        assertThat(futureProject.getConfidentialityLevel()).isEqualTo(Confidentiality.EXTERN_RAPPORTAGE);
        assertThat(futureProject.getProjectColor()).isEqualTo("#456456");
        assertThat(futureProject.getStartDate()).isEqualTo(LocalDate.now().plusDays(10).format(DAY_MONTH_YEAR_FORMATTER));
        assertThat(futureProject.getEndDate()).isEqualTo(LocalDate.now().plusDays(20).format(DAY_MONTH_YEAR_FORMATTER));
        //Test that the name changelog closest to the present moment is selected
        assertThat(futureProject.getProjectName()).isEqualTo("Future project Phase 1");
        //Test that only the gemeenterol_changelog values with the change_end_date null and with the closest milestone to the beginning of the project are selected
        assertThat(futureProject.getMunicipalityRole().size()).isEqualTo(1);
        assertThat(futureProject.getMunicipalityRole().get(0)).isEqualTo("Role 2");
        assertThat(futureProject.getProjectPhase()).isEqualTo(ProjectPhase._1_INITIATIEFFASE.name());
        //Test that only the changelog values with the closest milestone to the beginning of the project are selected
        assertThat(futureProject.getPlanType().size()).isEqualTo(1);
        assertThat(futureProject.getPlanType().get(0)).isEqualTo(PlanType.UITBREIDING_OVERIG.name());
        assertThat(futureProject.getMunicipality().size()).isEqualTo(1);
        assertThat(futureProject.getMunicipality().get(0)).isEqualTo("Gemeente 1");
    }
}
