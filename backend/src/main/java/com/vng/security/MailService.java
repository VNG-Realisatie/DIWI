package com.vng.security;

import java.io.IOException;
import java.util.Properties;

import javax.inject.Inject;
import javax.mail.Authenticator;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.vng.generic.ResourceUtil;

/**
 * Example config for /etc/phinion/mail.json
 *
 * The options starttls and ssl.enable are really important! They should be
 * true.
 *
 * { "username": "noreply@phinion.com", "password": "blabla", "mail.smtp.auth":
 * true, "mail.smtp.starttls.enable": "true", "mail.smtp.ssl.enable": "true",
 * "mail.smtp.host": "smtp.strato.com", "mail.smtp.port": "465",
 * "mail.smtp.ssl.trust": "smtp.strato.com" }
 *
 * There also is a mail.debug option.
 *
 */
public class MailService {
    // Class scope
    private static Logger logger = LogManager.getLogger();

    private static final class AuthenticatorExtension extends Authenticator {
        private String username;
        private String password;

        AuthenticatorExtension(String username, String password) {
            super();
            this.username = username;
            this.password = password;
        }

        @Override
        protected PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication(username, password);
        }
    }

    // Instance scope
    private MailConfig config;
    private Properties props;

    @Inject
    public MailService(MailConfig config) {
        this.config = config;
        this.props = new Properties();
        props.setProperty("mail.smtp.auth", config.auth.toString());
        props.setProperty("mail.smtp.starttls.enable", config.starttlsEnable.toString());
        props.setProperty("mail.smtp.ssl.enable", config.sslEnable.toString());
        props.setProperty("mail.smtp.host", config.host);
        props.setProperty("mail.smtp.port", config.port.toString());
        props.setProperty("mail.smtp.ssl.trust", config.host);
    }

    void sendWelcomeMail(String email) throws MailException {
        try {
            String template = ResourceUtil.getResourceAsString("welcomeMail.html");
            sendMail(template, "Welcome to __BASE_URL__", email);
        } catch (IOException | MessagingException ex) {
            logger.error(ex);
            throw new MailException(ex);
        }
    }

    public void sendMail(String template, String subjectTemplate, String email)
            throws MessagingException {

        final AuthenticatorExtension authenticator = new AuthenticatorExtension(config.username, config.password);
        Session session = Session.getInstance(props, authenticator);

        MimeMessage message = createMessage(subjectTemplate, template, email, session);

        Transport.send(message);
    }

    public MimeMessage createMessage(
            String subjectTemplate,
            String template,
            String email,
            Session session)
            throws MessagingException {
        String msg = applyTemplate(template, config.baseUrl, email);
        String subject = applyTemplate(subjectTemplate, config.baseUrl, email);

        MimeMessage message = new MimeMessage(session);

        message.setFrom("noreply@phinion.com");
        message.addRecipient(RecipientType.TO, new InternetAddress(email));
        message.setSubject(subject);

        MimeBodyPart mimeBodyPart = new MimeBodyPart();
        mimeBodyPart.setContent(msg, "text/html");

        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(mimeBodyPart);

        message.setContent(multipart);
        return message;
    }

    private static String applyTemplate(String template, String baseUrl, String email) {
        return template.replace("__BASE_URL__", baseUrl)
                .replace("__ACCOUNT_NAME__", email);
    }
}
