package nl.vng.diwi.models;

import com.fasterxml.uuid.impl.UUIDUtil;
import lombok.Data;
import nl.vng.diwi.dal.entities.enums.Confidentiality;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Data
public class ProjectListModel {

    public static final List<String> SORTABLE_COLUMNS = List.of("projectName", "projectOwners", "projectLeaders", "confidentialityLevel", "organizationName",
        "planType", "startDate", "endDate", "priority", "projectPhase", "municipalityRole", "planningPlanStatus", "totalValue", "municipality", "wijk", "buurt");
    public static final String DEFAULT_SORT_COLUMN = "startDate";

    private UUID projectId;

    private UUID projectStateId;

    private String projectName;

    private List<OrganizationModel> projectOwners = new ArrayList<>();

    private List<OrganizationModel> projectLeaders = new ArrayList<>();

    private String projectColor;

    private Confidentiality confidentialityLevel;

    private List<String> planType;

    private String startDate;

    private String endDate;

    private PriorityModel priority;

    private String projectPhase;

    private List<SelectModel> municipalityRole;

    private List<String> planningPlanStatus;

    private Long totalValue;

    private List<SelectModel> municipality;

    private List<SelectModel> wijk;

    private List<SelectModel> buurt;

    public ProjectListModel(ProjectListSqlModel sqlModel) {
        this.projectId = sqlModel.getProjectId();
        this.projectStateId = sqlModel.getProjectStateId();
        this.projectName = sqlModel.getProjectName();
        this.projectOwners = getOrganizationModelListFromSqlArray(sqlModel.getProjectOwnersArray());
        this.projectLeaders = getOrganizationModelListFromSqlArray(sqlModel.getProjectLeadersArray());
        this.projectColor = sqlModel.getProjectColor();
        this.confidentialityLevel = sqlModel.getConfidentialityLevel();
        this.planType = sqlModel.getPlanType();
        this.startDate = sqlModel.getStartDate();
        this.endDate = sqlModel.getEndDate();
        this.priority = new PriorityModel(getSelectModelListFromSqlArray(sqlModel.getPriority()));
        this.projectPhase = sqlModel.getProjectPhase();
        this.municipalityRole = getSelectModelListFromSqlArray(sqlModel.getMunicipalityRole());
        this.planningPlanStatus = sqlModel.getPlanningPlanStatus();
        this.totalValue = sqlModel.getTotalValue();
        this.municipality = getSelectModelListFromSqlArray(sqlModel.getMunicipality());
        this.wijk = getSelectModelListFromSqlArray(sqlModel.getWijk());
        this.buurt = getSelectModelListFromSqlArray(sqlModel.getBuurt());

        Collections.sort(this.municipalityRole);
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
