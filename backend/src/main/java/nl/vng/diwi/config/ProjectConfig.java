package nl.vng.diwi.config;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.Map;

import org.pac4j.core.authorization.authorizer.DefaultAuthorizers;
import org.pac4j.core.authorization.authorizer.IsAuthenticatedAuthorizer;
import org.pac4j.core.config.Config;
import org.pac4j.core.profile.factory.ProfileManagerFactory;
import org.pac4j.jee.context.JEEContextFactory;
import org.pac4j.jee.context.session.JEESessionStoreFactory;
import org.pac4j.oidc.client.KeycloakOidcClient;
import org.pac4j.oidc.config.KeycloakOidcConfiguration;

import com.nimbusds.oauth2.sdk.auth.ClientAuthenticationMethod;

import lombok.Data;
import lombok.extern.log4j.Log4j2;
import nl.vng.diwi.rest.InvalidConfigException;
import nl.vng.diwi.rest.pac4j.Constants;
import nl.vng.diwi.rest.pac4j.HttpActionAdapterImplementation;

@Data
@Log4j2
public class ProjectConfig {
    private final String baseUrl;

    private final String kcResourceName;
    private final String kcAuthServerUrl;
    private final String kcRealmName;
    private final String kcSecret;

    private final String dbHost;
    private final String dbPort;
    private final String dbName;
    private final String dbUser;
    private final String dbPass;

    private final boolean pac4jCentralLogout;
    private final Config pac4jConfig;

    public ProjectConfig(Map<String, String> env) throws InvalidConfigException {
        this.baseUrl = getURL(env, "BASE_URL");

        if (env.get("KC_AUTH_SERVER_URL") != null) {
            this.kcAuthServerUrl = getURL(env, "KC_AUTH_SERVER_URL");
            this.kcRealmName = getNotNull(env, "KC_REALM_NAME");
            this.kcResourceName = getNotNull(env, "KC_RESOURCE_NAME");
            this.kcSecret = getNotNull(env, "KC_SECRET");
        } else {
            this.kcAuthServerUrl = null;
            this.kcRealmName = null;
            this.kcResourceName = null;
            this.kcSecret = null;
        }

        this.pac4jCentralLogout = Boolean.parseBoolean(env.getOrDefault("PAC4J_CENTRAL_LOGOUT", "true"));

        this.dbHost = env.getOrDefault("DIWI_DB_HOST", "localhost");
        this.dbPort = env.getOrDefault("DIWI_DB_PORT", "5432");
        this.dbName = env.getOrDefault("DIWI_DB_NAME", "diwi");
        this.dbUser = env.getOrDefault("DIWI_DB_USERNAME", "diwi");
        this.dbPass = env.getOrDefault("DIWI_DB_PASSWORD", "diwi");

        if (kcAuthServerUrl != null) {
            final KeycloakOidcConfiguration keycloakConfig = new KeycloakOidcConfiguration();
            keycloakConfig.setBaseUri(this.getKcAuthServerUrl());
            keycloakConfig.setRealm(this.getKcRealmName());
            keycloakConfig.setClientId(this.getKcResourceName());
            keycloakConfig.setSecret(this.getKcSecret());
            keycloakConfig.setClientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_POST);

            KeycloakOidcClient client = new KeycloakOidcClient(keycloakConfig);
            log.info("Discovery URL: {}", client.getConfiguration().getDiscoveryURI());

            var oidcConfig = new Config(this.getBaseUrl() + Constants.REST_AUTH_CALLBACK, client);
            oidcConfig.setProfileManagerFactory(ProfileManagerFactory.DEFAULT);
            oidcConfig.setWebContextFactory(JEEContextFactory.INSTANCE);
            oidcConfig.setSessionStoreFactory(JEESessionStoreFactory.INSTANCE);
            oidcConfig.setHttpActionAdapter(new HttpActionAdapterImplementation());
            oidcConfig.setAuthorizers(Map.of(DefaultAuthorizers.IS_AUTHENTICATED, new IsAuthenticatedAuthorizer()));

            this.pac4jConfig = oidcConfig;
        } else {
            log.warn("Authentication is disabled");
            this.pac4jConfig = null;
        }
    }

    private static String getNotNull(Map<String, String> env, String key) throws InvalidConfigException {
        String value = env.get(key);
        if (value == null) {
            log.error("{} should be a set", key);
            throw new InvalidConfigException("Invalid configuration");
        }
        return value;

    }

    private static String getURL(Map<String, String> env, String key) throws InvalidConfigException {
        String value = env.get(key);
        try {
            new URL(value);
        } catch (MalformedURLException e) {
            log.error("{} should be a valid url, but is: {}", key, value, e);
            throw new InvalidConfigException("Invalid configuration");
        }
        return value;
    }

    public String getDbUrl() {
        return MessageFormat.format("jdbc:postgresql://{0}:{1}/{2}", getDbHost(), getDbPort(), getDbName());
    }
}
