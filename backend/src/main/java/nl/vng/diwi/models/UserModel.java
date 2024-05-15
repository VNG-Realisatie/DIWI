package nl.vng.diwi.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import nl.vng.diwi.dal.entities.UserState;
import nl.vng.diwi.security.UserRole;

import java.util.UUID;

@Data
@NoArgsConstructor
@EqualsAndHashCode
public class UserModel {

    @JsonProperty(required = true)
    private UUID id;
    @JsonProperty(required = true)
    private String firstName;
    @JsonProperty(required = true)
    private String lastName;
    @JsonProperty(required = true)
    private String email;
    @JsonProperty(required = true)
    private UserRole role;

    public UserModel(UserState user) {
        this.id = user.getId();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.email = user.getEmail();
        this.role = user.getUserRole();
    }

}
