package nl.vng.diwi.models;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import nl.vng.diwi.dal.entities.*;
import nl.vng.diwi.dal.entities.enums.*;
import nl.vng.diwi.models.superclasses.DatedDataModelSuperClass;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ProjectTimelineModel extends DatedDataModelSuperClass {
    private UUID projectId;
    private List<DatedDataModel<String>> projectName = new ArrayList<>();
    private String projectColor;
    private Confidentiality confidentialityLevel;
    private LocationModel location;
    private List<DatedDataModel<List<PlanType>>> planType = new ArrayList<>();
    private List<DatedPriorityModel> priority = new ArrayList<>();
    private List<DatedDataModel<ProjectPhase>> projectPhase = new ArrayList<>();
    private List<DatedDataModel<String>> municipalityRole = new ArrayList<>();
    private List<DatedDataModel<List<PlanStatus>>> planningPlanStatus = new ArrayList<>();
    private List<OrganizationModel> projectOwners = new ArrayList<>();
    private List<OrganizationModel> projectLeaders = new ArrayList<>();
    private Long totalValue;
    private String[] municipality;
    private String[] wijk;
    private String[] buurt;

    public ProjectTimelineModel(Project project) {
        projectId = project.getId();
        ProjectState projectState = project.getState().get(0);
        projectColor = projectState.getColor();
        confidentialityLevel = projectState.getConfidentiality();
        location = new LocationModel(projectState.getLatitude(), projectState.getLongitude());
        ProjectDurationChangelog projectDuration = project.getDuration().get(0);
        MilestoneModel projectStartMilestone = new MilestoneModel(projectDuration.getStartMilestone());
        MilestoneModel projectEndMilestone = new MilestoneModel(projectDuration.getEndMilestone());
        this.setStartDate(projectStartMilestone.getDate());
        this.setEndDate(projectEndMilestone.getDate());
        for (var item : project.getOrganizationProjectRoles()) {
            if (item.getProjectRole() == ProjectRole.OWNER) {
                projectOwners.add(new OrganizationModel(item.getOrganization()));
            } else if (item.getProjectRole() == ProjectRole.PROJECT_LEIDER) {
                projectLeaders.add(new OrganizationModel(item.getOrganization()));
            }
        }
        for (ProjectNameChangelog item : project.getName()) {
            DatedDataModel<String> data = new DatedDataModel<>();
            data.setData(item.getName());
            MilestoneModel startMilestone = new MilestoneModel(item.getStartMilestone());
            MilestoneModel endMilestone = new MilestoneModel(item.getEndMilestone());
            data.setStartDate(startMilestone.getDate());
            data.setEndDate(endMilestone.getDate());
            projectName.add(data);
        }
        for (ProjectFaseChangelog item : project.getPhase()) {
            DatedDataModel<ProjectPhase> data = new DatedDataModel<>();
            data.setData(item.getProjectPhase());
            MilestoneModel startMilestone = new MilestoneModel(item.getStartMilestone());
            MilestoneModel endMilestone = new MilestoneModel(item.getEndMilestone());
            data.setStartDate(startMilestone.getDate());
            data.setEndDate(endMilestone.getDate());
            projectPhase.add(data);
        }
        for (ProjectPlanTypeChangelog item : project.getPlanType()) {
            List<PlanType> values = new ArrayList<>();
            for (var value: item.getValue()) {
                values.add(value.getPlanType());
            }
            DatedDataModel<List<PlanType>> data = new DatedDataModel<>();
            data.setData(values);
            MilestoneModel startMilestone = new MilestoneModel(item.getStartMilestone());
            MilestoneModel endMilestone = new MilestoneModel(item.getEndMilestone());
            data.setStartDate(startMilestone.getDate());
            data.setEndDate(endMilestone.getDate());
            planType.add(data);
        }
        for (ProjectPlanologischePlanstatusChangelog item : project.getPlanologischePlanstatus()) {
            List<PlanStatus> values = new ArrayList<>();
            for (var value : item.getValue()) {
                values.add(value.getPlanStatus());
            }
            DatedDataModel<List<PlanStatus>> data = new DatedDataModel<>();
            data.setData(values);
            MilestoneModel startMilestone = new MilestoneModel(item.getStartMilestone());
            MilestoneModel endMilestone = new MilestoneModel(item.getEndMilestone());
            data.setStartDate(startMilestone.getDate());
            data.setEndDate(endMilestone.getDate());
            planningPlanStatus.add(data);
        }
    }
}
