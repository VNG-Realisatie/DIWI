package nl.vng.diwi.resources;

import jakarta.ws.rs.QueryParam;
import nl.vng.diwi.dal.GenericRepository;
import nl.vng.diwi.dal.VngRepository;
import nl.vng.diwi.security.LoggedUser;
import nl.vng.diwi.security.SecurityRoleConstants;
import nl.vng.diwi.models.SelectModel;
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
import java.util.UUID;

@Path("/wijk")
@RolesAllowed({SecurityRoleConstants.Admin})
public class WijkResource {
    private static final Logger logger = LogManager.getLogger();

    private final VngRepository repo;

    @Inject
    public WijkResource(GenericRepository genericRepository) {
        this.repo = new VngRepository(genericRepository.getDal().getSession());
    }

    @GET
    @Path("/list")
    @Produces(MediaType.APPLICATION_JSON)
    public List<SelectModel> getAllWijks(@Context LoggedUser loggedUser, @QueryParam("gemeenteId") List<UUID> gemeeenteIds) {

        return repo.getWijks(gemeeenteIds);

    }

}
