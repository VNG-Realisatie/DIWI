package nl.vng.diwi.security;

import java.util.List;

public enum UserRole {
    // User that only configures the system, but does not use it.
    // Should NOT have access to projects.
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
        UserAction.EDIT_ALL_PROJECTS,
        UserAction.CREATE_NEW_PROJECT,
        UserAction.IMPORT_PROJECTS,
        UserAction.EXPORT_PROJECTS,
        UserAction.VIEW_DASHBOARDS,
        UserAction.VIEW_GOALS,
        UserAction.EDIT_GOALS)),

    // Basic user that can access only relevant projects and edit only those.
    User(List.of(
        UserAction.VIEW_GROUPS,
        UserAction.VIEW_CONFIG,
        UserAction.VIEW_CUSTOM_PROPERTIES,
        UserAction.CAN_OWN_PROJECTS,
        UserAction.VIEW_OTHERS_PROJECTS,
        UserAction.VIEW_OWN_PROJECTS,
        UserAction.EDIT_OWN_PROJECTS,
        UserAction.CREATE_NEW_PROJECT,
        UserAction.VIEW_DASHBOARDS,
        UserAction.VIEW_GOALS)),

    // Identical to Council currently, might be able to own/edit in future?
    Management(List.of(
        UserAction.VIEW_GROUPS,
        UserAction.VIEW_CONFIG,
        UserAction.VIEW_CUSTOM_PROPERTIES,
        UserAction.VIEW_OTHERS_PROJECTS,
        UserAction.VIEW_DASHBOARDS,
        UserAction.VIEW_GOALS)),

    // User that only needs overviews, does not edit projects.
    Council(List.of(
        UserAction.VIEW_GROUPS,
        UserAction.VIEW_CONFIG,
        UserAction.VIEW_CUSTOM_PROPERTIES,
        UserAction.VIEW_OTHERS_PROJECTS,
        UserAction.VIEW_DASHBOARDS,
        UserAction.VIEW_GOALS)),

    // User that needs to access and edit existing projects, but cannot create them.
    External(List.of(
        UserAction.VIEW_GROUPS,
        UserAction.VIEW_CONFIG,
        UserAction.VIEW_CUSTOM_PROPERTIES,
        UserAction.CAN_OWN_PROJECTS,
        UserAction.VIEW_OWN_PROJECTS,
        UserAction.EDIT_OWN_PROJECTS,
        UserAction.VIEW_GOALS));

    public final List<UserAction> allowedActions;

    UserRole(List<UserAction> allowedActions) {
        this.allowedActions = allowedActions;
    }
}
