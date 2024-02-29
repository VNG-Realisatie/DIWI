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
    private PriorityModel priority = new PriorityModel();
    private ProjectPhase projectPhase;
    private List<SelectModel> municipalityRole = new ArrayList<>();
    private List<PlanStatus> planningPlanStatus = new ArrayList<>();
    private List<OrganizationModel> projectOwners = new ArrayList<>();
    private List<OrganizationModel> projectLeaders = new ArrayList<>();
    private Long totalValue;
    private List<SelectModel> municipality;
    private List<SelectModel> wijk;
    private List<SelectModel> buurt;

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

    private <T> DatedPriorityModel retrieveWeightedRangeOrValueSnapshotItem(List<DatedPriorityModel> list, LocalDate snapshotTime) {
        if (list == null) {
            return null;
        }

        return list.stream().filter(item -> !item.getStartDate().isAfter(snapshotTime) && item.getEndDate().isAfter(snapshotTime))
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

    private <T> List<SelectModel> retrieveSnapshotItems(List<DatedDataModel<T>> list, LocalDate snapshotTime) {
        if (list == null) {
            return new ArrayList<>();
        }

        return list.stream().filter(item -> !item.getStartDate().isAfter(snapshotTime) && item.getEndDate().isAfter(snapshotTime))
            .map(item -> new SelectModel(item.getId(), item.getData().toString()))
            .toList();
    }

    public void setPriority(DatedPriorityModel priorityWeightedModel) {
        if (priorityWeightedModel != null) {
            this.priority = priorityWeightedModel.getPriorityModel();
        }
    }

}
