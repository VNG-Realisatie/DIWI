package com.vng.services;

import com.vng.config.ProjectConfig;
import com.vng.dal.VngRepository;
import com.vng.dal.entities.OrganizationState;
import com.vng.models.SelectModel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jakarta.inject.Inject;
import java.util.List;

public class VngService {
    private static final Logger logger = LogManager.getLogger();
    private ProjectConfig projectConfig;

    @Inject
    public VngService(ProjectConfig projectConfig) {
        this.projectConfig = projectConfig;
    }

    public List<SelectModel> getAllOrganizationStates(VngRepository repo) {

        List<OrganizationState> organizationList = repo.findAll(OrganizationState.class);

        //TODO
        // apply service logic
        //end TODO

        return organizationList.stream().map(o -> new SelectModel(o.getId(), o.getName())).toList();
    }

}
