package nl.vng.diwi.services;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.UserRepresentation;

import jakarta.ws.rs.core.Response.Status.Family;
import nl.vng.diwi.models.UserModel;

public class KeycloakService {
    // Class scope
    private static Logger logger = LogManager.getLogger();

    private Keycloak keycloak;
    private RealmResource diwiRealm;
    private UsersResource keycloakUsers;

    public KeycloakService(String url, String realmName, String clientName, String secret) {
        this.keycloak = KeycloakBuilder.builder().serverUrl(url).realm(realmName).clientId(clientName)
                .clientSecret(secret).grantType(OAuth2Constants.CLIENT_CREDENTIALS).build();
        this.diwiRealm = this.keycloak.realm(realmName);
        this.keycloakUsers = this.diwiRealm.users();
    }

    public UserResource getUserById(String id) {
        return this.keycloakUsers.get(id);
    }

    public UserRepresentation getUserByEmail(String email) {
        List<UserRepresentation> usersByEmail = this.keycloakUsers.searchByEmail(email, true);
        if (usersByEmail.size() > 1) {
            logger.warn("Found multiple keycloak users with email: " + email);
        }
        return usersByEmail.size() >= 1 ? usersByEmail.get(0) : null;
    }

    public UserResource createUser(UserModel newUser) throws AddUserException {
        // make new keycloak user object
        UserRepresentation newKcUser = new UserRepresentation();
        newKcUser.setFirstName(newUser.getFirstName());
        newKcUser.setLastName(newUser.getLastName());
        newKcUser.setEmail(newUser.getEmail());
        newKcUser.setUsername("diwi-" + newUser.getEmail());
        newKcUser.setEnabled(true);
        // send to backend
        var response = keycloakUsers.create(newKcUser);
        if (!response.getStatusInfo().getFamily().equals(Family.SUCCESSFUL)) {
            throw new AddUserException(response);
        }
        // after success return new user
        var userId = CreatedResponseUtil.getCreatedId(response);
        return keycloakUsers.get(userId);
    }

    public boolean updateUser() {
        // TODO: implement updating an existing keycloak user with settings from diwi
        return false;
    }
}
