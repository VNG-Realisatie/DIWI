package nl.vng.diwi.resources;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import nl.vng.diwi.dal.GenericRepository;
import nl.vng.diwi.dal.FilterPaginationSorting;
import nl.vng.diwi.dal.VngRepository;
import nl.vng.diwi.dal.entities.Project;
import nl.vng.diwi.dal.entities.enums.Confidentiality;
import nl.vng.diwi.models.ProjectListModel;
import nl.vng.diwi.models.ProjectUpdateModel;
import nl.vng.diwi.rest.VngBadRequestException;
import nl.vng.diwi.rest.VngNotFoundException;
import nl.vng.diwi.security.LoggedUser;
import nl.vng.diwi.services.ProjectService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static nl.vng.diwi.security.SecurityRoleConstants.Admin;

@Path("/projects")
@RolesAllowed({Admin})
public class ProjectsResource {
    private static final Logger logger = LogManager.getLogger();

    private final VngRepository repo;
    private final ProjectService projectService;

    @Inject
    public ProjectsResource(
        GenericRepository genericRepository,
        ProjectService projectService) {
        this.repo = new VngRepository(genericRepository.getDal().getSession());
        this.projectService = projectService;
    }

    @GET
    @Path("/table")
    @Produces(MediaType.APPLICATION_JSON)
    public List<ProjectListModel> getAllProjects(@Context LoggedUser loggedUser, @BeanParam FilterPaginationSorting filtering)
        throws VngBadRequestException {

        if (filtering.getSortColumn() != null && !ProjectListModel.SORTABLE_COLUMNS.contains(filtering.getSortColumn())) {
            throw new VngBadRequestException("Sort column not supported.");
        }

        if (filtering.getSortColumn() == null) {
            filtering.setSortColumn(ProjectListModel.DEFAULT_SORT_COLUMN);
        }

        return repo.getProjectsDAO().getProjectsTable(filtering);

    }

    @GET
    @Path("/table/size")
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String, Integer> getAllProjectsListSize(@Context LoggedUser loggedUser, @BeanParam FilterPaginationSorting filtering) {

        Integer projectsCount = repo.getProjectsDAO().getProjectsTableCount(filtering);

        return Map.of("size", projectsCount);
    }

    @POST
    @Path("/{id}/update")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateProject(@PathParam("id") UUID projectUuid, ProjectUpdateModel projectUpdateModel)
        throws VngNotFoundException, VngBadRequestException {

        Project project = repo.findById(Project.class, projectUuid);
        if (project == null) {
            throw new VngNotFoundException();
        }

        String validationError = projectUpdateModel.validate();
        if (validationError != null) {
            throw new VngBadRequestException(validationError);
        }

        switch (projectUpdateModel.getProperty()) {
            case projectColor -> projectService.updateProjectColor(repo, projectUuid, projectUpdateModel.getValue());
            case confidentialityLevel -> {
                Confidentiality newConfidentiality = Confidentiality.valueOf(projectUpdateModel.getValue());
                projectService.updateProjectConfidentialityLevel(repo, projectUuid, newConfidentiality);
            }
        }

        return Response.status(Response.Status.OK).build(); //TODO: return updated project??
    }
}
