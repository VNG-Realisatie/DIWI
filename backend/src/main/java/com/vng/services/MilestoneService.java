package com.vng.services;

import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.vng.config.ProjectConfig;
import com.vng.dal.VngRepository;
import com.vng.dal.entities.MilestoneState;

import jakarta.inject.Inject;
import lombok.NonNull;

public class MilestoneService {
    private static final Logger logger = LogManager.getLogger();
    private ProjectConfig projectConfig;

    @Inject
    public MilestoneService(ProjectConfig projectConfig) {
        this.projectConfig = projectConfig;
    }

    public MilestoneState getCurrentState(VngRepository repo, @NonNull UUID milestoneUuid) {
        String query = "FROM MilestoneState M WHERE M.changeEndDate IS NULL AND M.milestone.id = :uuid";
        MilestoneState result = repo.getSession()
                .createSelectionQuery(query, MilestoneState.class)
                .setParameter("uuid", milestoneUuid)
                .getSingleResultOrNull();
        return result;
    }
}
