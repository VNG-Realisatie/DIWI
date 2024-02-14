package com.vng.rest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.glassfish.hk2.api.Factory;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.process.internal.RequestScoped;
import org.hibernate.Session;

import com.vng.config.ProjectConfig;
import com.vng.dal.Dal;
import com.vng.dal.DalFactory;
import com.vng.dal.GenericRepository;
import com.vng.security.LoggedUser;
import com.vng.services.KeycloakService;
import com.vng.services.KeycloakService.KeycloakPermissionException;
import com.vng.services.MilestoneService;
import com.vng.services.ProjectService;
import com.vng.services.UserService;
import com.vng.services.VngService;

import jakarta.inject.Inject;
import jakarta.ws.rs.container.ContainerRequestContext;

public class VngDependencyInjection extends AbstractBinder implements AutoCloseable {

    static Logger logger = LogManager.getLogger();

    static class SessionFactory implements Factory<Session> {
        private DalFactory dalFactory;

        public SessionFactory(DalFactory dalFactory) {
            this.dalFactory = dalFactory;
        }

        @Override
        public Session provide() {
            return dalFactory.constructDal().getSession();
        }

        @Override
        public void dispose(Session instance) {
            instance.close();

        }
    }

    static class GenericRepoFactory implements Factory<GenericRepository> {
        private DalFactory dalFactory;

        public GenericRepoFactory(DalFactory factory) {
            this.dalFactory = factory;
        }


        @Override
        public GenericRepository provide() {
            Dal dal = dalFactory.constructDal();
            return new GenericRepository(dal);
        }

        @Override
        public void dispose(GenericRepository instance) {
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
    private KeycloakService keycloakService;

    public VngDependencyInjection(DalFactory factory, ProjectConfig projectConfig) throws KeycloakPermissionException {
        super();
        this.dalFactory = factory;
        this.projectConfig = projectConfig;
        this.keycloakService = new KeycloakService(projectConfig);
    }

    @Override
    protected void configure() {
        bind(projectConfig).to(ProjectConfig.class);
        bind(dalFactory).to(DalFactory.class);
        bind(keycloakService).to(KeycloakService.class);

        bindFactory(new SessionFactory(dalFactory)).to(Session.class).in(RequestScoped.class);
        bindFactory(new GenericRepoFactory(dalFactory)).to(GenericRepository.class).in(RequestScoped.class);
        bindFactory(LoggedUserFactory.class).to(LoggedUser.class).in(RequestScoped.class);

        bind(new MilestoneService(projectConfig)).to(MilestoneService.class);
        bind(new ProjectService(projectConfig)).to(ProjectService.class);
        bind(new VngService(projectConfig)).to(VngService.class);
//        bind(new MailService(projectConfig.getMailConfig())).to(MailService.class);

        bind(UserService.class).to(UserService.class);
    }

    @Override
    public void close() throws Exception {
        keycloakService.close();
    }

}
