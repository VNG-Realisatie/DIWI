package nl.vng.diwi.rest;

import nl.vng.diwi.services.ExcelImportService;
import nl.vng.diwi.services.GeoJsonImportService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.glassfish.hk2.api.Factory;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.process.internal.RequestScoped;
import org.hibernate.Session;

import jakarta.inject.Inject;
import jakarta.ws.rs.container.ContainerRequestContext;
import lombok.extern.log4j.Log4j2;
import nl.vng.diwi.config.ProjectConfig;
import nl.vng.diwi.dal.Dal;
import nl.vng.diwi.dal.DalFactory;
import nl.vng.diwi.dal.GenericRepository;
import nl.vng.diwi.dal.UserDAO;
import nl.vng.diwi.security.LoggedUser;
import nl.vng.diwi.services.PropertiesService;
import nl.vng.diwi.services.HouseblockService;
import nl.vng.diwi.services.MilestoneService;
import nl.vng.diwi.services.UserGroupService;
import nl.vng.diwi.services.ProjectService;

public class VngDependencyInjection extends AbstractBinder {

    static Logger logger = LogManager.getLogger();

    @Log4j2
    static public class SessionFactory implements Factory<Session> {

        private DalFactory dalFactory;

        @Inject
        public SessionFactory(DalFactory dalFactory) {
            this.dalFactory = dalFactory;
        }

        @Override
        public Session provide() {
            log.debug("Construct DAL");
            return dalFactory.constructDal().getSession();
        }

        @Override
        public void dispose(Session instance) {
            instance.close();

        }
    }

    @Log4j2
    public static class LoggedUserFactory implements Factory<LoggedUser> {
        private final ContainerRequestContext context;

        @Inject
        public LoggedUserFactory(ContainerRequestContext context) {
            this.context = context;
        }

        @Override
        public LoggedUser provide() {
            log.debug("Getting LoggedUser");
            return (LoggedUser) context.getProperty("loggedUser");
        }

        @Override
        public void dispose(LoggedUser instance) {
            // nothing to dispose of in a LoggedUser
        }
    }

    private DalFactory dalFactory;
    private ProjectConfig projectConfig;

    public VngDependencyInjection(DalFactory factory, ProjectConfig projectConfig) {
        super();
        this.dalFactory = factory;
        this.projectConfig = projectConfig;
    }

    @Override
    protected void configure() {
        bind(projectConfig).to(ProjectConfig.class);
        bind(dalFactory).to(DalFactory.class);

        bindFactory(SessionFactory.class).to(Session.class).in(RequestScoped.class);
        bind(Dal.class).to(Dal.class).in(RequestScoped.class);
        bind(GenericRepository.class).to(GenericRepository.class).in(RequestScoped.class);
        bind(UserDAO.class).to(UserDAO.class).in(RequestScoped.class);

        bindFactory(LoggedUserFactory.class).to(LoggedUser.class).in(RequestScoped.class);

        bind(new MilestoneService()).to(MilestoneService.class);
        bind(new ProjectService()).to(ProjectService.class);
        bind(new UserGroupService()).to(UserGroupService.class);
        bind(new HouseblockService()).to(HouseblockService.class);
        bind(new PropertiesService()).to(PropertiesService.class);
        bind(new ExcelImportService()).to(ExcelImportService.class);
        bind(new GeoJsonImportService()).to(GeoJsonImportService.class);

    }
}
