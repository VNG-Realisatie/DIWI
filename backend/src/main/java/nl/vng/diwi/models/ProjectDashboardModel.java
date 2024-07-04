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

    public ProjectDashboardModel(ProjectDashboardSqlModel sqlModel) {
        this.physicalAppearance = sqlModel.getPhysicalAppearance();
    }
}
