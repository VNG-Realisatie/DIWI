package nl.vng.diwi.models;

import lombok.Data;
import nl.vng.diwi.dal.entities.ProjectHouseblockCustomPropertySqlModel;
import nl.vng.diwi.dal.entities.enums.PropertyType;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
public class ProjectHouseblockCustomPropertyModel {

    private UUID customPropertyId;

    private PropertyType propertyType;

    private String textValue;

    private Boolean booleanValue;

    private SingleValueOrRangeModel<BigDecimal> numericValue;

    private List<UUID> categories = new ArrayList<>();

    private SingleValueOrRangeModel<UUID> ordinals;

    public ProjectHouseblockCustomPropertyModel() {
        this.numericValue = new SingleValueOrRangeModel<>();
        this.ordinals = new SingleValueOrRangeModel<>();
    }

    public ProjectHouseblockCustomPropertyModel(ProjectHouseblockCustomPropertySqlModel sqlModel) {
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
        if (PropertyType.ORDINAL.equals(this.propertyType)) {
            this.ordinals = new SingleValueOrRangeModel<>(sqlModel.getOrdinalValueId(), sqlModel.getOrdinalMinValueId(), sqlModel.getOrdinalMaxValueId());
        }
    }
}
