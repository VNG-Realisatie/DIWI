package nl.vng.diwi.models;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import nl.vng.diwi.dal.entities.Project;
import nl.vng.diwi.dal.entities.enums.Confidentiality;
import nl.vng.diwi.dal.entities.enums.PlanStatus;
import nl.vng.diwi.dal.entities.enums.PlanType;
import nl.vng.diwi.dal.entities.enums.ProjectPhase;
import nl.vng.diwi.models.superclasses.DatedDataModelSuperClass;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@ToString
@RequiredArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Data
public class ProjectSnapshotModel extends DatedDataModelSuperClass {
    private UUID projectId;
    private String projectName;
    private String projectColor;
    private Confidentiality confidentialityLevel;
    private List<PlanType> planType = new ArrayList<>();
    private List<PriorityModel> priority = Arrays.asList(new PriorityModel[3]);
    private ProjectPhase projectPhase;
    private List<String> municipalityRole = new ArrayList<>();
    private List<PlanStatus> planningPlanStatus = new ArrayList<>();
    private List<OrganizationModel> projectOwners = new ArrayList<>();
    private List<OrganizationModel> projectLeaders = new ArrayList<>();
    private Long totalValue;
    private String[] municipality;
    private String[] wijk;
    private String[] buurt;

    public ProjectSnapshotModel(Project project) {
        ProjectTimelineModel timeline = new ProjectTimelineModel(project);
        this.setStartDate(timeline.getStartDate());
        this.setEndDate(timeline.getEndDate());
        LocalDate snapshotTime = this.getStartDate().isAfter(LocalDate.now()) ? this.getStartDate() : LocalDate.now();
        projectId = timeline.getProjectId();
        projectName = retrieveSnapshotItem(timeline.getProjectName(), snapshotTime);
        projectColor = timeline.getProjectColor();
        confidentialityLevel = timeline.getConfidentialityLevel();
        planType = retrieveSnapshotItem(timeline.getPlanType(), snapshotTime);
        if (planType == null) {
            planType = new ArrayList<>();
        }
        this.setPriority(retrieveWeightedRangeOrValueSnapshotItem(timeline.getPriority(), snapshotTime));
        projectPhase = retrieveSnapshotItem(timeline.getProjectPhase(), snapshotTime);
        municipalityRole = retrieveSnapshotItems(timeline.getMunicipalityRole(), snapshotTime);
        planningPlanStatus = retrieveSnapshotItem(timeline.getPlanningPlanStatus(), snapshotTime);
        if (planningPlanStatus == null) {
            planningPlanStatus = new ArrayList<>();
        }
        projectOwners = timeline.getProjectOwners();
        projectLeaders = timeline.getProjectLeaders();
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

}
