package nl.vng.diwi.models;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import lombok.NoArgsConstructor;
import nl.vng.diwi.dal.entities.Project;
import nl.vng.diwi.models.superclasses.ProjectSnapshotModelSuperclass;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class ProjectSnapshotModel extends ProjectSnapshotModelSuperclass {

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
        this.setPriority(retrieveWeightedRangeOrValueSnapshotItem(timeline.getPriority(), snapshotTime).getPriorityModel());
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

    private DatedPriorityModel retrieveWeightedRangeOrValueSnapshotItem(List<DatedPriorityModel> list, LocalDate snapshotTime) {
        if (list == null) {
            return null;
        }

        return list.stream().filter(item -> !item.getStartDate().isAfter(snapshotTime) && item.getEndDate().isAfter(snapshotTime))
            .findFirst().orElse(new DatedPriorityModel());
    }


    private <T> T retrieveSnapshotItem(List<DatedDataModel<T>> list, LocalDate snapshotTime) {
        if (list == null) {
            return null;
        }

        return list.stream().filter(item -> !item.getStartDate().isAfter(snapshotTime) && item.getEndDate().isAfter(snapshotTime))
            .map(DatedDataModel::getData)
            .findFirst().orElse(null);
    }

    private <T> List<SelectModel> retrieveSnapshotItems(List<DatedDataModel<T>> list, LocalDate snapshotTime) {
        if (list == null) {
            return new ArrayList<>();
        }

        return list.stream().filter(item -> !item.getStartDate().isAfter(snapshotTime) && item.getEndDate().isAfter(snapshotTime))
            .map(item -> new SelectModel(item.getId(), item.getData().toString()))
            .toList();
    }

}
