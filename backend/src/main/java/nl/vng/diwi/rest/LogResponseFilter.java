package nl.vng.diwi.rest;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.ext.Provider;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Provider
public class LogResponseFilter implements ContainerResponseFilter {
    private static Logger logger = LogManager.getLogger();

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) {
        logger.info("{} {} {}",
                requestContext.getUriInfo().getAbsolutePath(),
                requestContext.getMethod(),
                responseContext.getStatus());
    }
}
