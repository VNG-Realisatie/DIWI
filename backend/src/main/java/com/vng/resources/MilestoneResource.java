package com.vng.resources;

import java.util.UUID;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.uuid.impl.UUIDUtil;
import com.vng.dal.GenericRepository;
import com.vng.dal.VngRepository;
import com.vng.models.LocalDateModel;
import com.vng.models.MilestoneModel;
import com.vng.services.MilestoneService;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

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
	public Response getMilestone(@PathParam("id") String id) throws JsonProcessingException {
		UUID uuid = UUIDUtil.nilUUID();
		try {
			uuid = UUIDUtil.uuid(id);
		}
		catch (NumberFormatException e) {
			return Response.status(Status.BAD_REQUEST).entity("The provided id is not a valid UUID").build();
		}
		var result = milestoneService.getCurrentState(repo, uuid);
		
		if (result == null) {
			return Response.status(Status.NOT_FOUND).build();
		}
		
		var output = new MilestoneModel();

		output.setId(result.getMilestone().getId());
		output.setDescription(result.getDescription());
		output.setDate(new LocalDateModel(result.getDate()));
		output.setState(result.getState());
		
		var json = (new ObjectMapper())
				.writer()
				.withDefaultPrettyPrinter()
				.writeValueAsString(output);
		
		return Response.status(Status.OK).entity(json).build();
	}
}
