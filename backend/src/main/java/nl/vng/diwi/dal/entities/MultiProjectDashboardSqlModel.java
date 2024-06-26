package nl.vng.diwi.dal.entities;

import io.hypersistence.utils.hibernate.type.array.StringArrayType;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import nl.vng.diwi.dal.JsonListType;
import nl.vng.diwi.models.PieChartModel;
import org.hibernate.annotations.Type;
import org.hibernate.type.descriptor.java.StringJavaType;

import java.util.ArrayList;
import java.util.List;

@Convert(
   attributeName = "array",
   converter = StringJavaType.class
)
@NoArgsConstructor
@Getter
@Setter
@Log4j2
public class MultiProjectDashboardSqlModel {

    // MultiProjectDashboardSqlModel(Object input) {
    //     log.info("input {}", input);
    //     // this.multidimensional_array = multidimensional_array;
    // }

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
