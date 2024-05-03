package nl.vng.diwi.security;

public enum UserRole {
    Admin(SecurityRoleConstants.Admin),
	UserPlus(SecurityRoleConstants.UserPlus),
	User(SecurityRoleConstants.User),
	Management(SecurityRoleConstants.Management),
	Council(SecurityRoleConstants.Council),
	External(SecurityRoleConstants.External);

    public final String name;

    UserRole(String name) {
        this.name = name;
    }
}
