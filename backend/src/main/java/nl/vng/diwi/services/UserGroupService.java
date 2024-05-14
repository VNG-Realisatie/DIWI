package nl.vng.diwi.services;

import java.util.List;

import nl.vng.diwi.dal.VngRepository;
import nl.vng.diwi.models.UserGroupModel;
import nl.vng.diwi.models.UserGroupUserModel;

public class UserGroupService {
    public UserGroupService() {
    }

    public List<UserGroupModel> getAllUserGroups(VngRepository repo) {

        List<UserGroupUserModel> userGroupUserList = repo.getUsergroupDAO().getUserGroupUsersList();
        return UserGroupModel.fromUserGroupUserModelListToUserGroupModelList(userGroupUserList);
    }

}
