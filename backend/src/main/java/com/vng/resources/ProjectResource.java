package com.vng.resources;

import java.util.UUID;

import com.fasterxml.uuid.impl.UUIDUtil;
import com.vng.dal.GenericRepository;
import com.vng.dal.ProjectRepository;
import com.vng.dal.entities.Project;
import com.vng.models.ProjectModel;
import com.vng.rest.VngBadRequestException;
import com.vng.services.ProjectService;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/project")
public class ProjectResource {

    private ProjectRepository repo;
    private ProjectService projectService;

    @Inject
    public ProjectResource(
        GenericRepository genericRepository,
        ProjectService projectService) {
        this.repo = new ProjectRepository(genericRepository.getDal().getSession());
        this.projectService = projectService;
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Object getCurrentProjectState(@PathParam("id") String id) throws VngBadRequestException {
        UUID uuid = UUIDUtil.nilUUID();
        try {
            uuid = UUIDUtil.uuid(id);
        }
        catch (NumberFormatException e) {
            throw new VngBadRequestException("The provided id is not a valid UUID");
        }
        
        Project project = projectService.getCurrentState(repo, uuid);
        
        return new ProjectModel(project);
        //return project; //stub
    }
}
