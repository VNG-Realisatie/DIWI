package nl.vng.diwi.models;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
public class UserGroupUserSqlModel {

    UUID uuid;
    String firstName;
    String lastName;
    String initials;

    UUID userGroupUuid;
    String userGroupName;

}
