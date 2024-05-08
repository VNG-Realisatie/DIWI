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
import nl.vng.diwi.models.OrganizationModel;
import nl.vng.diwi.security.LoggedUser;
import nl.vng.diwi.services.OrganizationsService;

@Path("/organizations")
@RolesAllowed({CAN_OWN_PROJECTS})
public class OrganizationResource {

    private final VngRepository repo;
    private final OrganizationsService organizationsService;

    @Inject
    public OrganizationResource(
        GenericRepository genericRepository,
        OrganizationsService organizationsService) {
        this.repo = new VngRepository(genericRepository.getDal().getSession());
        this.organizationsService = organizationsService;
    }

    @GET
    @Path("/list")
    @Produces(MediaType.APPLICATION_JSON)
    public List<OrganizationModel> getAllOrganization(@Context LoggedUser loggedUser) {

        return organizationsService.getAllOrganizations(repo);

    }

}
