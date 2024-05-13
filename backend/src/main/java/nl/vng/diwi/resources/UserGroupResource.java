package nl.vng.diwi.resources;

import static nl.vng.diwi.security.UserActionConstants.CAN_OWN_PROJECTS;

import java.util.List;

import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import nl.vng.diwi.dal.GenericRepository;
import nl.vng.diwi.dal.VngRepository;
import nl.vng.diwi.models.UserGroupModel;
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
    public List<UserGroupModel> getAllUserGroups(@Context LoggedUser loggedUser) {

        return userGroupService.getAllUserGroups(repo);

    }

}
