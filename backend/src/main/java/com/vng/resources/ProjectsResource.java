package com.vng.resources;

import com.vng.dal.GenericRepository;
import com.vng.dal.FilterPaginationSorting;
import com.vng.dal.VngRepository;
import com.vng.models.ProjectListModel;
import com.vng.security.LoggedUser;
import com.vng.services.VngService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.BeanParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Map;

import static com.vng.security.SecurityRoleConstants.Admin;

@Path("/projects")
@RolesAllowed({Admin})
public class ProjectsResource {
    private static final Logger logger = LogManager.getLogger();

    private final VngRepository repo;
    private final VngService vngService;

    @Inject
    public ProjectsResource(
        GenericRepository genericRepository,
        VngService vngService) {
        this.repo = new VngRepository(genericRepository.getDal().getSession());
        this.vngService = vngService;
    }

    @GET
    @Path("/table")
    @Produces(MediaType.APPLICATION_JSON)
    public List<ProjectListModel> getAllProjects(@Context LoggedUser loggedUser, @BeanParam FilterPaginationSorting filtering) {

        if (filtering.getSortColumn() == null || !ProjectListModel.SORTABLE_COLUMNS.contains(filtering.getSortColumn())) {
            filtering.setSortColumn(ProjectListModel.DEFAULT_SORT_COLUMN);
        }

        return repo.getProjectsTable(filtering);

    }

    @GET
    @Path("/table/size")
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String, Integer> getAllProjectsListSize(@Context LoggedUser loggedUser, @BeanParam FilterPaginationSorting filtering) {

        Integer projectsCount = repo.getProjectsTableCount(filtering);

        return Map.of("size", projectsCount);
    }

}
