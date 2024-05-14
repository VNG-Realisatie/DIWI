package nl.vng.diwi.models;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import nl.vng.diwi.dal.entities.UserState;
import nl.vng.diwi.security.LoggedUser;
import nl.vng.diwi.security.UserRole;
import nl.vng.diwi.security.UserAction;

@Data
@NoArgsConstructor
@EqualsAndHashCode
public class UserInfoModel {

    @JsonProperty(required = true)
    UUID uuid;
    @JsonProperty(required = true)
    String firstName;
    @JsonProperty(required = true)
    String lastName;
    @JsonProperty(required = true)
    String initials;
    @JsonProperty(required = true)
    UserRole role;
    @JsonProperty(required = false)
    UserAction[] allowedActions;

    public UserInfoModel(UserState user) {
        this.uuid = user.getId();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.initials = createInitials();
        this.role = user.getUserRole();
        this.allowedActions = user.getUserRole().allowedActions.toArray(new UserAction[0]);
    }

    public UserInfoModel(LoggedUser loggedUser) {
        this.uuid = loggedUser.getUuid();
        this.firstName = loggedUser.getFirstName();
        this.lastName = loggedUser.getLastName();
        this.initials = createInitials();
        this.role = loggedUser.getRole();
        this.allowedActions = loggedUser.getRole().allowedActions.toArray(new UserAction[0]);
    }

    private String createInitials() {
        return this.getFirstName().substring(0, 1) + this.getLastName().substring(0, 1);
    }
}
