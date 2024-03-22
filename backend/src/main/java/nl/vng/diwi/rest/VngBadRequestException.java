package nl.vng.diwi.rest;

import nl.vng.diwi.models.ErrorResponse;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Provider
public class VngBadRequestException extends Exception implements ExceptionMapper<VngBadRequestException> {

    public VngBadRequestException() {
    }

    public VngBadRequestException(String message) {
        super(message);
    }

    public VngBadRequestException(String message, Exception ex) {
        super(message, ex);
    }

    @Override
    public Response toResponse(VngBadRequestException exception) {
        log.debug("VngBadRequestException", exception);

        return Response.status(Response.Status.BAD_REQUEST)
                .entity(new ErrorResponse(exception.getMessage()))
                .type(MediaType.APPLICATION_JSON_TYPE).build();
    }
}
