package nl.vng.diwi.models;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserGroupUserModel {

    UUID uuid;
    String firstName;
    String lastName;
    String initials;

    @JsonIgnore
    UUID userGroupUuid;
    @JsonIgnore
    String userGroupName;

}
