package com.vng.security;

import static com.vng.security.SecurityRoleConstants.Admin;

import java.util.List;

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
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import com.vng.dal.AutoCloseTransaction;
import com.vng.dal.GenericRepository;
import com.vng.dal.VngRepository;
import com.vng.services.KeycloakService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.vng.models.ErrorResponse;
import com.vng.services.UserService;

@Path("/users")
@RolesAllowed({ Admin })
public class UserResource {
    private static final Logger logger = LogManager.getLogger();

    private final SecurityRepository securityRepository;
    private final VngRepository repo;
    private final KeycloakService keycloak;
    private final UserService userService;
    private final MailService mailService;

    @Inject
    public UserResource(GenericRepository genericRepository,
                        KeycloakService keycloak,
                        UserService userService,
                        MailService mailService) {
        this.mailService = mailService;
        this.securityRepository = new SecurityRepository(genericRepository.getDal().getSession());
        this.repo = new VngRepository(genericRepository.getDal().getSession());
        this.keycloak = keycloak;
        this.userService = userService;
    }

    @GET
    @Path("/all")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllUsers() {
//        var users = keycloak.getUsers();
//
//        for (var user : users) {
//            userService.updateUserFromOidc(
//                    securityRepository,
//                    user.getUuid(),
//                    user.getName(),
//                    user.getEmail(),
//                    user.getRole());
//        }

        List<User> userEntitiesList = repo.findAll(User.class);
        List<UserModel> models = userEntitiesList.stream()
                .map(UserModel::fromEntityToModel)
                .toList();
        return Response.status(Response.Status.OK).entity(models).build();
    }

    @POST
    @Path("/add")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createUser(UserModel newUserModel) {
        if (newUserModel.getEmail() == null || newUserModel.getEmail().isEmpty() || newUserModel.getName() == null
                || newUserModel.getUserRole() == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity(new ErrorResponse("Missing info for user")).build();
        }

        User newEmailAlreadyUsed = securityRepository.getUserByEmail(newUserModel.getEmail());
        if (newEmailAlreadyUsed != null) {
            return Response.status(Response.Status.FORBIDDEN).entity(new ErrorResponse("There already is a user with that email.")).build();
        }

        User newUser = new User(newUserModel.getEmail(), newUserModel.getName(), newUserModel.getUserRole());

        try (AutoCloseTransaction transaction = securityRepository.beginTransaction()) {
            securityRepository.persist(newUser);
            transaction.commit();
            logger.info("New user created: {}", newUser.getEmail());
        } catch (Exception ex) {
            logger.error("Error while creating new user.", ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }

//        try {
//            keycloak.addOrUpdateUser(newUser);
//        } catch (Exception ex) {
//            logger.error("Error while creating new user.", ex);
//            return Response
//                    .status(Response.Status.INTERNAL_SERVER_ERROR)
//                    .entity(new ErrorResponse("Error updating or creating user in keycloak"))
//                    .build();
//        }
//
//        try {
//            mailService.sendWelcomeMail(newUserModel.getEmail());
//        } catch (MailException e) {
//            logger.error("Failed to send welcome mail", e);
//            return Response
//                    .status(Response.Status.INTERNAL_SERVER_ERROR)
//                    .entity(new ErrorResponse("Failed to send welcome mail"))
//                    .build();
//        }

        UserModel persistedUserModel = UserModel.fromEntityToModel(newUser);
        return Response.status(Response.Status.OK).entity(persistedUserModel).build();
    }

    @POST
    @Path("/{userId}/sendWelcomeMail")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response sendWelcomeMail(@PathParam("userId") Long userId, @Context LoggedUser loggedUser) {

        User siteUser = securityRepository.findById(User.class, userId);
        if (siteUser == null) {
            return Response.status(Response.Status.NOT_FOUND).entity(new ErrorResponse("Unknown used id.")).build();
        }

        try {
            mailService.sendWelcomeMail(siteUser.getEmail());
        } catch (MailException e) {
            logger.error("Failed to send welcome mail", e);
            return Response
                    .status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("Failed to send welcome mail"))
                    .build();
        }
//
//        try {
//            keycloak.triggerActionsEmail(siteUser);
//        } catch (FindUserException e) {
//            logger.error("Could not find user in keycloak while sending welcome mail", e);
//            return Response
//                    .status(Response.Status.NOT_FOUND)
//                    .entity(new ErrorResponse("Could not find user in the keycloak database"))
//                    .build();
//        } catch (BadRequestException e) {
//            logger.error("Error sending welcome email: {}", e.getResponse().readEntity(String.class));
//            logger.error("Error sending welcome email", e);
//
//            return Response
//                    .serverError()
//                    .entity(new ErrorResponse("Error while sending welcome mail"))
//                    .build();
//        }

        return Response.ok().build();
    }

    @PUT
    @Path("/{userId}/update")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateUser(@PathParam("userId") Long userId, UserModel updatedUserModel,
            @Context LoggedUser loggedUser) {

        User siteUser = securityRepository.findById(User.class, userId);
        if (siteUser == null) {
            return Response.status(Response.Status.NOT_FOUND).entity(new ErrorResponse("Unknown used id.")).build();
        }
        if (updatedUserModel.getEmail() != null && !updatedUserModel.getEmail().isEmpty()) {
            User newEmailAlreadyUsed = securityRepository.getUserByEmail(updatedUserModel.getEmail());
            if (newEmailAlreadyUsed != null && !newEmailAlreadyUsed.getId().equals(userId)) {
                return Response.status(Response.Status.FORBIDDEN).entity(new ErrorResponse("There already is a user with that email.")).build();
            }
            siteUser.setEmail(updatedUserModel.getEmail());
        }
        if (updatedUserModel.getName() != null && !updatedUserModel.getName().isEmpty()) {
            siteUser.setName(updatedUserModel.getName());
        }
        if (!loggedUser.getId().equals(userId)) {
            // only admin can change role and disabled flag, but not for himself
            if (updatedUserModel.getDisabled() != null) {
                siteUser.setDisabled(updatedUserModel.getDisabled());
            }
            if (updatedUserModel.getUserRole() != null) {
                // do not change own role
                siteUser.setRole(updatedUserModel.getUserRole());
            }
        }

        try (AutoCloseTransaction transaction = securityRepository.beginTransaction()) {
            securityRepository.saveOrUpdate(siteUser);
            transaction.commit();
            logger.info("User updated: {}", siteUser.getEmail());
        } catch (Exception ex) {
            logger.error("Error while updating user.", ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
//        try {
//            keycloak.addOrUpdateUser(siteUser);
//        } catch (Exception ex) {
//            logger.error("Error while updating user in keycloak.", ex);
//            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
//        }

        UserModel persistedUserModel = UserModel.fromEntityToModel(siteUser);
        return Response.status(Response.Status.OK).entity(persistedUserModel).build();
    }

    @DELETE
    @Path("/{userId}/delete")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteUser(@PathParam("userId") Long userId, @Context LoggedUser loggedUser) {

        if (loggedUser.getId().equals(userId)) {
            return Response.status(Response.Status.FORBIDDEN).entity(new ErrorResponse("Cannot delete own Admin account.")).build();
        }

        User user = securityRepository.findById(User.class, userId);
        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND).entity(new ErrorResponse("Unknown used id.")).build();
        }

        try (AutoCloseTransaction transaction = securityRepository.beginTransaction()) {
            securityRepository.saveOrUpdate(user);
            securityRepository.delete(user);
            transaction.commit();
            logger.info("User deleted: {}", user.getEmail());
        } catch (Exception ex) {
            logger.error("Error while deleting user.", ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }

//        try {
//            keycloak.deleteUser(user);
//        } catch (Exception ex) {
//            logger.error("Error while deleting user in keycloak.", ex);
//            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
//        }
        return Response.status(Response.Status.OK).build();
    }

    @GET
    @Path("/userinfo")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(Admin)
    public Response getUserInfo(@Context LoggedUser loggedUser) {
        logger.info("user info");
        User user = securityRepository.findById(User.class, loggedUser.getId());
        UserModel model = UserModel.fromEntityToModel(user);
        return Response.status(Response.Status.OK).entity(model).build();
    }

    @GET
    @Path("/{userId}/userinfo")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUserInfoByUserId(@PathParam("userId") Long userId, @Context LoggedUser loggedUser) {
        User user = securityRepository.findById(User.class, userId);
        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND).entity(new ErrorResponse("User with this ID not found")).build();
        }
        UserModel model = UserModel.fromEntityToModel(user);
        return Response.status(Response.Status.OK).entity(model).build();
    }
}
