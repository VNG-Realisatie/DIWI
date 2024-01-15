package com.vng.testutil;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.hibernate.Session;

import com.vng.dal.Dal;
import com.vng.dal.DalFactory;
import com.vng.dal.Database;
import com.vng.dal.GenericRepository;

public class TestDb implements AutoCloseable {
    private DalFactory dalFactory;
    private Map<String, String> env;

    private String jdbcUrl;
    private String username;
    private String password;

    public TestDb() throws Exception {
        env = new HashMap<>(System.getenv());
        env.putIfAbsent("hibernate_connection_url", "jdbc:postgresql://localhost:5432/vng_test");
        env.putIfAbsent("hibernate_connection_password", "postgres");
        env.putIfAbsent("hibernate_connection_username", "postgres");

        jdbcUrl = env.get("hibernate_connection_url");
        username = env.get("hibernate_connection_username");
        password = env.get("hibernate_connection_password");

        dalFactory = new DalFactory(env, GenericRepository.getEntities());

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
                session.createNativeQuery("DROP SCHEMA IF EXISTS \"public\" CASCADE").executeUpdate();
                transaction.commit();
            }
            try (var transaction = dal.beginTransaction();) {
                session.createNativeQuery("CREATE SCHEMA \"public\"").executeUpdate();
                transaction.commit();
            }
        }
        dalFactory.close();

        dalFactory = new DalFactory(env, GenericRepository.getEntities());
        Database.upgrade(getJdbcUrl(), getUsername(), getPassword(), version);
    }

    public DalFactory getDalFactory() {
        return dalFactory;
    }

    public Connection getJdbcConnection() throws SQLException {
        return DriverManager.getConnection(getJdbcUrl(), getUsername(), getPassword());
    }

    public String getJdbcUrl() {
        return jdbcUrl;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

}
