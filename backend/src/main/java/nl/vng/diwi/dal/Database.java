package nl.vng.diwi.dal;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.configuration.FluentConfiguration;

public class Database {
    private static Logger logger = LogManager.getLogger();

    /**
     * Upgrade db to the latest schema.
     */
    public static void upgrade(String url, String username, String password) {
        upgrade(url, username, password, null);
    }

    public static void upgrade(String url, String username, String password, String version) {
        logger.info("Upgrading db");
        FluentConfiguration configuration = Flyway
            .configure()
            .dataSource(url, username, password)
            .baselineOnMigrate(true)
            .outOfOrder(true);
        if (version != null) {
            configuration = configuration.target(version);
        }
        Flyway flyway = configuration
            .load();
        flyway.migrate();
        logger.info("Db upgrade complete");
    }

    public static void repair(String url, String username, String password) {
        logger.info("Repairing db");
        Flyway flyway = Flyway.configure()
            .dataSource(url, username, password)
            .load();

        // Start the migration
        flyway.repair();
        logger.info("Db repair complete");

    }

    public static void baseline(String url, String username, String password, String version) {
        Flyway flyway = Flyway.configure()
            .dataSource(url, username, password)
            .baselineVersion(version)
            .load();

        flyway.baseline();

    }
}
