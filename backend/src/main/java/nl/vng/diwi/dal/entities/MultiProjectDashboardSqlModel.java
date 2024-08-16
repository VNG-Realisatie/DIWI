package nl.vng.diwi.dal.entities;

import java.util.ArrayList;
import java.util.List;

import nl.vng.diwi.models.PlanningModel;
import nl.vng.diwi.models.RangeCategoryPieChartModel;
import org.hibernate.annotations.Type;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nl.vng.diwi.dal.JsonListType;
import nl.vng.diwi.models.PieChartModel;

@NoArgsConstructor
@Getter
@Setter
public class MultiProjectDashboardSqlModel {
    @Type(value = JsonListType.class)
    @Getter(AccessLevel.NONE)
    private List<PieChartModel> physicalAppearance;

    @Type(value = JsonListType.class)
    @Getter(AccessLevel.NONE)
    private List<PieChartModel> targetGroup;

    @Type(value = JsonListType.class)
    @Getter(AccessLevel.NONE)
    private List<RangeCategoryPieChartModel> priceCategoryRent;

    @Type(value = JsonListType.class)
    @Getter(AccessLevel.NONE)
    private List<RangeCategoryPieChartModel> priceCategoryOwn;

    @Type(value = JsonListType.class)
    @Getter(AccessLevel.NONE)
    private List<PlanningModel> planning;

    public List<PieChartModel> getPhysicalAppearance() {
        if (physicalAppearance == null) {
            return new ArrayList<>();
        }
        return physicalAppearance;
    }

    public List<PieChartModel> getTargetGroup() {
        if (targetGroup == null) {
            return new ArrayList<>();
        }
        return targetGroup;
    }

    public List<RangeCategoryPieChartModel> getPriceCategoryRent() {
        if (priceCategoryRent == null) {
            return new ArrayList<>();
        }
        return priceCategoryRent;
    }

    public List<RangeCategoryPieChartModel> getPriceCategoryOwn() {
        if (priceCategoryOwn == null) {
            return new ArrayList<>();
        }
        return priceCategoryOwn;
    }

    public List<PlanningModel> getPlanning() {
        if (planning == null) {
            return new ArrayList<>();
        }
        return planning;
    }

}
