package nl.vng.diwi.resources;

import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.MediaType;
import nl.vng.diwi.dal.GenericRepository;
import nl.vng.diwi.dal.VngRepository;
import nl.vng.diwi.models.BlueprintModel;
import nl.vng.diwi.security.LoggedUser;
import nl.vng.diwi.security.UserActionConstants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

@Path("/blueprints")
@RolesAllowed("BLOCKED_BY_DEFAULT") // This forces us to make sure each end-point has action(s) assigned, so we never have things open by default.
public class BlueprintResource {

    private static final Logger logger = LogManager.getLogger();

    private final VngRepository repo;

    @Inject
    public BlueprintResource(GenericRepository genericRepository) {
        this.repo = new VngRepository(genericRepository.getDal().getSession());
    }

    @GET
    @RolesAllowed(UserActionConstants.VIEW_ALL_BLUEPRINTS)
    @Produces(MediaType.APPLICATION_JSON)
    public List<BlueprintModel> getAllBlueprints(ContainerRequestContext requestContext) {

        var loggedUser = (LoggedUser) requestContext.getProperty("loggedUser");

        //TODO

        return null;
    }


}
