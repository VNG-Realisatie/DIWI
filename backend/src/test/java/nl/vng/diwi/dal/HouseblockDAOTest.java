package nl.vng.diwi.dal;

import nl.vng.diwi.dal.entities.HouseblockSnapshotSqlModel;
import nl.vng.diwi.dal.entities.PropertyCategoryValueState;
import nl.vng.diwi.dal.entities.enums.MutationType;
import nl.vng.diwi.dal.entities.enums.OwnershipType;
import nl.vng.diwi.testutil.TestDb;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class HouseblockDAOTest {

    private static DalFactory dalFactory;
    private static TestDb testDb;
    private Dal dal;
    private VngRepository repo;

    private static final String HOUSEBLOCKS_SCRIPT_PATH = "nl/vng/diwi/HouseblockDAOTest/getHouseblocks.sql";

    @BeforeAll
    static void beforeAll() throws Exception {
        testDb = new TestDb();
        testDb.reset();
        dalFactory = testDb.getDalFactory();
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
        dal = dalFactory.constructDal();
        repo = new VngRepository(dal.getSession());
    }

    @AfterEach
    void afterEach() {
        dal.close();
    }

    @ParameterizedTest
    @ValueSource(ints = { -10, -3, 3 })
    public void getHouseblocksByProjectUuidTest(int startDateOffset) throws Exception {

        UUID projectUuid = CustomUuidGenerator.generateUUIDv7();
        LocalDate startDate = LocalDate.now().plusDays(startDateOffset);
        LocalDate endDate = startDate.plusDays(5);

        try (var transaction = dal.beginTransaction()) {
            Path scriptPath = Path.of(HouseblockDAOTest.class.getClassLoader().getResource(HOUSEBLOCKS_SCRIPT_PATH).getPath());
            String scriptSql = Files.readString(scriptPath);
            String formattedSql = scriptSql.replace("_projectUuid_", projectUuid.toString())
                .replace("_startDate_", startDate.toString())
                .replace("_endDate_", endDate.toString());
            dal.getSession().createNativeMutationQuery(formattedSql).executeUpdate();
            transaction.commit();
        }

        List<HouseblockSnapshotSqlModel> houseblocks = repo.getHouseblockDAO().getHouseblocksByProjectUuid(projectUuid);

        assertThat(houseblocks.size()).isEqualTo(1);

        HouseblockSnapshotSqlModel hb = houseblocks.get(0);
        assertThat(hb.getStartDate()).isEqualTo(startDate);
        assertThat(hb.getEndDate()).isEqualTo(endDate);
        assertThat(hb.getProjectId()).isEqualTo(projectUuid);
        assertThat(hb.getHouseblockName()).isEqualTo("Woningblok 1");
        assertThat(hb.getSizeValue()).isNull();
        assertThat(hb.getSizeValueRange().lower()).isEqualTo(BigDecimal.valueOf(11.1));
        assertThat(hb.getSizeValueRange().upper()).isEqualTo(BigDecimal.valueOf(22.2));
        assertThat(hb.getProgramming()).isTrue();
        assertThat(hb.getMutationKind()).isEqualTo(MutationType.CONSTRUCTION);
        assertThat(hb.getMutationAmount()).isEqualTo(25);
        assertThat(hb.getOwnershipValueList().size()).isEqualTo(2);

        HouseblockSnapshotSqlModel.OwnershipValueSqlModel koop = hb.getOwnershipValueList().stream()
            .filter(o -> o.getOwnershipType().equals(OwnershipType.KOOPWONING)).findFirst().orElse(null);
        assertThat(koop).isNotNull();
        assertThat(koop.getOwnershipAmount()).isEqualTo(5);
        assertThat(koop.getOwnershipValue()).isEqualTo(10);
        assertThat(koop.getOwnershipRentalValue()).isEqualTo(20);

        HouseblockSnapshotSqlModel.OwnershipValueSqlModel huur = hb.getOwnershipValueList().stream()
            .filter(o -> o.getOwnershipType().equals(OwnershipType.HUURWONING_WONINGCORPORATIE)).findFirst().orElse(null);
        assertThat(huur).isNotNull();
        assertThat(huur.getOwnershipAmount()).isEqualTo(5);
        assertThat(huur.getOwnershipValueRangeMin()).isEqualTo(10);
        assertThat(huur.getOwnershipValueRangeMax()).isEqualTo(12);
        assertThat(huur.getOwnershipRentalValueRangeMin()).isEqualTo(8);
        assertThat(huur.getOwnershipRentalValueRangeMax()).isEqualTo(9);

        assertThat(hb.getNoPermissionOwner()).isEqualTo(30);
        assertThat(hb.getIntentionPermissionOwner()).isEqualTo(20);
        assertThat(hb.getFormalPermissionOwner()).isEqualTo(10);

        List<PropertyCategoryValueState> paVals = repo.getPropertyDAO().getCategoryStatesByPropertyName("physicalAppearance");
        Map<String, UUID> paMap = paVals.stream().collect(Collectors.toMap(PropertyCategoryValueState::getLabel, cs -> cs.getCategoryValue().getId()));
        assertThat(hb.getPhysicalAppearanceList().stream().filter(pa -> pa.getId().equals(paMap.get("Tussenwoning"))).findFirst().get().getAmount()).isEqualTo(5);
        assertThat(hb.getPhysicalAppearanceList().stream().filter(pa -> pa.getId().equals(paMap.get("Twee onder een kap"))).findFirst().get().getAmount()).isEqualTo(35);
        assertThat(hb.getPhysicalAppearanceList().stream().filter(pa -> pa.getId().equals(paMap.get("Portiekflat"))).findFirst().get().getAmount()).isEqualTo(55);
        assertThat(hb.getPhysicalAppearanceList().stream().filter(pa -> pa.getId().equals(paMap.get("Hoekwoning"))).findFirst().get().getAmount()).isEqualTo(15);
        assertThat(hb.getPhysicalAppearanceList().stream().filter(pa -> pa.getId().equals(paMap.get("Vrijstaand"))).findFirst().get().getAmount()).isEqualTo(25);
        assertThat(hb.getPhysicalAppearanceList().stream().filter(pa -> pa.getId().equals(paMap.get("Gallerijflat"))).findFirst().get().getAmount()).isEqualTo(45);

        assertThat(hb.getMeergezinswoning()).isEqualTo(67);
        assertThat(hb.getEengezinswoning()).isEqualTo(103);

        List<PropertyCategoryValueState> tgVals = repo.getPropertyDAO().getCategoryStatesByPropertyName("targetGroup");
        Map<String, UUID> tgMap = tgVals.stream().collect(Collectors.toMap(PropertyCategoryValueState::getLabel, cs -> cs.getCategoryValue().getId()));
        assertThat(hb.getTargetGroupList().stream().filter(tg -> tg.getId().equals(tgMap.get("Regulier"))).findFirst().get().getAmount()).isEqualTo(3);
        assertThat(hb.getTargetGroupList().stream().filter(tg -> tg.getId().equals(tgMap.get("Jongeren"))).findFirst().get().getAmount()).isEqualTo(6);
        assertThat(hb.getTargetGroupList().stream().filter(tg -> tg.getId().equals(tgMap.get("Student"))).findFirst().get().getAmount()).isEqualTo(9);
        assertThat(hb.getTargetGroupList().stream().filter(tg -> tg.getId().equals(tgMap.get("Ouderen"))).findFirst().get().getAmount()).isEqualTo(12);
        assertThat(hb.getTargetGroupList().stream().filter(tg -> tg.getId().equals(tgMap.get("Grote gezinnen"))).findFirst().get().getAmount()).isEqualTo(18);
        assertThat(hb.getTargetGroupList().stream().filter(tg -> tg.getId().equals(tgMap.get("GHZ"))).findFirst().get().getAmount()).isEqualTo(15);
    }
}
