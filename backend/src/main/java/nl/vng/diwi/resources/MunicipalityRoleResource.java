package nl.vng.diwi.resources;

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
import nl.vng.diwi.security.LoggedUser;
import nl.vng.diwi.security.SecurityRoleConstants;

@Path("/municipalityrole")
@RolesAllowed({SecurityRoleConstants.Admin})
public class MunicipalityRoleResource {

    private final VngRepository repo;

    @Inject
    public MunicipalityRoleResource(GenericRepository genericRepository) {
        this.repo = new VngRepository(genericRepository.getDal().getSession());
    }

    @GET
    @Path("/list")
    @Produces(MediaType.APPLICATION_JSON)
    public List<SelectModel> getAllMunicipalityRoles(@Context LoggedUser loggedUser) {

        return repo.getMunicipalityRoles();

    }

}
