package com.vng.resources;

import java.net.URI;

import com.vng.config.ProjectConfig;

import jakarta.inject.Inject;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;

@Path("/auth")
public class AuthResource {

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
        return Response.temporaryRedirect(URI.create(projectConfig.getBaseUrl())).build();
    }

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
