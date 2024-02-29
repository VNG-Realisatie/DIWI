package nl.vng.diwi.models;

import io.hypersistence.utils.hibernate.type.array.StringArrayType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import nl.vng.diwi.dal.entities.enums.Confidentiality;
import nl.vng.diwi.dal.entities.enums.PlanStatus;
import nl.vng.diwi.dal.entities.enums.PlanType;
import nl.vng.diwi.dal.entities.enums.ProjectPhase;
import org.hibernate.annotations.JdbcType;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Convert(
    attributeName = "multidimensional_array",
    converter = StringArrayType.class
)
@Entity
public class ProjectListSqlModel {

    @Id
    private UUID projectId;

    private UUID projectStateId;

    private String projectName;

    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(columnDefinition = "text[][]")
    private String[][] projectOwnersArray;

    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(columnDefinition = "text[][]")
    private String[][] projectLeadersArray;

    private String projectColor;

    @Enumerated(EnumType.STRING)
    @JdbcType(PostgreSQLEnumJdbcType.class)
    private Confidentiality confidentialityLevel;

    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(columnDefinition = "text[]")
    private List<String> planType;

    private LocalDate startDate;

    private LocalDate endDate;

    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(columnDefinition = "text[][]")
    private String[][] priority;

    @Enumerated(EnumType.STRING)
    @JdbcType(PostgreSQLEnumJdbcType.class)
    private ProjectPhase projectPhase;

    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(columnDefinition = "text[][]")
    private String[][] municipalityRole;

    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(columnDefinition = "text[]")
    private List<String> planningPlanStatus;

    private Long totalValue;

    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(columnDefinition = "text[][]")
    private String[][] municipality;

    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(columnDefinition = "text[][]")
    private String[][] wijk;

    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(columnDefinition = "text[][]")
    private String[][] buurt;

    public ProjectListSqlModel() {
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

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public String[][] getPriority() {
        return priority;
    }

    public void setPriority(String[][] priority) {
        this.priority = priority;
    }

    public ProjectPhase getProjectPhase() {
        return projectPhase;
    }

    public void setProjectPhase(ProjectPhase projectPhase) {
        this.projectPhase = projectPhase;
    }

    public String[][] getMunicipalityRole() {
        return municipalityRole;
    }

    public void setMunicipalityRole(String[][] municipalityRole) {
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

    public String[][] getMunicipality() {
        return municipality;
    }

    public void setMunicipality(String[][] municipality) {
        this.municipality = municipality;
    }

    public String[][] getWijk() {
        return wijk;
    }

    public void setWijk(String[][] wijk) {
        this.wijk = wijk;
    }

    public String[][] getBuurt() {
        return buurt;
    }

    public void setBuurt(String[][] buurt) {
        this.buurt = buurt;
    }
}
