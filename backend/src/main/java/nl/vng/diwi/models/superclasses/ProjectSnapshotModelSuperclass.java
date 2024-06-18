package nl.vng.diwi.models.superclasses;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import com.fasterxml.uuid.impl.UUIDUtil;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import nl.vng.diwi.dal.entities.ProjectListSqlModel;
import nl.vng.diwi.dal.entities.enums.PlanStatus;
import nl.vng.diwi.dal.entities.enums.PlanType;
import nl.vng.diwi.models.LocationModel;
import nl.vng.diwi.models.UserGroupModel;
import nl.vng.diwi.models.UserGroupUserModel;
import nl.vng.diwi.models.PriorityModel;
import nl.vng.diwi.models.SelectModel;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
abstract public class ProjectSnapshotModelSuperclass extends ProjectMinimalSnapshotModel {

    private UUID projectStateId;

    private List<PlanStatus> planningPlanStatus;
    private List<PlanType> planType = new ArrayList<>();
    private PriorityModel priority = new PriorityModel();
    private List<SelectModel> municipalityRole = new ArrayList<>();
    private List<UserGroupModel> projectOwners = new ArrayList<>();

    private Long totalValue;
    private List<SelectModel> municipality = new ArrayList<>();
    private List<SelectModel> district = new ArrayList<>();
    private List<SelectModel> neighbourhood = new ArrayList<>();

    private LocationModel location;

    private String geometry;

    public ProjectSnapshotModelSuperclass(ProjectListSqlModel sqlModel) {
        this.setProjectId(sqlModel.getProjectId());
        this.setProjectStateId(sqlModel.getProjectStateId());
        this.setProjectName(sqlModel.getProjectName());
        this.setProjectOwners(getUserGroupModelListFromSqlArray(sqlModel.getProjectOwnersArray()));
        this.setProjectColor(sqlModel.getProjectColor());
        this.setConfidentialityLevel(sqlModel.getConfidentialityLevel());
        this.setPlanType(sqlModel.getPlanType());
        this.setStartDate(sqlModel.getStartDate());
        this.setEndDate(sqlModel.getEndDate());
        this.setPriority(new PriorityModel(sqlModel.getPriority()));
        this.setProjectPhase(sqlModel.getProjectPhase());
        this.setMunicipalityRole(sqlModel.getMunicipalityRole());
        this.setPlanningPlanStatus(sqlModel.getPlanningPlanStatus());
        this.setTotalValue(sqlModel.getTotalValue());
        this.setMunicipality(sqlModel.getMunicipality());
        this.setDistrict(sqlModel.getDistrict());
        this.setNeighbourhood(sqlModel.getNeighbourhood());
        this.location = new LocationModel(sqlModel.getLatitude(), sqlModel.getLongitude());
        this.geometry = sqlModel.getGeometry();

        Collections.sort(this.getMunicipalityRole());
        Collections.sort(this.getMunicipality());
        Collections.sort(this.getDistrict());
        Collections.sort(this.getNeighbourhood());

    }


    public List<UserGroupModel> getUserGroupModelListFromSqlArray(String[][] userGroupUserArray) {
        List<UserGroupModel> result = new ArrayList<>();
        if (userGroupUserArray != null) {
            List<UserGroupUserModel> orgOwners = new ArrayList<>();
            for (String[] owner : userGroupUserArray) {
                UserGroupUserModel userGroupUserModel = getUserGroupUserModelFromSqlArrayData(owner);
                orgOwners.add(userGroupUserModel);
            }
            result.addAll(UserGroupModel.fromUserGroupUserModelListToUserGroupModelList(orgOwners));
        }
        return result;
    }

    private UserGroupUserModel getUserGroupUserModelFromSqlArrayData(String[] sqlUserData) {
        UserGroupUserModel groupUser = new UserGroupUserModel();
        groupUser.setUserGroupUuid(UUIDUtil.uuid(sqlUserData[0]));
        groupUser.setUserGroupName(sqlUserData[1]);
        if (sqlUserData[2] != null) {
            groupUser.setUuid(UUIDUtil.uuid(sqlUserData[2]));
        }
        groupUser.setInitials(sqlUserData[3]);
        groupUser.setLastName(sqlUserData[4]);
        groupUser.setFirstName(sqlUserData[5]);
        return groupUser;
    }
}
