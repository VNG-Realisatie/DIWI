package nl.vng.diwi.services;

import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import nl.vng.diwi.security.SecurityRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import nl.vng.diwi.security.User;
import nl.vng.diwi.security.UserRole;

public class UserService {
    private static final Logger logger = LogManager.getLogger();

    public User updateUserFromOidc(SecurityRepository repo, UUID uuid, String name, String email, UserRole userRole) {
        try (var transaction = repo.beginTransaction()) {
            User user = repo.getUserByUuid(uuid);
            if (user == null && email != null) {
                user = repo.getUserByEmail(email);
            }

            if (user != null && user.getUuid() != null && !user.getUuid().equals(uuid)) {
                logger.error("uuid in database doesn't match uuid in identity provider {} != {}", user.getUuid(), uuid);
                return null;
            }

            boolean persist = false;
            if (user == null) {
                user = new User();
                persist = true;
            }

            if (name != null && !Objects.equals(user.getName(), name)) {
                logger.info("Setting name to {} for user {}", name, email);
                user.setName(name);
                persist = true;
            }
            if (user.getName() == null) {
                user.setName("");
                persist = true;
            }

            if (!Objects.equals(user.getEmail(), email)) {
                user.setEmail(email);
                persist = true;
            }
            if (user.getEmail() == null) {
                user.setEmail("");
                persist = true;
            }

            if (user.getUuid() == null) {
                logger.info("Setting uuid to {} for user {}", uuid, email);
                user.setUuid(uuid);
                persist = true;
            } else if (!Objects.equals(user.getUuid(), uuid)) {
                // The UUID should always match or be null in the db
                // Otherwise there has been some incompatible change in one of the db's
                logger.warn("UUIDS don't match {} != {}", user.getUuid(), uuid);
                return null;
            }

            if (!Objects.equals(user.getRole(), userRole)) {
                logger.info("Setting role to {} for user {}", userRole, email);
                user.setRole(userRole);
                persist = true;
            }

            if (persist) {
                repo.persist(user);
                transaction.commit();
            }
            return user;
        }
    }

    /*
     * Return null if there is no match
     */
    public static UserRole getHighestRole(Set<String> roles) {
        for (var role : UserRole.values()) {
            if (roles.contains(role.toString())) {
                return role;
            }
        }
        return null;
    }
}
