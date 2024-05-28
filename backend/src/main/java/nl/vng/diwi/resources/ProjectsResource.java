package nl.vng.diwi.resources;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.BeanParam;
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
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import nl.vng.diwi.config.ProjectConfig;
import nl.vng.diwi.dal.AutoCloseTransaction;
import nl.vng.diwi.dal.FilterPaginationSorting;
import nl.vng.diwi.dal.GenericRepository;
import nl.vng.diwi.dal.VngRepository;
import nl.vng.diwi.dal.entities.Project;
import nl.vng.diwi.dal.entities.enums.Confidentiality;
import nl.vng.diwi.dal.entities.enums.ObjectType;
import nl.vng.diwi.dal.entities.enums.PlanStatus;
import nl.vng.diwi.dal.entities.enums.PlanType;
import nl.vng.diwi.dal.entities.enums.ProjectPhase;
import nl.vng.diwi.dal.entities.enums.PropertyKind;
import nl.vng.diwi.generic.Constants;
import nl.vng.diwi.models.ImportFileType;
import nl.vng.diwi.models.PropertyModel;
import nl.vng.diwi.models.HouseblockSnapshotModel;
import nl.vng.diwi.models.LocationModel;
import nl.vng.diwi.models.UserGroupModel;
import nl.vng.diwi.models.PlotModel;
import nl.vng.diwi.models.PriorityModel;
import nl.vng.diwi.models.ProjectHouseblockCustomPropertyModel;
import nl.vng.diwi.models.ProjectListModel;
import nl.vng.diwi.models.ProjectSnapshotModel;
import nl.vng.diwi.models.ProjectTimelineModel;
import nl.vng.diwi.models.ProjectUpdateModel;
import nl.vng.diwi.models.ProjectUpdateModel.ProjectProperty;
import nl.vng.diwi.models.SelectModel;
import nl.vng.diwi.models.SingleValueOrRangeModel;
import nl.vng.diwi.models.superclasses.ProjectCreateSnapshotModel;
import nl.vng.diwi.models.superclasses.ProjectMinimalSnapshotModel;
import nl.vng.diwi.rest.VngBadRequestException;
import nl.vng.diwi.rest.VngNotFoundException;
import nl.vng.diwi.rest.VngServerErrorException;
import nl.vng.diwi.security.LoggedUser;
import nl.vng.diwi.security.UserActionConstants;
import nl.vng.diwi.services.ExcelImportService;
import nl.vng.diwi.services.GeoJsonImportService;
import nl.vng.diwi.services.PropertiesService;
import nl.vng.diwi.services.HouseblockService;
import nl.vng.diwi.services.ProjectService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

@Path("/projects")
@RolesAllowed("BLOCKED_BY_DEFAULT") // This forces us to make sure each end-point has action(s) assigned, so we never have things open by default.
public class ProjectsResource {

    private static final Logger logger = LogManager.getLogger();
    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    private final VngRepository repo;
    private final ProjectService projectService;
    private final HouseblockService houseblockService;
    private final PropertiesService propertiesService;
    private final ProjectConfig projectConfig;
    private final ExcelImportService excelImportService;
    private final GeoJsonImportService geoJsonImportService;

    @Inject
    public ProjectsResource(
            GenericRepository genericRepository,
            ProjectService projectService,
            HouseblockService houseblockService,
            PropertiesService propertiesService,
            ProjectConfig projectConfig,
            ExcelImportService excelImportService,
            GeoJsonImportService geoJsonImportService) {
        this.repo = new VngRepository(genericRepository.getDal().getSession());
        this.projectService = projectService;
        this.houseblockService = houseblockService;
        this.propertiesService = propertiesService;
        this.projectConfig = projectConfig;
        this.excelImportService = excelImportService;
        this.geoJsonImportService = geoJsonImportService;
    }

    @POST
    @RolesAllowed({UserActionConstants.CREATE_NEW_PROJECT})
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public ProjectMinimalSnapshotModel createProject(@Context LoggedUser loggedUser, ProjectCreateSnapshotModel projectSnapshotModel)
            throws VngServerErrorException, VngBadRequestException, VngNotFoundException {
        String validationError = projectSnapshotModel.validate();
        if (validationError != null) {
            throw new VngBadRequestException(validationError);
        }

        ZonedDateTime now = ZonedDateTime.now();

        try (AutoCloseTransaction transaction = repo.beginTransaction()) {
            Project project = projectService.createProject(repo, loggedUser.getUuid(), projectSnapshotModel, now);
            transaction.commit();

            ProjectSnapshotModel projectSnapshot = projectService.getProjectSnapshot(repo, project.getId());

            return projectSnapshot;
        } catch (ConstraintViolationException ex) {
            throw new VngBadRequestException("Error saving new project due to invalid data", ex);
        }
    }

    @GET
    @Path("/{id}")
    @RolesAllowed({UserActionConstants.VIEW_OWN_PROJECTS, UserActionConstants.VIEW_OTHERS_PROJECTS})
    @Produces(MediaType.APPLICATION_JSON)
    public ProjectSnapshotModel getCurrentProjectSnapshot(@PathParam("id") UUID projectUuid) throws VngNotFoundException {
        return projectService.getProjectSnapshot(repo, projectUuid);
    }

    @DELETE
    @Path("/{id}")
    @RolesAllowed({UserActionConstants.EDIT_OWN_PROJECTS})
    public void deleteProject(@Context LoggedUser loggedUser, @PathParam("id") UUID projectUuid) throws VngNotFoundException {
        try (AutoCloseTransaction transaction = repo.beginTransaction()) {
            projectService.deleteProject(repo, projectUuid, loggedUser.getUuid());
            transaction.commit();
        }
    }

    @GET
    @Path("/{id}/timeline")
    @RolesAllowed({UserActionConstants.VIEW_OWN_PROJECTS, UserActionConstants.VIEW_OTHERS_PROJECTS})
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
    @RolesAllowed({UserActionConstants.VIEW_OWN_PROJECTS, UserActionConstants.VIEW_OTHERS_PROJECTS})
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
    @RolesAllowed({UserActionConstants.VIEW_OWN_PROJECTS, UserActionConstants.VIEW_OTHERS_PROJECTS})
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String, Integer> getAllProjectsListSize(@BeanParam FilterPaginationSorting filtering) {

        Integer projectsCount = repo.getProjectsDAO().getProjectsTableCount(filtering);

        return Map.of("size", projectsCount);
    }

    @GET
    @Path("/{id}/houseblocks")
    @RolesAllowed({UserActionConstants.VIEW_OWN_PROJECTS, UserActionConstants.VIEW_OTHERS_PROJECTS})
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public List<HouseblockSnapshotModel> getProjectHouseblocks(@PathParam("id") UUID projectUuid) {

        return houseblockService.getProjectHouseblocks(repo, projectUuid);

    }

    @GET
    @Path("/{id}/customproperties")
    @RolesAllowed({UserActionConstants.VIEW_OWN_PROJECTS, UserActionConstants.VIEW_OTHERS_PROJECTS})
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public List<ProjectHouseblockCustomPropertyModel> getProjectCustomProperties(@PathParam("id") UUID projectUuid) {

        return projectService.getProjectCustomProperties(repo, projectUuid);

    }

    @PUT
    @Path("/{id}/customproperties")
    @RolesAllowed({UserActionConstants.EDIT_OWN_PROJECTS})
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public List<ProjectHouseblockCustomPropertyModel> updateProjectCustomProperty(@Context LoggedUser loggedUser, @PathParam("id") UUID projectUuid, ProjectHouseblockCustomPropertyModel projectCPUpdateModel)
        throws VngNotFoundException, VngBadRequestException, VngServerErrorException {
        if (projectCPUpdateModel.getCustomPropertyId() == null){
            throw new VngBadRequestException("Custom property id must be set.");
        }
        PropertyModel dbCP = propertiesService.getProperty(repo, projectCPUpdateModel.getCustomPropertyId());
        if (dbCP == null || !dbCP.getObjectType().equals(ObjectType.PROJECT)) {
            throw new VngBadRequestException("Custom property id does not match any known property.");
        }
        if (dbCP.getDisabled() == Boolean.TRUE) {
            throw new VngBadRequestException("Custom property is disabled.");
        }
        if (dbCP.getType() != PropertyKind.CUSTOM) {
            throw new VngBadRequestException("Not a custom property.");
        }

        LocalDate updateDate = LocalDate.now();

        ProjectHouseblockCustomPropertyModel currentProjectCP = projectService.getProjectCustomProperties(repo, projectUuid).stream().filter(cp -> cp.getCustomPropertyId().equals(projectCPUpdateModel.getCustomPropertyId()))
            .findFirst().orElse(new ProjectHouseblockCustomPropertyModel());

        try (AutoCloseTransaction transaction = repo.beginTransaction()) {
            Project project = projectService.getCurrentProjectAndPerformPreliminaryUpdateChecks(repo, projectUuid);

            switch (dbCP.getPropertyType()) {
            case BOOLEAN -> {
                if (!Objects.equals(currentProjectCP.getBooleanValue(), projectCPUpdateModel.getBooleanValue())) {
                    projectService.updateProjectBooleanCustomProperty(repo, project, projectCPUpdateModel.getCustomPropertyId(),
                            projectCPUpdateModel.getBooleanValue(), loggedUser.getUuid(), updateDate);
                }
            }
            case TEXT -> {
                if (!Objects.equals(currentProjectCP.getTextValue(), projectCPUpdateModel.getTextValue())) {
                    projectService.updateProjectTextCustomProperty(repo, project, projectCPUpdateModel.getCustomPropertyId(),
                            projectCPUpdateModel.getTextValue(), loggedUser.getUuid(), updateDate);
                }
            }
            case NUMERIC -> {
                var currentNumericValue = currentProjectCP.getNumericValue();
                var updateNumericValue = projectCPUpdateModel.getNumericValue();
                if (updateNumericValue == null || !updateNumericValue.isValid(false)) {
                    throw new VngBadRequestException("Numeric value does not have a valid format.");
                }
                if (!Objects.equals(currentNumericValue.getValue() != null ? currentNumericValue.getValue().doubleValue() : null,
                        updateNumericValue.getValue() != null ? updateNumericValue.getValue().doubleValue() : null)
                        || !Objects.equals(currentNumericValue.getMin(), updateNumericValue.getMin())
                        || !Objects.equals(currentNumericValue.getMax(), updateNumericValue.getMax())) {
                    projectService.updateProjectNumericCustomProperty(repo, project, projectCPUpdateModel.getCustomPropertyId(),
                            projectCPUpdateModel.getNumericValue(), loggedUser.getUuid(), updateDate);
                }
            }
            case CATEGORY -> {
                var currentCategories = currentProjectCP.getCategories();
                var updateCategories = projectCPUpdateModel.getCategories();
                if (currentCategories.size() != updateCategories.size() || !currentCategories.containsAll(updateCategories)) {
                    projectService.updateProjectCategoryProperty(repo, project, projectCPUpdateModel.getCustomPropertyId(),
                            new HashSet<>(updateCategories), loggedUser.getUuid(), updateDate);
                }
            }
            case ORDINAL -> {
                if (projectCPUpdateModel.getOrdinals() == null || !projectCPUpdateModel.getOrdinals().isValid(false)) {
                    throw new VngBadRequestException("Ordinal value does not have a valid format.");
                }
                if (!Objects.equals(currentProjectCP.getOrdinals(), projectCPUpdateModel.getOrdinals())) {
                    projectService.updateProjectOrdinalCustomProperty(repo, project, projectCPUpdateModel.getCustomPropertyId(),
                            projectCPUpdateModel.getOrdinals(), loggedUser.getUuid(), updateDate);
                }
            }
            }
            transaction.commit();
            repo.getSession().clear();
        }

        return projectService.getProjectCustomProperties(repo, projectUuid);
    }

// TODO - endpoint not currently used, there is a ticket to fix it when it will be needed

//    @POST
//    @Path("/{id}/update")
//    @RolesAllowed({UserActionConstants.EDIT_OWN_PROJECTS})
//    @Produces(MediaType.APPLICATION_JSON)
//    @Consumes(MediaType.APPLICATION_JSON)
//    public ProjectSnapshotModel updateProjectSingleField(@Context LoggedUser loggedUser, @PathParam("id") UUID projectUuid, ProjectUpdateModel projectUpdateModel)
//            throws VngNotFoundException, VngBadRequestException, VngServerErrorException {
//
//        String validationError = projectUpdateModel.validate(repo);
//        if (validationError != null) {
//            throw new VngBadRequestException(validationError);
//        }
//        LocalDate updateDate = LocalDate.now();
//
//        try (AutoCloseTransaction transaction = repo.beginTransaction()) {
//            Project project = projectService.getCurrentProjectAndPerformPreliminaryUpdateChecks(repo, projectUuid);
//            updateProjectProperty(project, projectUpdateModel, loggedUser, updateDate, ZonedDateTime.now());
//            transaction.commit();
//        }
//
//        return projectService.getProjectSnapshot(repo, projectUuid);
//    }

    @GET
    @Path("/{id}/plots")
    @RolesAllowed({UserActionConstants.VIEW_OWN_PROJECTS, UserActionConstants.VIEW_OTHERS_PROJECTS})
    @Produces(MediaType.APPLICATION_JSON)
    public List<PlotModel> getProjectPlots(@PathParam("id") UUID projectUuid) throws VngNotFoundException {
        Project project = projectService.getCurrentProject(repo, projectUuid);

        if (project == null) {
            throw new VngNotFoundException("project not found");
        }

        return projectService.getCurrentPlots(project);
    }

    @POST
    @Path("/{id}/plots")
    @RolesAllowed({UserActionConstants.EDIT_OWN_PROJECTS})
    @Consumes(MediaType.APPLICATION_JSON)
    public void setProjectPlots(@Context LoggedUser loggedUser, @PathParam("id") UUID projectUuid, List<PlotModel> plots) throws VngNotFoundException, VngBadRequestException {
        Project project = projectService.getCurrentProject(repo, projectUuid);

        if (project == null) {
            throw new VngNotFoundException("project not found");
        }

        for (var plot : plots) {
            String validationError = plot.validate();
            if (validationError != null) {
                throw new VngBadRequestException(validationError);
            }
        }

        //prevent creating new changelogs in the DB when updating with the same values that already exist
        List<PlotModel> currentPlots = new ArrayList<>(projectService.getCurrentPlots(project));
        if (currentPlots.size() == plots.size()) {
            currentPlots.removeAll(plots);
            if (currentPlots.isEmpty()) {
                return;
            }
        }

        try (AutoCloseTransaction transaction = repo.beginTransaction()) {
            projectService.setPlots(repo, project, plots, loggedUser.getUuid());
            transaction.commit();
        }
    }

    @POST
    @Path("/update")
    @RolesAllowed({UserActionConstants.EDIT_OWN_PROJECTS})
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public ProjectSnapshotModel updateProjectSnapshot(@Context LoggedUser loggedUser, ProjectSnapshotModel projectSnapshotModelToUpdate)
            throws VngNotFoundException, VngBadRequestException, VngServerErrorException {

        UUID projectUuid = projectSnapshotModelToUpdate.getProjectId();
        Project project = projectService.getCurrentProject(repo, projectUuid);
        if (project == null) {
            throw new VngNotFoundException();
        }

        ProjectSnapshotModel projectSnapshotModelCurrent = projectService.getProjectSnapshot(repo, projectUuid);

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
            case priority -> {
                if (!Objects.equals(projectSnapshotModelToUpdate.getPriority(), projectSnapshotModelCurrent.getPriority())) {
                    PriorityModel priorityModelToUpdate = projectSnapshotModelToUpdate.getPriority();
                    String value = (priorityModelToUpdate.getValue() == null) ? null : priorityModelToUpdate.getValue().getId().toString();
                    UUID min = (priorityModelToUpdate.getMin() == null) ? null : priorityModelToUpdate.getMin().getId();
                    UUID max = (priorityModelToUpdate.getMax() == null) ? null : priorityModelToUpdate.getMax().getId();
                    projectUpdateModelList.add(new ProjectUpdateModel(ProjectProperty.priority, value, min, max));
                }
            }
            case projectColor -> {
                if (!Objects.equals(projectSnapshotModelToUpdate.getProjectColor(), projectSnapshotModelCurrent.getProjectColor())) {
                    projectUpdateModelList.add(new ProjectUpdateModel(ProjectProperty.projectColor, projectSnapshotModelToUpdate.getProjectColor()));
                }
            }
            case location -> {
                if (projectSnapshotModelToUpdate.getLocation() == null) {
                    projectSnapshotModelToUpdate.setLocation(new LocationModel());
                }
                if (!Objects.equals(projectSnapshotModelToUpdate.getLocation(), projectSnapshotModelCurrent.getLocation())) {
                    Double newLatitude = projectSnapshotModelToUpdate.getLocation().getLat();
                    Double newLongitude = projectSnapshotModelToUpdate.getLocation().getLng();
                    projectUpdateModelList.add(new ProjectUpdateModel(ProjectProperty.location, Arrays.asList(
                            newLatitude == null ? null : newLatitude.toString(),
                            newLongitude == null ? null : newLongitude.toString())));
                }
            }
            case planningPlanStatus -> {
                List<PlanStatus> currentPlanStatuses = projectSnapshotModelCurrent.getPlanningPlanStatus();
                List<PlanStatus> toUpdatePlanStatuses = projectSnapshotModelToUpdate.getPlanningPlanStatus();
                if (currentPlanStatuses.size() != toUpdatePlanStatuses.size() || !currentPlanStatuses.containsAll(toUpdatePlanStatuses)) {
                    projectUpdateModelList
                            .add(new ProjectUpdateModel(ProjectProperty.planningPlanStatus, toUpdatePlanStatuses.stream().map(PlanStatus::name).toList()));
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
                List<SelectModel> currentMunicipalityRoles = projectSnapshotModelCurrent.getMunicipalityRole();
                List<SelectModel> toUpdateMunicipalityRoles = projectSnapshotModelToUpdate.getMunicipalityRole();
                if (currentMunicipalityRoles.size() != toUpdateMunicipalityRoles.size() || !currentMunicipalityRoles.containsAll(toUpdateMunicipalityRoles)) {
                    projectUpdateModelList.add(new ProjectUpdateModel(ProjectProperty.municipalityRole, toUpdateMunicipalityRoles.stream().map(s -> s.getId().toString()).toList()));
                }
            }
            case projectOwners -> {
                List<UUID> currentOwnersUuids = projectSnapshotModelCurrent.getProjectOwners().stream().map(UserGroupModel::getUuid).toList();
                List<UUID> toUpdateOwnersUuids = projectSnapshotModelToUpdate.getProjectOwners().stream().map(UserGroupModel::getUuid).toList();
                if (toUpdateOwnersUuids.isEmpty()) {
                    throw new VngBadRequestException("Missing project owners property");
                }
                currentOwnersUuids.forEach(uuid -> {
                    if (!toUpdateOwnersUuids.contains(uuid)) {
                        projectUpdateModelList.add(new ProjectUpdateModel(ProjectProperty.projectOwners, null, uuid));
                    }
                });
                toUpdateOwnersUuids.forEach(uuid -> {
                    if (!currentOwnersUuids.contains(uuid)) {
                        projectUpdateModelList.add(new ProjectUpdateModel(ProjectProperty.projectOwners, uuid, null));
                    }
                });
            }
            case projectPhase -> {
                if (!Objects.equals(projectSnapshotModelToUpdate.getProjectPhase(), projectSnapshotModelCurrent.getProjectPhase())) {
                    projectUpdateModelList.add(new ProjectUpdateModel(ProjectProperty.projectPhase, projectSnapshotModelToUpdate.getProjectPhase().name()));
                }
            }
            case municipality -> {
                List<SelectModel> currentMunicipality = projectSnapshotModelCurrent.getMunicipality();
                List<SelectModel> toUpdateMunicipality = projectSnapshotModelToUpdate.getMunicipality();
                if (currentMunicipality.size() != toUpdateMunicipality.size() || !currentMunicipality.containsAll(toUpdateMunicipality)) {
                    projectUpdateModelList.add(new ProjectUpdateModel(ProjectProperty.municipality, toUpdateMunicipality.stream().map(s -> s.getId().toString()).toList()));
                }
            }
            case district -> {
                List<SelectModel> currentDistricts = projectSnapshotModelCurrent.getDistrict();
                List<SelectModel> toUpdateDistricts = projectSnapshotModelToUpdate.getDistrict();
                if (currentDistricts.size() != toUpdateDistricts.size() || !currentDistricts.containsAll(toUpdateDistricts)) {
                        projectUpdateModelList.add(new ProjectUpdateModel(ProjectProperty.district, toUpdateDistricts.stream().map(s -> s.getId().toString()).toList()));
                }
            }
            case neighbourhood -> {
                List<SelectModel> currentNeighbourhoods = projectSnapshotModelCurrent.getNeighbourhood();
                List<SelectModel> toUpdateNeighbourhoods = projectSnapshotModelToUpdate.getNeighbourhood();
                if (currentNeighbourhoods.size() != toUpdateNeighbourhoods.size() || !currentNeighbourhoods.containsAll(toUpdateNeighbourhoods)) {
                    projectUpdateModelList.add(new ProjectUpdateModel(ProjectProperty.neighbourhood, toUpdateNeighbourhoods.stream().map(s -> s.getId().toString()).toList()));
                }
            }
            case geometry -> {
                var currentGeometry = projectSnapshotModelCurrent.getGeometry();
                var toGeometry = projectSnapshotModelToUpdate.getGeometry();
                if (!Objects.equals(currentGeometry, toGeometry)) {
                    projectUpdateModelList.add(new ProjectUpdateModel(ProjectProperty.geometry, toGeometry));
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
        ZonedDateTime changeDate = ZonedDateTime.now();
        if (!projectUpdateModelList.isEmpty()) {
            try (AutoCloseTransaction transaction = repo.beginTransaction()) {
                projectService.getCurrentProjectAndPerformPreliminaryUpdateChecks(repo, project.getId());
                for (ProjectUpdateModel projectUpdateModel : projectUpdateModelList) {
                    String validationError = projectUpdateModel.validate(repo);
                    if (validationError != null) {
                        throw new VngBadRequestException(validationError);
                    }
                    updateProjectProperty(project, projectUpdateModel, loggedUser, updateDate, changeDate);
                }
                transaction.commit();
                repo.getSession().clear();
            }
        }

        return projectService.getProjectSnapshot(repo, projectUuid);
    }

    private void updateProjectProperty(Project project, ProjectUpdateModel projectUpdateModel, LoggedUser loggedUser, LocalDate updateDate, ZonedDateTime changeDate)
        throws VngNotFoundException, VngServerErrorException, VngBadRequestException {

        switch (projectUpdateModel.getProperty()) {
        case startDate -> projectService.updateProjectDuration(repo, project, LocalDate.parse(projectUpdateModel.getValue()), null, loggedUser.getUuid());
        case endDate -> projectService.updateProjectDuration(repo, project, null, LocalDate.parse(projectUpdateModel.getValue()), loggedUser.getUuid());
        case confidentialityLevel -> {
            Confidentiality newConfidentiality = Confidentiality.valueOf(projectUpdateModel.getValue());
            projectService.updateProjectConfidentialityLevel(repo, project, newConfidentiality, loggedUser.getUuid(), changeDate);
        }
        case name -> projectService.updateProjectName(repo, project, projectUpdateModel.getValue(), loggedUser.getUuid(), updateDate);
        case projectColor -> projectService.updateProjectColor(repo, project, projectUpdateModel.getValue(), loggedUser.getUuid(), changeDate);
        case location -> projectService.updateProjectLocation(repo, project, projectUpdateModel.getValues().stream()
                .map(v -> (v == null) ? null : Double.parseDouble(v))
                .toList(), loggedUser.getUuid(), changeDate);
        case planningPlanStatus -> {
            Set<PlanStatus> planStatuses = (projectUpdateModel.getValues() != null)
                    ? projectUpdateModel.getValues().stream().map(PlanStatus::valueOf).collect(Collectors.toSet())
                    : new HashSet<>();
            projectService.updateProjectPlanStatus(repo, project, planStatuses, loggedUser.getUuid(), updateDate);
        }
        case planType -> {
            Set<PlanType> planTypes = (projectUpdateModel.getValues() != null)
                    ? projectUpdateModel.getValues().stream().map(PlanType::valueOf).collect(Collectors.toSet())
                    : new HashSet<>();
            projectService.updateProjectPlanTypes(repo, project, planTypes, loggedUser.getUuid(), updateDate);
        }
        case priority -> {
            UUID priorityValue = (projectUpdateModel.getValue() == null) ? null : UUID.fromString(projectUpdateModel.getValue());
            UUID propertyId = propertiesService.getPropertyUuid(repo, Constants.FIXED_PROPERTY_PRIORITY);
            var newPriority = new SingleValueOrRangeModel<>(priorityValue, projectUpdateModel.getMin(), projectUpdateModel.getMax());
            projectService.updateProjectOrdinalCustomProperty(repo, project, propertyId, newPriority, loggedUser.getUuid(),
                    updateDate);
        }
        case projectPhase ->
            projectService.updateProjectPhase(repo, project, ProjectPhase.valueOf(projectUpdateModel.getValue()), loggedUser.getUuid(), updateDate);
        case projectOwners -> {
            UUID userGroupToAdd = projectUpdateModel.getAdd();
            UUID userGroupToRemove = projectUpdateModel.getRemove();
            projectService.updateProjectUserGroups(repo, project, userGroupToAdd, userGroupToRemove, loggedUser.getUuid());
        }
        case municipalityRole -> {
            Set<UUID> municipalityRoleCatUuids = projectUpdateModel.getValues().stream().map(UUID::fromString).collect(Collectors.toSet());
            UUID propertyId = propertiesService.getPropertyUuid(repo, Constants.FIXED_PROPERTY_MUNICIPALITY_ROLE);
            projectService.updateProjectCategoryProperty(repo, project, propertyId, municipalityRoleCatUuids, loggedUser.getUuid(), updateDate);
        }
        case municipality -> {
            Set<UUID> municipalityCatUuids = projectUpdateModel.getValues().stream().map(UUID::fromString).collect(Collectors.toSet());
            UUID propertyId = propertiesService.getPropertyUuid(repo, Constants.FIXED_PROPERTY_MUNICIPALITY);
            projectService.updateProjectCategoryProperty(repo, project, propertyId, municipalityCatUuids, loggedUser.getUuid(), updateDate);
        }
        case district -> {
            Set<UUID> districtCatUuids = projectUpdateModel.getValues().stream().map(UUID::fromString).collect(Collectors.toSet());
            UUID propertyId = propertiesService.getPropertyUuid(repo, Constants.FIXED_PROPERTY_DISTRICT);
            projectService.updateProjectCategoryProperty(repo, project, propertyId, districtCatUuids, loggedUser.getUuid(), updateDate);
        }
        case neighbourhood -> {
            Set<UUID> neighbourhoodCatUuids = projectUpdateModel.getValues().stream().map(UUID::fromString).collect(Collectors.toSet());
            UUID propertyId = propertiesService.getPropertyUuid(repo, Constants.FIXED_PROPERTY_NEIGHBOURHOOD);
            projectService.updateProjectCategoryProperty(repo, project, propertyId, neighbourhoodCatUuids, loggedUser.getUuid(), updateDate);
        }
        case geometry -> {
            UUID propertyId = propertiesService.getPropertyUuid(repo, Constants.FIXED_PROPERTY_GEOMETRY);
            projectService.updateProjectTextCustomProperty(repo, project, propertyId, projectUpdateModel.getValue(),loggedUser.getUuid(), updateDate);
        }
        }
    }

    @POST
    @Path("/import")
    @RolesAllowed({UserActionConstants.IMPORT_PROJECTS})
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public Response importFile(@FormDataParam("uploadFile") InputStream inputStream,
                                    @FormDataParam("uploadFile") FormDataContentDisposition formDataContentDisposition,
                                    @QueryParam("fileType") ImportFileType fileType,
                                    ContainerRequestContext requestContext) {

        var loggedUser = (LoggedUser) requestContext.getProperty("loggedUser");

        //save file to disk
        String fileSuffix = LocalDateTime.now().format(formatter);
        String filename = "excel_import_" + fileSuffix + ".xlsx";
        String filePath = projectConfig.getDataDir() + filename;
        java.nio.file.Path path = Paths.get(filePath);
        try {
            Files.copy(inputStream, path, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            logger.error("Could not save excel file to disk", e);
            throw new VngServerErrorException("Could not save excel file to disk");
        }

        //process import from file written on disk
        try {
            Map<String, Object> result;
            if (fileType == ImportFileType.GEOJSON) {
                result = geoJsonImportService.importGeoJson(filePath, repo, loggedUser.getUuid());
            } else {
                result = excelImportService.importExcel(filePath, repo, loggedUser.getUuid());
            }
            if (result.containsKey(ExcelImportService.errors)) {
                deleteFile(path);
                return Response.status(Response.Status.BAD_REQUEST).entity(result.get(ExcelImportService.errors)).build();
            }
            return Response.ok(result.get(ExcelImportService.result)).build();
        } catch (Exception ex) {
            logger.error("Error while uploading excel", ex);
            deleteFile(path);
            throw new VngServerErrorException("Error while uploading excel");
        }

    }

    private void deleteFile(java.nio.file.Path path) {
        try {   //delete excel file from disk since no changes were made in the database
            Files.deleteIfExists(path);
        } catch (IOException e) {
            logger.error("Error while deleting excel file which failed upload {}", path, e);
        }
    }
}
