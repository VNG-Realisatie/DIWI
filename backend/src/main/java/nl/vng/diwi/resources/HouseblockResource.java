package nl.vng.diwi.resources;

import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import nl.vng.diwi.dal.AutoCloseTransaction;
import nl.vng.diwi.dal.GenericRepository;
import nl.vng.diwi.dal.VngRepository;
import nl.vng.diwi.dal.entities.Houseblock;
import nl.vng.diwi.dal.entities.Milestone;
import nl.vng.diwi.dal.entities.Project;
import nl.vng.diwi.dal.entities.enums.GroundPosition;
import nl.vng.diwi.dal.entities.enums.HouseType;
import nl.vng.diwi.dal.entities.enums.ObjectType;
import nl.vng.diwi.dal.entities.enums.PhysicalAppearance;
import nl.vng.diwi.dal.entities.enums.Purpose;
import nl.vng.diwi.models.CustomPropertyModel;
import nl.vng.diwi.models.HouseblockSnapshotModel;
import nl.vng.diwi.models.HouseblockUpdateModel;
import nl.vng.diwi.models.MilestoneModel;
import nl.vng.diwi.models.ProjectHouseblockCustomPropertyModel;
import nl.vng.diwi.rest.VngBadRequestException;
import nl.vng.diwi.rest.VngNotFoundException;
import nl.vng.diwi.rest.VngServerErrorException;
import nl.vng.diwi.security.LoggedUser;
import nl.vng.diwi.services.CustomPropertiesService;
import nl.vng.diwi.services.HouseblockService;
import nl.vng.diwi.services.ProjectService;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import static nl.vng.diwi.security.SecurityRoleConstants.Admin;

@Path("/houseblock")
@RolesAllowed({Admin})
public class HouseblockResource {

    private final VngRepository repo;
    private final HouseblockService houseblockService;
    private final ProjectService projectService;
    private final CustomPropertiesService customPropertiesService;

    @Inject
    public HouseblockResource(
        GenericRepository genericRepository,
        HouseblockService houseblockService,
        ProjectService projectService,
        CustomPropertiesService customPropertiesService) {
        this.repo = new VngRepository(genericRepository.getDal().getSession());
        this.houseblockService = houseblockService;
        this.projectService = projectService;
        this.houseblockService.setProjectService(projectService);
        this.customPropertiesService = customPropertiesService;
    }

    @GET
    @Path("/{uuid}")
    @Produces(MediaType.APPLICATION_JSON)
    public HouseblockSnapshotModel getCurrentHouseblockSnapshot(@PathParam("uuid") UUID houseblockUuid) throws VngNotFoundException {

        return houseblockService.getHouseblockSnapshot(repo, houseblockUuid);

    }

    @GET
    @Path("/{id}/customproperties")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public List<ProjectHouseblockCustomPropertyModel> getProjectCustomProperties(@PathParam("id") UUID houseblockUuid) {

        return houseblockService.getHouseblockCustomProperties(repo, houseblockUuid);

    }

    @POST
    @Path("/add")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public HouseblockSnapshotModel createHouseblock(@Context LoggedUser loggedUser, HouseblockSnapshotModel houseblockSnapshotModel)
        throws VngNotFoundException, VngBadRequestException {

        try (AutoCloseTransaction transaction = repo.beginTransaction()) {
            Project project = projectService.getCurrentProject(repo, houseblockSnapshotModel.getProjectId());

            String validationError = houseblockSnapshotModel.validate(new MilestoneModel(project.getDuration().get(0).getStartMilestone()).getDate(),
                new MilestoneModel(project.getDuration().get(0).getEndMilestone()).getDate());
            if (validationError != null) {
                throw new VngBadRequestException(validationError);
            }

            Milestone startMilestone = projectService.getOrCreateMilestoneForProject(repo, project, houseblockSnapshotModel.getStartDate(), loggedUser.getUuid());
            Milestone endMilestone = projectService.getOrCreateMilestoneForProject(repo, project, houseblockSnapshotModel.getEndDate(), loggedUser.getUuid());

            Houseblock houseblock = houseblockService.createHouseblock(repo, houseblockSnapshotModel, startMilestone, endMilestone,
                loggedUser.getUuid(), ZonedDateTime.now());
            transaction.commit();

            return houseblockService.getHouseblockSnapshot(repo, houseblock.getId());
        }
    }

    @PUT
    @Path("/update")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public HouseblockSnapshotModel updateHouseblock(@Context LoggedUser loggedUser, HouseblockSnapshotModel houseblockModelToUpdate)
        throws VngNotFoundException, VngBadRequestException {

        UUID houseblockUuid = houseblockModelToUpdate.getHouseblockId();
        HouseblockSnapshotModel houseblockCurrentValues = houseblockService.getHouseblockSnapshot(repo, houseblockUuid);

        List<HouseblockUpdateModel> houseblockUpdateModelList = new ArrayList<>();
        for (HouseblockUpdateModel.HouseblockProperty blockProperty : HouseblockUpdateModel.HouseblockProperty.values()) {
            switch (blockProperty) {
                case name -> {
                    if (!Objects.equals(houseblockModelToUpdate.getHouseblockName(), houseblockCurrentValues.getHouseblockName())) {
                        houseblockUpdateModelList.add(new HouseblockUpdateModel(HouseblockUpdateModel.HouseblockProperty.name, houseblockModelToUpdate.getHouseblockName()));
                    }
                }
                case purpose -> {
                    if (!Objects.equals(houseblockModelToUpdate.getPurpose(), houseblockCurrentValues.getPurpose())) {
                        Map<Object, Integer> purposeMap = new HashMap<>();
                        purposeMap.put(Purpose.REGULIER, houseblockModelToUpdate.getPurpose().getRegular());
                        purposeMap.put(Purpose.JONGEREN, houseblockModelToUpdate.getPurpose().getYouth());
                        purposeMap.put(Purpose.STUDENTEN, houseblockModelToUpdate.getPurpose().getStudent());
                        purposeMap.put(Purpose.OUDEREN, houseblockModelToUpdate.getPurpose().getElderly());
                        purposeMap.put(Purpose.GEHANDICAPTEN_EN_ZORG, houseblockModelToUpdate.getPurpose().getGHZ());
                        purposeMap.put(Purpose.GROTE_GEZINNEN, houseblockModelToUpdate.getPurpose().getLargeFamilies());
                        houseblockUpdateModelList.add(new HouseblockUpdateModel(HouseblockUpdateModel.HouseblockProperty.purpose, purposeMap));
                    }
                }
                case groundPosition -> {
                    if (!Objects.equals(houseblockModelToUpdate.getGroundPosition(), houseblockCurrentValues.getGroundPosition())) {
                        Map<Object, Integer> groundPositionMap = new HashMap<>();
                        groundPositionMap.put(GroundPosition.FORMELE_TOESTEMMING_GRONDEIGENAAR, houseblockModelToUpdate.getGroundPosition().getFormalPermissionOwner());
                        groundPositionMap.put(GroundPosition.GEEN_TOESTEMMING_GRONDEIGENAAR, houseblockModelToUpdate.getGroundPosition().getNoPermissionOwner());
                        groundPositionMap.put(GroundPosition.INTENTIE_MEDEWERKING_GRONDEIGENAAR, houseblockModelToUpdate.getGroundPosition().getIntentionPermissionOwner());
                        houseblockUpdateModelList.add(new HouseblockUpdateModel(HouseblockUpdateModel.HouseblockProperty.groundPosition, groundPositionMap));
                    }
                }
                case size -> {
                    if (!houseblockCurrentValues.isSizeEqualTo(houseblockModelToUpdate.getSize())) {
                        houseblockUpdateModelList.add(new HouseblockUpdateModel(HouseblockUpdateModel.HouseblockProperty.size, houseblockModelToUpdate.getSize()));
                    }
                }
                case physicalAppearanceAndHouseType -> {
                    if (!Objects.equals(houseblockModelToUpdate.getPhysicalAppearance(), houseblockCurrentValues.getPhysicalAppearance()) ||
                    !Objects.equals(houseblockModelToUpdate.getHouseType(), houseblockCurrentValues.getHouseType())) {
                        Map<Object, Integer> physicalAppAndHouseTypeMap = new HashMap<>();
                        physicalAppAndHouseTypeMap.put(PhysicalAppearance.TUSSENWONING, houseblockModelToUpdate.getPhysicalAppearance().getTussenwoning());
                        physicalAppAndHouseTypeMap.put(PhysicalAppearance.HOEKWONING, houseblockModelToUpdate.getPhysicalAppearance().getHoekwoning());
                        physicalAppAndHouseTypeMap.put(PhysicalAppearance.TWEE_ONDER_EEN_KAP, houseblockModelToUpdate.getPhysicalAppearance().getTweeondereenkap());
                        physicalAppAndHouseTypeMap.put(PhysicalAppearance.VRIJSTAAND, houseblockModelToUpdate.getPhysicalAppearance().getVrijstaand());
                        physicalAppAndHouseTypeMap.put(PhysicalAppearance.PORTIEKFLAT, houseblockModelToUpdate.getPhysicalAppearance().getPortiekflat());
                        physicalAppAndHouseTypeMap.put(PhysicalAppearance.GALLERIJFLAT, houseblockModelToUpdate.getPhysicalAppearance().getGallerijflat());
                        physicalAppAndHouseTypeMap.put(HouseType.EENGEZINSWONING, houseblockModelToUpdate.getHouseType().getEengezinswoning());
                        physicalAppAndHouseTypeMap.put(HouseType.MEERGEZINSWONING, houseblockModelToUpdate.getHouseType().getMeergezinswoning());
                        houseblockUpdateModelList.add(new HouseblockUpdateModel(HouseblockUpdateModel.HouseblockProperty.physicalAppearanceAndHouseType, physicalAppAndHouseTypeMap));
                    }
                }
                case programming -> {
                    if (!Objects.equals(houseblockModelToUpdate.getProgramming(), houseblockCurrentValues.getProgramming())) {
                        houseblockUpdateModelList.add(new HouseblockUpdateModel(HouseblockUpdateModel.HouseblockProperty.programming, houseblockModelToUpdate.getProgramming()));
                    }
                }
                case mutation -> {
                    if (!Objects.equals(houseblockModelToUpdate.getMutation(), houseblockCurrentValues.getMutation())) {
                        houseblockUpdateModelList.add(new HouseblockUpdateModel(HouseblockUpdateModel.HouseblockProperty.mutation, houseblockModelToUpdate.getMutation()));
                    }
                }
                case startDate -> {
                    LocalDate newStartDate = houseblockModelToUpdate.getStartDate();
                    if (!Objects.equals(newStartDate, houseblockCurrentValues.getStartDate())) {
                        houseblockUpdateModelList.add(new HouseblockUpdateModel(HouseblockUpdateModel.HouseblockProperty.startDate, (newStartDate == null) ? null : newStartDate.toString()));
                    }
                }
                case endDate -> {
                    LocalDate newEndDate = houseblockModelToUpdate.getEndDate();
                    if (!Objects.equals(newEndDate, houseblockCurrentValues.getEndDate())) {
                        houseblockUpdateModelList.add(new HouseblockUpdateModel(HouseblockUpdateModel.HouseblockProperty.endDate, (newEndDate == null) ? null : newEndDate.toString()));
                    }
                }
                case ownershipValue -> {
                    List<HouseblockSnapshotModel.OwnershipValue> newOwnershipValues = houseblockModelToUpdate.getOwnershipValue();
                    List<HouseblockSnapshotModel.OwnershipValue> oldOwnershipValues = houseblockCurrentValues.getOwnershipValue();
                    for (var newOV : newOwnershipValues) {
                        if (newOV.getId() == null) {
                            houseblockUpdateModelList.add(new HouseblockUpdateModel(HouseblockUpdateModel.HouseblockProperty.ownershipValue, newOV, HouseblockUpdateModel.ActionType.add));
                        } else {
                            var olvOv = oldOwnershipValues.stream().filter(ov -> ov.getId().equals(newOV.getId())).findFirst()
                                .orElseThrow(() -> new VngBadRequestException("Unrecognized ownership value id."));
                            if (!Objects.equals(newOV, olvOv)) {
                                houseblockUpdateModelList.add(new HouseblockUpdateModel(HouseblockUpdateModel.HouseblockProperty.ownershipValue, newOV, HouseblockUpdateModel.ActionType.update));
                            }
                        }
                    }
                    for (var oldOV : oldOwnershipValues) {
                        var newOv = newOwnershipValues.stream().filter(ov -> oldOV.getId().equals(ov.getId())).findFirst().orElse(null);
                        if (newOv == null) {
                            houseblockUpdateModelList.add(new HouseblockUpdateModel(HouseblockUpdateModel.HouseblockProperty.ownershipValue, oldOV, HouseblockUpdateModel.ActionType.remove));
                        }
                    }
                }
                default -> throw new VngServerErrorException(String.format("Houseblock property not implemented %s ", blockProperty));
            }
        }

        LocalDate updateDate = LocalDate.now();

        Houseblock houseblock = houseblockService.getCurrentHouseblockAndPerformPreliminaryUpdateChecks(repo, houseblockModelToUpdate.getHouseblockId());
        Project project = projectService.getCurrentProject(repo, houseblockModelToUpdate.getProjectId());
        if (!houseblock.getProject().getId().equals(houseblockModelToUpdate.getProjectId())) {
            throw new VngBadRequestException(String.format("Houseblock %s does not belong to project %s", houseblockModelToUpdate.getHouseblockId(), houseblockModelToUpdate.getProjectId()));
        }

        //handle all other fields
        List<HouseblockUpdateModel> otherUpdateFields = houseblockUpdateModelList.stream().filter(m -> !m.getProperty().equals(HouseblockUpdateModel.HouseblockProperty.startDate) &&
            !m.getProperty().equals(HouseblockUpdateModel.HouseblockProperty.endDate)).toList();
        if (!otherUpdateFields.isEmpty()) {
            try (AutoCloseTransaction transaction1 = repo.beginTransaction()) {
                for (HouseblockUpdateModel houseblockUpdateModel : otherUpdateFields) {
                    String validationError = houseblockUpdateModel.validate();
                    if (validationError != null) {
                        throw new VngBadRequestException(validationError);
                    }
                    updateHouseblockProperty(project, houseblock, houseblockUpdateModel, loggedUser.getUuid(), updateDate);
                }
                transaction1.commit();
                repo.getSession().clear();
                houseblock = houseblockService.getCurrentHouseblockAndPerformPreliminaryUpdateChecks(repo, houseblockModelToUpdate.getHouseblockId());
                project = projectService.getCurrentProject(repo, houseblockModelToUpdate.getProjectId());
            }
        }

        //handle duration update last
        HouseblockUpdateModel startDateUpdateModel = houseblockUpdateModelList.stream()
            .filter(m -> m.getProperty().equals(HouseblockUpdateModel.HouseblockProperty.startDate))
            .findFirst().orElse(null);
        if (startDateUpdateModel != null) {
            String validationError = startDateUpdateModel.validate();
            if (validationError != null) {
                throw new VngBadRequestException(validationError);
            }
            try (AutoCloseTransaction transaction2 = repo.beginTransaction()) {
                updateHouseblockProperty(project, houseblock, startDateUpdateModel, loggedUser.getUuid(), updateDate);
                transaction2.commit();
                repo.getSession().clear();
                houseblock = houseblockService.getCurrentHouseblockAndPerformPreliminaryUpdateChecks(repo, houseblockModelToUpdate.getHouseblockId());
                project = projectService.getCurrentProject(repo, houseblockModelToUpdate.getProjectId());
            }
        }

        HouseblockUpdateModel endDateUpdateModel = houseblockUpdateModelList.stream()
            .filter(m -> m.getProperty().equals(HouseblockUpdateModel.HouseblockProperty.endDate))
            .findFirst().orElse(null);
        if (endDateUpdateModel != null) {
            String validationError = endDateUpdateModel.validate();
            if (validationError != null) {
                throw new VngBadRequestException(validationError);
            }
            try (AutoCloseTransaction transaction3 = repo.beginTransaction()) {
                updateHouseblockProperty(project, houseblock, endDateUpdateModel, loggedUser.getUuid(), updateDate);
                transaction3.commit();
                repo.getSession().clear();
            }
        }
        return houseblockService.getHouseblockSnapshot(repo, houseblockUuid);
    }

    private void updateHouseblockProperty(Project project, Houseblock houseblock, HouseblockUpdateModel updateModel, UUID loggedUserUuid, LocalDate updateDate)
        throws VngServerErrorException, VngBadRequestException {

        switch (updateModel.getProperty()) {
            case name -> houseblockService.updateHouseblockName(repo, project, houseblock, updateModel.getValue(), loggedUserUuid, updateDate);
            case purpose -> {
                Map<Purpose, Integer> purposeMap = updateModel.getValuesMap().entrySet().stream()
                    .collect(Collectors.toMap(e -> (Purpose) e.getKey(), Map.Entry::getValue));
                houseblockService.updateHouseblockPurpose(repo, project, houseblock, purposeMap, loggedUserUuid, updateDate);
            }
            case groundPosition -> {
                Map<GroundPosition, Integer> groundPositionMap = updateModel.getValuesMap().entrySet().stream()
                    .collect(Collectors.toMap(e -> (GroundPosition) e.getKey(), Map.Entry::getValue));
                houseblockService.updateHouseblockGroundPosition(repo, project, houseblock, groundPositionMap, loggedUserUuid, updateDate);
            }
            case size -> houseblockService.updateHouseblockSize(repo, project, houseblock, updateModel.getSizeValue(), loggedUserUuid, updateDate);
            case physicalAppearanceAndHouseType -> {
                Map<PhysicalAppearance, Integer> physicalAppearanceMap = updateModel.getValuesMap().entrySet().stream()
                    .filter(e -> e.getKey() instanceof PhysicalAppearance)
                    .collect(Collectors.toMap(e -> (PhysicalAppearance) e.getKey(), Map.Entry::getValue));
                Map<HouseType, Integer> houseTypeMap = updateModel.getValuesMap().entrySet().stream()
                    .filter(e -> e.getKey() instanceof HouseType)
                    .collect(Collectors.toMap(e -> (HouseType) e.getKey(), Map.Entry::getValue));
                houseblockService.updatePhysicalAppearanceAndHouseType(repo, project, houseblock, physicalAppearanceMap, houseTypeMap, loggedUserUuid, updateDate);
            }
            case programming -> houseblockService.updateHouseblockProgramming(repo, project, houseblock, updateModel.getBooleanValue(), loggedUserUuid, updateDate);
            case ownershipValue -> houseblockService.updateHouseblockOwnershipValue(repo, project, houseblock, updateModel.getOwnershipValue(),
                updateModel.getActionType(), loggedUserUuid, updateDate);
            case mutation -> houseblockService.updateHouseblockMutation(repo, project, houseblock, updateModel.getMutationValue(), loggedUserUuid, updateDate);
            case startDate ->
                houseblockService.updateHouseblockStartDate(repo, project, houseblock, LocalDate.parse(updateModel.getValue()), loggedUserUuid);
            case endDate ->
                houseblockService.updateHouseblockEndDate(repo, project, houseblock, LocalDate.parse(updateModel.getValue()), loggedUserUuid);
        }
    }

    @PUT
    @Path("/{id}/customproperties")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public List<ProjectHouseblockCustomPropertyModel> updateProjectCustomProperty(@Context LoggedUser loggedUser, @PathParam("id") UUID houseblockUuid,
                                                                                  ProjectHouseblockCustomPropertyModel houseblockCPUpdateModel)
        throws VngNotFoundException, VngBadRequestException, VngServerErrorException {

        CustomPropertyModel dbCP = customPropertiesService.getCustomProperty(repo, houseblockCPUpdateModel.getCustomPropertyId());
        if (dbCP == null || !dbCP.getObjectType().equals(ObjectType.WONINGBLOK)) {
            throw new VngBadRequestException("Custom property id does not match any known property.");
        }
        if (dbCP.getDisabled() == Boolean.TRUE) {
            throw new VngBadRequestException("Custom property is disabled.");
        }

        LocalDate updateDate = LocalDate.now();

        ProjectHouseblockCustomPropertyModel currentHouseblockCP = houseblockService.getHouseblockCustomProperties(repo, houseblockUuid).stream()
            .filter(cp -> cp.getCustomPropertyId().equals(houseblockCPUpdateModel.getCustomPropertyId()))
            .findFirst().orElse(new ProjectHouseblockCustomPropertyModel());

        try (AutoCloseTransaction transaction = repo.beginTransaction()) {
            Houseblock houseblock = houseblockService.getCurrentHouseblockAndPerformPreliminaryUpdateChecks(repo, houseblockUuid);
            Project project = projectService.getCurrentProjectAndPerformPreliminaryUpdateChecks(repo, houseblock.getProject().getId());

            switch (dbCP.getPropertyType()) {
                case BOOLEAN -> {
                    if (!Objects.equals(currentHouseblockCP.getBooleanValue(), houseblockCPUpdateModel.getBooleanValue())) {
                        houseblockService.updateHouseblockBooleanCustomProperty(repo, project, houseblock, houseblockCPUpdateModel.getCustomPropertyId(),
                            houseblockCPUpdateModel.getBooleanValue(), loggedUser.getUuid(), updateDate);
                    }
                }
                case TEXT -> {
                    if (!Objects.equals(currentHouseblockCP.getTextValue(), houseblockCPUpdateModel.getTextValue())) {
                        houseblockService.updateHouseblockTextCustomProperty(repo, project, houseblock, houseblockCPUpdateModel.getCustomPropertyId(),
                            houseblockCPUpdateModel.getTextValue(), loggedUser.getUuid(), updateDate);
                    }
                }
                case NUMERIC -> {
                    var currentNumericValue = currentHouseblockCP.getNumericValue();
                    var updateNumericValue = houseblockCPUpdateModel.getNumericValue();
                    if (updateNumericValue == null || !updateNumericValue.isValid()) {
                        throw new VngBadRequestException("Numeric value does not have a valid format.");
                    }
                    if (!Objects.equals(currentNumericValue.getValue() != null ? currentNumericValue.getValue().doubleValue() : null,
                        updateNumericValue.getValue() != null ? updateNumericValue.getValue().doubleValue() : null)
                        || !Objects.equals(currentNumericValue.getMin(), updateNumericValue.getMin())
                        || !Objects.equals(currentNumericValue.getMax(), updateNumericValue.getMax())) {
                        houseblockService.updateHouseblockNumericCustomProperty(repo, project, houseblock, houseblockCPUpdateModel.getCustomPropertyId(),
                            houseblockCPUpdateModel.getNumericValue(), loggedUser.getUuid(), updateDate);
                    }
                }
                case CATEGORY -> {
                    var currentCategories = currentHouseblockCP.getCategories();
                    var updateCategories = houseblockCPUpdateModel.getCategories();
                    if (currentCategories.size() != updateCategories.size() || !currentCategories.containsAll(updateCategories)) {
                        houseblockService.updateHouseblockCategoryCustomProperty(repo, project, houseblock, houseblockCPUpdateModel.getCustomPropertyId(),
                            new HashSet<>(updateCategories), loggedUser.getUuid(), updateDate);
                    }
                }
                case ORDINAL -> {
                    if (houseblockCPUpdateModel.getOrdinals() == null || !houseblockCPUpdateModel.getOrdinals().isValid()) {
                        throw new VngBadRequestException("Ordinal value does not have a valid format.");
                    }
                    if (!Objects.equals(currentHouseblockCP.getOrdinals(), houseblockCPUpdateModel.getOrdinals())) {
                        houseblockService.updateHouseblockOrdinalCustomProperty(repo, project, houseblock, houseblockCPUpdateModel.getCustomPropertyId(),
                            houseblockCPUpdateModel.getOrdinals(), loggedUser.getUuid(), updateDate);
                    }
                }
            }
            transaction.commit();
            repo.getSession().clear();
        }

        return houseblockService.getHouseblockCustomProperties(repo, houseblockUuid);
    }
}
