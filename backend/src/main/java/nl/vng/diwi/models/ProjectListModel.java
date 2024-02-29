package nl.vng.diwi.models;

import com.fasterxml.uuid.impl.UUIDUtil;
import lombok.Data;

import lombok.EqualsAndHashCode;
import nl.vng.diwi.dal.entities.enums.PlanStatus;

import nl.vng.diwi.dal.entities.enums.PlanType;
import nl.vng.diwi.models.superclasses.ProjectSnapshotModelSuperclass;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
public class ProjectListModel extends ProjectSnapshotModelSuperclass {

    public static final List<String> SORTABLE_COLUMNS = List.of("projectName", "projectOwners", "projectLeaders", "confidentialityLevel", "organizationName",
        "planType", "startDate", "endDate", "priority", "projectPhase", "municipalityRole", "planningPlanStatus", "totalValue", "municipality", "wijk", "buurt");
    public static final String DEFAULT_SORT_COLUMN = "startDate";

    private UUID projectStateId;

    private List<SelectModel> municipality;

    private List<SelectModel> wijk;

    private List<SelectModel> buurt;

    public ProjectListModel(ProjectListSqlModel sqlModel) {
        this.setProjectId(sqlModel.getProjectId());
        this.projectStateId = sqlModel.getProjectStateId();
        this.setProjectName(sqlModel.getProjectName());
        this.setProjectOwners(getOrganizationModelListFromSqlArray(sqlModel.getProjectOwnersArray()));
        this.setProjectLeaders(getOrganizationModelListFromSqlArray(sqlModel.getProjectLeadersArray()));
        this.setProjectColor(sqlModel.getProjectColor());
        this.setConfidentialityLevel(sqlModel.getConfidentialityLevel());
        this.setPlanType((sqlModel.getPlanType() == null) ? new ArrayList<>() : sqlModel.getPlanType().stream().map(PlanType::valueOf).toList());
        this.setStartDate(sqlModel.getStartDate());
        this.setEndDate(sqlModel.getEndDate());
        this.setPriority(new PriorityModel(getSelectModelListFromSqlArray(sqlModel.getPriority())));
        this.setProjectPhase(sqlModel.getProjectPhase());
        this.setMunicipalityRole(getSelectModelListFromSqlArray(sqlModel.getMunicipalityRole()));
        this.setPlanningPlanStatus((sqlModel.getPlanningPlanStatus() == null) ? new ArrayList<>() : sqlModel.getPlanningPlanStatus().stream().map(PlanStatus::valueOf).toList());
        this.setTotalValue(sqlModel.getTotalValue());
        this.municipality = getSelectModelListFromSqlArray(sqlModel.getMunicipality());
        this.wijk = getSelectModelListFromSqlArray(sqlModel.getWijk());
        this.buurt = getSelectModelListFromSqlArray(sqlModel.getBuurt());

        Collections.sort(this.getMunicipalityRole());
        Collections.sort(this.municipality);
        Collections.sort(this.wijk);
        Collections.sort(this.buurt);

    }

    public List<SelectModel> getSelectModelListFromSqlArray(String[][] sqlArray) {
        List<SelectModel> result = new ArrayList<>();
        if (sqlArray != null) {
            for (String[] sqlSelectModel : sqlArray) {
                result.add(new SelectModel(UUID.fromString(sqlSelectModel[0]), sqlSelectModel[1]));
            }
        }
        return result;
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
