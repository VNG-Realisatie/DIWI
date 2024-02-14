package nl.vng.diwi.security;

import java.util.Map;

public class MailConfig {
    protected MailConfig() {}
    public MailConfig(Map<String, String> env) {
        username = env.get("MAIL_USERNAME");
        password = env.get("MAIL_PASSWORD");
        baseUrl = env.get("BASE_URL");
        host = env.get("MAIL_SMTP_HOST");
        port = Integer.valueOf(env.get("MAIL_SMTP_PORT"));
        auth = Boolean.valueOf(env.get("MAIL_SMTP_AUTH"));
        starttlsEnable = Boolean.valueOf(env.get("MAIL_SMTP_STARTTLS"));
        sslEnable = Boolean.valueOf(env.get("MAIL_SMTP_SSL"));
    }

    public String username;
    public String password;
    public String baseUrl;

    public String host;
    public Integer port;

    public Boolean auth;
    public Boolean starttlsEnable;
    public Boolean sslEnable;
}
