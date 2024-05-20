package nl.vng.diwi.services;

import jakarta.inject.Inject;
import lombok.Getter;
import nl.vng.diwi.dal.UserDAO;
import nl.vng.diwi.dal.UserGroupDAO;
import nl.vng.diwi.dal.entities.User;
import nl.vng.diwi.dal.entities.UserGroup;
import nl.vng.diwi.dal.entities.UserGroupState;
import nl.vng.diwi.dal.entities.UserState;
import nl.vng.diwi.dal.entities.UserToUserGroup;
import nl.vng.diwi.models.UserModel;
import nl.vng.diwi.rest.VngNotFoundException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.ZonedDateTime;
import java.util.UUID;

@Getter
public class UserService {

    private static final Logger logger = LogManager.getLogger();

    private UserDAO userDAO;
    private UserGroupDAO userGroupDAO;

    @Inject
    public UserService(UserDAO userDAO, UserGroupDAO userGroupDAO) {
        this.userDAO = userDAO;
        this.userGroupDAO = userGroupDAO;
    }

    public UserState createUser(UserModel userModel, String identityProviderId, UUID loggedInUserUuid) {

        ZonedDateTime now = ZonedDateTime.now();
        User loggedUser = userDAO.getReferenceById(User.class, loggedInUserUuid);

        User newUser = new User();
        newUser.setSystemUser(false);
        userDAO.persist(newUser);

        UserState userEntity = new UserState();
        userEntity.setChangeStartDate(now);
        userEntity.setCreateUser(loggedUser);
        userEntity.setFirstName(userModel.getFirstName());
        userEntity.setLastName(userModel.getLastName());
        userEntity.setEmail(userModel.getEmail());
        userEntity.setUser(newUser);
        userEntity.setIdentityProviderId(identityProviderId);
        userEntity.setUserRole(userModel.getRole());
        userEntity.setPhoneNumber(userModel.getPhoneNumber());
        userEntity.setOrganization(userModel.getOrganization());
        userDAO.persist(userEntity);

        UserGroup group =  new UserGroup();
        group.setSingleUser(true);
        userDAO.persist(group);

        UserGroupState groupState = new UserGroupState();
        groupState.setChangeStartDate(now);
        groupState.setCreateUser(loggedUser);
        groupState.setName(userEntity.getFirstName() + " " + userEntity.getLastName());
        groupState.setUserGroup(group);
        userDAO.persist(groupState);

        UserToUserGroup groupToUser = new UserToUserGroup();
        groupToUser.setChangeStartDate(now);
        groupToUser.setCreateUser(loggedUser);
        groupToUser.setUserGroup(group);
        groupToUser.setUser(newUser);
        userDAO.persist(groupToUser);

        return userEntity;
    }

    public UserState updateUser(UserModel userModel, UUID loggedInUserUuid) {

        ZonedDateTime now = ZonedDateTime.now();
        User loggedUser = userDAO.getReferenceById(User.class, loggedInUserUuid);

        UserState oldUserEntity = userDAO.getUserById(userModel.getId());
        oldUserEntity.setChangeEndDate(now);
        oldUserEntity.setChangeUser(loggedUser);
        userDAO.persist(oldUserEntity);

        UserState newUserEntity = new UserState();
        newUserEntity.setChangeStartDate(now);
        newUserEntity.setCreateUser(loggedUser);
        newUserEntity.setFirstName(userModel.getFirstName());
        newUserEntity.setLastName(userModel.getLastName());
        newUserEntity.setEmail(userModel.getEmail());
        newUserEntity.setUser(oldUserEntity.getUser());
        newUserEntity.setIdentityProviderId(oldUserEntity.getIdentityProviderId());
        newUserEntity.setUserRole(userModel.getRole());
        newUserEntity.setPhoneNumber(userModel.getPhoneNumber());
        newUserEntity.setOrganization(userModel.getOrganization());
        userDAO.persist(newUserEntity);

        return newUserEntity;
    }


    public void deleteUser(UUID userId, UUID loggedInUserUuid) throws VngNotFoundException {
        ZonedDateTime now = ZonedDateTime.now();
        User loggedUser = userDAO.findById(User.class, loggedInUserUuid);

        UUID usergroupUuid = userGroupDAO.findSingleUserUserGroup(userId);
        if (usergroupUuid == null) {
            logger.error("Single-user usergroup for userId {} was not found.", userId);
        } else {
            UserGroup userGroup = userGroupDAO.getCurrentUserGroup(usergroupUuid);
            userGroup.getState().stream()
                .filter(ugs -> ugs.getChangeEndDate() == null)
                .forEach(ugs -> {
                    ugs.setChangeEndDate(now);
                    ugs.setChangeUser(loggedUser);
                    userGroupDAO.persist(ugs);
                });
        }

        UserState userState = userDAO.getUserById(userId);
        if (userState == null) {
            logger.error("User with uuid {} was not found.", userId);
            throw new VngNotFoundException();
        }
        userState.setChangeEndDate(now);
        userState.setChangeUser(loggedUser);
        userDAO.persist(userState);
    }
}
