package nl.vng.diwi.security;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@NoArgsConstructor
@Getter
@Setter
public class LoggedUser {

    private UUID uuid;

    private Long id;

    private String email;

    private String name;

    private UserRole role;

    private boolean disabled;


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

}
