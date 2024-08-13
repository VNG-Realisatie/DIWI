package nl.vng.diwi.models;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import nl.vng.diwi.dal.entities.ProjectDashboardSqlModel;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@EqualsAndHashCode
public class ProjectDashboardModel {

    List<PieChartModel> physicalAppearance = new ArrayList<>();

    List<RangeCategoryPieChartModel> priceCategoryRent = new ArrayList<>();

    List<RangeCategoryPieChartModel> priceCategoryOwn = new ArrayList<>();

    List<PlanningModel> planning = new ArrayList<>();

    public ProjectDashboardModel(ProjectDashboardSqlModel sqlModel) {
        this.physicalAppearance = sqlModel.getPhysicalAppearance();
        this.priceCategoryRent = sqlModel.getPriceCategoryRent();
        this.priceCategoryOwn = sqlModel.getPriceCategoryOwn();
        this.planning = sqlModel.getPlanning();
    }
}
