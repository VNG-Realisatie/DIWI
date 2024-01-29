package com.vng.config;

import java.text.MessageFormat;
import java.util.Map;

import lombok.Data;

@Data
public class ProjectConfig {
    private final String baseUrl;
    private final String kcResourceName;
    private final String kcAuthServerUrl;
    private final String kcRealmName;
    private final String kcSecret;
//    private final MailConfig mailConfig;
    private final String dbHost;
    private final String dbName;
    private final String  dbUser;
    private final String  dbPass ;

    public ProjectConfig(Map<String, String> env) {
        this.baseUrl = env.get("BASE_URL");

        this.kcAuthServerUrl = env.get("KC_AUTH_SERVER_URL");
        this.kcRealmName = env.get("KC_REALM_NAME");
        this.kcResourceName = env.get("KC_RESOURCE_NAME");
        this.kcSecret = env.get("KC_SECRET");
        this.dbHost = env.getOrDefault("DIWI_DB_HOST", "localhost");
        this.dbName = env.getOrDefault("DIWI_DB_NAME", "diwi");
        this.dbUser = env.getOrDefault("DIWI_DB_USERNAME", "diwi");
        this.dbPass = env.getOrDefault("DIWI_DB_PASSWORD", "diwi");
//        this.mailConfig = new MailConfig(env);
    }

    public String getDbUrl() {
        return  MessageFormat.format("jdbc:postgresql://{0}:5432/{1}", getDbHost(),
                getDbName());
    }

    }
