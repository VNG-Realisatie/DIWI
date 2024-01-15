package com.vng.config;

import java.util.Map;

import com.vng.security.MailConfig;

public class ProjectConfig {
    protected String baseUrl;
    private String kcResourceName;
    private String kcAuthServerUrl;
    private String kcRealmName;
    private String kcSecret;
    private MailConfig mailConfig;

    protected ProjectConfig() {
    }

    public ProjectConfig(Map<String, String> env) {
        this.baseUrl = env.get("BASE_URL");

        this.kcAuthServerUrl = env.get("KC_AUTH_SERVER_URL");
        this.kcRealmName = env.get("KC_REALM_NAME");
        this.kcResourceName = env.get("KC_RESOURCE_NAME");
        this.kcSecret = env.get("KC_SECRET");

        this.mailConfig = new MailConfig(env);
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public String getKcResourceName() {
        return kcResourceName;
    }

    public String getKcAuthServerUrl() {
        return kcAuthServerUrl;
    }

    public String getKcSecret() {
        return kcSecret;
    }

    public String getKcRealmName() {
        return kcRealmName;
    }

    public MailConfig getMailConfig() {
        return mailConfig;
    }

}
