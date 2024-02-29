package nl.vng.diwi.models;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import nl.vng.diwi.dal.entities.Project;
import nl.vng.diwi.models.superclasses.ProjectSnapshotModelSuperclass;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=true)
public class ProjectSnapshotModel extends ProjectSnapshotModelSuperclass {
    private List<PriorityModel> priority = Arrays.asList(new PriorityModel[3]);
    private Long totalValue;
    private String[] municipality;
    private String[] wijk;
    private String[] buurt;

    public ProjectSnapshotModel(Project project) {
        ProjectTimelineModel timeline = new ProjectTimelineModel(project);
        this.setStartDate(timeline.getStartDate());
        this.setEndDate(timeline.getEndDate());
        LocalDate snapshotTime = this.getStartDate().isAfter(LocalDate.now()) ? this.getStartDate() : LocalDate.now();
        this.setProjectId(timeline.getProjectId());
        this.setProjectName(retrieveSnapshotItem(timeline.getProjectName(), snapshotTime));
        this.setProjectColor(timeline.getProjectColor());
        this.setConfidentialityLevel(timeline.getConfidentialityLevel());
        this.setPlanType(retrieveSnapshotItem(timeline.getPlanType(), snapshotTime));
        if (this.getPlanType() == null) {
            this.setPlanType(new ArrayList<>());
        }
        this.setPriority(retrieveWeightedRangeOrValueSnapshotItem(timeline.getPriority(), snapshotTime));
        this.setProjectPhase(retrieveSnapshotItem(timeline.getProjectPhase(), snapshotTime));
        this.setMunicipalityRole(retrieveSnapshotItems(timeline.getMunicipalityRole(), snapshotTime));
        this.setPlanningPlanStatus(retrieveSnapshotItem(timeline.getPlanningPlanStatus(), snapshotTime));
        if (this.getPlanningPlanStatus() == null) {
            this.setPlanningPlanStatus(new ArrayList<>());
        }
        this.setProjectOwners(timeline.getProjectOwners());
        this.setProjectLeaders(timeline.getProjectLeaders());
        // totalValue
        // municipality
        // wijk
        // buurt
    }

    private <T> WeightedRangeOrValueModel<T> retrieveWeightedRangeOrValueSnapshotItem(List<DatedWeightedRangeOrValueModel<T>> list, LocalDate snapshotTime) {
        if (list == null) {
            return null;
        }

        return list.stream().filter(item -> !item.getStartDate().isAfter(snapshotTime) && item.getEndDate().isAfter(snapshotTime))
            .map(WeightedRangeOrValueModel::new)
            .findFirst().orElse(null);
    }


    private <T> T retrieveSnapshotItem(List<DatedDataModel<T>> list, LocalDate snapshotTime) {
        if (list == null) {
            return null;
        }

        return list.stream().filter(item -> !item.getStartDate().isAfter(snapshotTime) && item.getEndDate().isAfter(snapshotTime))
            .map(DatedDataModel::getData)
            .findFirst().orElse(null);
    }

    private <T> List<T> retrieveSnapshotItems(List<DatedDataModel<T>> list, LocalDate snapshotTime) {
        if (list == null) {
            return new ArrayList<>();
        }

        return list.stream().filter(item -> !item.getStartDate().isAfter(snapshotTime) && item.getEndDate().isAfter(snapshotTime))
            .map(DatedDataModel::getData)
            .toList();
    }

    public void setPriority(WeightedRangeOrValueModel<String> priority) {
        if (priority != null) {
            this.priority.set(0, new PriorityModel(priority.getLevelMin(), priority.getDataMin()));
            this.priority.set(1, new PriorityModel(priority.getLevel(), priority.getData()));
            this.priority.set(2, new PriorityModel(priority.getLevelMax(), priority.getDataMax()));
        }
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
