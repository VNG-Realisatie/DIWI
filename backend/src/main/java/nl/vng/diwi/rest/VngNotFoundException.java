package nl.vng.diwi.rest;

import nl.vng.diwi.models.ErrorResponse;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import lombok.extern.log4j.Log4j2;

@Log4j2
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
        log.debug("VngNotFoundException", exception);
        return Response.status(Response.Status.NOT_FOUND)
                .entity(new ErrorResponse(exception.getMessage()))
                .type(MediaType.APPLICATION_JSON_TYPE).build();
    }
}
