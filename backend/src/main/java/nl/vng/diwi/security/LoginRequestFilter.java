package nl.vng.diwi.security;

import java.io.IOException;

import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;

import nl.vng.diwi.dal.DalFactory;
import nl.vng.diwi.services.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import nl.vng.diwi.config.ProjectConfig;

@Priority(Priorities.AUTHENTICATION)
public class LoginRequestFilter implements ContainerRequestFilter {
    static Logger logger = LogManager.getLogger();

    private HttpServletRequest servletRequest;
    private DalFactory dalFactory;
    private ProjectConfig config;
    private UserService userService;

    @Inject
    public LoginRequestFilter(@Context HttpServletRequest servletRequest,
            DalFactory dalFactory,
            ProjectConfig config,
            UserService userService) {
        super();
        this.servletRequest = servletRequest;
        this.dalFactory = dalFactory;
        this.config = config;
        this.userService = userService;
    }

    @Override
    public void filter(final ContainerRequestContext requestContext) throws IOException {
//        Principal userPrincipal = servletRequest.getUserPrincipal();
//        UUID uuid = UUID.fromString(userPrincipal.getName());
//        if (config.getRequiredRole() != null && !servletRequest.isUserInRole(config.getRequiredRole())) {
//            logger.error("User with id '{}' needs role '{}' to be able to access application", uuid, config.getRequiredRole());
//            accessDenied(requestContext);
//            return;
//        }
//
//        HttpSession session = servletRequest.getSession();
//        var account = (OidcKeycloakAccount) session.getAttribute(KeycloakAccount.class.getName());
//        var securityContext = account.getKeycloakSecurityContext();
//        AccessToken token = securityContext.getToken();
//        String email = token.getEmail();
//        String name = token.getPreferredUsername();
//
//        try (Dal dal = dalFactory.constructDal();) {
//            var repo = new SecurityRepository(dal.getSession());
//            var resource = token.getResourceAccess(config.getKcResourceName());
//            if (resource == null) {
//                // If user doesn't have any roles at all they are not allowed
//                return;
//            }
//            UserRole userRole = UserService.getHighestRole(resource.getRoles()); // here
//            if (userRole == null) {
//                // If the user doesn't have any valid roles they don't have access
//                return;
//            }
//
//            User user = userService.updateUserFromOidc(repo, uuid, name, email, userRole);
//            if (user == null) {
//                return;
//            }

            User user = new User();
            user.setId(3L);
            user.setRole(UserRole.Admin);
            LoggedUser loggedUser = new LoggedUser(user);
            requestContext.setProperty("loggedUser", loggedUser);
            final LoginContext context = new LoginContext(loggedUser);
            requestContext.setSecurityContext(context);
//        }
    }

    private void accessDenied(final ContainerRequestContext requestContext) {
        requestContext.abortWith(
                Response
                        .status(Response.Status.FORBIDDEN)
                        .build());
    }
}
