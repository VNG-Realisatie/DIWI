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

        Map<UUID, OrganizationModel> organizationUsersMap = new HashMap<>();
        for (OrganizationUserModel orgUserModel : organizationUserList) {
            if (organizationUsersMap.containsKey(orgUserModel.getOrganizationUuid())) {
                organizationUsersMap.get(orgUserModel.getOrganizationUuid()).getUsers().add(orgUserModel);
            } else {
                OrganizationModel orgModel = new OrganizationModel();
                orgModel.setUuid(orgUserModel.getOrganizationUuid());
                orgModel.setName(orgUserModel.getOrganizationName());
                orgModel.getUsers().add(orgUserModel);
                organizationUsersMap.put(orgModel.getUuid(), orgModel);
            }
        }

        List<OrganizationModel> result = new ArrayList<>(organizationUsersMap.values());
        result.sort(Comparator.comparing(OrganizationModel::getName));

        return result;
    }

}
