package com.vng.dal;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.configuration.FluentConfiguration;

public class Database {
    private static Logger logger = LogManager.getLogger();

    public static void create(DbManipulator.DbInfo dbInfo) {
        DbManipulator.createEmptyDbStatic(dbInfo.dbName, dbInfo.dbUser);
        upgrade(System.getenv("hibernate_connection_url"), System.getenv("hibernate_connection_username"), System.getenv("hibernate_connection_password"));
    }

    /**
     * Upgrade db to the latest schema.
     */
    public static void upgrade(String url, String username, String password) {
        upgrade (url, username, password, null);
    }

    public static void upgrade(String url, String username, String password, String version) {
        logger.info("Upgrading db");
        FluentConfiguration configuration = Flyway
                .configure()
                .dataSource(url, username, password)
                .baselineOnMigrate(true)
                .outOfOrder(true);
        if (version != null) {
            configuration= configuration.target(version);
        }
        Flyway flyway = configuration
                .load();
        flyway.migrate();
        logger.info("Db upgrade complete");
    }

    public static void repair(DbManipulator.DbInfo dbInfo) {
        logger.info("Repairing db");
        Flyway flyway = Flyway.configure()
                .dataSource("jdbc:postgresql://localhost:5432/" + dbInfo.dbName, dbInfo.dbUser, dbInfo.dbPassword)
                .load();

        // Start the migration
        flyway.repair();
        logger.info("Db repair complete");

    }

    public static void baseline(DbManipulator.DbInfo dbInfo) {
        Flyway flyway = Flyway.configure()
                .dataSource("jdbc:postgresql://localhost:5432/" + dbInfo.dbName, dbInfo.dbUser, dbInfo.dbPassword)
                .baselineVersion("1.6.0")
                .load();

        flyway.baseline();

    }
}
