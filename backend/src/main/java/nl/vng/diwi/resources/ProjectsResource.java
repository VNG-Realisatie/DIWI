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
import nl.vng.diwi.models.OrganizationModel;
import nl.vng.diwi.models.ProjectListModel;
import nl.vng.diwi.models.ProjectSnapshotModel;
import nl.vng.diwi.models.ProjectTimelineModel;
import nl.vng.diwi.models.ProjectUpdateModel;
import nl.vng.diwi.models.ProjectUpdateModel.ProjectProperty;
import nl.vng.diwi.models.SelectModel;
import nl.vng.diwi.rest.VngBadRequestException;
import nl.vng.diwi.rest.VngNotFoundException;
import nl.vng.diwi.rest.VngServerErrorException;
import nl.vng.diwi.security.LoggedUser;
import nl.vng.diwi.services.ProjectService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
    public ProjectSnapshotModel getCurrentProjectSnapshot(@PathParam("id") UUID projectUuid) throws VngNotFoundException {

        Project project = projectService.getCurrentProject(repo, projectUuid);

        if (project == null) {
            throw new VngNotFoundException();
        }

        return new ProjectSnapshotModel(project);
    }

    @GET
    @Path("/{id}/timeline")
    @Produces(MediaType.APPLICATION_JSON)
    public ProjectTimelineModel getCurrentProjectTimeline(@PathParam("id") UUID projectUuid) throws VngNotFoundException {

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

        String validationError = projectUpdateModel.validate();
        if (validationError != null) {
            throw new VngBadRequestException(validationError);
        }
        LocalDate updateDate = LocalDate.now();

        try (AutoCloseTransaction transaction = repo.beginTransaction()) {
            Project project = projectService.getCurrentProjectAndPerformPreliminaryUpdateChecks(repo, projectUuid);
            updateProjectProperty(project, projectUpdateModel, loggedUser, updateDate);
            transaction.commit();
        }

        return Response.status(Response.Status.OK).build(); //TODO: return updated project??
    }

    @POST
    @Path("/update")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public ProjectSnapshotModel updateProject(@Context LoggedUser loggedUser, ProjectSnapshotModel projectSnapshotModelToUpdate)
        throws VngNotFoundException, VngBadRequestException, VngServerErrorException {

        UUID projectUuid = projectSnapshotModelToUpdate.getProjectId();
        Project project = projectService.getCurrentProject(repo, projectUuid);
        if (project == null) {
            throw new VngNotFoundException();
        }

        ProjectSnapshotModel projectSnapshotModelCurrent = new ProjectSnapshotModel(project);

        List<ProjectUpdateModel> projectUpdateModelList = new ArrayList<>();
        for (ProjectUpdateModel.ProjectProperty projectProperty : ProjectUpdateModel.ProjectProperty.values()) {
            switch (projectProperty) {
                case confidentialityLevel -> {
                    Confidentiality newConfidentiality = projectSnapshotModelToUpdate.getConfidentialityLevel();
                    if (!Objects.equals(newConfidentiality, projectSnapshotModelCurrent.getConfidentialityLevel())) {
                        projectUpdateModelList.add(new ProjectUpdateModel(ProjectProperty.confidentialityLevel,
                            (newConfidentiality == null) ? null : projectSnapshotModelToUpdate.getConfidentialityLevel().name()));
                    }
                }
                case name -> {
                    if (!Objects.equals(projectSnapshotModelToUpdate.getProjectName(), projectSnapshotModelCurrent.getProjectName())) {
                        projectUpdateModelList.add(new ProjectUpdateModel(ProjectProperty.name, projectSnapshotModelToUpdate.getProjectName()));
                    }
                }
                case projectColor -> {
                    if (!Objects.equals(projectSnapshotModelToUpdate.getProjectColor(), projectSnapshotModelCurrent.getProjectColor())) {
                        projectUpdateModelList.add(new ProjectUpdateModel(ProjectProperty.projectColor, projectSnapshotModelToUpdate.getProjectColor()));
                    }
                }
                case planningPlanStatus -> {
                    List<PlanStatus> currentPlanStatuses = projectSnapshotModelCurrent.getPlanningPlanStatus();
                    List<PlanStatus> toUpdatePlanStatuses = projectSnapshotModelToUpdate.getPlanningPlanStatus();
                    if (currentPlanStatuses.size() != toUpdatePlanStatuses.size() || !currentPlanStatuses.containsAll(toUpdatePlanStatuses)) {
                        projectUpdateModelList.add(new ProjectUpdateModel(ProjectProperty.planningPlanStatus, toUpdatePlanStatuses.stream().map(PlanStatus::name).toList()));
                    }
                }
                case planType -> {
                    List<PlanType> currentPlanTypes = projectSnapshotModelCurrent.getPlanType();
                    List<PlanType> toUpdatePlanTypes = projectSnapshotModelToUpdate.getPlanType();
                    if (currentPlanTypes.size() != toUpdatePlanTypes.size() || !currentPlanTypes.containsAll(toUpdatePlanTypes)) {
                        projectUpdateModelList.add(new ProjectUpdateModel(ProjectProperty.planType, toUpdatePlanTypes.stream().map(PlanType::name).toList()));
                    }
                }
                case municipalityRole -> {
                    List<UUID> currentMunicipalityRolesIds = projectSnapshotModelCurrent.getMunicipalityRole().stream().map(SelectModel::getId).toList();
                    List<UUID> toUpdateMunicipalityRolesIds = projectSnapshotModelToUpdate.getMunicipalityRole().stream().map(SelectModel::getId).toList();
                    currentMunicipalityRolesIds.forEach(id -> {
                        if (!toUpdateMunicipalityRolesIds.contains(id)) {
                            projectUpdateModelList.add(new ProjectUpdateModel(ProjectProperty.municipalityRole, null, id.toString()));
                        }
                    });
                    toUpdateMunicipalityRolesIds.forEach(id -> {
                        if (!currentMunicipalityRolesIds.contains(id)) {
                            projectUpdateModelList.add(new ProjectUpdateModel(ProjectProperty.municipalityRole, id.toString(), null));
                        }
                    });
                }
                case projectLeaders -> {
                    List<UUID> currentLeadersUuids = projectSnapshotModelCurrent.getProjectLeaders().stream().map(OrganizationModel::getUuid).toList();
                    List<UUID> toUpdateLeadersUuids = projectSnapshotModelToUpdate.getProjectLeaders().stream().map(OrganizationModel::getUuid).toList();
                    currentLeadersUuids.forEach(uuid -> {
                        if (!toUpdateLeadersUuids.contains(uuid)) {
                            projectUpdateModelList.add(new ProjectUpdateModel(ProjectProperty.projectLeaders, null, uuid.toString()));
                        }
                    });
                    toUpdateLeadersUuids.forEach(uuid -> {
                        if (!currentLeadersUuids.contains(uuid)) {
                            projectUpdateModelList.add(new ProjectUpdateModel(ProjectProperty.projectLeaders, uuid.toString(), null));
                        }
                    });
                }
                case projectOwners -> {
                    List<UUID> currentOwnersUuids = projectSnapshotModelCurrent.getProjectOwners().stream().map(OrganizationModel::getUuid).toList();
                    List<UUID> toUpdateOwnersUuids = projectSnapshotModelToUpdate.getProjectOwners().stream().map(OrganizationModel::getUuid).toList();
                    currentOwnersUuids.forEach(uuid -> {
                        if (!toUpdateOwnersUuids.contains(uuid)) {
                            projectUpdateModelList.add(new ProjectUpdateModel(ProjectProperty.projectOwners, null, uuid.toString()));
                        }
                    });
                    toUpdateOwnersUuids.forEach(uuid -> {
                        if (!currentOwnersUuids.contains(uuid)) {
                            projectUpdateModelList.add(new ProjectUpdateModel(ProjectProperty.projectOwners, uuid.toString(), null));
                        }
                    });
                }
                case projectPhase -> {
                    if (!Objects.equals(projectSnapshotModelToUpdate.getProjectPhase(), projectSnapshotModelCurrent.getProjectPhase())) {
                        projectUpdateModelList.add(new ProjectUpdateModel(ProjectProperty.projectPhase, projectSnapshotModelToUpdate.getProjectPhase().name()));
                    }
                }
                case startDate -> {
                    LocalDate newStartDate = projectSnapshotModelToUpdate.getStartDate();
                    if (!Objects.equals(newStartDate, projectSnapshotModelCurrent.getStartDate())) {
                        projectUpdateModelList.add(new ProjectUpdateModel(ProjectProperty.startDate, (newStartDate == null) ? null : newStartDate.toString()));
                    }
                }
                case endDate -> {
                    LocalDate newEndDate = projectSnapshotModelToUpdate.getEndDate();
                    if (!Objects.equals(newEndDate, projectSnapshotModelCurrent.getEndDate())) {
                        projectUpdateModelList.add(new ProjectUpdateModel(ProjectProperty.endDate, (newEndDate == null) ? null : newEndDate.toString()));
                    }
                }
                default -> throw new VngServerErrorException(String.format("Project property not implemented %s ", projectProperty));
            }
        }

        LocalDate updateDate = LocalDate.now();
        try (AutoCloseTransaction transaction = repo.beginTransaction()) {
            projectService.getCurrentProjectAndPerformPreliminaryUpdateChecks(repo, project.getId());
            for (ProjectUpdateModel projectUpdateModel : projectUpdateModelList) {
                String validationError = projectUpdateModel.validate();
                if (validationError != null) {
                    throw new VngBadRequestException(validationError);
                }
                updateProjectProperty(project, projectUpdateModel, loggedUser, updateDate);
            }
            transaction.commit();
            repo.getSession().clear();
        }

        return new ProjectSnapshotModel(repo.findById(Project.class, projectUuid));
    }

    private void updateProjectProperty(Project project, ProjectUpdateModel projectUpdateModel, LoggedUser loggedUser, LocalDate updateDate)
        throws VngNotFoundException, VngServerErrorException, VngBadRequestException {

        switch (projectUpdateModel.getProperty()) {
            case startDate -> projectService.updateProjectDuration(repo, project, LocalDate.parse(projectUpdateModel.getValue()), null, loggedUser.getUuid());
            case endDate -> projectService.updateProjectDuration(repo, project, null, LocalDate.parse(projectUpdateModel.getValue()), loggedUser.getUuid());
            case confidentialityLevel -> {
                Confidentiality newConfidentiality = Confidentiality.valueOf(projectUpdateModel.getValue());
                projectService.updateProjectConfidentialityLevel(repo, project, newConfidentiality, loggedUser.getUuid());
            }
            case name -> projectService.updateProjectName(repo, project, projectUpdateModel.getValue(), loggedUser.getUuid(), updateDate);
            case projectColor -> projectService.updateProjectColor(repo, project, projectUpdateModel.getValue(), loggedUser.getUuid());
            case planningPlanStatus -> {
                Set<PlanStatus> planStatuses = (projectUpdateModel.getValues() != null) ?
                    projectUpdateModel.getValues().stream().map(PlanStatus::valueOf).collect(Collectors.toSet()) : new HashSet<>();
                projectService.updateProjectPlanStatus(repo, project, planStatuses, loggedUser.getUuid(), updateDate);
            }
            case planType -> {
                Set<PlanType> planTypes = (projectUpdateModel.getValues() != null) ?
                    projectUpdateModel.getValues().stream().map(PlanType::valueOf).collect(Collectors.toSet()) : new HashSet<>();
                projectService.updateProjectPlanTypes(repo, project, planTypes, loggedUser.getUuid(), updateDate);
            }
            case projectPhase ->
                projectService.updateProjectPhase(repo, project, ProjectPhase.valueOf(projectUpdateModel.getValue()), loggedUser.getUuid(), updateDate);
            case projectLeaders -> {
                UUID organizationToAdd = (projectUpdateModel.getAdd() != null) ? UUID.fromString(projectUpdateModel.getAdd()) : null;
                UUID organizationToRemove = (projectUpdateModel.getRemove() != null) ? UUID.fromString(projectUpdateModel.getRemove()) : null;
                projectService.updateProjectOrganizations(repo, project, ProjectRole.PROJECT_LEIDER, organizationToAdd, organizationToRemove, loggedUser.getUuid());
            }
            case projectOwners -> {
                UUID organizationToAdd = (projectUpdateModel.getAdd() != null) ? UUID.fromString(projectUpdateModel.getAdd()) : null;
                UUID organizationToRemove = (projectUpdateModel.getRemove() != null) ? UUID.fromString(projectUpdateModel.getRemove()) : null;
                projectService.updateProjectOrganizations(repo, project, ProjectRole.OWNER, organizationToAdd, organizationToRemove, loggedUser.getUuid());
            }
            case municipalityRole -> {
                UUID municipalityRoleToAdd = (projectUpdateModel.getAdd() != null) ? UUID.fromString(projectUpdateModel.getAdd()) : null;
                UUID municipalityRoleToRemove = (projectUpdateModel.getRemove() != null) ? UUID.fromString(projectUpdateModel.getRemove()) : null;
                projectService.updateProjectMunicipalityRoles(repo, project, municipalityRoleToAdd, municipalityRoleToRemove, loggedUser.getUuid(), updateDate);
            }
        }
    }
}
