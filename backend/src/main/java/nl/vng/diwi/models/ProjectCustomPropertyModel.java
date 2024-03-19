package nl.vng.diwi.models;

import lombok.Data;
import lombok.NoArgsConstructor;
import nl.vng.diwi.dal.entities.ProjectCustomPropertySqlModel;
import nl.vng.diwi.dal.entities.enums.PropertyType;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
public class ProjectCustomPropertyModel {

    private UUID customPropertyId;

    private PropertyType propertyType;

    private String textValue;

    private Boolean booleanValue;

    private SingleValueOrRangeModel<BigDecimal> numericValue = new SingleValueOrRangeModel<>();

    private List<UUID> categories = new ArrayList<>();

    public ProjectCustomPropertyModel(ProjectCustomPropertySqlModel sqlModel) {
        this.customPropertyId = sqlModel.getCustomPropertyId();
        this.propertyType = sqlModel.getPropertyType();
        this.textValue = sqlModel.getTextValue();
        this.booleanValue = sqlModel.getBooleanValue();
        if (PropertyType.NUMERIC.equals(this.propertyType)) {
            this.numericValue = new SingleValueOrRangeModel<>(sqlModel.getNumericValue() != null ? BigDecimal.valueOf(sqlModel.getNumericValue()) : null, sqlModel.getNumericValueRange());
        }
        if (PropertyType.CATEGORY.equals(this.propertyType)) {
            this.categories.addAll(sqlModel.getCategories());
        }
    }
}
