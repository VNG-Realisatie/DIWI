package nl.vng.diwi.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import nl.vng.diwi.rest.InvalidConfigException;

class ProjectConfigTest {

    private static final Map<String, String> validConfiguration = new HashMap<>();
    static {
        validConfiguration.put("BASE_URL", "http://localhost");
        validConfiguration.put("KC_AUTH_SERVER_URL", "http://localhost");
        validConfiguration.put("KC_REALM_NAME", "value");
        validConfiguration.put("KC_RESOURCE_NAME", "value");
        validConfiguration.put("KC_SECRET", "value");
        validConfiguration.put("DIWI_DB_HOST", "value");
        validConfiguration.put("DIWI_DB_PORT", "value");
        validConfiguration.put("DIWI_DB_NAME", "value");
        validConfiguration.put("DIWI_DB_USERNAME", "value");
        validConfiguration.put("DIWI_DB_PASSWORD", "value");
        validConfiguration.put("MAIL_SMTP_PORT", "465");
        validConfiguration.put("MAIL_SMTP_AUTH", "true");
        validConfiguration.put("MAIL_SMTP_STARTTLS", "true");
        validConfiguration.put("MAIL_SMTP_SSL", "true");
    }

    @Test
    void testValid() throws Exception {
        var config = new ProjectConfig(validConfiguration);

        assertThat(config).isNotNull();
    }

    @ParameterizedTest
    @ValueSource(strings = { "BASE_URL", "KC_AUTH_SERVER_URL" })
    void testInvalidUrl(String key) {
        var env = new HashMap<String, String>(validConfiguration);
        env.put(key, "not a url");

        InvalidConfigException exception = assertThrows(InvalidConfigException.class, () -> {
            new ProjectConfig(env);
        });

        assertThat(exception.getMessage()).contains("Invalid configuration");
    }

    @ParameterizedTest
    @ValueSource(strings = { "KC_REALM_NAME", "KC_RESOURCE_NAME", "KC_SECRET" })
    void testUnsetVAlues(String key) {
        var env = new HashMap<String, String>(validConfiguration);
        env.remove(key);

        InvalidConfigException exception = assertThrows(InvalidConfigException.class, () -> {
            new ProjectConfig(env);
        });

        assertThat(exception.getMessage()).contains("Invalid configuration");
    }
}
