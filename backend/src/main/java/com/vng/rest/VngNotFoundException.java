package com.vng.rest;

import com.vng.models.ErrorResponse;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class VngNotFoundException extends Exception implements ExceptionMapper<VngNotFoundException> {

    public VngNotFoundException() {
        super("The requested resource could not be found");
    }

    public VngNotFoundException(String message) {
        super(message);
    }

    @Override
    public Response toResponse(VngNotFoundException exception) {
        return Response.status(Response.Status.NOT_FOUND)
                .entity(new ErrorResponse(exception.getMessage()))
                .type(MediaType.APPLICATION_JSON_TYPE).build();
    }
}
