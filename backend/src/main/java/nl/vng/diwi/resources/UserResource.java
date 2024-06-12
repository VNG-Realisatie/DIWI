package nl.vng.diwi.resources;

import jakarta.annotation.security.RolesAllowed;
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
import nl.vng.diwi.rest.VngNotAllowedException;
import nl.vng.diwi.rest.VngNotFoundException;
import nl.vng.diwi.rest.VngServerErrorException;
import nl.vng.diwi.security.LoggedUser;
import nl.vng.diwi.security.MailException;
import nl.vng.diwi.security.MailService;
import nl.vng.diwi.security.UserActionConstants;
import nl.vng.diwi.services.AddUserException;
import nl.vng.diwi.services.FindUserException;
import nl.vng.diwi.services.KeycloakService;
import nl.vng.diwi.services.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.keycloak.representations.idm.UserRepresentation;

import java.util.List;
import java.util.UUID;

@Path("/users")
// Specifically no @RolesAllowed("BLOCKED_BY_DEFAULT") because userinfo should always be accessible
public class UserResource {

    private static Logger logger = LogManager.getLogger();

    private final UserService userService;
    private final MailService mailService;
    private final KeycloakService keycloakService;

    @Inject
    public UserResource(UserService userService, MailService mailService, KeycloakService keycloakService) {
        this.userService = userService;
        this.mailService = mailService;
        this.keycloakService = keycloakService;
    }

    @GET
    @RolesAllowed(UserActionConstants.VIEW_USERS)
    @Produces(MediaType.APPLICATION_JSON)
    public List<UserModel> getAllUsers() {
        return userService.getUserDAO().getAllUsers().stream().map(UserModel::new).toList();
    }

    @GET
    @Path("/{userId}")
    @RolesAllowed(UserActionConstants.VIEW_USERS)
    @Produces(MediaType.APPLICATION_JSON)
    public UserModel getUser(@PathParam("userId") UUID userId) throws VngNotFoundException {

        UserState state = userService.getUserDAO().getUserById(userId);
        if (state == null) {
            throw new VngNotFoundException("User with id " + userId + " not found");
        }
        return new UserModel(state);
    }

    @POST
    @RolesAllowed(UserActionConstants.EDIT_USERS)
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public UserModel createUser(UserModel newUser, @Context LoggedUser loggedUser) throws VngBadRequestException {

        String validationError = newUser.validate();
        if (validationError != null) {
            throw new VngBadRequestException(validationError);
        }

        UserState sameEmailUser = userService.getUserDAO().getUserByEmail(newUser.getEmail());
        UserRepresentation keycloakSameEmailUser = keycloakService.getUserByEmail(newUser.getEmail());
        if (sameEmailUser != null || keycloakSameEmailUser != null) {
            throw new VngBadRequestException("User with this email already exists.");
        }

        try (AutoCloseTransaction transaction = userService.getUserDAO().beginTransaction()) {
            logger.info("creating new user: " + newUser.toString());
            var kcUserResource = keycloakService.createUser(newUser);
            String identityProviderId = kcUserResource.toRepresentation().getId();
            UserState newUserEntity = userService.createUser(newUser, identityProviderId, loggedUser.getUuid());
            transaction.commit(); 

            // Send initial welcome mail
            try {
                mailService.sendWelcomeMail(newUserEntity.getEmail());
            } catch (MailException e) {
                logger.error("Failed to send welcome mail", e);
                throw new VngServerErrorException("Failed to send welcome mail");
            }
            
            // Send second email for user to reset their login credentials
            kcUserResource.executeActionsEmail(List.of("UPDATE_PROFILE"));

            return new UserModel(newUserEntity);
        } catch (AddUserException e1) {
            logger.error("Failed to create user in keycloak", e1);
            throw new VngServerErrorException("Failed to create user in keycloak");
        }
    }

    @PUT
    @Path("/{id}")
    @RolesAllowed(UserActionConstants.EDIT_USERS)
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public UserModel updateUser(@PathParam("id") UUID userId, UserModel updatedUser, @Context LoggedUser loggedUser)
            throws VngBadRequestException, VngNotFoundException {

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
            updatedUser.setId(userId);

            keycloakService.updateUser(updatedUser);

            UserState updatedUserEntity = userService.updateUser(updatedUser, loggedUser.getUuid());
            transaction.commit();

            return new UserModel(updatedUserEntity);
        } catch (FindUserException e) {
            throw new VngBadRequestException("Could not update user in keycloak.");
        }
    }

    @DELETE
    @Path("/{userId}")
    @RolesAllowed(UserActionConstants.EDIT_USERS)
    @Produces(MediaType.APPLICATION_JSON)
    public void deleteUser(@PathParam("userId") UUID userId, ContainerRequestContext requestContext)
            throws VngNotFoundException {

        var loggedUser = (LoggedUser) requestContext.getProperty("loggedUser");

        try (AutoCloseTransaction transaction = userService.getUserDAO().beginTransaction()) {

            // TODO: delete/disable user in keycloak
            userService.deleteUser(userId, loggedUser.getUuid());
            transaction.commit();
        }

    }

    @GET
    @Path("/userinfo")
    @Produces(MediaType.APPLICATION_JSON)
    public UserInfoModel userInfo(ContainerRequestContext requestContext) throws VngNotAllowedException {
        var loggedUser = (LoggedUser) requestContext.getProperty("loggedUser");
        UserState state = userService.getUserDAO().getUserById(loggedUser.getUuid());

        // Here we will assume that if we cannot find the user they should not be
        // allowed in
        if (state == null) {
            throw new VngNotAllowedException(
                    "User with kc id " + loggedUser.getIdentityProviderId() + " not found in diwi");
        }
        return new UserInfoModel(state);
    }
}
