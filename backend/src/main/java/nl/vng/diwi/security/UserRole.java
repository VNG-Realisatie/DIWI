package nl.vng.diwi.security;

import java.util.List;

public enum UserRole {
    Admin(SecurityRoleConstants.ADMIN, List.of(
            UserAction.EDIT_CUSTOM_PROPERTIES,
            UserAction.EDIT_USERS,
            UserAction.CHANGE_PROJECT_OWNER)),
    
    UserPlus(SecurityRoleConstants.USER_PLUS, List.of(
            UserAction.EDIT_CUSTOM_PROPERTIES,
            UserAction.EDIT_USERS,
            UserAction.CAN_OWN_PROJECTS,
            UserAction.CHANGE_PROJECT_OWNER,
            UserAction.CREATE_NEW_PROJECT,
            UserAction.IMPORT_PROJECTS,
            UserAction.EXPORT_PROJECTS,
            UserAction.VIEW_OTHERS_PROJECTS)),
    
    User(SecurityRoleConstants.USER, List.of(
            UserAction.CAN_OWN_PROJECTS,
            UserAction.CHANGE_PROJECT_OWNER,
            UserAction.CREATE_NEW_PROJECT)),
    
    Management(SecurityRoleConstants.MANAGEMENT, List.of(
            UserAction.VIEW_OTHERS_PROJECTS)),
    
    Council(SecurityRoleConstants.COUNCIL, List.of(
            UserAction.VIEW_OTHERS_PROJECTS)),
    
    External(SecurityRoleConstants.EXTERNAL, List.of(
            UserAction.CAN_OWN_PROJECTS));

    public final String name;
    public final List<UserAction> allowedActions;

    UserRole(String name, List<UserAction> allowedActions) {
        this.name = name;
        this.allowedActions = allowedActions;
    }
}
