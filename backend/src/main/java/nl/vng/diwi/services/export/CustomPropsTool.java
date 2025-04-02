package nl.vng.diwi.services.export;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lombok.Data;
import nl.vng.diwi.dal.entities.ProjectExportSqlModelExtended.BooleanPropertyModel;
import nl.vng.diwi.dal.entities.ProjectExportSqlModelExtended.CategoryPropertyModel;
import nl.vng.diwi.dal.entities.ProjectExportSqlModelExtended.NumericPropertyModel;
import nl.vng.diwi.dal.entities.ProjectExportSqlModelExtended.OrdinalPropertyModel;
import nl.vng.diwi.dal.entities.ProjectExportSqlModelExtended.TextPropertyModel;
import nl.vng.diwi.dal.entities.enums.PropertyKind;
import nl.vng.diwi.models.PropertyModel;
import nl.vng.diwi.models.RangeSelectDisabledModel;

@Data
public class CustomPropsTool {

    private List<PropertyModel> properties;
    private Map<UUID, String> optionsMap;
    private Map<UUID, String> ordinalsMap;
    private Map<UUID, PropertyModel> customPropsMap;
    private Map<UUID, RangeSelectDisabledModel> rangeCategories;

    public CustomPropsTool(List<PropertyModel> customProps) {
        this.properties = customProps;

        optionsMap = customProps.stream()
                .flatMap(cp -> cp.getCategories() != null ? cp.getCategories().stream() : Stream.empty())
                .collect(Collectors.toMap(option -> option.getId(), option -> option.getName()));
        ordinalsMap = customProps.stream()
                .flatMap(cp -> cp.getOrdinals() != null ? cp.getOrdinals().stream() : Stream.empty())
                .collect(Collectors.toMap(option -> option.getId(), option -> option.getName()));

        rangeCategories = customProps.stream()
                .flatMap(cp -> cp.getRanges() != null ? cp.getRanges().stream() : Stream.empty())
                .collect(Collectors.toMap(option -> option.getId(), option -> option));

        customPropsMap = customProps.stream().collect(Collectors.toMap(PropertyModel::getId, Function.identity()));

    }

    public PropertyModel get(UUID id) {
        return customPropsMap.get(id);
    }

    public PropertyModel get(String propName) {
        return properties.stream()
                .filter(pfp -> pfp.getName().equals(propName))
                .findFirst()
                .orElse(null);
    }

    public PropertyModel getCustomProperty(UUID id) {
        PropertyModel propertyModel = customPropsMap.get(id);
        if (propertyModel != null && propertyModel.getType().equals(PropertyKind.CUSTOM)) {
            return propertyModel;
        }
        return null;
    }

    public PropertyModel getCustomProperty(String propName) {
        return properties.stream()
                .filter(pfp -> pfp.getType().equals(PropertyKind.CUSTOM) && pfp.getName().equals(propName))
                .findFirst()
                .orElse(null);
    }

    public String getOption(UUID id) {
        return optionsMap.get(id);
    }

    public String getOrdinal(UUID id) {
        return ordinalsMap.get(id);
    }

    public RangeSelectDisabledModel getRange(UUID id) {
        return rangeCategories.get(id);
    }

    public List<String> getOptions(List<UUID> optionIds) {
        if (optionIds == null) {
            return List.of();
        } else {
            return optionIds.stream()
                    .map(optionId -> optionsMap.get(optionId))
                    .toList();
        }
    }

    public Map<String, String> getCustomPropertyMap(
            List<TextPropertyModel> projectTextCustomProps,
            List<NumericPropertyModel> projectNumericCustomProps,
            List<BooleanPropertyModel> projectBooleanCustomProps,
            List<CategoryPropertyModel> projectCategoricalCustomProps,
            List<OrdinalPropertyModel> projectOrdinalPropertyModels) {

        Map<String, String> customProps = new HashMap<>();
        for (var prop : projectTextCustomProps) {
            PropertyModel propertyModel = getCustomProperty(prop.getPropertyId());
            if (propertyModel != null) {
                customProps.put(propertyModel.getName(), prop.getTextValue());
            }
        }
        for (var prop : projectNumericCustomProps) {
            PropertyModel propertyModel = getCustomProperty(prop.getPropertyId());
            if (propertyModel != null) {
                customProps.put(propertyModel.getName(), prop.getValue().toString());
            }
        }
        for (var prop : projectBooleanCustomProps) {
            PropertyModel propertyModel = getCustomProperty(prop.getPropertyId());
            if (propertyModel != null) {
                customProps.put(propertyModel.getName(), prop.getBooleanValue().toString());
            }
        }
        for (var prop : projectCategoricalCustomProps) {
            PropertyModel propertyModel = getCustomProperty(prop.getPropertyId());
            if (propertyModel != null) {
                String values = prop.getOptionValues()
                        .stream()
                        .map(optionId -> getOption(optionId))
                        .collect(Collectors.joining(","));
                customProps.put(propertyModel.getName(), values);
            }
        }
        for (var prop : projectOrdinalPropertyModels) {
            PropertyModel propertyModel = getCustomProperty(prop.getPropertyId());
            if (propertyModel != null) {
                String value = getOrdinal(prop.getPropertyValueId());
                String min = getOrdinal(prop.getMinPropertyValueId());
                String max = getOrdinal(prop.getMaxPropertyValueId());
                customProps.put(propertyModel.getName(), value != null ? value : min + "-" + max);
            }
        }
        return customProps;
    }

}
