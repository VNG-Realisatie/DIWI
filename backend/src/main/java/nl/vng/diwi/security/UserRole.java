package nl.vng.diwi.security;

public enum UserRole {
    Admin(SecurityRoleConstants.ADMIN),
    UserPlus(SecurityRoleConstants.USER_PLUS),
    User(SecurityRoleConstants.USER),
    Management(SecurityRoleConstants.MANAGEMENT),
    Council(SecurityRoleConstants.COUNCIL),
    External(SecurityRoleConstants.EXTERNAL);
    
    public final String name;

    UserRole(String name) {
        this.name = name;
    }
}
