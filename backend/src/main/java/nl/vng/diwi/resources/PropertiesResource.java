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
import nl.vng.diwi.dal.entities.enums.PropertyKind;
import nl.vng.diwi.models.PropertyModel;
import nl.vng.diwi.rest.VngBadRequestException;
import nl.vng.diwi.rest.VngNotFoundException;
import nl.vng.diwi.security.LoggedUser;
import nl.vng.diwi.security.UserActionConstants;
import nl.vng.diwi.services.PropertiesService;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@Path("/properties")
@RolesAllowed("BLOCKED_BY_DEFAULT") // This forces us to make sure each end-point has action(s) assigned, so we never have things open by default.
public class PropertiesResource {

    private final VngRepository repo;
    private final PropertiesService propertiesService;

    @Inject
    public PropertiesResource(GenericRepository genericRepository, PropertiesService propertiesService) {
        this.repo = new VngRepository(genericRepository.getDal().getSession());
        this.propertiesService = propertiesService;
    }

    @GET
    @RolesAllowed({UserActionConstants.VIEW_OWN_PROJECTS, UserActionConstants.VIEW_OTHERS_PROJECTS})
    @Produces(MediaType.APPLICATION_JSON)
    public List<PropertyModel> getAllProperties(@QueryParam("objectType") ObjectType objectType, @QueryParam("disabled") Boolean disabled,
                                                @QueryParam("type") PropertyKind type) {

        return propertiesService.getAllProperties(repo, objectType, disabled, type);

    }

    @GET
    @Path("/{id}")
    @RolesAllowed({UserActionConstants.VIEW_OWN_PROJECTS, UserActionConstants.VIEW_OTHERS_PROJECTS})
    @Produces(MediaType.APPLICATION_JSON)
    public PropertyModel getProperty(@PathParam("id") UUID customPropertyUuid) throws VngNotFoundException {

        PropertyModel propertyModel = propertiesService.getProperty(repo, customPropertyUuid);

        if (propertyModel == null) {
            throw new VngNotFoundException("Custom property could not be found.");
        }

        return propertyModel;

    }

    @POST
    @RolesAllowed({UserActionConstants.EDIT_OWN_PROJECTS})
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public PropertyModel createCustomProperty(@Context LoggedUser loggedUser, PropertyModel propertyModel) throws VngBadRequestException {

        try (AutoCloseTransaction transaction = repo.beginTransaction()) {

            String validationError = propertyModel.validate();
            if (validationError != null) {
                throw new VngBadRequestException(validationError);
            }

            UUID customPropertyUuid = propertiesService.createCustomProperty(repo, propertyModel, ZonedDateTime.now(), loggedUser.getUuid());
            transaction.commit();

            return propertiesService.getProperty(repo, customPropertyUuid);
        }
    }

    @PUT
    @Path("/{id}")
    @RolesAllowed({UserActionConstants.EDIT_OWN_PROJECTS})
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public PropertyModel updateProperty(@Context LoggedUser loggedUser, @PathParam("id") UUID customPropertyUuid,
                                        PropertyModel propertyModel) throws VngNotFoundException, VngBadRequestException {

        try (AutoCloseTransaction transaction = repo.beginTransaction()) {

            String validationError = propertyModel.validate();
            if (validationError != null) {
                throw new VngBadRequestException(validationError);
            }

            propertyModel.setId(customPropertyUuid);
            propertiesService.updatePropertyNameOrValues(repo, propertyModel, ZonedDateTime.now(), loggedUser.getUuid());
            transaction.commit();

            return propertiesService.getProperty(repo, customPropertyUuid);
        }

    }

    @DELETE
    @Path("/{id}")
    @RolesAllowed({UserActionConstants.EDIT_OWN_PROJECTS})
    @Produces(MediaType.APPLICATION_JSON)
    public PropertyModel disableCustomProperty(@Context LoggedUser loggedUser, @PathParam("id") UUID customPropertyUuid)
        throws VngNotFoundException {

        try (AutoCloseTransaction transaction = repo.beginTransaction()) {

            propertiesService.disableCustomProperty(repo, customPropertyUuid, ZonedDateTime.now(), loggedUser.getUuid());
            transaction.commit();

            return propertiesService.getProperty(repo, customPropertyUuid);
        }

    }
}
