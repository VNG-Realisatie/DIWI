package nl.vng.diwi.security;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nl.vng.diwi.dal.entities.UserState;

import java.util.UUID;

@NoArgsConstructor
@Getter
@Setter
@Builder
@AllArgsConstructor
public class LoggedUser {

    private UUID uuid;

    private UUID identityProviderId;

    private String firstName;
    private String lastName;

    private UserRole role;



    /**
     * This constructor can be called only when a DB session is open
     */
    public LoggedUser(UserState user) {
        this.uuid = user.getUser().getId();
        this.identityProviderId = UUID.fromString(user.getIdentityProviderId());
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.role = user.getUserRole();
    }
}
