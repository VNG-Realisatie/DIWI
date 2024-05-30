package nl.vng.diwi.resources;

import jakarta.ws.rs.container.ContainerRequestContext;
import nl.vng.diwi.security.UserActionConstants;

import java.util.List;
import java.util.UUID;

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
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import nl.vng.diwi.dal.AutoCloseTransaction;
import nl.vng.diwi.dal.entities.UserGroup;
import nl.vng.diwi.models.UserGroupModel;
import nl.vng.diwi.models.UserGroupUserModel;
import nl.vng.diwi.rest.VngBadRequestException;
import nl.vng.diwi.rest.VngNotFoundException;
import nl.vng.diwi.security.LoggedUser;
import nl.vng.diwi.services.UserGroupService;

@Path("/groups")
@RolesAllowed("BLOCKED_BY_DEFAULT") // This forces us to make sure each end-point has action(s) assigned, so we never have things open by default.
public class UserGroupResource {

    private final UserGroupService userGroupService;

    @Inject
    public UserGroupResource(UserGroupService userGroupService) {
        this.userGroupService = userGroupService;
    }

    @GET
    @RolesAllowed({UserActionConstants.VIEW_GROUPS})
    @Produces(MediaType.APPLICATION_JSON)
    public List<UserGroupModel> getAllUserGroups(@QueryParam("includeSingleUser") boolean includeSingleUser) {

        return userGroupService.getAllUserGroups(includeSingleUser);

    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public UserGroupModel createUserGroup(UserGroupModel newUserGroup, ContainerRequestContext requestContext) throws VngBadRequestException {

        var loggedUser = (LoggedUser) requestContext.getProperty("loggedUser");

        try (AutoCloseTransaction transaction = userGroupService.getUserGroupDAO().beginTransaction()) {

            if (newUserGroup.getName() == null || newUserGroup.getName().isEmpty()) {
                throw new VngBadRequestException("Missing usergroup name.");
            }

            UserGroup newGroup = userGroupService.createUserGroup(newUserGroup, loggedUser.getUuid());
            transaction.commit();

            List<UserGroupUserModel> userGroupModel = userGroupService.getUserGroupDAO().getUserGroupUsers(newGroup.getId());
            return UserGroupModel.fromUserGroupUserModelListToUserGroupModelList(userGroupModel).get(0);
        }
    }

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public UserGroupModel updateUserGroup(@PathParam("id") UUID groupId, UserGroupModel updatedUserGroup, ContainerRequestContext requestContext) throws VngBadRequestException, VngNotFoundException {

        var loggedUser = (LoggedUser) requestContext.getProperty("loggedUser");

        try (AutoCloseTransaction transaction = userGroupService.getUserGroupDAO().beginTransaction()) {

            if (updatedUserGroup.getName() == null || updatedUserGroup.getName().isEmpty()) {
                throw new VngBadRequestException("Missing usergroup name.");
            }
            updatedUserGroup.setUuid(groupId);
            userGroupService.updateUserGroup(updatedUserGroup, loggedUser.getUuid());
            transaction.commit();

            List<UserGroupUserModel> userGroupModel = userGroupService.getUserGroupDAO().getUserGroupUsers(groupId);
            return UserGroupModel.fromUserGroupUserModelListToUserGroupModelList(userGroupModel).get(0);
        }
    }

    @DELETE
    @Path("/{id}")
    public void deleteUserGroup(@PathParam("id") UUID groupId, ContainerRequestContext requestContext) throws VngNotFoundException, VngBadRequestException {

        var loggedUser = (LoggedUser) requestContext.getProperty("loggedUser");

        try (AutoCloseTransaction transaction = userGroupService.getUserGroupDAO().beginTransaction()) {
            userGroupService.deleteUserGroup(groupId, loggedUser.getUuid());
            transaction.commit();
        }
    }
}
