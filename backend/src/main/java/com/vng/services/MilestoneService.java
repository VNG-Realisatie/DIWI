package com.vng.services;

import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.vng.config.ProjectConfig;
import com.vng.dal.MilestoneRepository;
import com.vng.dal.entities.Milestone;

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
