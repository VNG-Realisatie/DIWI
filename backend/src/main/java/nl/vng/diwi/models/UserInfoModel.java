package nl.vng.diwi.models;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import nl.vng.diwi.dal.entities.UserState;
import nl.vng.diwi.security.UserAction;

@Data
@NoArgsConstructor
@EqualsAndHashCode
public class UserInfoModel extends UserModel{

    @JsonProperty(required = true)
    private String initials;
    @JsonProperty(required = false)
    private List<UserAction> allowedActions = new ArrayList<>();

    public UserInfoModel(UserState user) {
        this.id = user.getId();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.email = user.getEmail();
        this.role = user.getUserRole();
        this.organization = user.getOrganization();
        this.phoneNumber = user.getPhoneNumber();
        this.initials = createInitials();
        this.allowedActions.addAll(user.getUserRole().allowedActions);
    }

    private String createInitials() {
        return this.getFirstName().substring(0, 1) + this.getLastName().substring(0, 1);
    }
}
