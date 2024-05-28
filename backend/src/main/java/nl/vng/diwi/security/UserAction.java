package nl.vng.diwi.security;

/*
 * These constants should be identical to the ones defined in UserActionConstants.
 */
public enum UserAction {
    // User management
    EDIT_USERS,

    // Custom property related
    EDIT_CUSTOM_PROPERTIES,

    // Project related
    CAN_OWN_PROJECTS,
    CHANGE_PROJECT_OWNER,
    VIEW_OTHERS_PROJECTS,
    CREATE_NEW_PROJECT,

    IMPORT_PROJECTS, 
    EXPORT_PROJECTS;
}
