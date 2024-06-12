package nl.vng.diwi.services;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;

public class KeycloakService {
    // Class scope
    private static Logger logger = LogManager.getLogger();

    private Keycloak keycloak;
    private RealmResource diwiRealm;
    
    public KeycloakService(String url, String realmName, String clientName, String secret) {
        this.keycloak=KeycloakBuilder.builder()
                .serverUrl(url)
                .realm(realmName)
                .clientId(clientName)
                .clientSecret(secret)
                .grantType(OAuth2Constants.CLIENT_CREDENTIALS).build();  
        this.diwiRealm = this.keycloak.realm(realmName);
    }

    public boolean createUser() {
        // TODO: implement sending a user resource to keycloak for creation
        // temp example code how to access users
        logger.info("Fetching user count from keycloak...");
        var usercount = this.diwiRealm.users().count();
        logger.info("KC user count is " + usercount);        
        return false;
    }
    
    public boolean updateUser() {
        // TODO: implement updating an existing keycloak user with settings from diwi
        return false;
    }    
}
