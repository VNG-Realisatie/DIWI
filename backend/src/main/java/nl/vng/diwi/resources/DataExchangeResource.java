package nl.vng.diwi.resources;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import jakarta.ws.rs.core.StreamingOutput;
import nl.vng.diwi.config.ProjectConfig;
import nl.vng.diwi.dal.AutoCloseTransaction;
import nl.vng.diwi.dal.GenericRepository;
import nl.vng.diwi.dal.VngRepository;
import nl.vng.diwi.dal.entities.DataExchangeType;
import nl.vng.diwi.models.ConfigModel;
import nl.vng.diwi.models.DataExchangeExportModel;
import nl.vng.diwi.models.DataExchangeModel;
import nl.vng.diwi.models.PropertyModel;
import nl.vng.diwi.rest.VngBadRequestException;
import nl.vng.diwi.rest.VngNotFoundException;
import nl.vng.diwi.security.LoggedUser;
import nl.vng.diwi.security.UserActionConstants;
import nl.vng.diwi.services.DataExchangeExportError;
import nl.vng.diwi.services.DataExchangeService;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Path("/dataexchange")
@RolesAllowed("BLOCKED_BY_DEFAULT") // This forces us to make sure each end-point has action(s) assigned, so we never have things open by default.
public class DataExchangeResource {

    private final VngRepository repo;
    private final DataExchangeService dataExchangeService;
    private final ConfigModel configModel;

    public static final ObjectMapper MAPPER = new ObjectMapper();

    @Inject
    public DataExchangeResource(GenericRepository genericRepository, DataExchangeService dataExchangeService, ProjectConfig projectConfig) {
        this.repo = new VngRepository(genericRepository.getDal().getSession());
        this.dataExchangeService = dataExchangeService;
        this.configModel = projectConfig.getConfigModel();
    }

    @GET
    @RolesAllowed(UserActionConstants.VIEW_DATA_EXCHANGES)
    @Produces(MediaType.APPLICATION_JSON)
    public List<DataExchangeModel> getAllDataExchanges() {

        return dataExchangeService.getDataExchangeList(repo, false);

    }

    @GET
    @Path("/types")
    @RolesAllowed(UserActionConstants.VIEW_DATA_EXCHANGES)
    @Produces(MediaType.APPLICATION_JSON)
    public List<DataExchangeType> getTypes() {
        return List.of(DataExchangeType.values());
    }

    @GET
    @Path("/{id}")
    @RolesAllowed(UserActionConstants.VIEW_DATA_EXCHANGES)
    @Produces(MediaType.APPLICATION_JSON)
    public DataExchangeModel getDataExchange(@PathParam("id") UUID dataExchangeUuid) throws VngNotFoundException {

        return dataExchangeService.getDataExchangeModel(repo, dataExchangeUuid, false);

    }

    @POST
    @RolesAllowed(UserActionConstants.EDIT_DATA_EXCHANGES)
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public DataExchangeModel createDataExchange(DataExchangeModel dataExchangeModel, @Context LoggedUser loggedUser)
        throws VngBadRequestException, VngNotFoundException {

        try (AutoCloseTransaction transaction = repo.beginTransaction()) {

            String validationError = dataExchangeModel.validateDxState();
            if (validationError != null) {
                throw new VngBadRequestException(validationError);
            }

            UUID dataExchangeUuid = dataExchangeService.createDataExchange(repo, dataExchangeModel, ZonedDateTime.now(), loggedUser.getUuid());
            transaction.commit();

            return dataExchangeService.getDataExchangeModel(repo, dataExchangeUuid, false);
        }
    }

    @PUT
    @Path("/{id}")
    @RolesAllowed(UserActionConstants.EDIT_DATA_EXCHANGES)
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public DataExchangeModel updateDataExchange(@PathParam("id") UUID dataExchangeUuid, DataExchangeModel dataExchangeModel, @Context LoggedUser loggedUser)
        throws VngNotFoundException, VngBadRequestException {

        dataExchangeModel.setId(dataExchangeUuid);
        String validationError = dataExchangeModel.validateDxState();
        if (validationError != null) {
            throw new VngBadRequestException(validationError);
        }

        DataExchangeModel oldModel = dataExchangeService.getDataExchangeModel(repo, dataExchangeUuid, true);
        String validateTemplateError = dataExchangeModel.validateTemplateFields(oldModel.getProperties());
        if (validateTemplateError != null) {
            throw new VngBadRequestException(validateTemplateError);
        }

        List<PropertyModel> propertyModels = repo.getPropertyDAO().getPropertiesList(null, false, null);
        String validatePropertiesError = dataExchangeModel.validateDxProperties(propertyModels);
        if (validatePropertiesError != null) {
            throw new VngBadRequestException(validatePropertiesError);
        }

        List<DataExchangeModel.ValidationError> validationErrors = dataExchangeModel.validateConfigurationComplete(propertyModels);

        if (dataExchangeModel.getApiKey() == null) {
            dataExchangeModel.setApiKey(oldModel.getApiKey());
        }
        dataExchangeModel.setType(oldModel.getType());

        try (AutoCloseTransaction transaction = repo.beginTransaction()) {
            dataExchangeService.updateDataExchange(repo, dataExchangeModel, oldModel, ZonedDateTime.now(), loggedUser.getUuid());
            transaction.commit();
            repo.getSession().clear();
        }

        DataExchangeModel updatedModel = dataExchangeService.getDataExchangeModel(repo, dataExchangeUuid, false);
        updatedModel.setValidationErrors(validationErrors);
        return updatedModel;
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

    @POST
    @Path("/{id}/export")
    @RolesAllowed(UserActionConstants.EDIT_DATA_EXCHANGES)
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public void exportProjects(@PathParam("id") UUID dataExchangeUuid, DataExchangeExportModel dataExchangeExportModel, @Context LoggedUser loggedUser)
        throws VngBadRequestException, VngNotFoundException {

        List<DataExchangeExportError> errors = new ArrayList<>();
        StreamingOutput exportObj = dataExchangeService.getExportObject(repo, configModel, dataExchangeUuid, dataExchangeExportModel, errors, loggedUser);
        if (!errors.isEmpty()) {
            throw new VngBadRequestException("Could not export data");
        }
        dataExchangeService.exportProject(exportObj, dataExchangeExportModel.getToken());
    }

    @POST
    @Path("/{id}/download")
    @RolesAllowed(UserActionConstants.EDIT_DATA_EXCHANGES)
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public StreamingOutput downloadProjects(@PathParam("id") UUID dataExchangeUuid, DataExchangeExportModel dataExchangeExportModel, @Context LoggedUser loggedUser)
        throws VngNotFoundException, VngBadRequestException {

        List<DataExchangeExportError> errors = new ArrayList<>();

        StreamingOutput exportObj = dataExchangeService.getExportObject(repo, configModel, dataExchangeUuid, dataExchangeExportModel, errors, loggedUser);

        if (errors.isEmpty()) {
            return exportObj;
        } else {
            throw new VngBadRequestException(errors);
        }
    }
}
