package nl.vng.diwi.resources;

import io.swagger.v3.oas.integration.SwaggerConfiguration;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import jakarta.annotation.security.RolesAllowed;
import jakarta.servlet.ServletConfig;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import nl.vng.diwi.security.UserActionConstants;
import io.swagger.v3.jaxrs2.integration.resources.BaseOpenApiResource;
import io.swagger.v3.oas.annotations.Operation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Path("/openapi.{type:json|yaml}")
@RolesAllowed("BLOCKED_BY_DEFAULT") // This forces us to make sure each end-point has action(s) assigned, so we never have things open by default.
public class VngOpenApiResource extends BaseOpenApiResource {

    private static final Logger logger = LogManager.getLogger();

    private static Set<String> RESOURCE_PACKAGES = Set.of("nl.vng.diwi.resources");
    private static Set<String> RESOURCE_CLASSES = new HashSet<>();

    static {
        RESOURCE_PACKAGES.forEach(rp -> {
            try {
                RESOURCE_CLASSES.addAll(getClasses(rp));
            } catch (Exception e) {
                logger.error(e);
            }
        });
    }

    @Context
    ServletConfig config;

    @Context
    Application app;

    private static List<String> getClasses(String packageName) throws IOException {

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        String path = packageName.replace('.', '/');
        Enumeration<URL> resources = classLoader.getResources(path);
        List<File> dirs = new ArrayList<File>();
        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            dirs.add(new File(resource.getFile()));
        }
        List<String> classes = new ArrayList<>();
        for (File dir : dirs) {
            File[] files = dir.listFiles();
            for (File file : files) {
                if (!file.isDirectory() && file.getName().endsWith(".class")) {
                    classes.add(packageName + '.' + file.getName().substring(0, file.getName().length() - 6));
                }
            }
        }
        return classes;
    }

    @GET
    @RolesAllowed(UserActionConstants.VIEW_API)
    @Produces({MediaType.APPLICATION_JSON, "application/yaml"})
    @Operation(hidden = true)
    public Response getOpenApi(@Context HttpHeaders headers,
                               @Context UriInfo uriInfo,
                               @PathParam("type") String type) throws Exception {

        OpenAPI oas = new OpenAPI().info(new Info().title("DIWI"));
        SwaggerConfiguration oasConfig = new SwaggerConfiguration()
            .openAPI(oas)
            .prettyPrint(true)
            .resourceClasses(RESOURCE_CLASSES);

        setOpenApiConfiguration(oasConfig);

        return super.getOpenApi(headers, config, app, uriInfo, type);
    }
}
