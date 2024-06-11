package nl.vng.diwi.resources;

import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import nl.vng.diwi.config.ProjectConfig;
import nl.vng.diwi.models.ConfigModel;
import nl.vng.diwi.rest.VngServerErrorException;
import nl.vng.diwi.security.UserActionConstants;


@Path("/config")
@RolesAllowed("BLOCKED_BY_DEFAULT") // This forces us to make sure each end-point has action(s) assigned, so we never have things open by default.
public class ConfigResource {

    private ProjectConfig projectConfig;

    @Inject
    public ConfigResource(ProjectConfig projectConfig) {
        this.projectConfig = projectConfig;
    }

    @GET
    @RolesAllowed({UserActionConstants.VIEW_CONFIG})
    @Produces(MediaType.APPLICATION_JSON)
    public ConfigModel getConfig() throws VngServerErrorException {

        return projectConfig.getConfigModel();
    }
}
