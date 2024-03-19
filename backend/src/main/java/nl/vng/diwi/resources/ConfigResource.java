package nl.vng.diwi.resources;

import io.swagger.v3.jaxrs2.integration.resources.BaseOpenApiResource;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import nl.vng.diwi.config.ProjectConfig;
import nl.vng.diwi.models.ConfigModel;
import nl.vng.diwi.rest.VngServerErrorException;


@Path("/config")
public class ConfigResource extends BaseOpenApiResource {

    private ProjectConfig projectConfig;

    @Inject
    public ConfigResource(ProjectConfig projectConfig) {
        this.projectConfig = projectConfig;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public ConfigModel getConfig() throws VngServerErrorException {

        return projectConfig.getConfigModel();
    }
}
