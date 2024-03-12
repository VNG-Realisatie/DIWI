package nl.vng.diwi.services;

import java.util.List;

import nl.vng.diwi.dal.VngRepository;
import nl.vng.diwi.models.OrganizationModel;
import nl.vng.diwi.models.OrganizationUserModel;

public class OrganizationsService {
    public OrganizationsService() {
    }

    public List<OrganizationModel> getAllOrganizations(VngRepository repo) {

        List<OrganizationUserModel> organizationUserList = repo.getOrganizationDAO().getOrganizationUsersList();
        return OrganizationModel.fromOrgUserModelListToOrgModelList(organizationUserList);
    }

}
