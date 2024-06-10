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

    private Object entity;

    public VngBadRequestException() {
    }

    public VngBadRequestException(Object entity) {
        this.entity = entity;
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

        var response =  Response.status(Response.Status.BAD_REQUEST)
            .type(MediaType.APPLICATION_JSON_TYPE);

        if (exception.entity != null) {
            response.entity(exception.entity);
        } else {
            response.entity(new ErrorResponse(exception.getMessage()));
        }

         return response.build();
    }
}
