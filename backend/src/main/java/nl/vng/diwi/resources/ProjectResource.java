package nl.vng.diwi.resources;

import java.util.UUID;

import com.fasterxml.uuid.impl.UUIDUtil;
import nl.vng.diwi.dal.GenericRepository;
import nl.vng.diwi.dal.ProjectRepository;
import nl.vng.diwi.dal.entities.Project;
import nl.vng.diwi.models.ProjectModel;
import nl.vng.diwi.rest.VngBadRequestException;
import nl.vng.diwi.services.ProjectService;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/project")
public class ProjectResource {

    private ProjectRepository repo;
    private ProjectService projectService;

    @Inject
    public ProjectResource(
        GenericRepository genericRepository,
        ProjectService projectService) {
        this.repo = new ProjectRepository(genericRepository.getDal().getSession());
        this.projectService = projectService;
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Object getCurrentProjectState(@PathParam("id") String id) throws VngBadRequestException {
        UUID uuid = UUIDUtil.nilUUID();
        try {
            uuid = UUIDUtil.uuid(id);
        }
        catch (NumberFormatException e) {
            throw new VngBadRequestException("The provided id is not a valid UUID");
        }

        Project project = projectService.getCurrentState(repo, uuid);

        return new ProjectModel(project);
        //return project; //stub
    }
}
