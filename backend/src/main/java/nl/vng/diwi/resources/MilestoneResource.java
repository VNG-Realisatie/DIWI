package nl.vng.diwi.resources;

import java.util.UUID;

import nl.vng.diwi.dal.GenericRepository;
import nl.vng.diwi.dal.VngRepository;
import nl.vng.diwi.rest.VngNotFoundException;
import nl.vng.diwi.models.MilestoneModel;
import nl.vng.diwi.services.MilestoneService;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/milestone")
public class MilestoneResource {

    private VngRepository repo;
    private MilestoneService milestoneService;

    @Inject
    public MilestoneResource(
        GenericRepository genericRepository,
        MilestoneService milestoneService) {
        this.repo = new VngRepository(genericRepository.getDal().getSession());
        this.milestoneService = milestoneService;
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public MilestoneModel getMilestone(@PathParam("id") UUID milestoneUuid) throws VngNotFoundException {

        var result = milestoneService.getCurrentMilestone(repo, milestoneUuid);

        if (result == null) {
            throw new VngNotFoundException();
        }

        return new MilestoneModel(result);
    }
}
