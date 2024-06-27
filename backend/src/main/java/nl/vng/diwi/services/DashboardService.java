package nl.vng.diwi.services;

import nl.vng.diwi.dal.VngRepository;
import nl.vng.diwi.dal.entities.MultiProjectDashboardSqlModel;
import nl.vng.diwi.dal.entities.ProjectDashboardSqlModel;
import nl.vng.diwi.models.MultiProjectDashboardModel;
import nl.vng.diwi.models.ProjectDashboardModel;
import nl.vng.diwi.rest.VngNotFoundException;
import nl.vng.diwi.security.LoggedUser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDate;
import java.util.UUID;

public class DashboardService {
    private static final Logger logger = LogManager.getLogger();

    public DashboardService() {
    }

    public ProjectDashboardModel getProjectDashboardSnapshot(VngRepository repo, UUID projectUuid, LocalDate snapshotDate, LoggedUser loggedUser) throws VngNotFoundException {

        ProjectDashboardSqlModel projectModel = repo.getProjectsDAO().getProjectDashboardSnapshot(projectUuid, snapshotDate, loggedUser);

        if (projectModel == null) {
            logger.error("Project with uuid {} was not found.", projectUuid);
            throw new VngNotFoundException();
        }

        return new ProjectDashboardModel(projectModel);
    }

    public MultiProjectDashboardModel getMultiProjectDashboardSnapshot(VngRepository repo, LocalDate snapshotDate, LoggedUser loggedUser) throws VngNotFoundException {

        MultiProjectDashboardSqlModel projectModel = repo.getProjectsDAO().getMultiProjectDashboardSnapshot(snapshotDate, loggedUser);

        return new MultiProjectDashboardModel(projectModel);
    }
    
}
