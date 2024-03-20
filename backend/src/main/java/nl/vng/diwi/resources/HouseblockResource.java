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
import nl.vng.diwi.dal.entities.enums.Purpose;
import nl.vng.diwi.models.HouseblockSnapshotModel;
import nl.vng.diwi.models.HouseblockUpdateModel;
import nl.vng.diwi.models.MilestoneModel;
import nl.vng.diwi.rest.VngBadRequestException;
import nl.vng.diwi.rest.VngNotFoundException;
import nl.vng.diwi.rest.VngServerErrorException;
import nl.vng.diwi.security.LoggedUser;
import nl.vng.diwi.services.HouseblockService;
import nl.vng.diwi.services.ProjectService;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import static nl.vng.diwi.security.SecurityRoleConstants.Admin;

@Path("/houseblock")
@RolesAllowed({Admin})
public class HouseblockResource {

    private final VngRepository repo;
    private final HouseblockService houseblockService;
    private final ProjectService projectService;

    @Inject
    public HouseblockResource(
        GenericRepository genericRepository,
        HouseblockService houseblockService,
        ProjectService projectService) {
        this.repo = new VngRepository(genericRepository.getDal().getSession());
        this.houseblockService = houseblockService;
        this.projectService = projectService;
        this.houseblockService.setProjectService(projectService);
    }

    @GET
    @Path("/{uuid}")
    @Produces(MediaType.APPLICATION_JSON)
    public HouseblockSnapshotModel getCurrentHouseblockSnapshot(@PathParam("uuid") UUID houseblockUuid) throws VngNotFoundException {

        return houseblockService.getHouseblockSnapshot(repo, houseblockUuid);

    }

    @POST
    @Path("/add")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public HouseblockSnapshotModel createHouseblock(@Context LoggedUser loggedUser, HouseblockSnapshotModel houseblockSnapshotModel)
        throws VngNotFoundException, VngBadRequestException {

        try (AutoCloseTransaction transaction = repo.beginTransaction()) {
            Project project = projectService.getCurrentProject(repo, houseblockSnapshotModel.getProjectId());
            Milestone startMilestone = projectService.getOrCreateMilestoneForProject(repo, project, houseblockSnapshotModel.getStartDate(), loggedUser.getUuid());
            Milestone endMilestone = projectService.getOrCreateMilestoneForProject(repo, project, houseblockSnapshotModel.getEndDate(), loggedUser.getUuid());

            String validationError = houseblockSnapshotModel.validate(new MilestoneModel(project.getDuration().get(0).getStartMilestone()).getDate(),
                new MilestoneModel(project.getDuration().get(0).getEndMilestone()).getDate());

            if (validationError != null) {
                throw new VngBadRequestException(validationError);
            }

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
        for (HouseblockUpdateModel.HouseblockProperty projectProperty : HouseblockUpdateModel.HouseblockProperty.values()) {
            switch (projectProperty) {
                case name -> {
                    if (!Objects.equals(houseblockModelToUpdate.getHouseblockName(), houseblockCurrentValues.getHouseblockName())) {
                        houseblockUpdateModelList.add(new HouseblockUpdateModel(HouseblockUpdateModel.HouseblockProperty.name, houseblockModelToUpdate.getHouseblockName()));
                    }
                }
                case purpose -> {
                    if (!Objects.equals(houseblockModelToUpdate.getPurpose(), houseblockCurrentValues.getPurpose())) {
                        Map<Purpose, Integer> purposeMap = new HashMap<>();
                        purposeMap.put(Purpose.REGULIER, houseblockModelToUpdate.getPurpose().getRegular());
                        purposeMap.put(Purpose.JONGEREN, houseblockModelToUpdate.getPurpose().getYouth());
                        purposeMap.put(Purpose.STUDENTEN, houseblockModelToUpdate.getPurpose().getStudent());
                        purposeMap.put(Purpose.OUDEREN, houseblockModelToUpdate.getPurpose().getElderly());
                        purposeMap.put(Purpose.GEHANDICAPTEN_EN_ZORG, houseblockModelToUpdate.getPurpose().getGHZ());
                        purposeMap.put(Purpose.GROTE_GEZINNEN, houseblockModelToUpdate.getPurpose().getLargeFamilies());
                        houseblockUpdateModelList.add(new HouseblockUpdateModel(HouseblockUpdateModel.HouseblockProperty.purpose, purposeMap));
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
                default -> throw new VngServerErrorException(String.format("Houseblock property not implemented %s ", projectProperty));
            }
        }

        LocalDate updateDate = LocalDate.now();

        try (AutoCloseTransaction transaction = repo.beginTransaction()) {
            Project project = projectService.getCurrentProjectAndPerformPreliminaryUpdateChecks(repo, houseblockModelToUpdate.getProjectId());
            Houseblock houseblock = houseblockService.getCurrentHouseblockAndPerformPreliminaryUpdateChecks(repo, houseblockModelToUpdate.getHouseblockId());
            if (!houseblock.getProject().getId().equals(houseblockModelToUpdate.getProjectId())) {
                throw new VngBadRequestException(String.format("Houseblock %s does not belong to project %s", houseblockModelToUpdate.getHouseblockId(), houseblockModelToUpdate.getProjectId()));
            }
            for (HouseblockUpdateModel houseblockUpdateModel : houseblockUpdateModelList) {
                String validationError = houseblockUpdateModel.validate();
                if (validationError != null) {
                    throw new VngBadRequestException(validationError);
                }
                updateHouseblockProperty(project, houseblock, houseblockUpdateModel, loggedUser, updateDate);
            }
            transaction.commit();
            repo.getSession().clear();
        }

        return houseblockService.getHouseblockSnapshot(repo, houseblockUuid);
    }

    private void updateHouseblockProperty(Project project, Houseblock houseblock, HouseblockUpdateModel updateModel, LoggedUser loggedUser, LocalDate updateDate)
        throws VngServerErrorException {

        switch (updateModel.getProperty()) {
            case name -> houseblockService.updateHouseblockName(repo, project, houseblock, updateModel.getValue(), loggedUser.getUuid(), updateDate);
            case purpose -> houseblockService.updateHouseblockPurpose(repo, project, houseblock, updateModel.getPurposeMap(), loggedUser.getUuid(), updateDate);
        }
    }

}
