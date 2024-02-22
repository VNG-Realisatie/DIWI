package nl.vng.diwi.resources;

import static nl.vng.diwi.security.SecurityRoleConstants.Admin;

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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import nl.vng.diwi.security.LoggedUser;
import nl.vng.diwi.services.OrganizationsService;

@Path("/organizations")
@RolesAllowed({Admin})
public class OrganizationResource {
    private static final Logger logger = LogManager.getLogger();

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
