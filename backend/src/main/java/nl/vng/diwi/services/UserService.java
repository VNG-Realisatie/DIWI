package nl.vng.diwi.services;

import nl.vng.diwi.dal.UserDAO;
import nl.vng.diwi.dal.entities.User;
import nl.vng.diwi.dal.entities.UserState;
import nl.vng.diwi.rest.VngNotFoundException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.ZonedDateTime;
import java.util.UUID;

public class UserService {

    private static final Logger logger = LogManager.getLogger();

    public UserService() {
    }

    public void deleteUser(UserDAO userDAO, UUID userId, UUID loggedInUserUuid) throws VngNotFoundException {
        ZonedDateTime now = ZonedDateTime.now();
        User user = userDAO.findById(User.class, loggedInUserUuid);

        UserState userState = userDAO.getUserById(userId);
        if (userState == null) {
            logger.error("User with uuid {} was not found.", userId);
            throw new VngNotFoundException();
        }
        userState.setChangeEndDate(now);
        userState.setChangeUser(user);
        userDAO.persist(userState);
    }
}
