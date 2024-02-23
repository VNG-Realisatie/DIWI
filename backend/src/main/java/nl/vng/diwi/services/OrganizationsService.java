package nl.vng.diwi.services;

import nl.vng.diwi.dal.VngRepository;
import nl.vng.diwi.models.OrganizationModel;
import nl.vng.diwi.models.OrganizationUserModel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

public class OrganizationsService {

    private static final Logger logger = LogManager.getLogger();

    public OrganizationsService() {
    }

    public List<OrganizationModel> getAllOrganizations(VngRepository repo) {

        List<OrganizationUserModel> organizationUserList = repo.getOrganizationDAO().getOrganizationUsersList();
        return OrganizationModel.fromOrgUserModelListToOrgModelList(organizationUserList);
    }

}
