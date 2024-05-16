package nl.vng.diwi.resources;

import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import nl.vng.diwi.dal.UserDAO;
import nl.vng.diwi.models.UserInfoModel;
import nl.vng.diwi.models.UserModel;
import nl.vng.diwi.security.LoggedUser;
import nl.vng.diwi.security.UserActionConstants;

import java.util.List;

@Path("/users")
// Specifically no @RolesAllowed("BLOCKED_BY_DEFAULT") because userinfo should always be accessible
public class UserResource {

    private final UserDAO userDao;

    @Inject
    public UserResource(UserDAO repo) {
        this.userDao = repo;
    }

    @GET
    @RolesAllowed(UserActionConstants.VIEW_USERS)
    @Produces(MediaType.APPLICATION_JSON)
    public List<UserModel> getAllUsers() {
        return userDao.getAllUsers().stream().map(UserModel::new).toList();
    }

    @GET
    @Path("/userinfo")
    @Produces(MediaType.APPLICATION_JSON)
    public UserInfoModel login(@Context LoggedUser loggedUser) {
        return new UserInfoModel(loggedUser);
    }

}
