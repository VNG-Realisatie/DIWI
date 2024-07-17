package nl.vng.diwi.resources;

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
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import nl.vng.diwi.dal.AutoCloseTransaction;
import nl.vng.diwi.dal.GenericRepository;
import nl.vng.diwi.dal.VngRepository;
import nl.vng.diwi.dal.entities.BlueprintSqlModel;
import nl.vng.diwi.models.BlueprintModel;
import nl.vng.diwi.rest.VngBadRequestException;
import nl.vng.diwi.rest.VngNotFoundException;
import nl.vng.diwi.security.LoggedUser;
import nl.vng.diwi.security.UserActionConstants;
import nl.vng.diwi.services.DashboardService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@Path("/blueprints")
@RolesAllowed("BLOCKED_BY_DEFAULT") // This forces us to make sure each end-point has action(s) assigned, so we never have things open by default.
public class BlueprintResource {

    private static final Logger logger = LogManager.getLogger();

    private final VngRepository repo;
    private final DashboardService dashboardService;

    @Inject
    public BlueprintResource(GenericRepository genericRepository, DashboardService dashboardService) {
        this.repo = new VngRepository(genericRepository.getDal().getSession());
        this.dashboardService = dashboardService;
    }

    @GET
    @RolesAllowed(UserActionConstants.VIEW_ALL_BLUEPRINTS)
    @Produces(MediaType.APPLICATION_JSON)
    public List<BlueprintModel> getAllBlueprints() {

        List<BlueprintSqlModel> sqlModels = repo.getBlueprintDAO().getBlueprintsList(null);
        return sqlModels.stream().map(BlueprintModel::new).toList();

    }

    @GET
    @Path("/{id}")
    @RolesAllowed(UserActionConstants.VIEW_ALL_BLUEPRINTS)
    @Produces(MediaType.APPLICATION_JSON)
    public BlueprintModel getBlueprint(@PathParam("id") UUID blueprintUuid) throws VngNotFoundException {

        BlueprintSqlModel sqlModel = repo.getBlueprintDAO().getBlueprintById(blueprintUuid);
        if (sqlModel == null) {
            throw new VngNotFoundException();
        }

        return new BlueprintModel(sqlModel);
    }

    @POST
    @RolesAllowed(UserActionConstants.EDIT_ALL_BLUEPRINTS)
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public BlueprintModel createBlueprint(BlueprintModel blueprintModel, @Context LoggedUser loggedUser) throws VngBadRequestException {

        try (AutoCloseTransaction transaction = repo.beginTransaction()) {

            String validationError = blueprintModel.validate();
            if (validationError != null) {
                throw new VngBadRequestException(validationError);
            }

            UUID blueprintUuid = dashboardService.createBlueprint(repo, blueprintModel, ZonedDateTime.now(), loggedUser.getUuid());
            transaction.commit();

            return new BlueprintModel(repo.getBlueprintDAO().getBlueprintById(blueprintUuid));
        }
    }

    @PUT
    @Path("/{id}")
    @RolesAllowed(UserActionConstants.EDIT_ALL_BLUEPRINTS)
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public BlueprintModel updateBlueprint(@PathParam("id") UUID blueprintUuid, BlueprintModel blueprintModel, @Context LoggedUser loggedUser)
        throws VngNotFoundException, VngBadRequestException {

        String validationError = blueprintModel.validate();
        if (validationError != null) {
            throw new VngBadRequestException(validationError);
        }

        BlueprintSqlModel oldSqlModel = repo.getBlueprintDAO().getBlueprintById(blueprintUuid);
        if (oldSqlModel == null) {
            throw new VngNotFoundException();
        }
        BlueprintModel oldModel = new BlueprintModel(oldSqlModel);

        if (!oldModel.isSameAs(blueprintModel)) {
            blueprintModel.setUuid(blueprintUuid);

            try (AutoCloseTransaction transaction = repo.beginTransaction()) {
                dashboardService.updateBlueprint(repo, blueprintModel, ZonedDateTime.now(), loggedUser.getUuid());
                transaction.commit();
                repo.getSession().clear();
            }
        }

        return new BlueprintModel(repo.getBlueprintDAO().getBlueprintById(blueprintUuid));
    }

    @DELETE
    @Path("/{id}")
    @RolesAllowed(UserActionConstants.EDIT_ALL_BLUEPRINTS)
    public void deleteBlueprint(ContainerRequestContext requestContext, @PathParam("id") UUID blueprintUuid) throws VngNotFoundException {

        BlueprintSqlModel sqlModel = repo.getBlueprintDAO().getBlueprintById(blueprintUuid);
        if (sqlModel == null) {
            throw new VngNotFoundException();
        }

        var loggedUser = (LoggedUser) requestContext.getProperty("loggedUser");
        try (AutoCloseTransaction transaction = repo.beginTransaction()) {
            dashboardService.deleteBlueprint(repo, blueprintUuid, loggedUser.getUuid());
            transaction.commit();
        }
    }

}
