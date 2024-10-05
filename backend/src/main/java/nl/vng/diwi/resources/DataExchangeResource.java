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
import nl.vng.diwi.dal.entities.DataExchangeState;
import nl.vng.diwi.models.DataExchangeModel;
import nl.vng.diwi.rest.VngBadRequestException;
import nl.vng.diwi.rest.VngNotFoundException;
import nl.vng.diwi.security.LoggedUser;
import nl.vng.diwi.security.UserActionConstants;
import nl.vng.diwi.services.DataExchangeService;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@Path("/dataexchange")
@RolesAllowed("BLOCKED_BY_DEFAULT") // This forces us to make sure each end-point has action(s) assigned, so we never have things open by default.
public class DataExchangeResource {

    private final VngRepository repo;
    private final DataExchangeService dataExchangeService;

    @Inject
    public DataExchangeResource(GenericRepository genericRepository, DataExchangeService dataExchangeService) {
        this.repo = new VngRepository(genericRepository.getDal().getSession());
        this.dataExchangeService = dataExchangeService;
    }

    @GET
    @RolesAllowed(UserActionConstants.VIEW_DATA_EXCHANGES)
    @Produces(MediaType.APPLICATION_JSON)
    public List<DataExchangeModel> getAllDataExchanges() {

        return dataExchangeService.getDataExchangeList(repo);

    }

    @GET
    @Path("/{id}")
    @RolesAllowed(UserActionConstants.VIEW_DATA_EXCHANGES)
    @Produces(MediaType.APPLICATION_JSON)
    public DataExchangeModel getDataExchange(@PathParam("id") UUID dataExchangeUuid) throws VngNotFoundException {

        return dataExchangeService.getDataExchangeModel(repo, dataExchangeUuid);

    }

    @POST
    @RolesAllowed(UserActionConstants.EDIT_DATA_EXCHANGES)
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public DataExchangeModel createDataExchange(DataExchangeModel dataExchangeModel, @Context LoggedUser loggedUser)
        throws VngBadRequestException, VngNotFoundException {

        try (AutoCloseTransaction transaction = repo.beginTransaction()) {

            String validationError = dataExchangeModel.validate();
            if (validationError != null) {
                throw new VngBadRequestException(validationError);
            }

            UUID dataExchangeUuid = dataExchangeService.createDataExchange(repo, dataExchangeModel, ZonedDateTime.now(), loggedUser.getUuid());
            transaction.commit();

            return dataExchangeService.getDataExchangeModel(repo, dataExchangeUuid);
        }
    }

    @PUT
    @Path("/{id}")
    @RolesAllowed(UserActionConstants.EDIT_DATA_EXCHANGES)
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public DataExchangeModel updateDataExchange(@PathParam("id") UUID dataExchangeUuid, DataExchangeModel dataExchangeModel, @Context LoggedUser loggedUser)
        throws VngNotFoundException, VngBadRequestException {

        String validationError = dataExchangeModel.validate();
        if (validationError != null) {
            throw new VngBadRequestException(validationError);
        }

        DataExchangeState oldState = repo.getDataExchangeDAO().getActiveDataExchangeStateByDataExchangeUuid(dataExchangeUuid);
        if (oldState == null) {
            throw new VngNotFoundException();
        }
        DataExchangeModel oldModel = new DataExchangeModel(oldState);

        dataExchangeModel.setId(dataExchangeUuid);
        if (!oldModel.equals(dataExchangeModel)) {

            try (AutoCloseTransaction transaction = repo.beginTransaction()) {
                dataExchangeService.updateDataExchange(repo, dataExchangeModel, ZonedDateTime.now(), loggedUser.getUuid());
                transaction.commit();
                repo.getSession().clear();
            }
        }

        return dataExchangeService.getDataExchangeModel(repo, dataExchangeUuid);
    }

    @DELETE
    @Path("/{id}")
    @RolesAllowed(UserActionConstants.EDIT_DATA_EXCHANGES)
    public void deleteDataExchange(ContainerRequestContext requestContext, @PathParam("id") UUID dataExchangeUuid) throws VngNotFoundException {

        var loggedUser = (LoggedUser) requestContext.getProperty("loggedUser");
        try (AutoCloseTransaction transaction = repo.beginTransaction()) {
            dataExchangeService.deleteDataExchangeState(repo, dataExchangeUuid, ZonedDateTime.now(), loggedUser.getUuid());
            transaction.commit();
        }
    }

}
