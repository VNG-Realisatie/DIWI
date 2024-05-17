package nl.vng.diwi.services;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

import nl.vng.diwi.dal.VngRepository;
import nl.vng.diwi.dal.entities.User;
import nl.vng.diwi.dal.entities.UserGroup;
import nl.vng.diwi.dal.entities.UserGroupState;
import nl.vng.diwi.dal.entities.UserToUserGroup;
import nl.vng.diwi.dal.entities.superclasses.ChangeDataSuperclass;
import nl.vng.diwi.models.UserGroupModel;
import nl.vng.diwi.models.UserGroupUserModel;
import nl.vng.diwi.rest.VngBadRequestException;
import nl.vng.diwi.rest.VngNotFoundException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class UserGroupService {

    private static final Logger logger = LogManager.getLogger();

    public UserGroupService() {
    }

    public List<UserGroupModel> getAllUserGroups(VngRepository repo, boolean includeSingleUser) {

        List<UserGroupUserModel> userGroupUserList = repo.getUsergroupDAO().getAllUserGroupsUsersList(includeSingleUser);
        return UserGroupModel.fromUserGroupUserModelListToUserGroupModelList(userGroupUserList);
    }

    public UserGroup createUserGroup(VngRepository repo, UserGroupModel newUserGroupModel, UUID loggedUserId) throws VngBadRequestException {

        List<UserGroupState> states = repo.getUsergroupDAO().findActiveUserGroupStateByName(newUserGroupModel.getName());
        if (!states.isEmpty()) {
            throw new VngBadRequestException("User group name is already in use.");
        }

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
        return newGroup;
    }


    public void updateUserGroup(VngRepository repo, UserGroupModel updatedUserGroup, UUID loggedInUserUuid) throws VngNotFoundException, VngBadRequestException {

        UUID groupId = updatedUserGroup.getUuid();
        UserGroup userGroup = repo.getUsergroupDAO().getCurrentUserGroup(groupId);
        if (userGroup == null) {
            logger.error("UserGroup with uuid {} was not found.", groupId);
            throw new VngNotFoundException();
        }
        if (userGroup.getSingleUser() == Boolean.TRUE) {
            throw new VngBadRequestException("Cannot update single-user usergroups.");
        }
        List<UserGroupState> states = repo.getUsergroupDAO().findActiveUserGroupStateByName(updatedUserGroup.getName());
        if (!states.isEmpty()) {
            throw new VngBadRequestException("Cannot update as name is already in use.");
        }

        List<UserGroupUserModel> oldUserGroupModel = repo.getUsergroupDAO().getUserGroupUsers(updatedUserGroup.getUuid());
        UserGroupModel oldUserGroup = UserGroupModel.fromUserGroupUserModelListToUserGroupModelList(oldUserGroupModel).get(0);

        ZonedDateTime now = ZonedDateTime.now();
        User user = repo.findById(User.class, loggedInUserUuid);

        if (!oldUserGroup.getName().equals(updatedUserGroup.getName())) {
            userGroup.getState().stream()
                .filter(ugs -> ugs.getChangeEndDate() == null)
                .forEach(ugs -> {
                    ugs.setChangeEndDate(now);
                    ugs.setChangeUser(user);
                    repo.persist(ugs);
                });
            UserGroupState newGroupState = new UserGroupState();
            newGroupState.setUserGroup(userGroup);
            newGroupState.setCreateUser(user);
            newGroupState.setChangeStartDate(now);
            newGroupState.setName(updatedUserGroup.getName());
            repo.persist(newGroupState);
        }

        List<UUID> oldUserList = oldUserGroupModel.stream().map(UserGroupUserModel::getUuid).toList();
        List<UUID> newUserList = updatedUserGroup.getUsers().stream().map(UserGroupUserModel::getUuid).toList();

        List<UUID> usersToRemove = oldUserList.stream().filter(o -> !newUserList.contains(o)).toList();
        List<UUID> usersToAdd = newUserList.stream().filter(n -> !oldUserList.contains(n)).toList();

        userGroup.getUserToUserGroups().stream()
            .filter(utug -> utug.getChangeEndDate() == null)
            .forEach(utug -> {
                if (usersToRemove.contains(utug.getUser().getId())) {
                    utug.setChangeEndDate(now);
                    utug.setChangeUser(user);
                    repo.persist(utug);
                }
            });

        usersToAdd.forEach(u -> {
            UserToUserGroup utug = new UserToUserGroup();
            utug.setUserGroup(userGroup);
            utug.setUser(repo.getReferenceById(User.class, u));
            utug.setCreateUser(user);
            utug.setChangeStartDate(now);
            repo.persist(utug);
        });

    }

    public void deleteUserGroup(VngRepository repo, UUID groupId, UUID loggedInUserUuid) throws VngNotFoundException, VngBadRequestException {
        ZonedDateTime now = ZonedDateTime.now();
        User user = repo.findById(User.class, loggedInUserUuid);

        UserGroup userGroup = repo.getUsergroupDAO().getCurrentUserGroup(groupId);
        if (userGroup == null) {
            logger.error("UserGroup with uuid {} was not found.", groupId);
            throw new VngNotFoundException();
        }
        if (userGroup.getSingleUser() == Boolean.TRUE) {
            throw new VngBadRequestException("Cannot delete single-user usergroups.");
        }

        userGroup.getState().stream()
            .filter(ugs -> ugs.getChangeEndDate() == null)
            .forEach(ugs -> {
                ugs.setChangeEndDate(now);
                ugs.setChangeUser(user);
                repo.persist(ugs);
            });
    }
}
