package nl.vng.diwi.security;

/*
 * These constants should be identical to the ones defined in UserActionConstants.
 */
public enum UserAction {
    VIEW_API,
    VIEW_USERS,
    EDIT_USERS,
    VIEW_GROUPS,
    EDIT_GROUPS,
    VIEW_CONFIG,
    EDIT_CONFIG,
    VIEW_CUSTOM_PROPERTIES,
    EDIT_CUSTOM_PROPERTIES,
    CAN_OWN_PROJECTS,
    CHANGE_PROJECT_OWNER,
    VIEW_OTHERS_PROJECTS,
    VIEW_OWN_PROJECTS,
    EDIT_OWN_PROJECTS,
    EDIT_ALL_PROJECTS,
    CREATE_NEW_PROJECT,
    IMPORT_PROJECTS,
    EXPORT_PROJECTS,
    VIEW_DASHBOARDS
}
