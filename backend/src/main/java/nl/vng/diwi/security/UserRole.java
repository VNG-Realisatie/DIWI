package nl.vng.diwi.security;

import java.util.List;

public enum UserRole {
    Admin(List.of(UserAction.EDIT_CUSTOM_PROPERTIES, UserAction.EDIT_USERS, UserAction.CHANGE_PROJECT_OWNER)),

    UserPlus(List.of(UserAction.EDIT_CUSTOM_PROPERTIES, UserAction.EDIT_USERS, UserAction.CAN_OWN_PROJECTS,
                    UserAction.CHANGE_PROJECT_OWNER, UserAction.CREATE_NEW_PROJECT, UserAction.IMPORT_PROJECTS,
                    UserAction.EXPORT_PROJECTS, UserAction.VIEW_OTHERS_PROJECTS)),

    User(List.of(UserAction.CAN_OWN_PROJECTS, UserAction.CHANGE_PROJECT_OWNER, UserAction.CREATE_NEW_PROJECT)),

    Management(List.of(UserAction.VIEW_OTHERS_PROJECTS)),

    Council(List.of(UserAction.VIEW_OTHERS_PROJECTS)),

    External(List.of(UserAction.CAN_OWN_PROJECTS));

    public final List<UserAction> allowedActions;

    UserRole(List<UserAction> allowedActions) {
        this.allowedActions = allowedActions;
    }
}
