package nl.vng.diwi.services;

import nl.vng.diwi.dal.AutoCloseTransaction;
import nl.vng.diwi.dal.VngRepository;
import nl.vng.diwi.dal.entities.ProjectState;
import nl.vng.diwi.dal.entities.enums.Confidentiality;
import nl.vng.diwi.rest.VngNotFoundException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.UUID;

public class ProjectService {
    private static final Logger logger = LogManager.getLogger();

    public ProjectService() {
    }

    public void updateProjectColor(VngRepository repo, UUID projectUuid, String newColor) throws VngNotFoundException {

        try (AutoCloseTransaction transaction = repo.beginTransaction()) {
            ProjectState projectState = repo.getProjectsDAO().getCurrentProjectState(projectUuid);
            if (projectState == null) {
                logger.error("Active projectState was not found for projectUuid {}.", projectUuid);
                throw new VngNotFoundException();
            }
            projectState.setColor(newColor);
            repo.persist(projectState);
            transaction.commit();
        }
    }

    public void updateProjectConfidentialityLevel(VngRepository repo, UUID projectUuid, Confidentiality newConfidentiality) throws VngNotFoundException {

        try (AutoCloseTransaction transaction = repo.beginTransaction()) {
            ProjectState projectState = repo.getProjectsDAO().getCurrentProjectState(projectUuid);
            if (projectState == null) {
                logger.error("Active projectState was not found for projectUuid {}.", projectUuid);
                throw new VngNotFoundException();
            }
            projectState.setConfidentiality(newConfidentiality);
            repo.persist(projectState);
            transaction.commit();
        }
    }
}
