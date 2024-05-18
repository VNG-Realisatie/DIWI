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
    protected UUID id;
    @JsonProperty(required = true)
    protected String firstName;
    @JsonProperty(required = true)
    protected String lastName;
    @JsonProperty(required = true)
    protected String email;
    @JsonProperty(required = true)
    protected UserRole role;
    @JsonProperty(required = true)
    protected String organization;
    @JsonProperty(required = true)
    protected String phoneNumber;

    public UserModel(UserState user) {
        this.id = user.getId();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.email = user.getEmail();
        this.role = user.getUserRole();
        this.organization = user.getOrganization();
        this.phoneNumber = user.getPhoneNumber();
    }

}
