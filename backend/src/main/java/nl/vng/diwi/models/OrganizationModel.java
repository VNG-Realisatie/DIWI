package nl.vng.diwi.models;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;

import nl.vng.diwi.dal.entities.Organization;

@Data
@NoArgsConstructor
public class OrganizationModel {

    UUID uuid;
    String name;
    List<OrganizationUserModel> users = new ArrayList<>();
    
    public OrganizationModel(Organization organization) {
        uuid = organization.getId();
        // stub
    }

    public static List<OrganizationModel> fromOrgUserModelListToOrgModelList(List<OrganizationUserModel> organizationUserList) {

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
