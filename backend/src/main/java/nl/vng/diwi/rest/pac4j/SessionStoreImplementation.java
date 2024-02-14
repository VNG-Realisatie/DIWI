package nl.vng.diwi.rest.pac4j;

import java.util.Optional;

import org.apache.commons.lang3.NotImplementedException;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.context.session.SessionStore;

import jakarta.servlet.http.HttpSession;

final class SessionStoreImplementation implements SessionStore {

    private HttpSession getSession(WebContext context) {
        assert context instanceof WebContextImplementation;
        return ((WebContextImplementation) context).getRequest().getSession();
    }

    @Override
    public void set(WebContext context, String key, Object value) {
        if (value == null) {
            getSession(context).removeAttribute(key);
        } else {
            getSession(context).setAttribute(key, value);
        }
    }

    @Override
    public boolean renewSession(WebContext context) {
        throw new NotImplementedException();
    }

    @Override
    public Optional<Object> getTrackableSession(WebContext context) {
        return Optional.ofNullable(getSession(context));
    }

    @Override
    public Optional<String> getSessionId(WebContext context, boolean createSession) {
        HttpSession session = getSession(context);
        return (session != null) ? Optional.of(session.getId()) : Optional.empty();
    }

    @Override
    public Optional<Object> get(WebContext context, String key) {
        HttpSession session = getSession(context);
        return Optional.ofNullable(session.getAttribute(key));
    }

    @Override
    public boolean destroySession(WebContext context) {
        final HttpSession session = getSession(context);
        session.invalidate();
        return true;
    }

    @Override
    public Optional<SessionStore> buildFromTrackableSession(WebContext context, Object trackableSession) {
        throw new NotImplementedException();
    }
}
