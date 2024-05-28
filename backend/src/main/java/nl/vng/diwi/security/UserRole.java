package nl.vng.diwi.security;

import java.util.List;

public enum UserRole {
    Admin(List.of(
        UserAction.VIEW_API,
        UserAction.VIEW_USERS,
        UserAction.EDIT_USERS,
        UserAction.VIEW_GROUPS,
        UserAction.EDIT_GROUPS,
        UserAction.VIEW_CONFIG,
        UserAction.EDIT_CONFIG,
        UserAction.CHANGE_PROJECT_OWNER)),

    // Currently has everything! 
    UserPlus(List.of(
            UserAction.VIEW_API,
            UserAction.VIEW_USERS,
            UserAction.EDIT_USERS,
            UserAction.VIEW_GROUPS,
            UserAction.EDIT_GROUPS,
            UserAction.VIEW_CONFIG,
            UserAction.EDIT_CONFIG,
            UserAction.VIEW_CUSTOM_PROPERTIES,
            UserAction.EDIT_CUSTOM_PROPERTIES,
            UserAction.CAN_OWN_PROJECTS,
            UserAction.VIEW_OTHERS_PROJECTS,
            UserAction.VIEW_OWN_PROJECTS,
            UserAction.EDIT_OWN_PROJECTS,
            UserAction.CREATE_NEW_PROJECT,
            UserAction.IMPORT_PROJECTS,
            UserAction.EXPORT_PROJECTS)),

    User(List.of(
            UserAction.VIEW_CONFIG,
            UserAction.VIEW_CUSTOM_PROPERTIES,
            UserAction.CAN_OWN_PROJECTS,
            UserAction.VIEW_OTHERS_PROJECTS,
            UserAction.VIEW_OWN_PROJECTS,
            UserAction.EDIT_OWN_PROJECTS,
            UserAction.CREATE_NEW_PROJECT)),

    Management(List.of(
            UserAction.VIEW_CONFIG,
            UserAction.VIEW_CUSTOM_PROPERTIES,
            UserAction.VIEW_OTHERS_PROJECTS)),

    Council(List.of(
            UserAction.VIEW_CONFIG,
            UserAction.VIEW_CUSTOM_PROPERTIES,
            UserAction.VIEW_OTHERS_PROJECTS)),

    External(List.of(
            UserAction.VIEW_CONFIG,
            UserAction.VIEW_CUSTOM_PROPERTIES,
            UserAction.CAN_OWN_PROJECTS,
            UserAction.VIEW_OWN_PROJECTS,
            UserAction.EDIT_OWN_PROJECTS));

    public final List<UserAction> allowedActions;

    UserRole(List<UserAction> allowedActions) {
        this.allowedActions = allowedActions;
    }
}
