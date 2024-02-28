package nl.vng.diwi.models.superclasses;

import java.util.ArrayList;
//import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import nl.vng.diwi.dal.entities.enums.Confidentiality;
import nl.vng.diwi.dal.entities.enums.PlanStatus;
import nl.vng.diwi.dal.entities.enums.PlanType;
import nl.vng.diwi.dal.entities.enums.ProjectPhase;
//import nl.vng.diwi.models.LocalDateModel;
//import nl.vng.diwi.models.PriorityModel;
//import nl.vng.diwi.models.WeightedRangeOrValueModel;
//import nl.vng.diwi.models.interfaces.DatedDataModelInterface;
import nl.vng.diwi.models.interfaces.ProjectSnapshotModelInterface;

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@ToString
@RequiredArgsConstructor
@EqualsAndHashCode
abstract public class ProjectSnapshotModelSuperclass implements ProjectSnapshotModelInterface/*, DatedDataModelInterface*/ {
    private UUID projectId;
    //private LocalDateModel startDate;
    //private LocalDateModel endDate;
    private String projectName;
    private String projectColor;
    private Confidentiality confidentialityLevel;
    private List<PlanType> planType = new ArrayList<>();
    //private List<PriorityModel> priority = Arrays.asList(new PriorityModel[3]);
    private ProjectPhase projectPhase;
    private List<String> municipaltyRole = new ArrayList<>();
    private List<PlanStatus> planningPlanStatus = new ArrayList<>();
/*
    public void setPriority(WeightedRangeOrValueModel<String> priority) {
        this.priority.set(0, new PriorityModel(priority.getLevelMin(), priority.getDataMin()));
        this.priority.set(1, new PriorityModel(priority.getLevel(), priority.getData()));
        this.priority.set(2, new PriorityModel(priority.getLevelMax(), priority.getDataMax()));
    }
*/
    public UUID getProjectId() {
        return projectId;
    }

    public void setProjectId(UUID projectId) {
        this.projectId = projectId;
    }
/*
    public LocalDateModel getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateModel startDate) {
        this.startDate = startDate;
    }

    public LocalDateModel getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateModel endDate) {
        this.endDate = endDate;
    }
*/
    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
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

    public List<PlanType> getPlanType() {
        return planType;
    }

    public void setPlanType(List<PlanType> planType) {
        this.planType = planType;
    }
/*
    public List<PriorityModel> getPriority() {
        return priority;
    }

    public void setPriority(List<PriorityModel> priority) {
        this.priority = priority;
    }
*/
    public ProjectPhase getProjectPhase() {
        return projectPhase;
    }

    public void setProjectPhase(ProjectPhase projectPhase) {
        this.projectPhase = projectPhase;
    }

    public List<String> getMunicipalityRole() {
        return municipaltyRole;
    }

    public void setMunicipalityRole(List<String> municipaltyRole) {
        this.municipaltyRole = municipaltyRole;
    }

    public List<PlanStatus> getPlanningPlanStatus() {
        return planningPlanStatus;
    }

    public void setPlanningPlanStatus(List<PlanStatus> planningPlanStatus) {
        this.planningPlanStatus = planningPlanStatus;
    }
}
