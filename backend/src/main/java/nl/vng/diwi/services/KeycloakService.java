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

import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response.Status.Family;
import nl.vng.diwi.models.UserModel;

public class KeycloakService implements AutoCloseable {
    // Class scope
    private static Logger logger = LogManager.getLogger();

    private Keycloak keycloak;
    private RealmResource diwiRealm;
    private UsersResource keycloakUsers;

    @Inject
    public KeycloakService(String url, String realmName, String clientName, String secret) {
        try {
            this.keycloak = KeycloakBuilder.builder().serverUrl(url).realm(realmName).clientId(clientName)
                    .clientSecret(secret).grantType(OAuth2Constants.CLIENT_CREDENTIALS).build();
            this.diwiRealm = this.keycloak.realm(realmName);
            this.keycloakUsers = this.diwiRealm.users();
        } catch (Exception e) {
        }
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
        newKcUser.setUsername(newUser.getEmail());
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

    public void updateUser(String identityProviderId, UserModel updatedUser) throws FindUserException {
        // store the resource for the existing user
        var kcUserResource = keycloakUsers.get(identityProviderId);
        if (kcUserResource == null) {
            throw new FindUserException("Could not find keycloak user with id " + identityProviderId);
        } else {
            // create a copy of the representation and update relevant fields
            var kcUserRepresentation = kcUserResource.toRepresentation();
            kcUserRepresentation.setFirstName(updatedUser.getFirstName());
            kcUserRepresentation.setLastName(updatedUser.getLastName());
            kcUserRepresentation.setEmail(updatedUser.getEmail());

            // save new representation to keycloak, no feedback unfortunately
            kcUserResource.update(kcUserRepresentation);
        }
    }

    @Override
    public void close() throws Exception {
        logger.info("cleaning up keycloak service");
        keycloak.close();
    }
}
