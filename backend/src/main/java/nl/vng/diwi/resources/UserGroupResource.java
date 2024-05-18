package nl.vng.diwi.resources;

import static nl.vng.diwi.security.UserActionConstants.CAN_OWN_PROJECTS;

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
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import nl.vng.diwi.dal.AutoCloseTransaction;
import nl.vng.diwi.dal.GenericRepository;
import nl.vng.diwi.dal.VngRepository;
import nl.vng.diwi.dal.entities.UserGroup;
import nl.vng.diwi.models.UserGroupModel;
import nl.vng.diwi.models.UserGroupUserModel;
import nl.vng.diwi.rest.VngBadRequestException;
import nl.vng.diwi.rest.VngNotFoundException;
import nl.vng.diwi.security.LoggedUser;
import nl.vng.diwi.services.UserGroupService;

@Path("/groups")
@RolesAllowed({CAN_OWN_PROJECTS})
public class UserGroupResource {

    private final VngRepository repo;
    private final UserGroupService userGroupService;

    @Inject
    public UserGroupResource(
        GenericRepository genericRepository,
        UserGroupService userGroupService) {
        this.repo = new VngRepository(genericRepository.getDal().getSession());
        this.userGroupService = userGroupService;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<UserGroupModel> getAllUserGroups(@QueryParam("includeSingleUser") boolean includeSingleUser) {

        return userGroupService.getAllUserGroups(repo, includeSingleUser);

    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public UserGroupModel createUserGroup(UserGroupModel newUserGroup, @Context LoggedUser loggedUser) throws VngBadRequestException {

        try (AutoCloseTransaction transaction = repo.beginTransaction()) {

            if (newUserGroup.getName() == null || newUserGroup.getName().isEmpty()) {
                throw new VngBadRequestException("Missing usergroup name.");
            }

            UserGroup newGroup = userGroupService.createUserGroup(repo, newUserGroup, loggedUser.getUuid());
            transaction.commit();

            List<UserGroupUserModel> userGroupModel = repo.getUsergroupDAO().getUserGroupUsers(newGroup.getId());
            return UserGroupModel.fromUserGroupUserModelListToUserGroupModelList(userGroupModel).get(0);
        }
    }

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public UserGroupModel updateUserGroup(@PathParam("id") UUID groupId, UserGroupModel updatedUserGroup, @Context LoggedUser loggedUser) throws VngBadRequestException, VngNotFoundException {

        try (AutoCloseTransaction transaction = repo.beginTransaction()) {

            if (updatedUserGroup.getName() == null || updatedUserGroup.getName().isEmpty()) {
                throw new VngBadRequestException("Missing usergroup name.");
            }
            updatedUserGroup.setUuid(groupId);
            userGroupService.updateUserGroup(repo, updatedUserGroup, loggedUser.getUuid());
            transaction.commit();

            List<UserGroupUserModel> userGroupModel = repo.getUsergroupDAO().getUserGroupUsers(groupId);
            return UserGroupModel.fromUserGroupUserModelListToUserGroupModelList(userGroupModel).get(0);
        }
    }

    @DELETE
    @Path("/{id}")
    public void deleteUserGroup(@Context LoggedUser loggedUser, @PathParam("id") UUID groupId) throws VngNotFoundException, VngBadRequestException {

        try (AutoCloseTransaction transaction = repo.beginTransaction()) {
            userGroupService.deleteUserGroup(repo, groupId, loggedUser.getUuid());
            transaction.commit();
        }
    }
}
