package nl.vng.diwi.dal;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.assertj.core.groups.Tuple;
import org.hibernate.Session;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import nl.vng.diwi.testutil.TestDb;

public class DatabaseTest {
    private static TestDb testDb;


    @BeforeAll
    static void beforeAll() throws Exception {
        testDb = new TestDb();
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
        // Pre migration - V2024.07.17.14.00__DashboardBlueprints
        testDb.upgrade("2024.07.17.14.00");

        executeUpdate(
                "INSERT INTO diwi.user (id, system_user) VALUES ('d9cedd33-a3b2-49b1-8f33-b7315aca84fa', true)",

                "INSERT INTO diwi.property (id, type) VALUES ('560aa6bc-1852-43f1-945d-cb55481b20c3', 'FIXED')",
                "INSERT INTO diwi.property (id, type) VALUES ('fc95b3b7-3592-42b8-971e-b0bebe792c49', 'FIXED')",

                "INSERT INTO diwi.property_range_category_value (id, property_id) VALUES ('c58249ca-0bfa-4efd-be51-cc0bb4a3e809', '560aa6bc-1852-43f1-945d-cb55481b20c3')",
                "INSERT INTO diwi.property_range_category_value (id, property_id) VALUES ('024f0b9d-a7f8-47c1-ab31-7553d3abb087', '560aa6bc-1852-43f1-945d-cb55481b20c3')",
                "INSERT INTO diwi.property_range_category_value (id, property_id) VALUES ('a91a033d-7a4b-4a60-8367-7de7e62cdcf3', '560aa6bc-1852-43f1-945d-cb55481b20c3')",
                "INSERT INTO diwi.property_range_category_value (id, property_id) VALUES ('a78b34a1-9250-4967-8851-f70692677bc0', '560aa6bc-1852-43f1-945d-cb55481b20c3')",
                "INSERT INTO diwi.property_range_category_value (id, property_id) VALUES ('725d22e8-2c7e-4ec5-ac63-857eb85ece0a', 'fc95b3b7-3592-42b8-971e-b0bebe792c49')",
                "INSERT INTO diwi.property_range_category_value (id, property_id) VALUES ('ab6d92a5-32f1-4bb1-aea8-416c32546775', 'fc95b3b7-3592-42b8-971e-b0bebe792c49')",
                "INSERT INTO diwi.property_range_category_value (id, property_id) VALUES ('b6d1c8d7-2d31-4889-bfee-9e0199d6f3c6', 'fc95b3b7-3592-42b8-971e-b0bebe792c49')",
                "INSERT INTO diwi.property_range_category_value (id, property_id) VALUES ('e05e04fe-4670-4db5-acde-f422b95fb1c8', 'fc95b3b7-3592-42b8-971e-b0bebe792c49')",
                "INSERT INTO diwi.property_range_category_value (id, property_id) VALUES ('c34d7e77-e423-4726-b647-06b01ea17b16', 'fc95b3b7-3592-42b8-971e-b0bebe792c49')",
                "INSERT INTO diwi.property_range_category_value (id, property_id) VALUES ('a7f23802-3bfb-47fb-a4fd-4a540f40da00', 'fc95b3b7-3592-42b8-971e-b0bebe792c49')",
                "INSERT INTO diwi.property_range_category_value (id, property_id) VALUES ('ab54fcb9-d0fb-4e0b-a27b-6ddd6834c16a', 'fc95b3b7-3592-42b8-971e-b0bebe792c49')",

                "INSERT INTO diwi.property_range_category_value_state (id, range_category_value_id, name, min, max, create_user_id, change_user_id, change_start_date, change_end_date) VALUES ('0e4ea09b-c0c3-4caf-bd25-dcb255b3c466', 'c58249ca-0bfa-4efd-be51-cc0bb4a3e809', 'Koop: 30000 - 40001', 30000.0, 40001.0, 'd9cedd33-a3b2-49b1-8f33-b7315aca84fa', NULL, '2024-09-10 08:45:36.071733+02', NULL)",
                "INSERT INTO diwi.property_range_category_value_state (id, range_category_value_id, name, min, max, create_user_id, change_user_id, change_start_date, change_end_date) VALUES ('b61c15c3-5779-4c9a-a643-6cd456e3b20e', '024f0b9d-a7f8-47c1-ab31-7553d3abb087', 'Koop: 20000 - 30001', 20000.0, 30001.0, 'd9cedd33-a3b2-49b1-8f33-b7315aca84fa', NULL, '2024-09-10 08:45:36.071733+02', NULL)",
                "INSERT INTO diwi.property_range_category_value_state (id, range_category_value_id, name, min, max, create_user_id, change_user_id, change_start_date, change_end_date) VALUES ('dcb8cb4e-1718-4b9a-9854-2c0f13e93d43', 'a91a033d-7a4b-4a60-8367-7de7e62cdcf3', 'Koop: 10000 - 20001', 10000.0, 20001.0, 'd9cedd33-a3b2-49b1-8f33-b7315aca84fa', NULL, '2024-09-10 08:45:36.071733+02', NULL)",
                "INSERT INTO diwi.property_range_category_value_state (id, range_category_value_id, name, min, max, create_user_id, change_user_id, change_start_date, change_end_date) VALUES ('f4fa8440-aae3-40bc-9df2-bfe1bcce13ba', 'a78b34a1-9250-4967-8851-f70692677bc0', 'Koop: 100000', 100000.0, 100000.0, 'd9cedd33-a3b2-49b1-8f33-b7315aca84fa', NULL, '2024-09-10 08:45:36.071733+02', NULL)",
                "INSERT INTO diwi.property_range_category_value_state (id, range_category_value_id, name, min, max, create_user_id, change_user_id, change_start_date, change_end_date) VALUES ('36163b3a-8c22-4fd4-898c-b6198436e907', '725d22e8-2c7e-4ec5-ac63-857eb85ece0a', 'Huur: 10000 - 20001', 10000.0, 20001.0, 'd9cedd33-a3b2-49b1-8f33-b7315aca84fa', NULL, '2024-09-10 08:45:36.071733+02', NULL)",
                "INSERT INTO diwi.property_range_category_value_state (id, range_category_value_id, name, min, max, create_user_id, change_user_id, change_start_date, change_end_date) VALUES ('fc47684f-8af3-4a00-b06c-cb2b9050d02c', 'ab6d92a5-32f1-4bb1-aea8-416c32546775', 'Huur: 50000 - 60001', 50000.0, 60001.0, 'd9cedd33-a3b2-49b1-8f33-b7315aca84fa', NULL, '2024-09-10 08:45:36.071733+02', NULL)",
                "INSERT INTO diwi.property_range_category_value_state (id, range_category_value_id, name, min, max, create_user_id, change_user_id, change_start_date, change_end_date) VALUES ('49008b1b-4318-4cd0-832f-6d968077b5fa', 'b6d1c8d7-2d31-4889-bfee-9e0199d6f3c6', 'Huur: 40000 - 50001', 40000.0, 50001.0, 'd9cedd33-a3b2-49b1-8f33-b7315aca84fa', NULL, '2024-09-10 08:45:36.071733+02', NULL)",
                "INSERT INTO diwi.property_range_category_value_state (id, range_category_value_id, name, min, max, create_user_id, change_user_id, change_start_date, change_end_date) VALUES ('74dc3161-7f5a-4cfc-8305-6ac1b294c52b', 'e05e04fe-4670-4db5-acde-f422b95fb1c8', 'Huur: 60000 - 70001', 60000.0, 70001.0, 'd9cedd33-a3b2-49b1-8f33-b7315aca84fa', NULL, '2024-09-10 08:45:36.071733+02', NULL)",
                "INSERT INTO diwi.property_range_category_value_state (id, range_category_value_id, name, min, max, create_user_id, change_user_id, change_start_date, change_end_date) VALUES ('caa08110-0c7f-43b9-a7c4-69282de66ccb', 'c34d7e77-e423-4726-b647-06b01ea17b16', 'Huur: 20000 - 30001', 20000.0, 30001.0, 'd9cedd33-a3b2-49b1-8f33-b7315aca84fa', NULL, '2024-09-10 08:45:36.071733+02', NULL)",
                "INSERT INTO diwi.property_range_category_value_state (id, range_category_value_id, name, min, max, create_user_id, change_user_id, change_start_date, change_end_date) VALUES ('0e3244bf-d98d-423c-b8ea-10b518973df2', 'a7f23802-3bfb-47fb-a4fd-4a540f40da00', 'Huur: 30000 - 40001', 30000.0, 40001.0, 'd9cedd33-a3b2-49b1-8f33-b7315aca84fa', NULL, '2024-09-10 08:45:36.071733+02', NULL)",
                "INSERT INTO diwi.property_range_category_value_state (id, range_category_value_id, name, min, max, create_user_id, change_user_id, change_start_date, change_end_date) VALUES ('9623fd62-5075-42a3-aabb-aa96a2421d18', 'ab54fcb9-d0fb-4e0b-a27b-6ddd6834c16a', 'Huur: 100000', 100000.0, 100000.0, 'd9cedd33-a3b2-49b1-8f33-b7315aca84fa', NULL, '2024-09-10 08:45:36.071733+02', NULL)");

        // Post migration -
        // V2024.07.19.14.00__HouseblockOwnershipMigrateToRangeCategories
        testDb.upgrade("2024.07.19.14.00");

        executeUpdate(
                "INSERT INTO diwi.user (id, system_user) VALUES ('0191daa8-72a7-7484-803f-c41451b33e1b', false)",

                "INSERT INTO diwi.property_range_category_value (id, property_id) VALUES ('0191daaf-a540-71f5-9942-09d515058549', '560aa6bc-1852-43f1-945d-cb55481b20c3')",
                "INSERT INTO diwi.property_range_category_value (id, property_id) VALUES ('0191daaf-f08d-7f9b-aaa6-1db056e999a0', 'fc95b3b7-3592-42b8-971e-b0bebe792c49')",

                "INSERT INTO diwi.property_range_category_value_state (id, range_category_value_id, name, min, max, create_user_id, change_user_id, change_start_date, change_end_date) VALUES ('0191daaf-a548-790c-a675-3b7f528e17c0', '0191daaf-a540-71f5-9942-09d515058549', 'Koop 1.6', 100.0, 200.0, '0191daa8-72a7-7484-803f-c41451b33e1b', NULL, '2024-09-10 08:47:09.843901+02', NULL)",
                "INSERT INTO diwi.property_range_category_value_state (id, range_category_value_id, name, min, max, create_user_id, change_user_id, change_start_date, change_end_date) VALUES ('0191daaf-f08d-7d05-9afd-c4b7d59e4037', '0191daaf-f08d-7f9b-aaa6-1db056e999a0', 'Huur 1.6', 100.0, 200.0, '0191daa8-72a7-7484-803f-c41451b33e1b', NULL, '2024-09-10 08:47:29.159038+02', NULL)");

        testDb.upgradeToLatest();

        var dalFactory = testDb.getDalFactory();
        try (Dal dal = dalFactory.constructDal();
                Session session = dal.getSession();
                var transaction = dal.beginTransaction()) {

            var result = session.createNativeQuery(
                    "SELECT name, CAST (min AS double precision), CAST (max AS double precision) FROM diwi.property_range_category_value_state ORDER BY name",
                    List.class)
                    .stream()
                    .map(l -> new Tuple (l.get(0), l.get(1), l.get(2)))
                    .toList();
            assertThat(result).contains(
                    new Tuple("Huur: 1000,00", 100000., 100000.),
                    new Tuple("Huur: 100,00 - 200,01", 10000.0, 20001.0),
                    new Tuple("Huur 1.6", 10000.0, 20000.0),
                    new Tuple("Huur: 200,00 - 300,01", 20000.0, 30001.0),
                    new Tuple("Huur: 300,00 - 400,01", 30000.0, 40001.0),
                    new Tuple("Huur: 400,00 - 500,01", 40000.0, 50001.0),
                    new Tuple("Huur: 500,00 - 600,01", 50000.0, 60001.0),
                    new Tuple("Huur: 600,00 - 700,01", 60000.0, 70001.0),
                    new Tuple("Koop: 1000,00", 100000.0, 100000.0),
                    new Tuple("Koop: 100,00 - 200,01", 10000.0, 20001.0),
                    new Tuple("Koop 1.6", 10000.0, 20000.0),
                    new Tuple("Koop: 200,00 - 300,01", 20000.0, 30001.0),
                    new Tuple("Koop: 300,00 - 400,01", 30000.0, 40001.0)
            );
        }
    }
}
