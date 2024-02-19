package nl.vng.diwi.security;

import java.util.UUID;

public class LoggedUser {

    private UUID uuid;

    private Long id;

    private String email;

    private String name;

    private UserRole role;

    private boolean disabled;

    public LoggedUser() {
    }

    /**
     * This constructor can be called only when a DB session is open
     */
    public LoggedUser(User user) {
        this.uuid = UUID.fromString("2122426c-6e70-419c-a054-f51dd24d798b"); //TODO - use real one
        this.id = user.getId();
        this.email = user.getEmail();
        this.name = user.getName();
        this.role = user.getRole();
        this.disabled = user.isDisabled();
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }
}
