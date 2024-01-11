package com.vng.dal;

import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Stream;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.metamodel.Metamodel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.hibernate.tool.schema.TargetType;

public class DalFactory implements AutoCloseable {
    private static Logger logger = LogManager.getLogger();

    private SessionFactory sessionFactory;

    private Metadata metadata;

    public DalFactory(Map<String, String> env, List<Class<? extends Object>> entities) {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));

        logger.info("Building session factory for config file ");

        this.metadata = createMetadata(env, entities);
        sessionFactory = metadata.buildSessionFactory();

        logger.info("Built session factory");
    }

    /**
     * Set configuration for DAL (data access layer) object.
     *
     * @return a Dal
     */
    public Dal constructDal() {
        Session session = sessionFactory.withOptions()
                .jdbcTimeZone(TimeZone.getTimeZone("UTC"))
                .openSession();
        return new Dal(session);
    }

    private Metadata createMetadata(Map<String, String> env, List<Class<? extends Object>> entities) {
        var standardServiceRegistryBuilder = new StandardServiceRegistryBuilder();
        standardServiceRegistryBuilder.applySetting(AvailableSettings.JDBC_TIME_ZONE, TimeZone.getTimeZone("UTC"));

        var settings = Map.of(
                "hibernate.dialect", env.getOrDefault("hibernate_dialect", "org.hibernate.dialect.PostgreSQLDialect"),
                "hibernate.connection.driver_class", env.getOrDefault("hibernate_connection_driver_class", "org.postgresql.Driver"),
                "hibernate.connection.url", env.getOrDefault("hibernate_connection_url", "jdbc:postgresql://localhost:5432/vng"),
                "hibernate.connection.username", env.getOrDefault("hibernate_connection_username", "postgres"),
                "hibernate.connection.password", env.getOrDefault("hibernate.connection.password", "postgres"),
                "connection_pool_size", env.getOrDefault("connection_pool_size", "1"),
                "hibernate.show_sql", env.getOrDefault("hibernate_show_sql", "true"));

        standardServiceRegistryBuilder.applySettings(new HashMap<>(settings));

        StandardServiceRegistry registry = standardServiceRegistryBuilder.build();
        MetadataSources sources = new MetadataSources(registry);
        if (entities != null) {
            for (Class<? extends Object> entity : entities) {
                sources.addAnnotatedClass(entity);
            }
        }
        return sources.buildMetadata();
    }

    public void exportSchema() {
        SchemaExport schemaExport = new SchemaExport();

        schemaExport.setDelimiter(";");
        schemaExport.setOutputFile("create.sql");
        schemaExport.createOnly(EnumSet.of(TargetType.SCRIPT), metadata);
    }

    public Stream<String> getTableNames() {
        Metamodel m = ((EntityManagerFactory) sessionFactory).getMetamodel();
        return m.getEntities()
                .stream()
                .filter(e -> {
                    int modifiers = e.getJavaType().getModifiers();
                    return !Modifier.isAbstract(modifiers);
                })
                .map(e -> e.getName());
    }

    @Override
    public void close() {
        sessionFactory.close();
    }
}
