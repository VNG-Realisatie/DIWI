package com.vng.rest.pac4j;

import org.pac4j.core.context.FrameworkParameters;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.context.WebContextFactory;

import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.container.ContainerRequestContext;

final class WebContextFactoryImplementation implements WebContextFactory {
    private ContainerRequestContext requestContext;
    private HttpServletRequest httpRequest;

    @Inject
    public WebContextFactoryImplementation(ContainerRequestContext requestContext, HttpServletRequest httpRequest) {
        this.requestContext = requestContext;
        this.httpRequest = httpRequest;
    }

    @Override
    public WebContext newContext(FrameworkParameters parameters) {
        return new WebContextImplementation(requestContext, httpRequest);
    }
}
