package nl.vng.diwi.services;

import java.util.UUID;

import nl.vng.diwi.config.ProjectConfig;
import nl.vng.diwi.dal.MilestoneRepository;
import nl.vng.diwi.dal.entities.Milestone;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jakarta.inject.Inject;

public class MilestoneService {
    private static final Logger logger = LogManager.getLogger();
    private ProjectConfig projectConfig;

    @Inject
    public MilestoneService(ProjectConfig projectConfig) {
        this.projectConfig = projectConfig;
    }

    public Milestone getCurrentData(MilestoneRepository repo, UUID milestoneUuid) {
        return repo.getCurrentData(milestoneUuid);
    }
}
