package com.vng.rest;

import com.vng.models.ErrorResponse;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class VngBadRequestException extends Exception implements ExceptionMapper<VngBadRequestException> {

    public VngBadRequestException() {
    }

    public VngBadRequestException(String message) {
        super(message);
    }

    @Override
    public Response toResponse(VngBadRequestException exception) {
        return ResponseFactory.jsonClientErrorResponse(new ErrorResponse(exception.getMessage()));
    }
}
