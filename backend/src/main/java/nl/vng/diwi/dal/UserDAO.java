package nl.vng.diwi.dal;

import org.hibernate.Session;

import jakarta.inject.Inject;
import nl.vng.diwi.dal.entities.User;
import nl.vng.diwi.dal.entities.UserState;

public class UserDAO extends AbstractRepository {
    @Inject
    public UserDAO(Session session) {
        super(session);
    }

    public UserState getUserByIdentityProviderId(String identityProviderId) {
        return session
                .createQuery("FROM UserState " +
                        "WHERE identityProviderId = :identityProviderId AND changeEndDate is null", UserState.class)
                .setParameter("identityProviderId", identityProviderId)
                .uniqueResult();
    }

    public User getSystemUser() {
        return session.createQuery("FROM User WHERE systemUser = TRUE", User.class)
                .setMaxResults(1)
                .uniqueResult();
    }
}
