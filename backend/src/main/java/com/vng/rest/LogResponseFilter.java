package com.vng.rest;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;

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
