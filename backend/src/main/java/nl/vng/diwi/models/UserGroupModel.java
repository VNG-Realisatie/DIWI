package nl.vng.diwi.models;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;

import com.fasterxml.jackson.annotation.JsonProperty;

import nl.vng.diwi.dal.entities.UserGroup;

@Data
@NoArgsConstructor
public class UserGroupModel {

    @JsonProperty(required = true)
    UUID uuid;
    @JsonProperty(required = true)
    String name;
    List<UserGroupUserModel> users = new ArrayList<>();

    public UserGroupModel(UserGroup userGroup) {
        uuid = userGroup.getId();
        // stub
    }

    public static List<UserGroupModel> fromUserGroupUserModelListToUserGroupModelList(List<UserGroupUserModel> userGroupUserList) {

        Map<UUID, UserGroupModel> userGroupUsersMap = new HashMap<>();
        for (UserGroupUserModel userGroupUserModel : userGroupUserList) {
            if (userGroupUsersMap.containsKey(userGroupUserModel.getUserGroupUuid())) {
                userGroupUsersMap.get(userGroupUserModel.getUserGroupUuid()).getUsers().add(userGroupUserModel);
            } else {
                UserGroupModel userGroupModel = new UserGroupModel();
                userGroupModel.setUuid(userGroupUserModel.getUserGroupUuid());
                userGroupModel.setName(userGroupUserModel.getUserGroupName());
                userGroupModel.getUsers().add(userGroupUserModel);
                userGroupUsersMap.put(userGroupModel.getUuid(), userGroupModel);
            }
        }

        List<UserGroupModel> result = new ArrayList<>(userGroupUsersMap.values());
        result.sort(Comparator.comparing(UserGroupModel::getName));

        return result;
    }


}
