package nl.vng.diwi.models;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import nl.vng.diwi.dal.entities.Project;
import nl.vng.diwi.dal.entities.enums.Confidentiality;
import nl.vng.diwi.dal.entities.enums.PlanStatus;
import nl.vng.diwi.dal.entities.enums.PlanType;
import nl.vng.diwi.dal.entities.enums.ProjectPhase;
import nl.vng.diwi.models.superclasses.DatedDataModelSuperClass;

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@ToString
@RequiredArgsConstructor
@EqualsAndHashCode(callSuper=true)
public class ProjectSnapshotModel extends DatedDataModelSuperClass {
    private UUID projectId;
    private String projectName;
    private String projectColor;
    private Confidentiality confidentialityLevel;
    private List<PlanType> planType = new ArrayList<>();
    private WeightedRangeOrValueModel<String> priority;
    private ProjectPhase projectPhase;
    private List<String> municipalityRole = new ArrayList<>();
    private List<PlanStatus> planningPlanStatus = new ArrayList<>();
    private Long totalValue;
    private String[] municipality;
    private String[] wijk;
    private String[] buurt;
    
    public ProjectSnapshotModel(Project project) {
        ProjectTimelineModel timeline = new ProjectTimelineModel(project);
        projectId = timeline.getProjectId();
        projectName = retrieveSnapshotItem(timeline.getProjectName()).getData();
        projectColor = timeline.getProjectColor();
        confidentialityLevel = timeline.getConfidentialityLevel();
        planType = retrieveSnapshotItem(timeline.getPlanType()).getData();
        priority = retrieveWeightedRangeOrValueSnapshotItem(timeline.getPriority());
        projectPhase = retrieveSnapshotItem(timeline.getProjectPhase()).getData();
        municipalityRole = retrieveSnapshotItem(timeline.getMunicipalityRole()).getData();
        planningPlanStatus = retrieveSnapshotItem(timeline.getPlanningPlanStatus()).getData();
        // totalValue
        // municipality
        // wijk
        // buurt
        this.setStartDate(timeline.getStartDate());
        this.setEndDate(timeline.getEndDate());
    }

    private <T> WeightedRangeOrValueModel<T> retrieveWeightedRangeOrValueSnapshotItem(List<DatedWeightedRangeOrValueModel<T>> list) {
        if (list == null) {
            return null;
        }

        // we assume the oldest date is the first in the list, and the date is sorted.
        // This could be a date in the future.
        WeightedRangeOrValueModel<T> output = new WeightedRangeOrValueModel<T>(list.get(0));
        LocalDateModel now = new LocalDateModel(LocalDate.now());
        

        for (var item : list) {
            // if the date we check is in the future, return the cached data.
            if (item.getStartDate().getDate().compareTo(now.getDate()) > 0) {
                return output;
            }
            // otherwise cache the current data.
            output = new WeightedRangeOrValueModel<T>(item);
        }

        return output;        
    }

    
    private <T> DatedDataModel<T> retrieveSnapshotItem(List<DatedDataModel<T>> list) {
        if (list == null) {
            return null;
        }

        // we assume the oldest date is the first in the list, and the date is sorted.
        // This could be a date in the future.
        DatedDataModel<T> output = list.get(0);
        LocalDateModel now = new LocalDateModel(LocalDate.now());
        

        for (var item : list) {
            // if the date we check is in the future, return the cached data.
            if (item.getStartDate().getDate().compareTo(now.getDate()) > 0) {
                return output;
            }
            // otherwise cache the current data.
            output = item;
        }

        return output;
    }

    public UUID getProjectId() {
        return projectId;
    }

    public void setProjectId(UUID projectId) {
        this.projectId = projectId;
    }

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

    public WeightedRangeOrValueModel<String> getPriority() {
        return priority;
    }

    public void setPriority(WeightedRangeOrValueModel<String> priority) {
        this.priority = priority;
    }

    public ProjectPhase getProjectPhase() {
        return projectPhase;
    }

    public void setProjectPhase(ProjectPhase projectPhase) {
        this.projectPhase = projectPhase;
    }

    public List<String> getMunicipalityRole() {
        return municipalityRole;
    }

    public void setMunicipalityRole(List<String> municipalityRole) {
        this.municipalityRole = municipalityRole;
    }

    public List<PlanStatus> getPlanningPlanStatus() {
        return planningPlanStatus;
    }

    public void setPlanningPlanStatus(List<PlanStatus> planningPlanStatus) {
        this.planningPlanStatus = planningPlanStatus;
    }

    public Long getTotalValue() {
        return totalValue;
    }

    public void setTotalValue(Long totalValue) {
        this.totalValue = totalValue;
    }

    public String[] getMunicipality() {
        return municipality;
    }

    public void setMunicipality(String[] municipality) {
        this.municipality = municipality;
    }

    public String[] getWijk() {
        return wijk;
    }

    public void setWijk(String[] wijk) {
        this.wijk = wijk;
    }

    public String[] getBuurt() {
        return buurt;
    }

    public void setBuurt(String[] buurt) {
        this.buurt = buurt;
    }
}
