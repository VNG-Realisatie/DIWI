package nl.vng.diwi.services;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import jakarta.inject.Inject;
import lombok.Getter;
import nl.vng.diwi.dal.UserGroupDAO;
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

@Getter
public class UserGroupService {

    private static final Logger logger = LogManager.getLogger();

    private UserGroupDAO userGroupDAO;

    @Inject
    public UserGroupService(UserGroupDAO userGroupDAO) {
        this.userGroupDAO = userGroupDAO;
    }

    public List<UserGroupModel> getAllUserGroups(boolean includeSingleUser) {

        List<UserGroupUserModel> userGroupUserList = userGroupDAO.getAllUserGroupsUsersList(includeSingleUser);
        return UserGroupModel.fromUserGroupUserModelListToUserGroupModelList(userGroupUserList);
    }

    public UserGroup createUserGroup(UserGroupModel newUserGroupModel, UUID loggedUserId) throws VngBadRequestException {

        List<UserGroupState> states = userGroupDAO.findActiveUserGroupStateByName(newUserGroupModel.getName());
        if (!states.isEmpty()) {
            throw new VngBadRequestException("User group name is already in use.");
        }

        ZonedDateTime now = ZonedDateTime.now();
        User createUser = userGroupDAO.getReferenceById(User.class, loggedUserId);
        Consumer<ChangeDataSuperclass> setChangeValues = (ChangeDataSuperclass entity) -> {
            entity.setCreateUser(createUser);
            entity.setChangeStartDate(now);
        };

        UserGroup newGroup = new UserGroup();
        newGroup.setSingleUser(false);
        userGroupDAO.persist(newGroup);

        UserGroupState newGroupState = new UserGroupState();
        newGroupState.setUserGroup(newGroup);
        newGroupState.setName(newUserGroupModel.getName());
        setChangeValues.accept(newGroupState);
        userGroupDAO.persist(newGroupState);

        for (UserGroupUserModel userModel : newUserGroupModel.getUsers()) {
            UserToUserGroup userToUserGroup = new UserToUserGroup();
            userToUserGroup.setUserGroup(newGroup);
            userToUserGroup.setUser(userGroupDAO.getReferenceById(User.class, userModel.getUuid()));
            setChangeValues.accept(userToUserGroup);
            userGroupDAO.persist(userToUserGroup);
        }
        return newGroup;
    }


    public void updateUserGroup(UserGroupModel updatedUserGroup, UUID loggedInUserUuid) throws VngNotFoundException, VngBadRequestException {

        UUID groupId = updatedUserGroup.getUuid();
        UserGroup userGroup = userGroupDAO.getCurrentUserGroup(groupId);
        if (userGroup == null) {
            logger.error("UserGroup with uuid {} was not found.", groupId);
            throw new VngNotFoundException();
        }
        if (userGroup.getSingleUser() == Boolean.TRUE) {
            throw new VngBadRequestException("Cannot update single-user usergroups.");
        }
        Set<UUID> sameNameGroupIds = userGroupDAO.findActiveUserGroupStateByName(updatedUserGroup.getName())
            .stream().map(s -> s.getUserGroup().getId()).filter(id -> !id.equals(groupId)).collect(Collectors.toSet());
        if (!sameNameGroupIds.isEmpty()) {
            throw new VngBadRequestException("Cannot update as name is already in use.");
        }

        List<UserGroupUserModel> oldUserGroupModel = userGroupDAO.getUserGroupUsers(updatedUserGroup.getUuid());
        UserGroupModel oldUserGroup = UserGroupModel.fromUserGroupUserModelListToUserGroupModelList(oldUserGroupModel).get(0);

        ZonedDateTime now = ZonedDateTime.now();
        User user = userGroupDAO.findById(User.class, loggedInUserUuid);

        if (!oldUserGroup.getName().equals(updatedUserGroup.getName())) {
            userGroup.getState().stream()
                .filter(ugs -> ugs.getChangeEndDate() == null)
                .forEach(ugs -> {
                    ugs.setChangeEndDate(now);
                    ugs.setChangeUser(user);
                    userGroupDAO.persist(ugs);
                });
            UserGroupState newGroupState = new UserGroupState();
            newGroupState.setUserGroup(userGroup);
            newGroupState.setCreateUser(user);
            newGroupState.setChangeStartDate(now);
            newGroupState.setName(updatedUserGroup.getName());
            userGroupDAO.persist(newGroupState);
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
                    userGroupDAO.persist(utug);
                }
            });

        usersToAdd.forEach(u -> {
            UserToUserGroup utug = new UserToUserGroup();
            utug.setUserGroup(userGroup);
            utug.setUser(userGroupDAO.getReferenceById(User.class, u));
            utug.setCreateUser(user);
            utug.setChangeStartDate(now);
            userGroupDAO.persist(utug);
        });

    }

    public void deleteUserGroup(UUID groupId, UUID loggedInUserUuid) throws VngNotFoundException, VngBadRequestException {
        ZonedDateTime now = ZonedDateTime.now();
        User user = userGroupDAO.findById(User.class, loggedInUserUuid);

        UserGroup userGroup = userGroupDAO.getCurrentUserGroup(groupId);
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
                userGroupDAO.persist(ugs);
            });
    }
}
