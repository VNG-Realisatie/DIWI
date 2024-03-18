package nl.vng.diwi.rest;

import nl.vng.diwi.config.ProjectConfig;
import nl.vng.diwi.security.LoggedUser;
import nl.vng.diwi.services.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.glassfish.hk2.api.Factory;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.process.internal.RequestScoped;
import org.hibernate.Session;

import nl.vng.diwi.dal.DalFactory;
import nl.vng.diwi.dal.GenericRepository;
import nl.vng.diwi.dal.UserDAO;
import nl.vng.diwi.rest.setup.GenericRepoFactory;
import jakarta.inject.Inject;
import jakarta.ws.rs.container.ContainerRequestContext;
import lombok.extern.log4j.Log4j2;

public class VngDependencyInjection extends AbstractBinder {

    static Logger logger = LogManager.getLogger();

    @Log4j2
    static public class SessionFactory implements Factory<Session> {
        private DalFactory dalFactory;

        public SessionFactory(DalFactory dalFactory) {
            this.dalFactory = dalFactory;
        }

        @Override
        public Session provide() {
            log.debug("Creating hibernate session");
            return dalFactory.constructDal().getSession();
        }

        @Override
        public void dispose(Session instance) {
            instance.close();

        }
    }

    public static class LoggedUserFactory implements Factory<LoggedUser> {
        private final ContainerRequestContext context;

        @Inject
        public LoggedUserFactory(ContainerRequestContext context) {
            this.context = context;
        }

        @Override
        public LoggedUser provide() {
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
        // bind(keycloakService).to(KeycloakService.class);

        bindFactory(new SessionFactory(dalFactory)).to(Session.class);
        bindFactory(new GenericRepoFactory(dalFactory)).to(GenericRepository.class);
        bind(UserDAO.class).to(UserDAO.class);
        bindFactory(LoggedUserFactory.class).to(LoggedUser.class).in(RequestScoped.class);

        bind(new MilestoneService()).to(MilestoneService.class);
        bind(new ProjectService()).to(ProjectService.class);
        bind(new OrganizationsService()).to(OrganizationsService.class);
        bind(new HouseblockService()).to(HouseblockService.class);
        bind(new CustomPropertiesService()).to(CustomPropertiesService.class);
        // bind(new MailService(projectConfig.getMailConfig())).to(MailService.class);

    }
}
