package com.vng.rest;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.ext.Provider;

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
