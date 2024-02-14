package com.vng.services;

import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.vng.config.ProjectConfig;
import com.vng.dal.ProjectRepository;
import com.vng.dal.entities.Project;

import jakarta.inject.Inject;

public class ProjectService {
    private static final Logger logger = LogManager.getLogger();
    private ProjectConfig projectConfig;

    @Inject
    public ProjectService(ProjectConfig projectConfig) {
        this.projectConfig = projectConfig;
    }

    public Project getCurrentState(ProjectRepository repo, UUID uuid) {
        return repo.getCurrentState(uuid);
    }

}
