package nl.vng.diwi.dal;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.assertj.core.groups.Tuple;
import org.hibernate.Session;
import org.hibernate.query.NativeQuery;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import nl.vng.diwi.generic.ResourceUtil;
import nl.vng.diwi.testutil.TestDb;

public class DatabaseTest {
    private static TestDb testDb;

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
                    new Tuple("Koop 1.6", 10000.0, 20000.0)
            );
        }
    }
}
