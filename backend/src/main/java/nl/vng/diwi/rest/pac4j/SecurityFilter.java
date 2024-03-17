package nl.vng.diwi.rest.pac4j;

import java.io.IOException;

import org.pac4j.core.authorization.authorizer.DefaultAuthorizers;
import org.pac4j.core.config.Config;
import org.pac4j.core.engine.DefaultSecurityLogic;
import org.pac4j.core.exception.TechnicalException;
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
import nl.vng.diwi.rest.VngServerErrorException;

@Log4j2
@Priority(Priorities.AUTHENTICATION)
public class SecurityFilter implements ContainerRequestFilter {

    @Context
    private HttpServletRequest httpRequest;

    @Context
    private HttpServletResponse httpResponse;

    private ProjectConfig config;

    @Inject
    public SecurityFilter(ProjectConfig config) {
        this.config = config;
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
        try {
            DefaultSecurityLogic securityLogic = new DefaultSecurityLogic();
            securityLogic.perform(pac4jConfig, (ctx, sessionStore, profiles) -> "AUTH_GRANTED", null, DefaultAuthorizers.IS_AUTHENTICATED, null,
                    new JEEFrameworkParameters(httpRequest, httpResponse));
        } catch (TechnicalException | NullPointerException e) {
            log.info("config: {}", config);
            throw new VngServerErrorException("Server error", e);
        }
    }

}
