package nl.vng.diwi.security;

public enum UserAction {
    ViewCustomProperties(UserActionConstants.VIEW_CUSTOM_PROPERTIES),
    EditCustomProperties(UserActionConstants.EDIT_CUSTOMER_PROPERTIES),
    
    ViewOwnProjects(UserActionConstants.VIEW_OWN_PROJECTS),
    EditOwnProjects(UserActionConstants.EDIT_OWN_PROJECTS),
    
    ViewOthersProjects(UserActionConstants.VIEW_OTHERS_PROJECTS),
    EditOthersProjects(UserActionConstants.EDIT_OTHERS_PROJECTS),
    ChangeOwner(UserActionConstants.CHANGE_OWNER),
    
    ViewUsers(UserActionConstants.VIEW_USERS),
    EditUsers(UserActionConstants.EDIT_USERS),
    
    ImportProjects(UserActionConstants.IMPORT_PROJECTS),
    ExportProjects(UserActionConstants.EXPORT_PROJECTS);
    
    public final String name;

    UserAction(String name) {
        this.name = name;
    }
}
