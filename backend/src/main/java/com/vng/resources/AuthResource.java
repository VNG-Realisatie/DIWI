package com.vng.resources;

import java.net.URI;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;

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
