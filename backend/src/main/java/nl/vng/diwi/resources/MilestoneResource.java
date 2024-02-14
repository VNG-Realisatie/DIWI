package nl.vng.diwi.resources;

import java.util.UUID;

import com.fasterxml.uuid.impl.UUIDUtil;
import nl.vng.diwi.dal.GenericRepository;
import nl.vng.diwi.dal.MilestoneRepository;
import nl.vng.diwi.rest.VngNotFoundException;
import nl.vng.diwi.models.MilestoneModel;
import nl.vng.diwi.rest.VngBadRequestException;
import nl.vng.diwi.services.MilestoneService;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/milestone")
public class MilestoneResource {

    private MilestoneRepository repo;
    private MilestoneService milestoneService;

    @Inject
    public MilestoneResource(
        GenericRepository genericRepository,
        MilestoneService milestoneService) {
        this.repo = new MilestoneRepository(genericRepository.getDal().getSession());
        this.milestoneService = milestoneService;
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public MilestoneModel getMilestone(@PathParam("id") String id) throws VngBadRequestException, VngNotFoundException {
        UUID uuid = UUIDUtil.nilUUID();
        try {
            uuid = UUIDUtil.uuid(id);
        }
        catch (NumberFormatException e) {
            throw new VngBadRequestException("The provided id is not a valid UUID");
        }

        var result = milestoneService.getCurrentData(repo, uuid);

        if (result == null) {
            throw new VngNotFoundException();
        }

        return new MilestoneModel(result);
    }
}
