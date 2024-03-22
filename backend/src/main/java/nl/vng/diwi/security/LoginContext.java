package nl.vng.diwi.security;

import java.security.Principal;
import jakarta.ws.rs.core.SecurityContext;

public class LoginContext implements SecurityContext {

    private LoggedUser loggedUser;

    public LoginContext(LoggedUser loggedUser) {
        this.loggedUser = loggedUser;
    }

    @Override
    public Principal getUserPrincipal() {
        if (loggedUser == null) {
            return null;
        } else {
            return () -> loggedUser.getUuid().toString();
        }
    }

    @Override
    public boolean isUserInRole(final String role) {
        return  loggedUser.getRole().toString().equals(role);
    }

    @Override
    public boolean isSecure() {
        return false;
    }

    @Override
    public String getAuthenticationScheme() {
        return null;
    }
}
