package com.vng.rest;

import java.nio.charset.Charset;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import com.vng.resources.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;

import com.vng.config.ProjectConfig;
import com.vng.dal.DalFactory;
import com.vng.dal.Database;
import com.vng.dal.GenericRepository;
import com.vng.security.LoginRequestFilter;
import com.vng.security.UserResource;

import jakarta.annotation.PreDestroy;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.ws.rs.ApplicationPath;

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
        register(LoginRequestFilter.class);
        register(RolesAllowedDynamicFeature.class);
        register(MultiPartFeature.class);
        register(CORSFilter.class);
        register(VngBadRequestException.class);

        // Then the end points
        register(VngResource.class);
        register(AuthResource.class);
        register(UserResource.class);
        register(ProjectsResource.class);
        register(MunicipalityResource.class);
        register(MunicipalityRoleResource.class);
        register(BuurtResource.class);
        register(WijkResource.class);
        register(PriorityResource.class);

        // Flyway migrations
        Database.upgrade(projectConfig.getDbUrl(), projectConfig.getDbUser(), projectConfig.getDbPass());

    }

    @PreDestroy
    public void destroy() {
        logger.info("Destroy Vng Application servlet name {}, context path {}", config.getServletName(),
                config.getServletContext().getContextPath());

        if (dependencyInjection != null) {
            try {
                dependencyInjection.close();
            } catch (Exception e) {
                logger.info("Error stopping services", e);
            }
        }

        logger.info("Destroy Vng Application servlet name {}, context path {} complete", config.getServletName(),
                config.getServletContext().getContextPath());
    }
}
