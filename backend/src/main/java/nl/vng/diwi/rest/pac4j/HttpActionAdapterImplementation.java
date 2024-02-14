package nl.vng.diwi.rest.pac4j;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.pac4j.core.context.HttpConstants;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.exception.http.HttpAction;
import org.pac4j.core.exception.http.WithContentAction;
import org.pac4j.core.exception.http.WithLocationAction;
import org.pac4j.core.http.adapter.HttpActionAdapter;

import jakarta.ws.rs.core.Response;

/**
 * This is where the action is taken. E.g. redirect to keycloak Different
 * actions match different http responses.
 */
public final class HttpActionAdapterImplementation implements HttpActionAdapter {
    static Logger logger = LogManager.getLogger();

    @Override
    public Object adapt(HttpAction action, WebContext context) {
        if (action == null) {
            throw new RuntimeException();
        }

        var webContext = (WebContextImplementation) context;

        var builder = Response.status(action.getCode());
        for (var entry : webContext.getResponseHeaders().entrySet()) {
            builder.header(entry.getKey(), entry.getValue());
        }

        builder.cookie(webContext.getResponseCookie());

        if (action instanceof WithLocationAction) {
            final WithLocationAction withLocationAction = (WithLocationAction) action;
            builder.header(HttpConstants.LOCATION_HEADER, withLocationAction.getLocation());
        } else if (action instanceof WithContentAction) {
            final String content = ((WithContentAction) action).getContent();
            builder.entity(content);
        }

        webContext.getRequestContext().abortWith(builder.build());
        return null;
    }
}
