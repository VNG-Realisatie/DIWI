package nl.vng.diwi.models;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import nl.vng.diwi.dal.entities.Project;
import nl.vng.diwi.models.superclasses.ProjectSnapshotModelSuperclass;

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@ToString
@RequiredArgsConstructor
@EqualsAndHashCode(callSuper=true)
public class ProjectSnapshotModel extends ProjectSnapshotModelSuperclass {
    private LocalDateModel startDate;
    private LocalDateModel endDate;
    private List<PriorityModel> priority = Arrays.asList(new PriorityModel[3]);
    private Long totalValue;
    private String[] municipality;
    private String[] wijk;
    private String[] buurt;

    public ProjectSnapshotModel(Project project) {
        ProjectTimelineModel timeline = new ProjectTimelineModel(project);
        this.setProjectId(timeline.getProjectId());
        this.setProjectName(retrieveSnapshotItem(timeline.getProjectName()).getData());
        this.setProjectColor(timeline.getProjectColor());
        this.setConfidentialityLevel(timeline.getConfidentialityLevel());
        this.setPlanType(retrieveSnapshotItem(timeline.getPlanType()).getData());
        this.setPriority(retrieveWeightedRangeOrValueSnapshotItem(timeline.getPriority()));
        this.setProjectPhase(retrieveSnapshotItem(timeline.getProjectPhase()).getData());
        this.setMunicipalityRole(retrieveSnapshotItem(timeline.getMunicipalityRole()).getData());
        this.setPlanningPlanStatus(retrieveSnapshotItem(timeline.getPlanningPlanStatus()).getData());
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

    public List<PriorityModel> getPriority() {
        return priority;
    }

    public void setPriority(WeightedRangeOrValueModel<String> priority) {
        this.priority.set(0, new PriorityModel(priority.getLevelMin(), priority.getDataMin()));
        this.priority.set(1, new PriorityModel(priority.getLevel(), priority.getData()));
        this.priority.set(2, new PriorityModel(priority.getLevelMax(), priority.getDataMax()));
    }

    public void setPriority(List<PriorityModel> priority) {
        this.priority = priority;
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
