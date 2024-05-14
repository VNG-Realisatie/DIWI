package nl.vng.diwi.services;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

import nl.vng.diwi.dal.AutoCloseTransaction;
import nl.vng.diwi.dal.VngRepository;
import nl.vng.diwi.dal.entities.User;
import nl.vng.diwi.dal.entities.UserGroup;
import nl.vng.diwi.dal.entities.UserGroupState;
import nl.vng.diwi.dal.entities.UserToUserGroup;
import nl.vng.diwi.dal.entities.superclasses.ChangeDataSuperclass;
import nl.vng.diwi.models.UserGroupModel;
import nl.vng.diwi.models.UserGroupUserModel;
import nl.vng.diwi.rest.VngBadRequestException;

public class UserGroupService {
    public UserGroupService() {
    }

    public List<UserGroupModel> getAllUserGroups(VngRepository repo, boolean includeSingleUser) {

        List<UserGroupUserModel> userGroupUserList = repo.getUsergroupDAO().getUserGroupUsersList(includeSingleUser);
        return UserGroupModel.fromUserGroupUserModelListToUserGroupModelList(userGroupUserList);
    }

    public UserGroupModel createUserGroup(VngRepository repo, UserGroupModel newUserGroupModel, UUID loggedUserId) throws VngBadRequestException {

        List<UserGroupState> states = repo.getUsergroupDAO().findActiveUserGroupStateByName(newUserGroupModel.getName());
        if (!states.isEmpty()) {
            throw new VngBadRequestException("User group name is already in use.");
        }

        try (AutoCloseTransaction transaction = repo.beginTransaction()) {
            ZonedDateTime now = ZonedDateTime.now();
            User createUser = repo.getReferenceById(User.class, loggedUserId);
            Consumer<ChangeDataSuperclass> setChangeValues = (ChangeDataSuperclass entity) -> {
                entity.setCreateUser(createUser);
                entity.setChangeStartDate(now);
            };

            UserGroup newGroup = new UserGroup();
            newGroup.setSingleUser(false);
            repo.persist(newGroup);

            UserGroupState newGroupState = new UserGroupState();
            newGroupState.setUserGroup(newGroup);
            newGroupState.setName(newUserGroupModel.getName());
            setChangeValues.accept(newGroupState);
            repo.persist(newGroupState);

            for (UserGroupUserModel userModel : newUserGroupModel.getUsers()) {
                UserToUserGroup userToUserGroup = new UserToUserGroup();
                userToUserGroup.setUserGroup(newGroup);
                userToUserGroup.setUser(repo.getReferenceById(User.class, userModel.getUuid()));
                setChangeValues.accept(userToUserGroup);
                repo.persist(userToUserGroup);
            }
            transaction.commit();

            List<UserGroupUserModel> userGroupModel = repo.getUsergroupDAO().getUserGroupUsers(newGroup.getId());
            return UserGroupModel.fromUserGroupUserModelListToUserGroupModelList(userGroupModel).get(0);
        }
    }
}
