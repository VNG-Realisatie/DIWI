package nl.vng.diwi.rest.pac4j;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.UUID;

import org.hibernate.Session;
import org.pac4j.core.authorization.authorizer.DefaultAuthorizers;
import org.pac4j.core.config.Config;
import org.pac4j.core.engine.DefaultSecurityLogic;
import org.pac4j.core.engine.SecurityGrantedAccessAdapter;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.profile.UserProfile;
import org.pac4j.jee.context.JEEFrameworkParameters;

import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.Context;
import lombok.extern.log4j.Log4j2;
import nl.vng.diwi.config.ProjectConfig;
import nl.vng.diwi.dal.DalFactory;
import nl.vng.diwi.dal.UserGroupDAO;
import nl.vng.diwi.dal.UserDAO;
import nl.vng.diwi.dal.entities.UserGroup;
import nl.vng.diwi.dal.entities.UserGroupState;
import nl.vng.diwi.dal.entities.User;
import nl.vng.diwi.dal.entities.UserState;
import nl.vng.diwi.dal.entities.UserToUserGroup;
import nl.vng.diwi.rest.VngServerErrorException;
import nl.vng.diwi.security.LoggedUser;
import nl.vng.diwi.security.LoginContext;
import nl.vng.diwi.security.UserRole;

@Log4j2
@Priority(Priorities.AUTHENTICATION)
public class SecurityFilter implements ContainerRequestFilter {

    @Context
    private HttpServletRequest httpRequest;

    @Context
    private HttpServletResponse httpResponse;

    private ProjectConfig config;
    private DalFactory dalFactory;

    @Inject
    public SecurityFilter(ProjectConfig config, DalFactory dalFactory) {
        this.config = config;
        this.dalFactory = dalFactory;
    }

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        if (Constants.REST_AUTH_CALLBACK.equals(requestContext.getUriInfo().getAbsolutePath().getPath())) {
            return;
        }
        if (Constants.REST_AUTH_LOGOUT.equals(requestContext.getUriInfo().getAbsolutePath().getPath())) {
            return;
        }

        Config pac4jConfig = config.getPac4jConfig();
        if (pac4jConfig == null) {
            return;
        }

        try {
            SecurityGrantedAccessAdapter securityGrantedAccessAdapter = (ctx, sessionStore, profiles) -> {
                for (var profile : profiles) {
                    var userEntity = getUserForProfile(profile);
                    LoggedUser loggedUser;

                    if (userEntity == null) {
                        loggedUser = new LoggedUser();
                        loggedUser.setFirstName("X");
                        loggedUser.setLastName("X");
                        loggedUser.setUuid(null);
                        loggedUser.setIdentityProviderId(UUID.fromString(profile.getId()));
                    } else {
                        loggedUser = new LoggedUser(userEntity);
                    }
                    requestContext.setProperty("loggedUser", loggedUser);
                    final LoginContext context = new LoginContext(loggedUser);
                    requestContext.setSecurityContext(context);

                    break;
                }
                return null;
            };
            DefaultSecurityLogic securityLogic = new DefaultSecurityLogic();
            securityLogic.perform(pac4jConfig, securityGrantedAccessAdapter, null, DefaultAuthorizers.IS_AUTHENTICATED,
                    null, new JEEFrameworkParameters(httpRequest, httpResponse));
        } catch (TechnicalException | NullPointerException e) {
            log.info("config: {}", config);
            throw new VngServerErrorException("Server error", e);
        }
    }

    private UserState getUserForProfile(UserProfile profile) throws Exception {
        Session session = dalFactory.constructDal().getSession();
        var userDao = new UserDAO(session);
        var userGroupDAO = new UserGroupDAO(session);

        try (var transaction = userDao.beginTransaction()) {
            var profileUuid = profile.getId();

            var firsttName = profile.getAttribute("given_name");
            var lastName = profile.getAttribute("family_name");

            var userEntity = userDao.getUserByIdentityProviderId(profileUuid);
            if (userEntity == null) {
                // user does not exist in diwi database
                // must match role name defined in keycloak!
                var hasDiwiAdminRole = profile.getRoles().contains("diwi-admin");
                if (hasDiwiAdminRole) {
                    ZonedDateTime now = ZonedDateTime.now();
                    User systemUser = userDao.getSystemUser();

                    var newUser = new User();
                    newUser.setSystemUser(false);
                    userDao.persist(newUser);

                    userEntity = new UserState();
                    userEntity.setChangeStartDate(now);
                    userEntity.setCreateUser(systemUser);
                    userEntity.setFirstName((String) firsttName);
                    userEntity.setLastName((String) lastName);
                    userEntity.setUser(newUser);
                    userEntity.setIdentityProviderId(profileUuid);
                    userEntity.setUserRole(UserRole.Admin); // Keycloak users should by default not see projects
                    userDao.persist(userEntity);

                    var group = new UserGroup();
                    group.setSingleUser(true);
                    userGroupDAO.persist(group);

                    var groupState = new UserGroupState();
                    groupState.setChangeStartDate(now);
                    groupState.setCreateUser(systemUser);
                    groupState.setName(userEntity.getFirstName() + " " + userEntity.getLastName());
                    groupState.setUserGroup(group);
                    userGroupDAO.persist(groupState);

                    var groupToUser = new UserToUserGroup();
                    groupToUser.setChangeStartDate(now);
                    groupToUser.setCreateUser(systemUser);
                    groupToUser.setUserGroup(group);
                    groupToUser.setUser(newUser);
                    userGroupDAO.persist(groupToUser);

                    transaction.commit();
                } else {
                    // create empty userstate as we want to block any further actions by this user.
                    userEntity = null;
                }
            }
            return userEntity;

        }
    }
}
