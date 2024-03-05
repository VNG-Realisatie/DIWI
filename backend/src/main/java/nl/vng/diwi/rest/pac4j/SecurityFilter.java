package nl.vng.diwi.rest.pac4j;

import java.io.IOException;

import org.pac4j.core.config.Config;
import org.pac4j.core.engine.DefaultSecurityLogic;
import org.pac4j.jee.context.JEEFrameworkParameters;

import jakarta.annotation.Priority;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.Context;

@Priority(Priorities.AUTHENTICATION)
public class SecurityFilter implements ContainerRequestFilter {

    @Context
    private HttpServletRequest httpRequest;

    @Context
    private HttpServletResponse httpResponse;

    private Config pac4jConfig;

    public SecurityFilter(Config pac4jConfig) {
        this.pac4jConfig = pac4jConfig;
    }

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        if (Constants.REST_AUTH_CALLBACK.equals(requestContext.getUriInfo().getAbsolutePath().getPath())) {
            return;
        }
        if (Constants.REST_AUTH_LOGOUT.equals(requestContext.getUriInfo().getAbsolutePath().getPath())) {
            return;
        }

        DefaultSecurityLogic securityLogic = new DefaultSecurityLogic();
        securityLogic.perform(pac4jConfig, (ctx, sessionStore, profiles) -> "AUTH_GRANTED", null, null, null,
                new JEEFrameworkParameters(httpRequest, httpResponse));
    }

}
