package nl.vng.diwi.models;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import nl.vng.diwi.dal.entities.MultiProjectDashboardSqlModel;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@EqualsAndHashCode
public class MultiProjectDashboardModel {

    List<PieChartModel> physicalAppearance = new ArrayList<>();

    public MultiProjectDashboardModel(MultiProjectDashboardSqlModel sqlModel) {
        this.physicalAppearance = sqlModel.getPhysicalAppearance();
    }
}
