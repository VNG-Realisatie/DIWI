package nl.vng.diwi.security;

/*
 * These values are used to initialize the enum security.dal.entities.UserAction
 */
public final class UserActionConstants {
    /*
     * Used for user management, who can edit all the users in this instance?
     */
    public static final String EDIT_USERS = "EditUsers";
    
    /*
     * Used for managing the custom properties, who can  edit them in this instance? 
     */
    public static final String EDIT_CUSTOM_PROPERTIES = "EditCustomProperties";
    
    /*
     * ANYTHING project related has an additional 'filter' in the form of confidentiality level!
     * This should be implemented in the request that is fetching the specific data as there the 
     * confidentiality information is available.
     */    
    public static final String CAN_OWN_PROJECTS = "CanOwnProjects";
    
    /*
     * CHANGE OWNER only relates to projects where the user is NOT the owner 
     */
    public static final String CHANGE_PROJECT_OWNER = "ChangeProjectOwner";
    
    /*
     * Can view projects where this user is NOT the owner.
     */
    public static final String VIEW_OTHERS_PROJECTS = "ViewOthersProjects";
    
    
    /*
     * Is the user allowed to use the wizard for new projects
     */
    public static final String CREATE_NEW_PROJECT = "CreateNewProject";
    
    
    public static final String IMPORT_PROJECTS = "ImportProjects";
    public static final String EXPORT_PROJECTS = "ExportProjects";
}
