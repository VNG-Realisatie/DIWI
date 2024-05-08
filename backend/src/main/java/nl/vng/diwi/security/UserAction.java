package nl.vng.diwi.security;

public enum UserAction {
    ViewCustomProperties(UserActionConstants.ViewCustomProperties),
    EditCustomProperties(UserActionConstants.EditCustomProperties),
    
    ViewOwnProjects(UserActionConstants.ViewOwnProjects),
    EditOwnProjects(UserActionConstants.EditOwnProjects),
    
    ViewOthersProjects(UserActionConstants.ViewOthersProjects),
    EditOthersProjects(UserActionConstants.EditOthersProjects),
    ChangeOwner(UserActionConstants.ChangeOwner),
    
    ViewUsers(UserActionConstants.ViewUsers),
    EditUsers(UserActionConstants.EditUsers),
    
    ImportProjects(UserActionConstants.ImportProjects),
    ExportProjects(UserActionConstants.ExportProjects);
    
    public final String name;

    UserAction(String name) {
        this.name = name;
    }
}
