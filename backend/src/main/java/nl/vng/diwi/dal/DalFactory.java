package nl.vng.diwi.dal;

import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Stream;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.metamodel.Metamodel;
import nl.vng.diwi.config.ProjectConfig;
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

    public DalFactory(ProjectConfig projectConfig, List<Class<? extends Object>> entities) {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));

        logger.info("Building session factory for config file ");

        this.metadata = createMetadata(projectConfig, entities);
        sessionFactory = metadata.buildSessionFactory();

        logger.info("Built session factory");
    }

    /**
     * Set configuration for DAL (data access layer) object.
     *
     * @return a Dal
     */
    public Dal constructDal() {
        Session session = sessionFactory.withOptions().jdbcTimeZone(TimeZone.getTimeZone("UTC")).openSession();
        return new Dal(session);
    }

    private Metadata createMetadata(ProjectConfig projectConfig, List<Class<? extends Object>> entities) {
        var standardServiceRegistryBuilder = new StandardServiceRegistryBuilder();
        standardServiceRegistryBuilder.applySetting(AvailableSettings.JDBC_TIME_ZONE, TimeZone.getTimeZone("UTC"));



        var settings = new HashMap<String, String>();
        settings.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
        settings.put("hibernate.connection.driver_class", "org.postgresql.Driver");
        settings.put("hibernate.connection.url", projectConfig.getDbUrl());
        settings.put("hibernate.connection.username", projectConfig.getDbUser());
        settings.put("hibernate.connection.password", projectConfig.getDbPass());
        settings.put("connection_pool_size", "1");
        settings.put("hibernate.show_sql", "false");

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
        return m.getEntities().stream().filter(e -> {
            int modifiers = e.getJavaType().getModifiers();
            return !Modifier.isAbstract(modifiers);
        }).map(e -> e.getName());
    }

    @Override
    public void close() {
        sessionFactory.close();
    }
}
