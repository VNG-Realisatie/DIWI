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
@EqualsAndHashCode(callSuper=true)
public class ProjectTimelineModel extends DatedDataModelSuperClass {
    private UUID projectId;
    private List<DatedDataModel<String>> projectName = new ArrayList<>();
    private String projectColor;
    private Confidentiality confidentialityLevel;
    private List<DatedDataModel<List<PlanType>>> planType = new ArrayList<>();
    private List<DatedWeightedRangeOrValueModel<String>> priority = new ArrayList<>();
    private List<DatedDataModel<ProjectPhase>> projectPhase = new ArrayList<>();
    private List<DatedDataModel<List<String>>> municipalityRole = new ArrayList<>();
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
        ProjectDurationChangelog projectDuration = project.getDuration().get(0);
        this.setStartDate(projectDuration.getStartMilestone());
        this.setEndDate(projectDuration.getEndMilestone());
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
            data.setStartDate(item.getStartMilestone());
            data.setEndDate(item.getEndMilestone());
            projectName.add(data);
        }
        for (ProjectFaseChangelog item : project.getPhase()) {
            DatedDataModel<ProjectPhase> data = new DatedDataModel<>();
            data.setData(item.getProjectPhase());
            data.setStartDate(item.getStartMilestone());
            data.setEndDate(item.getEndMilestone());
            projectPhase.add(data);
        }
        for (ProjectPlanTypeChangelog item : project.getPlanType()) {
            List<PlanType> values = new ArrayList<>();
            for (var value: item.getValue()) {
                values.add(value.getPlanType());
            }
            DatedDataModel<List<PlanType>> data = new DatedDataModel<>();
            data.setData(values);
            data.setStartDate(item.getStartMilestone());
            data.setEndDate(item.getEndMilestone());
            planType.add(data);
        }
        for (ProjectGemeenteRolChangelog item : project.getMunicipalityRole()) {
            List<String> values = new ArrayList<>();
            for (var value : item.getValue().getState()) {
                values.add(value.getValueLabel());
            }
            DatedDataModel<List<String>> data = new DatedDataModel<>();
            data.setData(values);
            data.setStartDate(item.getStartMilestone());
            data.setEndDate(item.getEndMilestone());
            municipalityRole.add(data);
        }
        for (ProjectPlanologischePlanstatusChangelog item : project.getPlanologischePlanstatus()) {
            List<PlanStatus> values = new ArrayList<>();
            for (var value : item.getValue()) {
                values.add(value.getPlanStatus());
            }
            DatedDataModel<List<PlanStatus>> data = new DatedDataModel<>();
            data.setData(values);
            data.setStartDate(item.getStartMilestone());
            data.setEndDate(item.getEndMilestone());
            planningPlanStatus.add(data);
        }
        for (ProjectPrioriseringChangelog item : project.getPriority()) {
            DatedWeightedRangeOrValueModel<String> data = new DatedWeightedRangeOrValueModel<String>();
            if (item.getMinValue() != null) {
                data.setMin(
                        item.getMinValue().getState().get(0).getOrdinalLevel(),
                        item.getMinValue().getState().get(0).getValueLabel()
                );
            }
            if (item.getMaxValue() != null) {
                data.setMax(
                        item.getMaxValue().getState().get(0).getOrdinalLevel(),
                        item.getMaxValue().getState().get(0).getValueLabel()
                );
            }
            if (item.getValue() != null) {
                data.setData(item.getValue().getState().get(0).getValueLabel());
                data.setLevel(item.getValue().getState().get(0).getOrdinalLevel());
            }
            data.setStartDate(item.getStartMilestone());
            data.setEndDate(item.getEndMilestone());
            priority.add(data);
        }
    }
}
