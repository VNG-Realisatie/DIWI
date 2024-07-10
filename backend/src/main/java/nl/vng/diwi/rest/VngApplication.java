package nl.vng.diwi.rest;

import java.nio.charset.Charset;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import nl.vng.diwi.resources.AuditResource;
import nl.vng.diwi.resources.ConfigResource;
import nl.vng.diwi.resources.DashboardResource;
import nl.vng.diwi.resources.PropertiesResource;
import nl.vng.diwi.resources.HouseblockResource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.flywaydb.core.api.exception.FlywayValidateException;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;

import jakarta.annotation.PreDestroy;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.ws.rs.ApplicationPath;
import nl.vng.diwi.config.ProjectConfig;
import nl.vng.diwi.dal.DalFactory;
import nl.vng.diwi.dal.Database;
import nl.vng.diwi.dal.GenericRepository;
import nl.vng.diwi.resources.AuthResource;
import nl.vng.diwi.resources.MilestoneResource;
import nl.vng.diwi.resources.ProjectsResource;
import nl.vng.diwi.resources.UserResource;
import nl.vng.diwi.resources.VngOpenApiResource;
import nl.vng.diwi.resources.UserGroupResource;
import nl.vng.diwi.rest.pac4j.SecurityFilter;

@ApplicationPath("rest")
@MultipartConfig(fileSizeThreshold = 0, maxFileSize = -1, maxRequestSize = -1)
public class VngApplication extends ResourceConfig {
    private static Logger logger = LogManager.getLogger();

    private ServletConfig config;
    private DalFactory dalFactory;
    private ProjectConfig projectConfig;

    private VngDependencyInjection dependencyInjection;

    public VngApplication(@jakarta.ws.rs.core.Context ServletConfig config) throws Exception {
        this.config = config;

        Map<String, String> env = System.getenv();
        projectConfig = new ProjectConfig(env);

        ServletContext context = config.getServletContext();

        logger.info("Init Vng Application servlet name {}, context path {}", config.getServletName(),
                context.getContextPath());
        logger.warn("Default locale {}", Locale.getDefault());
        logger.warn("Default charset {}", Charset.defaultCharset());

        // Init
        try {
            dalFactory = new DalFactory(projectConfig, GenericRepository.getEntities());

            Properties prop = new Properties();
            prop.setProperty("org.quartz.threadPool.threadCount", "1");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // Dependency injectors
        dependencyInjection = new VngDependencyInjection(dalFactory, projectConfig);
        register(dependencyInjection);


        // Filters and features
        register(JacksonFeature.class);
        register(LogRequestFilter.class);
        register(LogResponseFilter.class);
        register(SecurityFilter.class);
        // register(LoginRequestFilter.class);
        register(RolesAllowedDynamicFeature.class);
        register(MultiPartFeature.class);
        register(CORSFilter.class);

        // Exceptions for the endpoints
        register(VngNotFoundException.class);
        register(VngBadRequestException.class);
        register(VngServerErrorException.class);
        register(VngNotAllowedException.class);

        // Then the end points
        register(VngOpenApiResource.class);
        register(UserGroupResource.class);
        register(AuthResource.class);
        register(ProjectsResource.class);
        register(MilestoneResource.class);
        register(HouseblockResource.class);
        register(PropertiesResource.class);
        register(ConfigResource.class);
        register(UserResource.class);
        register(DashboardResource.class);
        register(AuditResource.class);

        // Flyway migrations
        try{
            Database.upgrade(projectConfig.getDbUrl(), projectConfig.getDbUser(), projectConfig.getDbPass());
        }
        catch (FlywayValidateException e) {
            logger.error(e.getMessage());
            if (!projectConfig.isAllowFlywayErrors()) {
                throw e;
            }
        }

    }

    @PreDestroy
    public void destroy() {
        logger.info("Destroy Vng Application servlet name {}, context path {}", config.getServletName(),
                config.getServletContext().getContextPath());

        logger.info("Destroy Vng Application servlet name {}, context path {} complete", config.getServletName(),
                config.getServletContext().getContextPath());
    }
}
