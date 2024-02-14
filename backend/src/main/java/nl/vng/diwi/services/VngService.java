package nl.vng.diwi.services;

import nl.vng.diwi.config.ProjectConfig;
import nl.vng.diwi.dal.VngRepository;
import nl.vng.diwi.dal.entities.OrganizationState;
import nl.vng.diwi.models.SelectModel;
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

        return organizationList.stream().map(o -> new SelectModel(o.getId().toString(), o.getName())).toList();
    }

}
