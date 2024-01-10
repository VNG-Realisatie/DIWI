package com.vng.resources;

import java.net.URI;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

@Path("/auth")
public class AuthResource {
    @GET
    @Path("/login")
    public Response login() {
        return Response.temporaryRedirect(URI.create("/")).build();
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
        return Response.temporaryRedirect(URI.create("/")).build();
    }
}
