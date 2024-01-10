package com.vng.security;

import java.util.UUID;

import javax.inject.Inject;

import com.vng.dal.AbstractRepository;
import org.hibernate.Session;

public class SecurityRepository extends AbstractRepository {
    @Inject
    public SecurityRepository(Session session) {
        super(session);
    }

    public User getUserByEmail(String email) {
        return session.createQuery("FROM User u WHERE LOWER(u.email) LIKE LOWER(:email)", User.class)
                .setParameter("email", email)
                .uniqueResult();
    }

    public User getUserByUuid(UUID uuid) {
        return session.createQuery("FROM User u WHERE u.uuid = :uuid", User.class)
                .setParameter("uuid", uuid)
                .uniqueResult();
    }
}
