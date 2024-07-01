package nl.vng.diwi.resources;

import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.MediaType;
import nl.vng.diwi.dal.GenericRepository;
import nl.vng.diwi.dal.VngRepository;
import nl.vng.diwi.models.MultiProjectDashboardModel;
import nl.vng.diwi.models.ProjectDashboardModel;
import nl.vng.diwi.rest.VngNotFoundException;
import nl.vng.diwi.security.LoggedUser;
import nl.vng.diwi.security.UserActionConstants;
import nl.vng.diwi.services.DashboardService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Path("/dashboard")
@RolesAllowed("BLOCKED_BY_DEFAULT") // This forces us to make sure each end-point has action(s) assigned, so we never have things open by default.
public class DashboardResource {

    private static final Logger logger = LogManager.getLogger();
    private static DateTimeFormatter localDateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private final VngRepository repo;
    private final DashboardService dashboardService;

    @Inject
    public DashboardResource(
        GenericRepository genericRepository,
        DashboardService dashboardService) {
        this.repo = new VngRepository(genericRepository.getDal().getSession());
        this.dashboardService = dashboardService;
    }

    @GET
    @Path("/project/{id}")
    @RolesAllowed({UserActionConstants.VIEW_OWN_PROJECTS, UserActionConstants.VIEW_OTHERS_PROJECTS})
    @Produces(MediaType.APPLICATION_JSON)
    public ProjectDashboardModel getProjectDashboardSnapshot(ContainerRequestContext requestContext, @PathParam("id") UUID projectUuid,
                                                             @QueryParam("snapshotDate") String snapshotDateStr) throws VngNotFoundException {
        var loggedUser = (LoggedUser) requestContext.getProperty("loggedUser");
        LocalDate snapshotDate = LocalDate.parse(snapshotDateStr, localDateFormatter);

        return dashboardService.getProjectDashboardSnapshot(repo, projectUuid, snapshotDate, loggedUser);
    }

    @GET
    @Path("/projects")
    @RolesAllowed({UserActionConstants.VIEW_OWN_PROJECTS, UserActionConstants.VIEW_OTHERS_PROJECTS})
    @Produces(MediaType.APPLICATION_JSON)
    public MultiProjectDashboardModel getProjectDashboardSnapshot(ContainerRequestContext requestContext, @QueryParam("snapshotDate") String snapshotDateStr) throws VngNotFoundException {
        var loggedUser = (LoggedUser) requestContext.getProperty("loggedUser");
        LocalDate snapshotDate = LocalDate.parse(snapshotDateStr, localDateFormatter);

        return dashboardService.getMultiProjectDashboardSnapshot(repo, snapshotDate, loggedUser);
    }

}
