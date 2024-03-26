    package nl.vng.diwi.resources;

import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import nl.vng.diwi.dal.UserDAO;
import nl.vng.diwi.models.UserModel;
import nl.vng.diwi.security.LoggedUser;

@Path("/users")
public class UserResource {

    @Context
    private HttpServletRequest httpRequest;
    @Context
    private HttpServletResponse httpResponse;

    private final UserDAO userDao;

    @Inject
    public UserResource(UserDAO repo) {
        this.userDao = repo;
    }

    @GET
    @Path("/userinfo")
    @Produces(MediaType.APPLICATION_JSON)
    public UserModel login(@Context LoggedUser loggedUser) {
        return new UserModel(loggedUser);
    }

}
