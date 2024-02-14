package com.vng.testutil;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.hibernate.Session;

import com.vng.config.ProjectConfig;
import com.vng.dal.Dal;
import com.vng.dal.DalFactory;
import com.vng.dal.Database;
import com.vng.dal.GenericRepository;

public class TestDb implements AutoCloseable {
    private DalFactory dalFactory;
    private Map<String, String> env;

    private ProjectConfig projectConfig;


    public TestDb() throws Exception {
        env = new HashMap<>(System.getenv());
        env.putIfAbsent("DIWI_DB_HOST", "localhost");
        env.putIfAbsent("DIWI_DB_NAME", "diwi_test");
        env.putIfAbsent("DIWI_DB_USERNAME", "diwi");
        env.putIfAbsent("DIWI_DB_PASSWORD", "diwi");

        this.projectConfig = new ProjectConfig(env);
        dalFactory = new DalFactory(projectConfig, GenericRepository.getEntities());

        reset();
    }

    @Override
    public void close() throws Exception {
        dalFactory.close();
    }

    public void reset() throws Exception {
        reset(null);
    }

    public void reset(String version) throws Exception {
        try (
                Dal dal = dalFactory.constructDal();
                Session session = dal.getSession();) {
            try (var transaction = dal.beginTransaction();) {
                session.createNativeMutationQuery("DROP SCHEMA IF EXISTS \"public\" CASCADE").executeUpdate();
                session.createNativeMutationQuery("DROP SCHEMA IF EXISTS \"diwi_testset\" CASCADE").executeUpdate();
                transaction.commit();
            }
            try (var transaction = dal.beginTransaction();) {
                session.createNativeMutationQuery("CREATE SCHEMA \"public\"").executeUpdate();
                transaction.commit();
            }
        }
        dalFactory.close();

        dalFactory = new DalFactory(new ProjectConfig(env), GenericRepository.getEntities());
        Database.upgrade(projectConfig.getDbUrl(), projectConfig.getDbUser(), projectConfig.getDbPass(), version);
    }

    public DalFactory getDalFactory() {
        return dalFactory;
    }

    public Connection getJdbcConnection() throws SQLException {
        return DriverManager.getConnection(projectConfig.getDbUrl(), projectConfig.getDbUser(), projectConfig.getDbPass());
    }

}
