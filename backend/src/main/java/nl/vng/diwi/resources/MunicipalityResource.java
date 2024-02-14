package nl.vng.diwi.resources;

import nl.vng.diwi.dal.GenericRepository;
import nl.vng.diwi.dal.VngRepository;
import nl.vng.diwi.models.SelectModel;
import nl.vng.diwi.security.LoggedUser;
import nl.vng.diwi.security.SecurityRoleConstants;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

@Path("/municipality")
@RolesAllowed({SecurityRoleConstants.Admin})
public class MunicipalityResource {
    private static final Logger logger = LogManager.getLogger();

    private final VngRepository repo;

    @Inject
    public MunicipalityResource(GenericRepository genericRepository) {
        this.repo = new VngRepository(genericRepository.getDal().getSession());
    }

    @GET
    @Path("/list")
    @Produces(MediaType.APPLICATION_JSON)
    public List<SelectModel> getAllMunicipalities(@Context LoggedUser loggedUser) {

        return repo.getMunicipalities();

    }

}
