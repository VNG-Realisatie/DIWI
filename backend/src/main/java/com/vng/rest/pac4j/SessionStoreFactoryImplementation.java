package com.vng.rest.pac4j;

import org.pac4j.core.context.FrameworkParameters;
import org.pac4j.core.context.session.SessionStore;
import org.pac4j.core.context.session.SessionStoreFactory;

public final class SessionStoreFactoryImplementation implements SessionStoreFactory {
    @Override
    public SessionStore newSessionStore(FrameworkParameters parameters) {
        return new SessionStoreImplementation();
    }
}
