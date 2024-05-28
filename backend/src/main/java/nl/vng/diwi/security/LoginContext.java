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
    public boolean isUserInRole(final String userAction) {
        // the function name is a bit strange maybe but @RolesAllowed should contain UserAction 
        // in this function we determine if that action is allowed for the role of the loggedUser
        UserRole role = loggedUser.getRole();
        UserAction action = UserAction.valueOf(userAction);
        return role.allowedActions.contains(action);
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
