package com.vng.resources;

import java.util.UUID;

import com.fasterxml.uuid.impl.UUIDUtil;
import com.vng.dal.GenericRepository;
import com.vng.dal.MilestoneRepository;
import com.vng.models.MilestoneModel;
import com.vng.rest.VngBadRequestException;
import com.vng.rest.VngNotFoundException;
import com.vng.services.MilestoneService;

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

        var result = milestoneService.getCurrentState(repo, uuid);

        if (result == null) {
            throw new VngNotFoundException();
        }

        return new MilestoneModel(result);
    }
}
