package nl.vng.diwi.rest;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import nl.vng.diwi.models.ErrorResponse;

@Provider
public class VngServerErrorException extends Exception implements ExceptionMapper<VngServerErrorException> {

    public VngServerErrorException() {
    }

    public VngServerErrorException(String message) {
        super(message);
    }

    @Override
    public Response toResponse(VngServerErrorException exception) {
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(new ErrorResponse(exception.getMessage()))
                .type(MediaType.APPLICATION_JSON_TYPE).build();
    }
}
