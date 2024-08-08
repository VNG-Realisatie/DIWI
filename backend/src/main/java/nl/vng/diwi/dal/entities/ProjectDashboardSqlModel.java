package nl.vng.diwi.dal.entities;

import io.hypersistence.utils.hibernate.type.array.StringArrayType;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nl.vng.diwi.dal.JsonListType;
import nl.vng.diwi.models.PieChartModel;
import nl.vng.diwi.models.PlanningModel;
import nl.vng.diwi.models.RangeCategoryPieChartModel;
import org.hibernate.annotations.Type;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Convert(
    attributeName = "multidimensional_array",
    converter = StringArrayType.class
)
@Entity
@NoArgsConstructor
@Getter
@Setter
public class ProjectDashboardSqlModel {

    @Id
    private UUID projectId;

    @Type(value = JsonListType.class)
    @Getter(AccessLevel.NONE)
    private List<PieChartModel> physicalAppearance;

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
