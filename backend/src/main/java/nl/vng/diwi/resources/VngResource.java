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
import nl.vng.diwi.models.SelectModel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import nl.vng.diwi.security.LoggedUser;
import nl.vng.diwi.services.VngService;

@Path("/vng")
@RolesAllowed({Admin})
public class VngResource {
    private static final Logger logger = LogManager.getLogger();

    private final VngRepository repo;
    private final VngService vngService;

    @Inject
    public VngResource(
        GenericRepository genericRepository,
        VngService vngService) {
        this.repo = new VngRepository(genericRepository.getDal().getSession());
        this.vngService = vngService;
    }

    @GET
    @Path("/organizations/all")
    @Produces(MediaType.APPLICATION_JSON)
    public List<SelectModel> getAllOrganizationStates(@Context LoggedUser loggedUser) {

        return vngService.getAllOrganizationStates(repo);

    }

}
