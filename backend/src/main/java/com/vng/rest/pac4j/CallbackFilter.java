package com.vng.rest.pac4j;

import java.io.IOException;

import org.pac4j.core.config.Config;
import org.pac4j.core.context.WebContextFactory;
import org.pac4j.core.engine.DefaultCallbackLogic;

import com.vng.config.ProjectConfig;

import jakarta.annotation.Priority;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.Context;

@Priority(Priorities.AUTHENTICATION)
public class CallbackFilter implements ContainerRequestFilter {
    @Context
    private HttpServletRequest httpRequest;

    private Config pac4jConfig;

    private ProjectConfig projectConfig;

    public CallbackFilter(ProjectConfig projectConfig, Config pac4jConfig) {
        this.projectConfig = projectConfig;
        this.pac4jConfig = pac4jConfig;
    }

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        if (!Constants.REST_AUTH_CALLBACK.equals(requestContext.getUriInfo().getPath())) {
            return;
        }

        WebContextFactory webContextFactory = new WebContextFactoryImplementation(requestContext, httpRequest);
        Config requestOidcConfig = pac4jConfig.withWebContextFactory(webContextFactory);

        DefaultCallbackLogic callbackLogic = new DefaultCallbackLogic();
        callbackLogic.perform(requestOidcConfig, projectConfig.getBaseUrl(), null, null, null);
    }

}
