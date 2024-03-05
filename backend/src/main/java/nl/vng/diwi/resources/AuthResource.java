package nl.vng.diwi.resources;

import java.net.URI;

import org.pac4j.core.engine.DefaultCallbackLogic;
import org.pac4j.core.engine.DefaultLogoutLogic;
import org.pac4j.jee.context.JEEFrameworkParameters;
import org.pac4j.oidc.exceptions.OidcMissingSessionStateException;
import org.pac4j.oidc.exceptions.OidcStateMismatchException;

import nl.vng.diwi.config.ProjectConfig;

import jakarta.inject.Inject;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;

@Path("/auth")
public class AuthResource {

    @Context
    private HttpServletRequest httpRequest;
    @Context
    private HttpServletResponse httpResponse;

    ProjectConfig projectConfig;

    @Inject
    public AuthResource(ProjectConfig projectConfig) {
        this.projectConfig = projectConfig;
    }

    @GET
    @Path("/login")
    public Response login() {
        return Response.temporaryRedirect(URI.create(projectConfig.getBaseUrl())).build();
    }

    @GET
    @Path("/callback")
    public Response callback() {
        try {
            DefaultCallbackLogic callbackLogic = new DefaultCallbackLogic();
            callbackLogic.perform(projectConfig.getPac4jConfig(), projectConfig.getBaseUrl(), null, null,
                    new JEEFrameworkParameters(httpRequest, httpResponse));
            return Response.ok().build();
        } catch (OidcMissingSessionStateException | OidcStateMismatchException e) {
            // In this case the Keycloak or the back end server might have restarted between
            // the redirect to Keycloak and entering the password or the user might just have an used an old open tab to login and the state
            // has gone stale
            // Redirect to base URL to start a clean attempt
            return Response.temporaryRedirect(URI.create(projectConfig.getBaseUrl())).build();
        }
    }

    @GET
    @Path("/loggedIn")
    public Response loggedIn() {
        return Response.ok().build();
    }

    @GET
    @Path("/logout")
    public Response logout(@Context HttpServletRequest request) throws ServletException {
        DefaultLogoutLogic logoutLogic = new DefaultLogoutLogic();

        logoutLogic.perform(projectConfig.getPac4jConfig(), projectConfig.getBaseUrl(), null, true, true, projectConfig.isPac4jCentralLogout(),
                new JEEFrameworkParameters(httpRequest, httpResponse));

        return Response.ok().build();
    }
}
