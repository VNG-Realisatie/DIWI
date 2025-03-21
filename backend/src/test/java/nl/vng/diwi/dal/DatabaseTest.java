package nl.vng.diwi.dal;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.*;

import org.assertj.core.groups.Tuple;
import org.hibernate.Session;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import nl.vng.diwi.dal.entities.DataExchange;
import nl.vng.diwi.dal.entities.DataExchangePriceCategoryMapping;
import nl.vng.diwi.dal.entities.DataExchangePriceCategoryMappingState;
import nl.vng.diwi.dal.entities.DataExchangeState;
import nl.vng.diwi.dal.entities.DataExchangeType;
import nl.vng.diwi.dal.entities.Property;
import nl.vng.diwi.dal.entities.PropertyRangeCategoryValue;
import nl.vng.diwi.dal.entities.User;
import nl.vng.diwi.dal.entities.enums.OwnershipCategory;
import nl.vng.diwi.dal.entities.enums.PropertyKind;
import nl.vng.diwi.generic.ResourceUtil;
import nl.vng.diwi.testutil.TestDb;

public class DatabaseTest {
    private static TestDb testDb;

    private static ZonedDateTime now = ZonedDateTime.of(2000, 1, 1, 12, 0, 0, 0, ZoneId.of("UTC"));

    private DataExchangePriceCategoryMappingState dxpms;

    @BeforeAll
    static void beforeAll() throws Exception {
        testDb = new TestDb();
        testDb.reset();
    }

    @AfterAll
    static void afterAll() {
        testDb.close();
    }

    void executeUpdate(String... lines) {
        try (Dal dal = testDb.getDalFactory().constructDal();
                Session session = dal.getSession();
                var transaction = dal.beginTransaction()) {
            for (var line : lines) {
                session.createNativeMutationQuery(line).executeUpdate();
            }
            transaction.commit();
        }
    }

    <T> T persist(T newEntity) {
        try (var dal = testDb.getDalFactory().constructDal();
                var session = dal.getSession();
                var transaction = dal.beginTransaction()) {
            session.persist(newEntity);
            transaction.commit();
        }
        return newEntity;
    }

    @Test
    void dataExchangeState() throws Exception {
        var user = persist(new User());

        var dx = persist(new DataExchange());

        Supplier<DataExchangeState> createDxs = () -> {
            var dxs = new DataExchangeState();
            dxs.setName("dx");
            dxs.setType(DataExchangeType.GEO_JSON);
            dxs.setDataExchange(dx);
            dxs.setCreateUser(user);
            dxs.setChangeStartDate(now);
            dxs.setValid(true);
            return dxs;
        };

        // Test name not null constraints
        assertThatException().isThrownBy(() -> {
            var dxs = createDxs.get();
            dxs.setName(null);
            persist(dxs);
        });

        assertThatException().isThrownBy(() -> {
            var dxs = createDxs.get();
            dxs.setType(null);
            persist(dxs);
        });

        assertThatException().isThrownBy(() -> {
            var dxs = createDxs.get();
            dxs.setDataExchange(null);
            persist(dxs);
        });

        assertThatException().isThrownBy(() -> {
            var dxs = createDxs.get();
            dxs.setCreateUser(null);
            persist(dxs);
        });

        assertThatException().isThrownBy(() -> {
            var dxs = createDxs.get();
            dxs.setChangeStartDate(null);
            persist(dxs);
        });

        assertThatException().isThrownBy(() -> {
            var dxs = createDxs.get();
            dxs.setValid(null);
            persist(dxs);
        });

        // Test correct
        persist(createDxs.get());
    }

    @Test
    void priceCategoryMapping() throws Exception {
        var user = persist(new User());

        var p = new Property();
        p.setType(PropertyKind.FIXED);
        persist(p);

        var prcv = new PropertyRangeCategoryValue();
        prcv.setProperty(p);
        persist(prcv);

        var dx = persist(new DataExchange());

        Supplier<DataExchangePriceCategoryMapping> createDxpm = () -> {
            var entitiy = new DataExchangePriceCategoryMapping();
            entitiy.setDataExchange(dx);
            entitiy.setOwnershipCategory(OwnershipCategory.HUUR1);
            return entitiy;
        };

        assertThatException().isThrownBy(() -> {
            var entity = createDxpm.get();
            entity.setDataExchange(null);
            persist(entity);
        });

        assertThatException().isThrownBy(() -> {
            var entity = createDxpm.get();
            entity.setOwnershipCategory(null);
            persist(entity);
        });

        var dxpm = persist(createDxpm.get());
        Supplier<DataExchangePriceCategoryMappingState> createDxpms = () -> {
            var entitiy = new DataExchangePriceCategoryMappingState();
            entitiy.setDataExchangePriceCategoryMapping(dxpm);
            entitiy.setPriceRange(prcv);
            entitiy.setCreateUser(user);
            entitiy.setChangeStartDate(now);
            return entitiy;
        };

        assertThatException().isThrownBy(() -> {
            var entity = createDxpms.get();
            entity.setDataExchangePriceCategoryMapping(null);
            persist(entity);
        });

        assertThatException().isThrownBy(() -> {
            var entitiy = createDxpms.get();
            entitiy.setPriceRange(null);
            persist(entitiy);
        });

        assertThatException().isThrownBy(() -> {
            var entity = createDxpms.get();
            entity.setCreateUser(null);
            persist(entity);
        });

        assertThatException().isThrownBy(() -> {
            var entity = createDxpms.get();
            entity.setChangeStartDate(null);
            persist(entity);
        });

        dxpms = createDxpms.get();
        persist(dxpms);

        List<DataExchangePriceCategoryMappingState> mapping;
        try (var dal = testDb.getDalFactory().constructDal();
                var session = dal.getSession();
                var transaction = dal.beginTransaction()) {
             mapping = new DataExchangeDAO(session).getDataExchangePriceMappings(dx.getId());
        }
        assertThat(mapping)
                .extracting("id", "priceRange.id", "dataExchangePriceCategoryMapping.ownershipCategory")
                .containsExactly(Tuple.tuple(dxpms.getId(), prcv.getId(), OwnershipCategory.HUUR1));
    }

    @Test
    void testCategoryMigration() throws Exception {
        testDb.clearDB();

        try (Dal dal = testDb.getDalFactory().constructDal();
                Session session = dal.getSession();) {
            var dump = ResourceUtil.getResourceAsString("DatabaseTest/713-price-category-fix-test.sql");
            session.doWork(connection -> {
                connection.createStatement().executeUpdate(dump);
                connection.commit();
            });
        }

        testDb.upgradeToLatest();

        var dalFactory = testDb.getDalFactory();
        try (Dal dal = dalFactory.constructDal();
                Session session = dal.getSession();
                var transaction = dal.beginTransaction()) {

            var result = session.createNativeQuery(
                    "SELECT name, CAST (min AS double precision), CAST (max AS double precision) FROM diwi.property_range_category_value_state ORDER BY name",
                    jakarta.persistence.Tuple.class)
                    .stream()
                    .map(t -> new Tuple(t.get(0), t.get(1), t.get(2)))
                    .toList();
            assertThat(result).contains(
                    new Tuple("Huur: 1000,00", 100000., 100000.),
                    new Tuple("Huur: 100,00 - 200,00", 10000.0, 20000.0),
                    new Tuple("Huur: 200,00 - 300,00", 20000.0, 30000.0),
                    new Tuple("Huur: 300,00 en hoger", 30000.0, null),
                    new Tuple("Huur: 400,00 - 500,00", 40000.0, 50000.0),
                    new Tuple("Huur: 500,00 - 600,00", 50000.0, 60000.0),
                    new Tuple("Huur: 600,00 en hoger", 60000.0, null),
                    new Tuple("Koop: 100,00 - 200,00", 10000.0, 20000.0),
                    new Tuple("Koop: 200,00 - 300,00", 20000.0, 30000.0),
                    new Tuple("Koop: 300,00 en hoger", 30000.0, null),
                    new Tuple("Koop: 1000,00", 100000.0, 100000.0),
                    new Tuple("Huur 1.6", 10000.0, 20000.0),
                    new Tuple("Koop 1.6", 10000.0, 20000.0));
        }
    }
}
