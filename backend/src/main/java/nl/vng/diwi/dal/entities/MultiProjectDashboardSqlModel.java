package nl.vng.diwi.dal.entities;

//import io.hypersistence.utils.hibernate.type.array.StringArrayType;
//import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nl.vng.diwi.dal.JsonListType;
import nl.vng.diwi.models.PieChartModel;
import org.hibernate.annotations.Type;

import java.util.ArrayList;
import java.util.List;

//@Convert(
//    attributeName = "multidimensional_array",
//    converter = StringArrayType.class
//)
@Entity
@NoArgsConstructor
@Getter
@Setter
public class MultiProjectDashboardSqlModel {

    @Type(value = JsonListType.class)
    @Getter(AccessLevel.NONE)
    private List<PieChartModel> physicalAppearance;

    public List<PieChartModel> getPhysicalAppearance() {
        if (physicalAppearance == null) {
            return new ArrayList<>();
        }
        return physicalAppearance;
    }

}
