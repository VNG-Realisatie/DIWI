package nl.vng.diwi.resources;

import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.MediaType;
import nl.vng.diwi.dal.GenericRepository;
import nl.vng.diwi.dal.VngRepository;
import nl.vng.diwi.models.ProjectAuditModel;
import nl.vng.diwi.rest.VngBadRequestException;
import nl.vng.diwi.security.LoggedUser;
import nl.vng.diwi.security.UserActionConstants;
import nl.vng.diwi.services.ProjectService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Path("/audit")
@RolesAllowed("BLOCKED_BY_DEFAULT") // This forces us to make sure each end-point has action(s) assigned, so we never have things open by default.
public class AuditResource {

    private static final Logger logger = LogManager.getLogger();

    private final VngRepository repo;
    private final ProjectService projectService;

    @Inject
    public AuditResource(
            GenericRepository genericRepository,
            ProjectService projectService) {
        this.repo = new VngRepository(genericRepository.getDal().getSession());
        this.projectService = projectService;
    }

    @GET
    @Path("/project")
    @RolesAllowed({UserActionConstants.VIEW_OWN_PROJECTS, UserActionConstants.VIEW_OTHERS_PROJECTS})
    @Produces(MediaType.APPLICATION_JSON)
    public List<ProjectAuditModel> getProjectAuditLog(ContainerRequestContext requestContext, @QueryParam("projectId") UUID projectId, @QueryParam("startTime") String startTime,
                                                          @QueryParam("endTime") String endTime) throws VngBadRequestException {

            var loggedUser = (LoggedUser) requestContext.getProperty("loggedUser");

            if (startTime == null || endTime == null) {
                throw new VngBadRequestException("Missing start and/or end time.");
            }
            LocalDateTime startDateTime = LocalDateTime.parse(startTime, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            LocalDateTime endDateTime = LocalDateTime.parse(endTime, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            List<ProjectAuditModel> projectAuditList = projectService.getProjectAuditLog(repo, projectId, startDateTime, endDateTime, loggedUser);

            return projectAuditList;

    }

}
