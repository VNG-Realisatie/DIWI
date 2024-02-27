package nl.vng.diwi.resources;

import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.BeanParam;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import nl.vng.diwi.dal.AutoCloseTransaction;
import nl.vng.diwi.dal.FilterPaginationSorting;
import nl.vng.diwi.dal.GenericRepository;
import nl.vng.diwi.dal.VngRepository;
import nl.vng.diwi.dal.entities.Project;
import nl.vng.diwi.dal.entities.enums.Confidentiality;
import nl.vng.diwi.dal.entities.enums.PlanStatus;
import nl.vng.diwi.dal.entities.enums.PlanType;
import nl.vng.diwi.dal.entities.enums.ProjectPhase;
import nl.vng.diwi.dal.entities.enums.ProjectRole;
import nl.vng.diwi.models.ProjectListModel;
import nl.vng.diwi.models.ProjectSnapshotModel;
import nl.vng.diwi.models.ProjectTimelineModel;
import nl.vng.diwi.models.ProjectUpdateModel;
import nl.vng.diwi.rest.VngBadRequestException;
import nl.vng.diwi.rest.VngNotFoundException;
import nl.vng.diwi.rest.VngServerErrorException;
import nl.vng.diwi.security.LoggedUser;
import nl.vng.diwi.services.ProjectService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

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
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Object getCurrentProjectSnapshot(@PathParam("id") UUID projectUuid) throws VngNotFoundException {

        Project project = projectService.getCurrentProject(repo, projectUuid);

        if (project == null) {
            throw new VngNotFoundException();
        }

        return new ProjectSnapshotModel(project);
    }

    @GET
    @Path("/{id}/timeline")
    @Produces(MediaType.APPLICATION_JSON)
    public Object getCurrentProjectTimeline(@PathParam("id") UUID projectUuid) throws VngNotFoundException {

        Project project = projectService.getCurrentProject(repo, projectUuid);

        if (project == null) {
            throw new VngNotFoundException();
        }

        return new ProjectTimelineModel(project);
    }

    @GET
    @Path("/table")
    @Produces(MediaType.APPLICATION_JSON)
    public List<ProjectListModel> getAllProjects(@BeanParam FilterPaginationSorting filtering)
        throws VngBadRequestException {

        if (filtering.getSortColumn() != null && !ProjectListModel.SORTABLE_COLUMNS.contains(filtering.getSortColumn())) {
            throw new VngBadRequestException("Sort column not supported.");
        }

        if (filtering.getSortColumn() == null) {
            filtering.setSortColumn(ProjectListModel.DEFAULT_SORT_COLUMN);
        }

        return projectService.getProjectsTable(repo, filtering);

    }

    @GET
    @Path("/table/size")
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String, Integer> getAllProjectsListSize(@BeanParam FilterPaginationSorting filtering) {

        Integer projectsCount = repo.getProjectsDAO().getProjectsTableCount(filtering);

        return Map.of("size", projectsCount);
    }

    @POST
    @Path("/{id}/update")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateProject(@Context LoggedUser loggedUser, @PathParam("id") UUID projectUuid, ProjectUpdateModel projectUpdateModel)
        throws VngNotFoundException, VngBadRequestException, VngServerErrorException {

        Project project = repo.findById(Project.class, projectUuid);
        if (project == null) {
            throw new VngNotFoundException();
        }

        String validationError = projectUpdateModel.validate();
        if (validationError != null) {
            throw new VngBadRequestException(validationError);
        }

        LocalDate updateDate = LocalDate.now();

        try (AutoCloseTransaction transaction = repo.beginTransaction()) {
            switch (projectUpdateModel.getProperty()) {
                case confidentialityLevel -> {
                    Confidentiality newConfidentiality = Confidentiality.valueOf(projectUpdateModel.getValue());
                    projectService.updateProjectConfidentialityLevel(repo, projectUuid, newConfidentiality, loggedUser.getUuid());
                }
                case name -> projectService.updateProjectName(repo, projectUuid, projectUpdateModel.getValue(), loggedUser.getUuid(), updateDate);
                case projectColor -> projectService.updateProjectColor(repo, projectUuid, projectUpdateModel.getValue(), loggedUser.getUuid());
                case planningPlanStatus -> {
                    Set<PlanStatus> planStatuses = (projectUpdateModel.getValues() != null) ?
                        projectUpdateModel.getValues().stream().map(PlanStatus::valueOf).collect(Collectors.toSet()) : new HashSet<>();
                    projectService.updateProjectPlanStatus(repo, projectUuid, planStatuses, loggedUser.getUuid(), updateDate);
                }
                case planType -> {
                    Set<PlanType> planTypes = (projectUpdateModel.getValues() != null) ?
                        projectUpdateModel.getValues().stream().map(PlanType::valueOf).collect(Collectors.toSet()) : new HashSet<>();
                    projectService.updateProjectPlanTypes(repo, projectUuid, planTypes, loggedUser.getUuid(), updateDate);
                }
                case projectPhase ->
                    projectService.updateProjectPhase(repo, projectUuid, ProjectPhase.valueOf(projectUpdateModel.getValue()), loggedUser.getUuid(), updateDate);
                case projectLeaders -> {
                    UUID organizationToAdd = (projectUpdateModel.getAdd() != null) ? UUID.fromString(projectUpdateModel.getAdd()) : null;
                    UUID organizationToRemove = (projectUpdateModel.getRemove() != null) ? UUID.fromString(projectUpdateModel.getRemove()) : null;
                    projectService.updateProjectOrganizations(repo, projectUuid, ProjectRole.PROJECT_LEIDER, organizationToAdd, organizationToRemove, loggedUser.getUuid());
                }
                case projectOwners -> {
                    UUID organizationToAdd = (projectUpdateModel.getAdd() != null) ? UUID.fromString(projectUpdateModel.getAdd()) : null;
                    UUID organizationToRemove = (projectUpdateModel.getRemove() != null) ? UUID.fromString(projectUpdateModel.getRemove()) : null;
                    projectService.updateProjectOrganizations(repo, projectUuid, ProjectRole.OWNER, organizationToAdd, organizationToRemove, loggedUser.getUuid());
                }
                case municipalityRole -> {
                    UUID municipalityRoleToAdd = (projectUpdateModel.getAdd() != null) ? UUID.fromString(projectUpdateModel.getAdd()) : null;
                    UUID municipalityRoleToRemove = (projectUpdateModel.getRemove() != null) ? UUID.fromString(projectUpdateModel.getRemove()) : null;
                    projectService.updateProjectMunicipalityRoles(repo, projectUuid, municipalityRoleToAdd, municipalityRoleToRemove, loggedUser.getUuid(), updateDate);
                }
            }

            transaction.commit();
        }

        return Response.status(Response.Status.OK).build(); //TODO: return updated project??
    }
}
