package nl.vng.diwi.security;

/*
 * These values should be identical to the enum values in security.dal.entities.UserAction
 */
public final class UserActionConstants {
    /*
     * Api page should only be available to technical roles
     */
    public static final String VIEW_API = "VIEW_API";
    
    /*
     * Used for user management, who can view/edit all the users in this instance?
     */
    public static final String VIEW_USERS = "VIEW_USERS";
    public static final String EDIT_USERS = "EDIT_USERS";

    /*
     * Used for user management, who can view/edit all the users in this instance?
     */
    public static final String VIEW_GROUPS = "VIEW_GROUPS";
    public static final String EDIT_GROUPS = "EDIT_GROUPS";
    
    /*
     * Used to check who can view/edit project config settings
     */
    public static final String VIEW_CONFIG = "VIEW_CONFIG";
    public static final String EDIT_CONFIG = "EDIT_CONFIG";
    
    /*
     * Used for managing the custom properties, who can view/edit them in this instance? 
     */
    public static final String VIEW_CUSTOM_PROPERTIES = "VIEW_CUSTOM_PROPERTIES";
    public static final String EDIT_CUSTOM_PROPERTIES = "EDIT_CUSTOM_PROPERTIES";
    
    /*
     * ANYTHING project related has an additional 'filter' in the form of confidentiality level!
     * This should be implemented in the request that is fetching the specific data as there the 
     * confidentiality information is available.
     */    
    public static final String CAN_OWN_PROJECTS = "CAN_OWN_PROJECTS";
    
    /*
     * CHANGE OWNER only relates to projects where the user is NOT the owner 
     */
    public static final String CHANGE_PROJECT_OWNER = "CHANGE_PROJECT_OWNER";
    
    /*
     * Can view projects where this user is NOT the owner.
     */
    public static final String VIEW_OTHERS_PROJECTS = "VIEW_OTHERS_PROJECTS";
    
    /*
     * Can view/edit projects where this user is (part of) the owner(s)
     */
    public static final String VIEW_OWN_PROJECTS = "VIEW_OWN_PROJECTS";
    public static final String EDIT_OWN_PROJECTS = "EDIT_OWN_PROJECTS";
    
    
    /*
     * Is the user allowed to use the wizard for new projects
     */
    public static final String CREATE_NEW_PROJECT = "CREATE_NEW_PROJECT";
    
    
    public static final String IMPORT_PROJECTS = "IMPORT_PROJECTS";
    public static final String EXPORT_PROJECTS = "EXPORT_PROJECTS";
}
