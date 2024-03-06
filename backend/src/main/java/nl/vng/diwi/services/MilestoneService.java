package nl.vng.diwi.services;

import java.util.UUID;

import nl.vng.diwi.dal.VngRepository;
import nl.vng.diwi.dal.entities.Milestone;

public class MilestoneService {

    public MilestoneService() {
    }

    public Milestone getCurrentMilestone(VngRepository repo, UUID milestoneUuid) {
        return repo.getMilestoneDAO().getCurrentMilestone(milestoneUuid);
    }
}
