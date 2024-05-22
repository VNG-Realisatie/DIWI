package nl.vng.diwi.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import nl.vng.diwi.dal.entities.UserState;
import nl.vng.diwi.security.UserRole;
import org.apache.commons.validator.routines.EmailValidator;

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
    @JsonProperty(required = true)
    protected String department;
    @JsonProperty(required = true)
    protected String contactPerson;
    @JsonProperty(required = true)
    protected String prefixes;

    public UserModel(UserState user) {
        this.id = user.getUser().getId();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.email = user.getEmail();
        this.role = user.getUserRole();
        this.organization = user.getOrganization();
        this.phoneNumber = user.getPhoneNumber();
        this.department = user.getDepartment();
        this.contactPerson = user.getContactPerson();
        this.prefixes = user.getPrefixes();
    }

    public String validate() {
        if (firstName == null || lastName == null || email == null || role == null) {
            return "Missing mandatory fields";
        }
        if (!EmailValidator.getInstance().isValid(email)) {
            return "Invalid email address";
        }
        return null;
    }

}
