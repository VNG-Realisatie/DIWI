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
import nl.vng.diwi.models.OrganizationModel;
import nl.vng.diwi.models.OrganizationUserModel;
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
    private List<OrganizationModel> projectOwners = new ArrayList<>();
    private List<OrganizationModel> projectLeaders = new ArrayList<>();

    private Long totalValue;
    private List<SelectModel> region = new ArrayList<>();
    private List<SelectModel> district = new ArrayList<>();
    private List<SelectModel> neighbourhood = new ArrayList<>();

    private LocationModel location;

    public ProjectSnapshotModelSuperclass(ProjectListSqlModel sqlModel) {
        this.setProjectId(sqlModel.getProjectId());
        this.setProjectStateId(sqlModel.getProjectStateId());
        this.setProjectName(sqlModel.getProjectName());
        this.setProjectOwners(getOrganizationModelListFromSqlArray(sqlModel.getProjectOwnersArray()));
        this.setProjectLeaders(getOrganizationModelListFromSqlArray(sqlModel.getProjectLeadersArray()));
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
        this.setRegion(sqlModel.getRegion());
        this.setDistrict(sqlModel.getDistrict());
        this.setNeighbourhood(sqlModel.getNeighbourhood());
        this.location = new LocationModel(sqlModel.getLatitude(), sqlModel.getLongitude());

        Collections.sort(this.getMunicipalityRole());
        Collections.sort(this.getRegion());
        Collections.sort(this.getDistrict());
        Collections.sort(this.getNeighbourhood());

    }


    public List<OrganizationModel> getOrganizationModelListFromSqlArray(String[][] organizationUserArray) {
        List<OrganizationModel> result = new ArrayList<>();
        if (organizationUserArray != null) {
            List<OrganizationUserModel> orgOwners = new ArrayList<>();
            for (String[] owner : organizationUserArray) {
                OrganizationUserModel organizationUserModel = getOrganizationUserModelFromSqlArrayData(owner);
                orgOwners.add(organizationUserModel);
            }
            result.addAll(OrganizationModel.fromOrgUserModelListToOrgModelList(orgOwners));
        }
        return result;
    }

    private OrganizationUserModel getOrganizationUserModelFromSqlArrayData(String[] sqlUserData) {
        OrganizationUserModel orgUser = new OrganizationUserModel();
        orgUser.setOrganizationUuid(UUIDUtil.uuid(sqlUserData[0]));
        orgUser.setOrganizationName(sqlUserData[1]);
        orgUser.setUuid(UUIDUtil.uuid(sqlUserData[2]));
        orgUser.setInitials(sqlUserData[3]);
        orgUser.setLastName(sqlUserData[4]);
        orgUser.setFirstName(sqlUserData[5]);
        return orgUser;
    }
}
