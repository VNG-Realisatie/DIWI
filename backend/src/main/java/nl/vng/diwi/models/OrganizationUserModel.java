package nl.vng.diwi.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class OrganizationUserModel {

    String uuid;
    String firstName;
    String lastName;
    String initials;

    @JsonIgnore
    String organizationUuid;
    @JsonIgnore
    String organizationName;

}
