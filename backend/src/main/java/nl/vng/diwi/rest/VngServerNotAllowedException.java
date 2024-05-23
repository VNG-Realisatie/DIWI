package nl.vng.diwi.rest;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import lombok.extern.log4j.Log4j2;
import nl.vng.diwi.models.ErrorResponse;

@Log4j2
@Provider
public class VngServerNotAllowedException extends RuntimeException implements ExceptionMapper<VngServerNotAllowedException> {

    public VngServerNotAllowedException() {
    }

    public VngServerNotAllowedException(String message) {
        super(message);
    }

    public VngServerNotAllowedException(String message, Exception e) {
        super(message, e);
    }

    @Override
    public Response toResponse(VngServerNotAllowedException exception) {
        log.error("server exception", exception);
        return Response.status(Response.Status.FORBIDDEN)
                .entity(new ErrorResponse(exception.getMessage()))
                .type(MediaType.APPLICATION_JSON_TYPE).build();
    }
}
