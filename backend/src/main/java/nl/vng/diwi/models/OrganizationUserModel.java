package nl.vng.diwi.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
public class OrganizationUserModel {

    UUID uuid;
    String firstName;
    String lastName;
    String initials;

    @JsonIgnore
    UUID organizationUuid;
    @JsonIgnore
    String organizationName;

}
