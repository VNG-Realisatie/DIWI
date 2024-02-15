package nl.vng.diwi.services;

import nl.vng.diwi.dal.AutoCloseTransaction;
import nl.vng.diwi.dal.VngRepository;
import nl.vng.diwi.dal.entities.ProjectState;
import nl.vng.diwi.dal.entities.User;
import nl.vng.diwi.dal.entities.enums.Confidentiality;
import nl.vng.diwi.rest.VngNotFoundException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.UUID;

public class ProjectService {
    private static final Logger logger = LogManager.getLogger();

    public ProjectService() {
    }

    public void updateProjectColor(VngRepository repo, UUID projectUuid, String newColor, UUID loggedInUserUuid)
        throws VngNotFoundException {

        try (AutoCloseTransaction transaction = repo.beginTransaction()) {
            ProjectState oldProjectState = repo.getProjectsDAO().getCurrentProjectState(projectUuid);
            if (oldProjectState == null) {
                logger.error("Active projectState was not found for projectUuid {}.", projectUuid);
                throw new VngNotFoundException();
            }

            if (!Objects.equals(oldProjectState.getColor(), newColor)) {
                ZonedDateTime now = ZonedDateTime.now();
                oldProjectState.setChangeEndDate(now);

                ProjectState newProjectState = new ProjectState();
                newProjectState.setProject(oldProjectState.getProject());
                newProjectState.setConfidentiality(oldProjectState.getConfidentiality());
                newProjectState.setColor(newColor);
                newProjectState.setChangeStartDate(now);
                newProjectState.setChangeUser(repo.findById(User.class, loggedInUserUuid));

                repo.persist(oldProjectState);
                repo.persist(newProjectState);
                transaction.commit();
            }
        }
    }

    public void updateProjectConfidentialityLevel(VngRepository repo, UUID projectUuid, Confidentiality newConfidentiality, UUID loggedInUserUuid)
        throws VngNotFoundException {

        try (AutoCloseTransaction transaction = repo.beginTransaction()) {
            ProjectState oldProjectState = repo.getProjectsDAO().getCurrentProjectState(projectUuid);
            if (oldProjectState == null) {
                logger.error("Active projectState was not found for projectUuid {}.", projectUuid);
                throw new VngNotFoundException();
            }

            if (!Objects.equals(oldProjectState.getConfidentiality(), newConfidentiality)) {
                ZonedDateTime now = ZonedDateTime.now();
                oldProjectState.setChangeEndDate(now);

                ProjectState newProjectState = new ProjectState();
                newProjectState.setProject(oldProjectState.getProject());
                newProjectState.setConfidentiality(newConfidentiality);
                newProjectState.setColor(oldProjectState.getColor());
                newProjectState.setChangeStartDate(now);
                newProjectState.setChangeUser(repo.findById(User.class, loggedInUserUuid));

                repo.persist(oldProjectState);
                repo.persist(newProjectState);
                transaction.commit();
            }
        }
    }
}
