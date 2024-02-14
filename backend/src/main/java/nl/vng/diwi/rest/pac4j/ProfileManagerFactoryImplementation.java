package nl.vng.diwi.rest.pac4j;

import org.pac4j.core.context.WebContext;
import org.pac4j.core.context.session.SessionStore;
import org.pac4j.core.profile.ProfileManager;
import org.pac4j.core.profile.factory.ProfileManagerFactory;


public final class ProfileManagerFactoryImplementation implements ProfileManagerFactory {
    @Override
    public ProfileManager apply(WebContext context, SessionStore store) {
        return new ProfileManager(context, store);
    }
}
