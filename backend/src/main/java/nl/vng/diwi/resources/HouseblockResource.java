package nl.vng.diwi.resources;

import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
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
import nl.vng.diwi.models.HouseblockSnapshotModel;
import nl.vng.diwi.rest.VngNotFoundException;
import nl.vng.diwi.security.LoggedUser;
import nl.vng.diwi.services.HouseblockService;
import nl.vng.diwi.services.ProjectService;

import java.time.ZonedDateTime;
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
        throws VngNotFoundException {

        try (AutoCloseTransaction transaction = repo.beginTransaction()) {
            Project project = projectService.getCurrentProject(repo, houseblockSnapshotModel.getProjectId());
            Milestone startMilestone = projectService.getOrCreateMilestoneForProject(repo, project, houseblockSnapshotModel.getStartDate(), loggedUser.getUuid());
            Milestone endMilestone = projectService.getOrCreateMilestoneForProject(repo, project, houseblockSnapshotModel.getEndDate(), loggedUser.getUuid());

            Houseblock houseblock = houseblockService.createHouseblock(repo, houseblockSnapshotModel, startMilestone, endMilestone,
                loggedUser.getUuid(), ZonedDateTime.now());
            transaction.commit();

            return houseblockService.getHouseblockSnapshot(repo, houseblock.getId());
        }
    }


}
