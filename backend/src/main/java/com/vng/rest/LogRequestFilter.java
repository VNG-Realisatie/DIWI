package com.vng.rest;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.ext.Provider;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Provider
public class LogRequestFilter implements ContainerRequestFilter {
    private static Logger logger = LogManager.getLogger();

    @Override
    public void filter(ContainerRequestContext requestContext) {
        logger.info("{} {}",
                requestContext.getUriInfo().getAbsolutePath(),
                requestContext.getMethod());
    }

}
