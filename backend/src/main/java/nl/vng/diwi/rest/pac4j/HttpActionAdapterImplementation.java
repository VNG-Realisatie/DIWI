package nl.vng.diwi.rest.pac4j;

import java.io.IOException;

import org.pac4j.core.context.WebContext;
import org.pac4j.core.exception.http.HttpAction;
import org.pac4j.core.exception.http.WithContentAction;
import org.pac4j.core.exception.http.WithLocationAction;
import org.pac4j.core.http.adapter.HttpActionAdapter;
import org.pac4j.jee.context.JEEContext;

/**
 * This is where the action is taken. E.g. redirect to keycloak Different
 * actions match different http responses.
 */
public final class HttpActionAdapterImplementation implements HttpActionAdapter {

    @Override
    public Object adapt(HttpAction action, WebContext context) {
        if (action == null) {
            throw new RuntimeException();
        }

        var webContext = (JEEContext) context;
        var response = webContext.getNativeResponse();

        int code = action.getCode();
        if (code >= 400) {
            try {
                response.sendError(code);
                return null;
            } catch (IOException e) {
                throw new RuntimeException("Failed to redirect");
            }
        }

        response.setStatus(code);

        if (action instanceof WithLocationAction withLocationAction) {
            try {
                response.sendRedirect(withLocationAction.getLocation());
                return null;
            } catch (IOException e) {
                throw new RuntimeException("Failed to redirect");
            }
        }

        if (action instanceof WithContentAction withContentAction) {
            final String content = (withContentAction).getContent();
            try {
                response.getOutputStream().print(content);
                return null;
            } catch (IOException e) {
                throw new RuntimeException("Failed to write response");
            }
        }

        return null;
    }
}
