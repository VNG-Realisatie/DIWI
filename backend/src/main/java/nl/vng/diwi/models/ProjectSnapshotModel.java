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
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper=true)
public class ProjectSnapshotModel extends DatedDataModelSuperClass {
    private UUID projectId;
    private String projectName;
    private String projectColor;
    private Confidentiality confidentialityLevel;
    private List<PlanType> planType = new ArrayList<>();
    private WeightedRangeOrValueModel<String> priority;
    private ProjectPhase projectPhase;
    private String[] municipalityRole;
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
        // municipalityRole
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
}
