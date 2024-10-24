package nl.vng.diwi.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import nl.vng.diwi.dal.entities.DataExchangeState;
import nl.vng.diwi.dal.entities.DataExchangeType;
import nl.vng.diwi.dal.entities.enums.PropertyType;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@EqualsAndHashCode
public class DataExchangeModel {

    @JsonProperty(required = true)
    private UUID id;

    @JsonProperty(required = true)
    private String name;

    @JsonProperty(required = true)
    private DataExchangeType type;

    private String apiKey;

    private String projectUrl;

    private String projectDetailUrl;

    private List<DataExchangePropertyModel> properties = new ArrayList<>();

    public DataExchangeModel(DataExchangeState dataExchangeState, boolean includeApiKey) {
        this.setId(dataExchangeState.getDataExchange().getId());
        this.setName(dataExchangeState.getName());
        this.setType(dataExchangeState.getType());
        if (includeApiKey) {
            this.setApiKey(dataExchangeState.getApiKey());
        }
        this.setProjectUrl(dataExchangeState.getProjectUrl());
        this.setProjectDetailUrl(dataExchangeState.getProjectDetailUrl());
    }

    public String validateDxState() {
        if (this.name == null || this.name.isBlank()) {
            return "Property name can not be null.";
        }
        if (this.type == null) {
            return "Property type can not be null.";
        }
        return null;
    }

    public String validateTemplateFields(List<DataExchangePropertyModel> templateProperties) {
        if (templateProperties.size() != this.properties.size()) {
            return "Model does not include all the properties in the template.";
        }

        Map<UUID, DataExchangePropertyModel> templatePropsMap = templateProperties.stream().collect(Collectors.toMap(DataExchangePropertyModel::getId, p -> p));
        for (DataExchangePropertyModel propModel : this.properties) {
            var templateModel = templatePropsMap.get(propModel.getId());
            if (templateModel == null) {
                return "Data exchange property " + propModel.getId() + " does not exist.";
            }
            propModel.setMandatory(templateModel.getMandatory());
            propModel.setSingleSelect(templateModel.getSingleSelect());
            propModel.setObjectType(templateModel.getObjectType());
            propModel.setPropertyTypes(templateModel.getPropertyTypes());

            Set<UUID> templateOptionIds = new HashSet<>();
            Set<UUID> propOptionIds = new HashSet<>();
            if (templateModel.getOptions() != null) {
                templateModel.getOptions().stream().forEach(o -> templateOptionIds.add(o.getId()));
            }
            if (propModel.getOptions() != null) {
                propModel.getOptions().stream().forEach(o -> propOptionIds.add(o.getId()));
            }
            if (templateOptionIds.size() != propOptionIds.size() || !templateOptionIds.containsAll(propOptionIds)) {
                return "Model does not include all the options in the template for property " + propModel.getId() + ".";
            }
        }

        return null;
    }

    public String validateDxProperties(List<PropertyModel> propertyModels) {

        Map<UUID, PropertyModel> propertiesMap = propertyModels.stream().collect(Collectors.toMap(PropertyModel::getId, p -> p));

        for (DataExchangePropertyModel dxProp : this.properties) {
            if (dxProp.getCustomPropertyId() != null) {
                PropertyModel prop = propertiesMap.get(dxProp.getCustomPropertyId());
                if (prop == null || prop.getObjectType() != dxProp.getObjectType() || prop.getMandatory() != dxProp.getMandatory()
                || prop.getSingleSelect() != dxProp.getSingleSelect() || !dxProp.getPropertyTypes().contains(prop.getPropertyType())) {
                    return "The selected property is not found or does not match the expected object type, property type, mandatory and single select values for this data exchange property.";
                }
                if (dxProp.getOptions() != null) {
                    List<UUID> catValues = new ArrayList<>();
                    if (prop.getCategories() != null) {
                        catValues.addAll(prop.getCategories().stream().filter(v -> v.getDisabled() == Boolean.FALSE)
                            .map(SelectDisabledModel::getId).toList());
                    }
                    List<UUID> ordValues = new ArrayList<>();
                    if (prop.getOrdinals() != null) {
                        ordValues.addAll(prop.getOrdinals().stream().filter(v -> v.getDisabled() == Boolean.FALSE)
                            .map(SelectDisabledModel::getId).toList());
                    }
                    for (var dxPropOption : dxProp.getOptions()) {
                        if (dxPropOption.getPropertyCategoryValueIds() == null) {
                            dxPropOption.setPropertyCategoryValueIds(new ArrayList<>());
                        }
                        if (dxPropOption.getPropertyOrdinalValueIds() == null) {
                            dxPropOption.setPropertyOrdinalValueIds(new ArrayList<>());
                        }
                        if (prop.getPropertyType() == PropertyType.CATEGORY && !catValues.containsAll(dxPropOption.getPropertyCategoryValueIds())) {
                            return "The selected property category value ids are not valid options for custom property " + prop.getId();
                        }
                        if (prop.getPropertyType() == PropertyType.ORDINAL && !ordValues.containsAll(dxPropOption.getPropertyOrdinalValueIds())) {
                            return "The selected property ordinal value ids are not valid options for custom property " + prop.getId();
                        }
                    }

                }
            } else {
                if (dxProp.getOptions() != null) {
                    dxProp.getOptions().forEach(o -> {
                        o.setPropertyCategoryValueIds(null);
                        o.setPropertyOrdinalValueIds(null);
                    });
                }
            }
        }

        return null;
    }

    public boolean areStateFieldsDifferent(DataExchangeModel other) {
        return !Objects.equals(this.name, other.name) ||
            !Objects.equals(this.projectUrl, other.projectUrl) ||
            !Objects.equals(this.projectDetailUrl, other.projectDetailUrl) ||
            !Objects.equals(this.apiKey, other.apiKey);
    }
}
