package com.vng.resources;

import java.util.UUID;

import com.fasterxml.uuid.impl.UUIDUtil;
import com.vng.dal.GenericRepository;
import com.vng.dal.VngRepository;
import com.vng.models.MilestoneModel;
import com.vng.rest.ResponseFactory;
import com.vng.rest.VngBadRequestException;
import com.vng.services.MilestoneService;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

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
    public Response getMilestone(@PathParam("id") String id) throws VngBadRequestException {
        UUID uuid = UUIDUtil.nilUUID();
        try {
            uuid = UUIDUtil.uuid(id);
        }
        catch (NumberFormatException e) {
            throw new VngBadRequestException("The provided id is not a valid UUID");
        }

        var result = milestoneService.getCurrentState(repo, uuid);

        if (result == null) {
            return ResponseFactory.jsonNotFoundResponse();
        }

        return ResponseFactory.jsonSuccesResponse(new MilestoneModel(result));
    }
}
