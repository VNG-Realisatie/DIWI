package nl.vng.diwi.resources;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

import nl.vng.diwi.services.export.ArcGisProjectExporter;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.StreamingOutput;
import nl.vng.diwi.dal.AutoCloseTransaction;
import nl.vng.diwi.dal.Dal;
import nl.vng.diwi.dal.DalFactory;
import nl.vng.diwi.dal.GenericRepository;
import nl.vng.diwi.dal.VngRepository;
import nl.vng.diwi.dal.entities.DataExchangeType;
import nl.vng.diwi.dal.entities.User;
import nl.vng.diwi.dal.entities.UserGroup;
import nl.vng.diwi.dal.entities.UserState;
import nl.vng.diwi.generic.ResourceUtil;
import nl.vng.diwi.models.DataExchangeExportModel;
import nl.vng.diwi.models.DataExchangeModel;
import nl.vng.diwi.security.LoggedUser;
import nl.vng.diwi.security.UserRole;
import nl.vng.diwi.services.DataExchangeService;
import nl.vng.diwi.services.ExcelImportService;
import nl.vng.diwi.services.GeoJsonImportService;
import nl.vng.diwi.services.HouseblockService;
import nl.vng.diwi.services.ProjectService;
import nl.vng.diwi.services.PropertiesService;
import nl.vng.diwi.services.UserGroupService;
import nl.vng.diwi.testutil.ProjectsUtil;
import nl.vng.diwi.testutil.TestDb;

public class DataExchangeResourceTest {
    private static final ObjectMapper MAPPER = new ObjectMapper();
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
    private PropertiesService propertiesService;
    private DataExchangeResource dataExchangeResource;
    private GenericRepository genericRepository;
    private DataExchangeService dataExchangeService;
    private ArcGisProjectExporter arcGisProjectExporter;

    @BeforeEach
    void beforeEach() {
        dal = dalFactory.constructDal();

        repo = new VngRepository(dal.getSession());
        genericRepository = new GenericRepository(dal);
        projectResource = new ProjectsResource(genericRepository,
                new ProjectService(), new HouseblockService(), new UserGroupService(null), new PropertiesService(), testDb.projectConfig,
                new ExcelImportService(), new GeoJsonImportService());
        projectResource.getUserGroupService().setUserGroupDAO(repo.getUsergroupDAO());
        blockResource = new HouseblockResource(genericRepository, new HouseblockService(), new ProjectService(), new PropertiesService());
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

        arcGisProjectExporter = new ArcGisProjectExporter();
        dataExchangeService = new DataExchangeService(arcGisProjectExporter);
        dataExchangeResource = new DataExchangeResource(genericRepository, dataExchangeService, testDb.projectConfig);
    }

    @AfterEach
    void afterEach() {
        dal.close();
    }

    @EnumSource(names = { "GEO_JSON" })
    @ParameterizedTest
    public void exportGeoJSON(DataExchangeType type) throws Exception {
        LocalDate startDate = LocalDate.of(2024, 12, 30);
        LocalDate endDate = LocalDate.of(2025, 1, 19);

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

        DataExchangeModel model = new DataExchangeModel()
                .withName("geojson")
                .withType(type)
                .withValid(true);

        var dataExchangeModel = dataExchangeResource.createDataExchange(model, loggedUser);

        // We need to update it to set the valid flag
        dataExchangeResource.updateDataExchange(dataExchangeModel.getId(), dataExchangeModel, loggedUser);

        DataExchangeExportModel exportModel = new DataExchangeExportModel();
        exportModel.setProjectIds(List.of(fixture.getProject().getProjectId()));
        StreamingOutput stream = dataExchangeResource.downloadProjects(dataExchangeModel.getId(), exportModel, loggedUser);
        var result = new ByteArrayOutputStream();
        stream.write(result);
        String actualString = result.toString();
        var actualTree = MAPPER.readTree(actualString);

        var expected = ResourceUtil.getResourceAsString("DataExchangeResourceTest/" + type.toString() + ".geojson");
        var expectedTree = MAPPER.readTree(expected);

        assertThat(
                actualTree.toPrettyString()
                        .lines()
                        .filter(l -> !l.contains("diwi_id"))
                        .collect(Collectors.joining("\n")))
                .isEqualToIgnoringWhitespace(expectedTree.toPrettyString());
    }

    @EnumSource(names = { "GDB_GELDERLAND" })
    @ParameterizedTest
    public void create(DataExchangeType type) throws Exception {
        LocalDate startDate = LocalDate.of(2024, 12, 30);
        LocalDate endDate = LocalDate.of(2025, 1, 19);

        // Create the initial project and block
        ProjectsUtil.createTestProject(
                userGroup,
                userState,
                startDate,
                endDate,
                dal,
                testDb.projectConfig,
                repo,
                loggedUser);

        DataExchangeModel model = new DataExchangeModel()
                .withName(type.name())
                .withType(type)
                .withValid(true);

        var dataExchangeModel = dataExchangeResource.createDataExchange(model, loggedUser);
        assertThat(dataExchangeModel).isNotNull();
    }
}
