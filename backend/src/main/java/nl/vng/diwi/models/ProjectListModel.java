package nl.vng.diwi.models;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import nl.vng.diwi.dal.entities.enums.Confidentiality;

import org.hibernate.annotations.JdbcType;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;
import org.hibernate.type.SqlTypes;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.uuid.impl.UUIDUtil;

import io.hypersistence.utils.hibernate.type.array.StringArrayType;
import jakarta.persistence.*;

@Convert(
    attributeName = "multidimensional_array",
    converter = StringArrayType.class
)
@Entity
public class ProjectListModel {

    public static final List<String> SORTABLE_COLUMNS = List.of("projectName", "projectOwners", "projectLeaders", "confidentialityLevel", "organizationName",
        "planType", "startDate", "endDate", "priority", "projectPhase", "municipalityRole", "planningPlanStatus", "totalValue", "municipality", "wijk", "buurt");
    public static final String DEFAULT_SORT_COLUMN = "startDate";

    @Id
    private UUID projectId;

    private UUID projectStateId;

    private String projectName;

    @JsonIgnore
    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(columnDefinition = "text[][]")
    private String[][] projectOwnersArray;

    @JsonIgnore
    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(columnDefinition = "text[][]")
    private String[][] projectLeadersArray;

    @Transient
    private List<OrganizationModel> projectOwners = new ArrayList<>();

    @Transient
    private List<OrganizationModel> projectLeaders = new ArrayList<>();

    private String projectColor;

    @Enumerated(EnumType.STRING)
    @JdbcType(PostgreSQLEnumJdbcType.class)
    private Confidentiality confidentialityLevel;

    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(columnDefinition = "text[]")
    private List<String> planType;

    private String startDate;

    private String endDate;

    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(columnDefinition = "text[]")
    private List<String> priority;

    private String projectPhase;

    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(columnDefinition = "text[]")
    private List<String> municipalityRole;

    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(columnDefinition = "text[]")
    private List<String> planningPlanStatus;

    private Long totalValue;

    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(columnDefinition = "text[]")
    private List<String> municipality;

    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(columnDefinition = "text[]")
    private List<String> wijk;

    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(columnDefinition = "text[]")
    private List<String> buurt;

    public ProjectListModel() {
    }

    public void processProjectOwnersAndLeadersArrays() {

        if (projectOwnersArray != null) {
            List<OrganizationUserModel> orgOwners = new ArrayList<>();
            for (String[] owner : projectOwnersArray) {
                OrganizationUserModel organizationUserModel = getOrganizationUserModelFromSqlArrayData(owner);
                orgOwners.add(organizationUserModel);
            }
            this.projectOwners.addAll(OrganizationModel.fromOrgUserModelListToOrgModelList(orgOwners));
        }

        if (projectLeadersArray != null) {
            List<OrganizationUserModel> orgLeaders = new ArrayList<>();
            for (String[] leader : projectLeadersArray) {
                OrganizationUserModel organizationUserModel = getOrganizationUserModelFromSqlArrayData(leader);
                orgLeaders.add(organizationUserModel);
            }
            this.projectLeaders.addAll(OrganizationModel.fromOrgUserModelListToOrgModelList(orgLeaders));
        }
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

    public UUID getProjectId() {
        return projectId;
    }

    public void setProjectId(UUID projectId) {
        this.projectId = projectId;
    }

    public UUID getProjectStateId() {
        return projectStateId;
    }

    public void setProjectStateId(UUID projectStateId) {
        this.projectStateId = projectStateId;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String[][] getProjectOwnersArray() {
        return projectOwnersArray;
    }

    public void setProjectOwnersArray(String[][] projectOwnersArray) {
        this.projectOwnersArray = projectOwnersArray;
    }

    public String[][] getProjectLeadersArray() {
        return projectLeadersArray;
    }

    public void setProjectLeadersArray(String[][] projectLeadersArray) {
        this.projectLeadersArray = projectLeadersArray;
    }

    public List<OrganizationModel> getProjectOwners() {
        return projectOwners;
    }

    public void setProjectOwners(List<OrganizationModel> projectOwners) {
        this.projectOwners = projectOwners;
    }

    public List<OrganizationModel> getProjectLeaders() {
        return projectLeaders;
    }

    public void setProjectLeaders(List<OrganizationModel> projectLeaders) {
        this.projectLeaders = projectLeaders;
    }

    public String getProjectColor() {
        return projectColor;
    }

    public void setProjectColor(String projectColor) {
        this.projectColor = projectColor;
    }

    public Confidentiality getConfidentialityLevel() {
        return confidentialityLevel;
    }

    public void setConfidentialityLevel(Confidentiality confidentialityLevel) {
        this.confidentialityLevel = confidentialityLevel;
    }

    public List<String> getPlanType() {
        return planType;
    }

    public void setPlanType(List<String> planType) {
        this.planType = planType;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public List<String> getPriority() {
        return priority;
    }

    public void setPriority(List<String> priority) {
        this.priority = priority;
    }

    public String getProjectPhase() {
        return projectPhase;
    }

    public void setProjectPhase(String projectPhase) {
        this.projectPhase = projectPhase;
    }

    public List<String> getMunicipalityRole() {
        return municipalityRole;
    }

    public void setMunicipalityRole(List<String> municipalityRole) {
        this.municipalityRole = municipalityRole;
    }

    public List<String> getPlanningPlanStatus() {
        return planningPlanStatus;
    }

    public void setPlanningPlanStatus(List<String> planningPlanStatus) {
        this.planningPlanStatus = planningPlanStatus;
    }

    public Long getTotalValue() {
        return totalValue;
    }

    public void setTotalValue(Long totalValue) {
        this.totalValue = totalValue;
    }

    public List<String> getMunicipality() {
        return municipality;
    }

    public void setMunicipality(List<String> municipality) {
        this.municipality = municipality;
    }

    public List<String> getWijk() {
        return wijk;
    }

    public void setWijk(List<String> wijk) {
        this.wijk = wijk;
    }

    public List<String> getBuurt() {
        return buurt;
    }

    public void setBuurt(List<String> buurt) {
        this.buurt = buurt;
    }
}
