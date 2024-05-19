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
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import nl.vng.diwi.dal.AutoCloseTransaction;
import nl.vng.diwi.dal.UserDAO;
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

    private final UserDAO userDao;
    private final UserService userService;

    @Inject
    public UserResource(UserDAO repo, UserService userService) {
        this.userDao = repo;
        this.userService = userService;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<UserModel> getAllUsers() {
        return userDao.getAllUsers().stream().map(UserModel::new).toList();
    }

    @GET
    @Path("/{userId}")
    @Produces(MediaType.APPLICATION_JSON)
    public UserModel getUser(@PathParam("userId") UUID userId) throws VngNotFoundException {

        UserState state = userDao.getUserById(userId);
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

        UserState sameNameUser = userDao.getUserByName(newUser.getFirstName(), newUser.getLastName()); //TODO: unique name? or unique email? or both?
        if (sameNameUser != null) {
            throw new VngBadRequestException("User with this name already exists.");
        }

        try (AutoCloseTransaction transaction = userDao.beginTransaction()) {

            //TODO: create user in keycloak
            String identityProviderId = "identityProviderId"; //TODO

            UserState newUserEntity = userService.createUser(userDao, newUser, identityProviderId, loggedUser.getUuid());
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

        UserState state = userDao.getUserById(userId);
        if (state == null) {
            throw new VngNotFoundException("User with id " + userId + " not found");
        }

        String validationError = updatedUser.validate();
        if (validationError != null) {
            throw new VngBadRequestException(validationError);
        }

        UserState sameNameUser = userDao.getUserByName(updatedUser.getFirstName(), updatedUser.getLastName()); //TODO: unique name? or unique email? or both?
        if (sameNameUser != null && !sameNameUser.getUser().getId().equals(userId)) {
            throw new VngBadRequestException("User with this name already exists.");
        }

        try (AutoCloseTransaction transaction = userDao.beginTransaction()) {

            //TODO: update user in keycloak?? what fields are allowed to be updated??

            updatedUser.setId(userId);
            UserState updatedUserEntity = userService.updateUser(userDao, updatedUser, loggedUser.getUuid());
            transaction.commit();

            return new UserModel(updatedUserEntity);
        }
    }

    @DELETE
    @Path("/{userId}")
    @Produces(MediaType.APPLICATION_JSON)
    public void deleteUser(@PathParam("userId") UUID userId, @Context LoggedUser loggedUser) throws VngNotFoundException {

        try (AutoCloseTransaction transaction = userDao.beginTransaction()) {
            userService.deleteUser(userDao, userId, loggedUser.getUuid());
            transaction.commit();
        }

    }

    @GET
    @Path("/userinfo")
    @Produces(MediaType.APPLICATION_JSON)
    public UserInfoModel login(@Context LoggedUser loggedUser) {
        UserState state = userDao.getUserById(loggedUser.getUuid());
        return new UserInfoModel(state);
    }

}
