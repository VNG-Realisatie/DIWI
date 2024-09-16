package nl.vng.diwi.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Id;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nl.vng.diwi.dal.VngRepository;
import nl.vng.diwi.dal.entities.PlanCategory;
import nl.vng.diwi.dal.entities.enums.GoalDirection;
import nl.vng.diwi.dal.entities.enums.GoalType;
import nl.vng.diwi.dal.entities.enums.GroundPosition;
import nl.vng.diwi.dal.entities.enums.HouseType;
import nl.vng.diwi.dal.entities.enums.OwnershipType;
import nl.vng.diwi.dal.entities.enums.PropertyKind;
import nl.vng.diwi.dal.entities.enums.PropertyType;
import nl.vng.diwi.generic.Constants;
import nl.vng.diwi.models.superclasses.DatedDataModelSuperClass;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class PlanModel extends DatedDataModelSuperClass {

    @Id
    private UUID id;

    @JsonProperty(required = true)
    private String name;

    @JsonProperty(required = true)
    private GoalType goalType;

    @JsonProperty(required = true)
    private GoalDirection goalDirection;

    @JsonProperty(required = true)
    private BigDecimal goalValue;

    @JsonProperty(required = false)
    private SelectModel category;

    @JsonProperty(required = false)
    private List<PlanConditionModel> conditions = new ArrayList<>();

    @JsonProperty(required = false)
    private PlanGeographyModel geography;

    public PlanModel(PlanSqlModel sqlModel) {
        this.id = sqlModel.getId();
        this.name = sqlModel.getName();
        this.setStartDate(sqlModel.getStartDate());
        this.setEndDate(sqlModel.getEndDate());
        this.goalType = sqlModel.getGoalType();
        this.goalDirection = sqlModel.getGoalDirection();
        this.goalValue = sqlModel.getGoalValue();
        this.category = sqlModel.getPlanCategory();

        if (sqlModel.getGeographyConditionId() != null) {
            this.geography = new PlanGeographyModel();
            this.geography.setConditionId(sqlModel.getGeographyConditionId());
            this.geography.setOptions(sqlModel.getGeographyOptions());
        }

        if (sqlModel.getProgrammingConditionId() != null) {
            PlanConditionModel condition = new PlanConditionModel();
            condition.setConditionFieldType(ConditionFieldType.PROGRAMMING);
            condition.setConditionId(sqlModel.getProgrammingConditionId());
            condition.setBooleanValue(sqlModel.getProgrammingValue());
            this.conditions.add(condition);
        }

        if (sqlModel.getGroundPositionConditionId() != null) {
            PlanConditionModel condition = new PlanConditionModel();
            condition.setConditionFieldType(ConditionFieldType.GROUND_POSITION);
            condition.setConditionId(sqlModel.getGroundPositionConditionId());
            condition.setListOptions(sqlModel.getGroundPositionOptions().stream().map(Enum::name).toList());
            this.conditions.add(condition);
        }

        if (sqlModel.getHouseTypeConditionId() != null) {
            PlanConditionModel condition = new PlanConditionModel();
            condition.setConditionFieldType(ConditionFieldType.HOUSE_TYPE);
            condition.setConditionId(sqlModel.getHouseTypeConditionId());
            condition.setListOptions(sqlModel.getHouseTypeOptions().stream().map(Enum::name).toList());
            this.conditions.add(condition);
        }

        if (sqlModel.getOwnershipConditionId() != null) {
            PlanConditionModel condition = new PlanConditionModel();
            condition.setConditionFieldType(ConditionFieldType.OWNERSHIP);
            condition.setConditionId(sqlModel.getOwnershipConditionId());
            condition.setOwnershipOptions(sqlModel.getOwnershipOptions().stream().map(PlanOwnershipModel::new).toList());
            this.conditions.add(condition);
        }

        if (sqlModel.getPropertyConditions() != null) {
            sqlModel.getPropertyConditions().forEach(pc -> {
                PlanConditionModel condition = new PlanConditionModel();
                condition.setConditionFieldType(ConditionFieldType.PROPERTY);
                condition.setPropertyId(pc.getPropertyId());
                condition.setPropertyName(pc.getPropertyName());
                condition.setPropertyType(pc.getPropertyType());
                condition.setPropertyKind(pc.getPropertyKind());
                condition.setConditionId(pc.getConditionId());
                condition.setBooleanValue(pc.getBooleanValue());
                condition.setCategoryOptions(pc.getCategoryOptions());
                this.conditions.add(condition);
            });
        }
    }

    public boolean isPlanStateDataEqual(PlanModel that) {
        return Objects.equals(this.id, that.id) && Objects.equals(this.name, that.name) &&
            Objects.equals(this.goalType, that.goalType) && Objects.equals(this.goalDirection, that.goalDirection) &&
            Objects.equals(this.goalValue, that.goalValue) && Objects.equals(this.category, that.category);
    }

    public String validate(VngRepository repo) {
        if (this.name == null || this.name.isBlank()) {
            return "Goal name can not be null.";
        } else if (this.getStartDate() == null) {
            return "Goal start date can not be null";
        } else if (this.getEndDate() == null) {
            return "Goal deadline can not be null";
        } else if (this.goalType == null) {
            return "Goal type can not be null.";
        } else if (this.goalValue == null || this.goalValue.compareTo(BigDecimal.ZERO) < 0) {
            return "Goal value can not be null or negative.";
        } else if (this.goalDirection == null) {
            return "Goal direction can not be null.";
        }

        if (this.goalType == GoalType.NUMBER && (this.goalDirection != GoalDirection.MAXIMAL || !isIntegerValue(this.goalValue))) {
            return "Only Maximal goal direction and Integer goal values are allowed when selecting Number goal type.";
        }
        if (this.goalType == GoalType.PERCENTAGE && this.goalValue.compareTo(BigDecimal.valueOf(100)) > 0) {
            return "Goal value must be between 0 and 100 when selecting Percentage goal type.";
        }
        if (this.goalType == GoalType.PERCENTAGE && (this.conditions == null || this.conditions.isEmpty())) {
            return "Goal must have a condition when selecting Percentage goal type.";
        }

        if (this.category != null) {
            if (this.category.getId() != null) {
                PlanCategory category = repo.findById(PlanCategory.class, this.category.getId());
                if (category == null) {
                    return "Unknown plan category id.";
                }
            } else {
                this.category = null;
            }
        }

        if (this.geography != null && !this.geography.options.isEmpty()) {
            String geographyValidationError = this.geography.validate();
            if (geographyValidationError != null) {
                return geographyValidationError;
            }
        } else {
            this.geography = null;
        }

        if (this.conditions != null && !this.conditions.isEmpty()) {
            if (this.conditions.size() > 1) {
                return "Only one condition is currently allowed for a goal.";
            }
            Map<ConditionFieldType, Integer> fieldTypeValidationMap = new HashMap<>();
            Set<UUID> propertyIds = new HashSet<>();
            Arrays.stream(ConditionFieldType.values()).forEach(cft -> fieldTypeValidationMap.put(cft, 0));
            this.conditions.forEach(c -> {
                fieldTypeValidationMap.put(c.getConditionFieldType(), fieldTypeValidationMap.get(c.getConditionFieldType()) + 1);
                if (c.getConditionFieldType() == ConditionFieldType.PROPERTY) {
                    propertyIds.add(c.getPropertyId());
                }
            });
            for (ConditionFieldType cft : ConditionFieldType.values()) {
                if (cft != ConditionFieldType.PROPERTY && fieldTypeValidationMap.get(cft) > 1) {
                    return "Only one " + cft.name() + " condition is allowed.";
                }
                if (cft == ConditionFieldType.PROPERTY && fieldTypeValidationMap.get(cft) != propertyIds.size()) {
                    return "Only one condition is allowed for a property.";
                }
            }
            for (PlanConditionModel condition : this.conditions) {
                String validationError = condition.validate(repo);
                if (validationError != null) {
                    return validationError;
                }
            }
        } else {
            this.conditions = null;
        }
        return null;
    }


    private boolean isIntegerValue(BigDecimal bd) {
        return bd != null && bd.remainder(BigDecimal.ONE).compareTo(BigDecimal.ZERO) == 0;
    }

    public enum ConditionFieldType {
        PROPERTY,
        GROUND_POSITION,
        PROGRAMMING,
        HOUSE_TYPE,
        OWNERSHIP
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class PlanConditionModel {

        private UUID conditionId;
        private ConditionFieldType conditionFieldType;

        private UUID propertyId;
        private String propertyName;
        private PropertyKind propertyKind;
        private PropertyType propertyType;

        private Boolean booleanValue;
        private List<SelectModel> categoryOptions;
        private SingleValueOrRangeModel<SelectModel> ordinalOptions;
        private List<String> listOptions;
        private List<PlanOwnershipModel> ownershipOptions;

        @JsonIgnore
        private PropertyModel propertyModel;

        public boolean isPlanConditionDataEqual(VngRepository repo, PlanConditionModel that) {
            if (!Objects.equals(this.conditionId, that.conditionId) || !Objects.equals(this.conditionFieldType, that.conditionFieldType)) {
                return false;
            }

            if (this.conditionFieldType == ConditionFieldType.PROGRAMMING) {
                return Objects.equals(this.booleanValue, that.booleanValue);
            } else if (this.conditionFieldType == ConditionFieldType.HOUSE_TYPE || this.conditionFieldType == ConditionFieldType.GROUND_POSITION) {
                return this.listOptions.size() == that.listOptions.size() && this.listOptions.containsAll(that.listOptions);
            } else if (this.conditionFieldType == ConditionFieldType.OWNERSHIP) {
                return this.ownershipOptions.size() == that.ownershipOptions.size() && this.ownershipOptions.containsAll(that.ownershipOptions);
            } else if (this.conditionFieldType == ConditionFieldType.PROPERTY) {
                if (!Objects.equals(this.propertyId, that.propertyId)) {
                    return false;
                }
                PropertyModel propertyModel = repo.getPropertyDAO().getPropertyById(propertyId);
                if (propertyModel.getPropertyType() == PropertyType.BOOLEAN) {
                    return Objects.equals(this.booleanValue, that.booleanValue);
                } else if (propertyModel.getPropertyType() == PropertyType.CATEGORY) {
                    return this.categoryOptions.size() == that.categoryOptions.size() && this.categoryOptions.containsAll(that.categoryOptions);
                } else if (propertyModel.getPropertyType() == PropertyType.ORDINAL) {
                    return Objects.equals(this.ordinalOptions, that.ordinalOptions);
                }
            }

            return false;
        }

        public String validate(VngRepository repo) {
            if (conditionFieldType == null) {
                return "Condition field type can not be null.";
            } else if (conditionFieldType == ConditionFieldType.PROGRAMMING && booleanValue == null) {
                return "Boolean value can not be null for a programming condition.";
            } else if (conditionFieldType == ConditionFieldType.HOUSE_TYPE) {
                if (listOptions == null || listOptions.isEmpty()) {
                    return "List options can not be null for a house type condition.";
                }
                List<String> allHouseTypes = Arrays.stream(HouseType.values()).map(Enum::name).toList();
                if (!allHouseTypes.containsAll(listOptions)) {
                    return "List options contain values that are not recognized house types.";
                }
            } else if (conditionFieldType == ConditionFieldType.GROUND_POSITION) {
                if (listOptions == null || listOptions.isEmpty()) {
                    return "List options can not be null for a house type condition.";
                }
                List<String> allHouseTypes = Arrays.stream(GroundPosition.values()).map(Enum::name).toList();
                if (!allHouseTypes.containsAll(listOptions)) {
                    return "List options contain values that are not recognized ground positions.";
                }
            } else if (conditionFieldType == ConditionFieldType.OWNERSHIP) {
                if (ownershipOptions == null || ownershipOptions.isEmpty()) {
                    return "Ownership options can not be null for an ownership condition.";
                }
                PropertyModel priceRangeBuyProperty = repo.getPropertyDAO().getActivePropertyByName(Constants.FIXED_PROPERTY_PRICE_RANGE_BUY);
                PropertyModel priceRangeRentProperty = repo.getPropertyDAO().getActivePropertyByName(Constants.FIXED_PROPERTY_PRICE_RANGE_RENT);

                List<UUID> priceRangeBuyOptions = priceRangeBuyProperty.getRanges().stream().filter(r -> r.getDisabled() == Boolean.FALSE).map(SelectModel::getId).toList();
                List<UUID> priceRangeRentOptions = priceRangeRentProperty.getRanges().stream().filter(r -> r.getDisabled() == Boolean.FALSE).map(SelectModel::getId).toList();

                for (PlanOwnershipModel ownershipModel : ownershipOptions) {
                    String validationError = ownershipModel.validate(priceRangeBuyOptions, priceRangeRentOptions);
                    if (validationError != null) {
                        return validationError;
                    }
                }

            } else if (conditionFieldType == ConditionFieldType.PROPERTY) {
                if (propertyId == null) {
                    return "Property id can not be null for a property-type condition.";
                }
                propertyModel = repo.getPropertyDAO().getPropertyById(propertyId);
                if (propertyModel == null || propertyModel.getDisabled() == Boolean.TRUE) {
                    return "Unknown property id.";
                }
                propertyName = propertyModel.getName();
                propertyKind = propertyModel.getType();
                propertyType = propertyModel.getPropertyType();
                if (propertyType != PropertyType.BOOLEAN && propertyType != PropertyType.CATEGORY) {
                    return "Unsupported property type.";
                }
                if (propertyType == PropertyType.BOOLEAN && booleanValue == null) {
                    return "Boolean value can not be null for a boolean property condition.";
                }
                if (propertyType == PropertyType.CATEGORY) {
                    if (categoryOptions == null || categoryOptions.isEmpty()) {
                        return "Category options can not be null for a category property condition.";
                    }
                    List<UUID> knownOptions = propertyModel.getCategories().stream()
                        .filter(c -> c.getDisabled() == Boolean.FALSE).map(SelectDisabledModel::getId).toList();
                    List<UUID> givenOptions = categoryOptions.stream().map(SelectModel::getId).toList();
                    if (!knownOptions.containsAll(givenOptions)) {
                        return "Category options contain values that are not recognized property options";
                    }
                }
                if (propertyType == PropertyType.ORDINAL) {
                    if (ordinalOptions == null || !ordinalOptions.isValid(false)) {
                        return "Ordinal options can not be null or invalid for an ordinal property condition.";
                    }
                    List<UUID> knownOptions = propertyModel.getOrdinals().stream()
                        .filter(c -> c.getDisabled() == Boolean.FALSE).map(SelectDisabledModel::getId).toList();
                    List<UUID> givenOptions = new ArrayList<>();
                    if (ordinalOptions.getValue() != null) {
                        givenOptions.add(ordinalOptions.getValue().getId());
                    }
                    if (ordinalOptions.getMin() != null) {
                        givenOptions.add(ordinalOptions.getMin().getId());
                    }
                    if (ordinalOptions.getMax() != null) {
                        givenOptions.add(ordinalOptions.getMax().getId());
                    }
                    if (!knownOptions.containsAll(givenOptions)) {
                        return "Ordinal options contain values that are not recognized property options";
                    }
                }
            }

            return null;
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @EqualsAndHashCode
    public static class PlanOwnershipModel {

        private OwnershipType type;
        private SingleValueOrRangeModel<Integer> value;
        private SelectModel rangeCategoryOption;

        public PlanOwnershipModel(PlanSqlModel.PlanSqlOwnershipOptionModel sqlOption) {
            this.type = sqlOption.getOwnershipType();
            if (this.type == OwnershipType.KOOPWONING) {
                if (sqlOption.getValue() != null || sqlOption.getValueRangeMin() != null) {
                    this.value = new SingleValueOrRangeModel<>(sqlOption.getValue(), sqlOption.getValueRangeMin(), sqlOption.getValueRangeMax());
                }
                if (sqlOption.getRangeCategoryId() != null) {
                    rangeCategoryOption = new SelectModel(sqlOption.getRangeCategoryId(), sqlOption.getRangeCategoryName());
                }
            } else {
                if (sqlOption.getRentValue() != null || sqlOption.getRentValueRangeMin() != null) {
                    this.value = new SingleValueOrRangeModel<>(sqlOption.getRentValue(), sqlOption.getRentValueRangeMin(), sqlOption.getRentValueRangeMax());
                }
                if (sqlOption.getRentRangeCategoryId() != null) {
                    rangeCategoryOption = new SelectModel(sqlOption.getRentRangeCategoryId(), sqlOption.getRentRangeCategoryName());
                }
            }
        }

        public String validate(List<UUID> priceOwnRangeOptions, List<UUID> priceRentRangeOptions) {

            if (rangeCategoryOption == null || rangeCategoryOption.getId() == null) {
                return "Range category option cannot be null"; //we only support range category option for now!!
            }

            if (value != null && value.getValue() == null && value.getMin() == null && value.getMax() == null) {
                this.value = null;
            }

            if (type == null) {
                return "Ownership type cannot be null.";
            } else if (rangeCategoryOption != null && value != null) {
                return "Only one type of ownership value can be entered.";
            } else if (value != null && !value.isValid(true)) {
                return "Invalid numeric value or numeric range.";
            } else if (rangeCategoryOption != null) {
                if (type == OwnershipType.KOOPWONING && !priceOwnRangeOptions.contains(rangeCategoryOption.getId())) {
                    return "Unknown range category option.";
                }
                if (type != OwnershipType.KOOPWONING && !priceRentRangeOptions.contains(rangeCategoryOption.getId())) {
                    return "Unknown range category option.";
                }
            }

            return null;
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class PlanGeographyModel {

        private UUID conditionId;
        private List<GeographyOptionModel> options;

        public String validate() {
            for (GeographyOptionModel option : options) {
                String validationError = option.validate();
                if (validationError != null) {
                    return validationError;
                }
            }
            return null;
        }

        public boolean isGeographyDataEqual(PlanGeographyModel that) {
            return Objects.equals(this.conditionId, that.conditionId) &&
                ((this.options == null && that.options == null) ||
                    (this.options != null && that.options != null && this.options.size() == that.options.size() && this.options.containsAll(that.options)));
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @EqualsAndHashCode
    public static class GeographyOptionModel {

        private String brkGemeenteCode;
        private String brkSectie;
        private Long brkPerceelNummer;

        public String validate() {
            if (brkGemeenteCode == null) {
                return "Missing brkGemeenteCode";
            } else if (brkSectie == null && brkPerceelNummer != null) {
                return "Missing brkSectie when a brkPerceelNummer value was entered.";
            } else {
                return null;
            }
        }
    }

}
