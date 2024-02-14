package nl.vng.diwi.services;

import nl.vng.diwi.dal.AutoCloseTransaction;
import nl.vng.diwi.dal.VngRepository;
import nl.vng.diwi.dal.entities.ProjectState;
import nl.vng.diwi.dal.entities.enums.Confidentiality;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.UUID;

public class ProjectService {
    private static final Logger logger = LogManager.getLogger();

    public ProjectService() {
    }

    public void updateProjectColor(VngRepository repo, UUID projectUuid, String newColor) {

        try (AutoCloseTransaction transaction = repo.beginTransaction()) {

            ProjectState projectState = repo.getProjectsDAO().getCurrentProjectState(projectUuid); //TODO: check projectState is not null?
            projectState.setColor(newColor);
            repo.persist(projectState);

            transaction.commit();
        }
    }

    public void updateProjectConfidentialityLevel(VngRepository repo, UUID projectUuid, Confidentiality newConfidentiality) {

        try (AutoCloseTransaction transaction = repo.beginTransaction()) {

            ProjectState projectState = repo.getProjectsDAO().getCurrentProjectState(projectUuid); //TODO: check projectState is not null?
            projectState.setConfidentiality(newConfidentiality);
            repo.persist(projectState);

            transaction.commit();
        }
    }
}
