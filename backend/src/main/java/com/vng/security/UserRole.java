package com.vng.security;

public enum UserRole {
    Admin(SecurityRoleConstants.Admin);

    public final String name;

    UserRole(String name) {
        this.name = name;
    }
}
