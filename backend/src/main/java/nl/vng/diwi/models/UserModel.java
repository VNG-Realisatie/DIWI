package nl.vng.diwi.models;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import nl.vng.diwi.dal.entities.UserState;
import nl.vng.diwi.security.LoggedUser;

@Data
@NoArgsConstructor
@EqualsAndHashCode
public class UserModel {

    @JsonProperty(required = true)
    UUID uuid;
    @JsonProperty(required = true)
    String firstName;
    @JsonProperty(required = true)
    String lastName;
    @JsonProperty(required = true)
    String initials;

    public UserModel(UserState user) {
        this.uuid = user.getId();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.initials = createInitials();
    }

    public UserModel(LoggedUser loggedUser) {
        this.uuid = loggedUser.getUuid();
        this.firstName = loggedUser.getFirstName();
        this.lastName = loggedUser.getLastName();
        this.initials = createInitials();
    }

    private String createInitials() {
        return this.getFirstName().substring(0, 1) + this.getLastName().substring(0, 1);
    }
}
