package nl.vng.diwi.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.With;
import nl.vng.diwi.dal.entities.DataExchangeState;
import nl.vng.diwi.dal.entities.DataExchangeType;
import nl.vng.diwi.dal.entities.enums.Confidentiality;
import nl.vng.diwi.dal.entities.enums.PropertyType;
import nl.vng.diwi.dataexchange.DataExchangeTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Data
@With
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class DataExchangeModel {

    @JsonProperty(required = true)
    private UUID id;

    @JsonProperty(required = true)
    private String name;

    @JsonProperty(required = true)
    private DataExchangeType type;

    private Confidentiality minimumConfidentiality;

    private String apiKey;

    private UUID clientId;

    private String projectUrl;

    private String projectDetailUrl;

    private Boolean valid;

    private List<DataExchangePropertyModel> properties = new ArrayList<>();

    private List<ValidationError> validationErrors;

    public DataExchangeModel(
            DataExchangeState dataExchangeState,
            boolean includeApiKey) {
        this.setId(dataExchangeState.getDataExchange().getId());
        this.setName(dataExchangeState.getName());
        this.setType(dataExchangeState.getType());
        if (includeApiKey) {
            this.setApiKey(dataExchangeState.getApiKey());
        }
        this.setProjectUrl(dataExchangeState.getProjectUrl());
        this.setValid(dataExchangeState.getValid());

        var template = DataExchangeTemplate.templates.get(dataExchangeState.getType());
        this.setMinimumConfidentiality(template.getMinimumConfidentiality());
        this.setClientId(dataExchangeState.getClientId());
    }

    public String validateDxState() {
        if (this.name == null || this.name.isBlank()) {
            return "Property name can not be null.";
        }
        if (this.type == null) {
            return "Property type can not be null.";
        }
        this.valid = false;
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

                String baseError = "The selected property for '" + dxProp.getName() + "' ";
                if (prop == null) {
                    return baseError + " with id " + dxProp.getCustomPropertyId() + " is not found.";
                } else if (prop.getObjectType() != dxProp.getObjectType()) {
                    return baseError + "does not match the expected object type (" + dxProp.getObjectType() + ").";
                } else if (!dxProp.getPropertyTypes().contains(prop.getPropertyType())) {
                    return baseError + "does not match the expected property type.";
                } else if (dxProp.getMandatory() && (prop.getMandatory() != dxProp.getMandatory())) {
                    return baseError + "does not have the expected mandatory flag (" + dxProp.getMandatory() + ").";
                } else if (List.of(PropertyType.CATEGORY, PropertyType.ORDINAL).contains(prop.getPropertyType())
                        && prop.getSingleSelect() != dxProp.getSingleSelect()) {
                    return baseError + "does not have the expected single select flag.";
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
                            return baseError + "category value ids are not valid options for custom property " + prop.getId();
                        }
                        if (prop.getPropertyType() == PropertyType.ORDINAL && !ordValues.containsAll(dxPropOption.getPropertyOrdinalValueIds())) {
                            return baseError + "ordinal value ids are not valid options for custom property " + prop.getId();
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

    public List<ValidationError> validateConfigurationComplete(List<PropertyModel> propertyModels) {
        this.validationErrors = new ArrayList<>();

        for (DataExchangePropertyModel dxPropModel : this.properties) {
            PropertyModel propertyModel = propertyModels.stream().filter(pm -> pm.getId().equals(dxPropModel.getCustomPropertyId())).findFirst().orElse(null);
            if (propertyModel == null) {
                validationErrors.add(new ValidationError(dxPropModel.getName(), null, DxValidationError.MISSING_CUSTOM_PROP));
            } else if (propertyModel.getPropertyType() == PropertyType.CATEGORY && dxPropModel.getOptions() != null) {
                Map<UUID, List<UUID>> diwiOptionToDxOption = new HashMap<>();
                dxPropModel.getOptions().forEach(dxOption -> {
                    if (dxOption.getPropertyCategoryValueIds() != null && !dxOption.getPropertyCategoryValueIds().isEmpty()) {
                        dxOption.getPropertyCategoryValueIds().forEach(diwiOptionId -> {
                            if (!diwiOptionToDxOption.containsKey(diwiOptionId)) {
                                diwiOptionToDxOption.put(diwiOptionId, new ArrayList<>());
                            }
                            diwiOptionToDxOption.get(diwiOptionId).add(dxOption.getId());
                        });
                    }
                });

                // If a dx prop has options to map to, check that that is done correctly.
                if (dxPropModel.getOptions() != null && !dxPropModel.getOptions().isEmpty() && propertyModel.getCategories() != null) {
                    propertyModel.getCategories().stream().filter(cOption -> cOption.getDisabled() == Boolean.FALSE)
                            .forEach(diwiOption -> {
                                if (!diwiOptionToDxOption.containsKey(diwiOption.getId())) {
                                    validationErrors.add(new ValidationError(dxPropModel.getName(), diwiOption.getName(), DxValidationError.OPTION_NOT_MAPPED));
                                } else if (diwiOptionToDxOption.get(diwiOption.getId()).size() > 1) {
                                    validationErrors.add(
                                            new ValidationError(dxPropModel.getName(), diwiOption.getName(), DxValidationError.OPTION_MAPPED_MULTIPLE_TIMES));
                                }
                            });
                }
            } else if (propertyModel.getPropertyType() == PropertyType.ORDINAL && dxPropModel.getOptions() != null) {
                Map<UUID, List<UUID>> diwiOptionToDxOption = new HashMap<>();
                dxPropModel.getOptions().forEach(dxOption -> {
                    if (dxOption.getPropertyOrdinalValueIds() != null && !dxOption.getPropertyOrdinalValueIds().isEmpty()) {
                        dxOption.getPropertyOrdinalValueIds().forEach(diwiOptionId -> {
                            if (!diwiOptionToDxOption.containsKey(diwiOptionId)) {
                                diwiOptionToDxOption.put(diwiOptionId, new ArrayList<>());
                            }
                            diwiOptionToDxOption.get(diwiOptionId).add(dxOption.getId());
                        });
                    }
                });

                if (propertyModel.getOrdinals() != null) {
                    propertyModel.getOrdinals().stream().filter(oOption -> oOption.getDisabled() == Boolean.FALSE)
                            .forEach(diwiOption -> {
                                if (!diwiOptionToDxOption.containsKey(diwiOption.getId())) {
                                    validationErrors.add(new ValidationError(dxPropModel.getName(), diwiOption.getName(), DxValidationError.OPTION_NOT_MAPPED));
                                } else if (diwiOptionToDxOption.get(diwiOption.getId()).size() > 1) {
                                validationErrors.add(new ValidationError(dxPropModel.getName(), diwiOption.getName(), DxValidationError.OPTION_MAPPED_MULTIPLE_TIMES));
                                }
                            });
                }
            }
        }

        this.valid = this.validationErrors.isEmpty();
        return this.validationErrors;
    }

    public boolean areStateFieldsDifferent(DataExchangeModel other) {
        return !Objects.equals(this.name, other.name) ||
                !Objects.equals(this.projectUrl, other.projectUrl) ||
                !Objects.equals(this.projectDetailUrl, other.projectDetailUrl) ||
                !Objects.equals(this.apiKey, other.apiKey) ||
                !Objects.equals(this.valid, other.valid);
    }

    public enum DxValidationError {

        MISSING_CUSTOM_PROP("missing_custom_prop", "Missing custom property."),
        OPTION_NOT_MAPPED("option_not_mapped", "Option of custom category is not mapped."),
        OPTION_MAPPED_MULTIPLE_TIMES("option_mapped_multiple_times", "Option of custom category is mapped to multiple template options.");

        public final String errorMsg;

        public final String errorCode;

        DxValidationError(String errorCode, String errorMsg) {
            this.errorCode = errorCode;
            this.errorMsg = errorMsg;
        }

    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ValidationError {
        private String dxProperty;
        private String diwiOption;
        private String error;
        private String errorCode;

        public ValidationError(String dxProperty, String diwiOption, DxValidationError dxError) {
            this.dxProperty = dxProperty;
            this.diwiOption = diwiOption;
            this.error = dxError.errorMsg;
            this.errorCode = dxError.errorCode;
        }
    }
}
