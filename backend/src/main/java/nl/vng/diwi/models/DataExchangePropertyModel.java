package nl.vng.diwi.models;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import nl.vng.diwi.dal.entities.DataExchangePropertySqlModel;
import nl.vng.diwi.dal.entities.enums.ObjectType;
import nl.vng.diwi.dal.entities.enums.PropertyType;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@EqualsAndHashCode
public class DataExchangePropertyModel {

    private UUID id;

    private String name;

    private UUID customPropertyId;

    private ObjectType objectType;

    private List<PropertyType> propertyTypes;

    private Boolean mandatory;

    private Boolean singleSelect;

    private List<DataExchangeOptionModel> options = new ArrayList<>();

    public DataExchangePropertyModel(DataExchangePropertySqlModel sqlProperty) {
        this.id = sqlProperty.getId();
        this.name = sqlProperty.getName();
        this.customPropertyId = sqlProperty.getCustomPropertyId();
        this.objectType = sqlProperty.getObjectType();
        this.propertyTypes = sqlProperty.getPropertyTypes();
        this.mandatory = sqlProperty.getMandatory();
        this.singleSelect = sqlProperty.getSingleSelect();
        sqlProperty.getOptions().forEach(option -> this.options.add(new DataExchangeOptionModel(option)));
    }
}
