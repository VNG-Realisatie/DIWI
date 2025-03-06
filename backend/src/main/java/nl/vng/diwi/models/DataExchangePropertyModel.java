package nl.vng.diwi.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import nl.vng.diwi.dal.entities.DataExchangePropertySqlModel;
import nl.vng.diwi.dal.entities.enums.ObjectType;
import nl.vng.diwi.dal.entities.enums.PropertyType;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Builder
public class DataExchangePropertyModel {

    private UUID id;

    private String name;

    private UUID customPropertyId;

    private ObjectType objectType;

    private List<PropertyType> propertyTypes;

    private Boolean mandatory;

    private Boolean singleSelect;

    @Builder.Default
    private List<DataExchangeOptionModel> options = new ArrayList<>();

    public DataExchangePropertyModel(DataExchangePropertySqlModel sqlProperty) {
        this.id = sqlProperty.getId();
        this.name = sqlProperty.getName();
        this.customPropertyId = sqlProperty.getCustomPropertyId();
        this.objectType = sqlProperty.getObjectType();
        this.propertyTypes = sqlProperty.getPropertyTypes();
        this.mandatory = sqlProperty.getMandatory();
        this.singleSelect = sqlProperty.getSingleSelect();
        Map<UUID, DataExchangeOptionModel> optionsMap = new HashMap<>();
        sqlProperty.getOptions().forEach(option -> {
            if (!optionsMap.containsKey(option.getId())) {
                optionsMap.put(option.getId(), new DataExchangeOptionModel(option));
            }
            DataExchangeOptionModel optionModel = optionsMap.get(option.getId());
            if (option.getPropertyCategoryValueId() != null) {
                optionModel.getPropertyCategoryValueIds().add(option.getPropertyCategoryValueId());
            }
            if (option.getPropertyOrdinalValueId() != null) {
                optionModel.getPropertyOrdinalValueIds().add(option.getPropertyOrdinalValueId());
            }
        });
        this.options.addAll(optionsMap.values());
        this.options.sort(Comparator.comparing(DataExchangeOptionModel::getName));
    }
}
