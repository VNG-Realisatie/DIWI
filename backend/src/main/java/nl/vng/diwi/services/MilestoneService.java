package nl.vng.diwi.services;

import java.util.UUID;

import nl.vng.diwi.dal.VngRepository;
import nl.vng.diwi.dal.entities.Milestone;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MilestoneService {
    private static final Logger logger = LogManager.getLogger();

    public MilestoneService() {
    }

    public Milestone getCurrentMilestone(VngRepository repo, UUID milestoneUuid) {
        return repo.getMilestoneDAO().getCurrentMilestone(milestoneUuid);
    }
}
