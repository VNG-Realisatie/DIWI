package nl.vng.diwi.resources;

import java.net.URI;

import org.pac4j.core.engine.DefaultCallbackLogic;
import org.pac4j.jee.context.JEEFrameworkParameters;

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
        DefaultCallbackLogic callbackLogic = new DefaultCallbackLogic();
        callbackLogic.perform(projectConfig.getPac4jConfig(), projectConfig.getBaseUrl(), null, null, new JEEFrameworkParameters(httpRequest, httpResponse));
        return Response.ok().build();    }

    @GET
    @Path("/loggedIn")
    public Response loggedIn() {
        return Response.ok().build();
    }

    @GET
    @Path("/logout")
    public Response logout(@Context HttpServletRequest request) throws ServletException {
        request.logout();
        return Response.temporaryRedirect(URI.create(projectConfig.getBaseUrl())).build();
    }
}
