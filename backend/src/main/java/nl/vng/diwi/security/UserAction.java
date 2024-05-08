package nl.vng.diwi.security;

public enum UserAction {
    // User management
    EDIT_USERS(UserActionConstants.EDIT_USERS),

    // Custom property related
    EDIT_CUSTOM_PROPERTIES(UserActionConstants.EDIT_CUSTOM_PROPERTIES),

    // Project related
    CAN_OWN_PROJECTS(UserActionConstants.CAN_OWN_PROJECTS),
    CHANGE_PROJECT_OWNER(UserActionConstants.CHANGE_PROJECT_OWNER),
    VIEW_OTHERS_PROJECTS(UserActionConstants.VIEW_OTHERS_PROJECTS),
    CREATE_NEW_PROJECT(UserActionConstants.CREATE_NEW_PROJECT),

    IMPORT_PROJECTS(UserActionConstants.IMPORT_PROJECTS), 
    EXPORT_PROJECTS(UserActionConstants.EXPORT_PROJECTS);

    public final String name;

    UserAction(String name) {
        this.name = name;
    }
}
