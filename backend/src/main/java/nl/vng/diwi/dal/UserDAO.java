package nl.vng.diwi.dal;

import java.util.List;
import java.util.UUID;

import org.hibernate.Session;

import jakarta.inject.Inject;
import nl.vng.diwi.dal.entities.User;
import nl.vng.diwi.dal.entities.UserState;

public class UserDAO extends AbstractRepository {
    @Inject
    public UserDAO(Session session) {
        super(session);
    }

    public UserState getUserById(UUID uuid) {
        return session
                .createQuery("FROM UserState " +
                        "WHERE user.id = :userId AND changeEndDate is null", UserState.class)
                .setParameter("userId", uuid)
                .uniqueResult();
    }

    public List<UserState> getAllUsers() {
        return session
            .createQuery("FROM UserState us " +
                "WHERE changeEndDate is null ORDER BY us.lastName ASC ", UserState.class)
            .list();
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
