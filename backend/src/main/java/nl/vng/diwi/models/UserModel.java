package nl.vng.diwi.models;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import nl.vng.diwi.dal.entities.UserState;

@Data
@NoArgsConstructor
@EqualsAndHashCode
public class UserModel {
    private static  String createInitials(UserState user) {
        return user.getFirstName().substring(0,1)+user.getLastName().substring(0,1);
    }

    public UserModel(UserState user) {
        this.uuid = user.getId();

        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.initials = createInitials(user);
    }

    @JsonProperty(required = true)
    UUID uuid;

    @JsonProperty(required = true)
    String firstName;

    @JsonProperty(required = true)
    String lastName;

    @JsonProperty(required = true)
    String initials;
}
