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
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import nl.vng.diwi.dal.AutoCloseTransaction;
import nl.vng.diwi.dal.GenericRepository;
import nl.vng.diwi.dal.VngRepository;
import nl.vng.diwi.dal.entities.enums.ObjectType;
import nl.vng.diwi.models.CustomPropertyModel;
import nl.vng.diwi.rest.VngBadRequestException;
import nl.vng.diwi.rest.VngNotFoundException;
import nl.vng.diwi.security.LoggedUser;
import nl.vng.diwi.security.SecurityRoleConstants;
import nl.vng.diwi.services.CustomPropertiesService;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@Path("/customproperties")
@RolesAllowed({SecurityRoleConstants.Admin})
public class CustomPropertiesResource {

    private final VngRepository repo;
    private final CustomPropertiesService customPropertiesService;

    @Inject
    public CustomPropertiesResource(GenericRepository genericRepository, CustomPropertiesService customPropertiesService) {
        this.repo = new VngRepository(genericRepository.getDal().getSession());
        this.customPropertiesService = customPropertiesService;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<CustomPropertyModel> getAllCustomProperties(@QueryParam("objectType") ObjectType objectType, @QueryParam("disabled") Boolean disabled) {

        return customPropertiesService.getAllCustomProperties(repo, objectType, disabled);

    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public CustomPropertyModel getCustomProperty(@PathParam("id") UUID customPropertyUuid) throws VngNotFoundException {

        CustomPropertyModel customPropertyModel = customPropertiesService.getCustomProperty(repo, customPropertyUuid);

        if (customPropertyModel == null) {
            throw new VngNotFoundException("Custom property could not be found.");
        }

        return customPropertyModel;

    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public CustomPropertyModel createCustomProperty(@Context LoggedUser loggedUser, CustomPropertyModel customPropertyModel) throws VngBadRequestException {

        try (AutoCloseTransaction transaction = repo.beginTransaction()) {

            String validationError = customPropertyModel.validate();
            if (validationError != null) {
                throw new VngBadRequestException(validationError);
            }

            UUID customPropertyUuid = customPropertiesService.createCustomProperty(repo, customPropertyModel, ZonedDateTime.now(), loggedUser.getUuid());
            transaction.commit();

            return customPropertiesService.getCustomProperty(repo, customPropertyUuid);
        }
    }

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public CustomPropertyModel updateCustomProperty(@Context LoggedUser loggedUser, @PathParam("id") UUID customPropertyUuid,
                                                    CustomPropertyModel customPropertyModel) throws VngNotFoundException, VngBadRequestException {

        try (AutoCloseTransaction transaction = repo.beginTransaction()) {

            String validationError = customPropertyModel.validate();
            if (validationError != null) {
                throw new VngBadRequestException(validationError);
            }

            customPropertyModel.setId(customPropertyUuid);
            customPropertiesService.updateCustomPropertyNameOrValues(repo, customPropertyModel, ZonedDateTime.now(), loggedUser.getUuid());
            transaction.commit();

            return customPropertiesService.getCustomProperty(repo, customPropertyUuid);
        }

    }

    @DELETE
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public CustomPropertyModel disableCustomProperty(@Context LoggedUser loggedUser, @PathParam("id") UUID customPropertyUuid)
        throws VngNotFoundException {

        try (AutoCloseTransaction transaction = repo.beginTransaction()) {

            customPropertiesService.disableCustomProperty(repo, customPropertyUuid, ZonedDateTime.now(), loggedUser.getUuid());
            transaction.commit();

            return customPropertiesService.getCustomProperty(repo, customPropertyUuid);
        }

    }
}
