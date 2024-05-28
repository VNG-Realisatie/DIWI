package nl.vng.diwi.resources;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import nl.vng.diwi.dal.AutoCloseTransaction;
import nl.vng.diwi.dal.entities.UserState;
import nl.vng.diwi.models.UserInfoModel;
import nl.vng.diwi.models.UserModel;
import nl.vng.diwi.rest.VngBadRequestException;
import nl.vng.diwi.rest.VngNotFoundException;
import nl.vng.diwi.security.LoggedUser;
import nl.vng.diwi.services.UserService;

import java.util.List;
import java.util.UUID;

@Path("/users")
public class UserResource {

    private final UserService userService;

    @Inject
    public UserResource(UserService userService) {
        this.userService = userService;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<UserModel> getAllUsers() {
        return userService.getUserDAO().getAllUsers().stream().map(UserModel::new).toList();
    }

    @GET
    @Path("/{userId}")
    @Produces(MediaType.APPLICATION_JSON)
    public UserModel getUser(@PathParam("userId") UUID userId) throws VngNotFoundException {

        UserState state = userService.getUserDAO().getUserById(userId);
        if (state == null) {
            throw new VngNotFoundException("User with id " + userId + " not found");
        }
        return new UserModel(state);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public UserModel createUser(UserModel newUser, @Context LoggedUser loggedUser) throws VngBadRequestException {

        String validationError = newUser.validate();
        if (validationError != null) {
            throw new VngBadRequestException(validationError);
        }

        UserState sameEmailUser = userService.getUserDAO().getUserByEmail(newUser.getEmail());
        if (sameEmailUser != null) {
            throw new VngBadRequestException("User with this email already exists.");
        }

        try (AutoCloseTransaction transaction = userService.getUserDAO().beginTransaction()) {

            //TODO: create user in keycloak
            String identityProviderId = "identityProviderId"; //TODO - get from keycloak

            UserState newUserEntity = userService.createUser(newUser, identityProviderId, loggedUser.getUuid());
            transaction.commit();

            //TODO: send welcome email

            return new UserModel(newUserEntity);
        }
    }

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public UserModel updateUser(@PathParam("id") UUID userId, UserModel updatedUser, @Context LoggedUser loggedUser) throws VngBadRequestException, VngNotFoundException {

        UserState state = userService.getUserDAO().getUserById(userId);
        if (state == null) {
            throw new VngNotFoundException("User with id " + userId + " not found");
        }

        String validationError = updatedUser.validate();
        if (validationError != null) {
            throw new VngBadRequestException(validationError);
        }

        UserState sameEmailUser = userService.getUserDAO().getUserByEmail(updatedUser.getEmail());
        if (sameEmailUser != null && !sameEmailUser.getUser().getId().equals(userId)) {
            throw new VngBadRequestException("User with this email already exists.");
        }

        try (AutoCloseTransaction transaction = userService.getUserDAO().beginTransaction()) {

            //TODO: update email / last name / first name in keycloak

            updatedUser.setId(userId);
            UserState updatedUserEntity = userService.updateUser(updatedUser, loggedUser.getUuid());
            transaction.commit();

            return new UserModel(updatedUserEntity);
        }
    }

    @DELETE
    @Path("/{userId}")
    @Produces(MediaType.APPLICATION_JSON)
    public void deleteUser(@PathParam("userId") UUID userId, @Context LoggedUser loggedUser) throws VngNotFoundException {

        try (AutoCloseTransaction transaction = userService.getUserDAO().beginTransaction()) {

            //TODO: delete/disable user in keycloak
            userService.deleteUser(userId, loggedUser.getUuid());
            transaction.commit();
        }

    }

    @GET
    @Path("/userinfo")
    @Produces(MediaType.APPLICATION_JSON)
    public UserInfoModel userInfo(ContainerRequestContext requestContext) {
        var loggedUser = (LoggedUser) requestContext.getProperty("loggedUser");

        UserState state = userService.getUserDAO().getUserById(loggedUser.getUuid());
        return new UserInfoModel(state);
    }

}
