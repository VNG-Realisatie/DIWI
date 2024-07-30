package nl.vng.diwi.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Id;
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
import nl.vng.diwi.models.superclasses.DatedDataModelSuperClass;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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

        if (sqlModel.getOwnershipCondition() != null) {
            PlanConditionModel condition = new PlanConditionModel();
            condition.setConditionFieldType(ConditionFieldType.OWNERSHIP);

            var oc = sqlModel.getOwnershipCondition();
            condition.setConditionId(oc.getOwnershipConditionId());
            PlanOwnershipModel ownership = new PlanOwnershipModel();
            ownership.setType(oc.getOwnershipType());
            if (oc.getOwnershipValue() != null || oc.getOwnershipValueRangeMin() != null) {
                ownership.setValue(new SingleValueOrRangeModel<>(oc.getOwnershipValue(), oc.getOwnershipValueRangeMin(), oc.getOwnershipValueRangeMax()));
            }
            if (oc.getOwnershipRangeCategoryId() != null) {
                ownership.setRangeCategoryOption(new SelectModel(oc.getOwnershipConditionId(), oc.getOwnershipRangeCategoryName()));
            }
            condition.setOwnershipOption(ownership);
            this.conditions.add(condition);
        }
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

        if (this.goalType == GoalType.NUMBER && ( this.goalDirection != GoalDirection.MAXIMAL || !isIntegerValue(this.goalValue))) {
            return "Only Maximal goal direction and Integer goal values are allowed when selecting Number goal type.";
        }
        if (this.goalType == GoalType.PERCENTAGE && this.goalValue.compareTo(BigDecimal.valueOf(100)) > 0) {
            return "Goal value must be between 0 and 100 when selecting Percentage goal type.";
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
            //TODO: overall validations only one of each for programming / house type / ownership / ground position
            //TODO: for properties, only one use of a property allowed
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
        private PlanOwnershipModel ownershipOption;

        @JsonIgnore
        private PropertyModel propertyModel;

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
//TODO

//                propertyModel = repo.getPropertyDAO().getPropertyById(propertyId);
//                if (propertyModel == null || propertyModel.getDisabled() == Boolean.TRUE ||
//                    propertyModel.getPropertyType() != PropertyType.RANGE_CATEGORY && propertyModel.getType() != PropertyKind.FIXED) {
//                    return "Property id does not match a known range-category property used for ownership.";
//                }
//                String ownershipError = ownershipOption.validate(propertyModel);
//                if (ownershipError != null) {
//                    return ownershipError;
//                }
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
                if (propertyType != PropertyType.BOOLEAN && propertyType != PropertyType.CATEGORY && propertyType != PropertyType.ORDINAL) {
                    return "Unsupported property type.";
                }
                if (propertyType == PropertyType.BOOLEAN && booleanValue == null) {
                    return "Boolean value can not be null for a boolean property condition.";
                }
                if (propertyType == PropertyType.CATEGORY) {
                    if (categoryOptions == null || categoryOptions.isEmpty()){
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
                    if (ordinalOptions == null || !ordinalOptions.isValid(false)){
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
    public static class PlanOwnershipModel {

        private OwnershipType type;
        private SingleValueOrRangeModel<Integer> value = new SingleValueOrRangeModel<>();
        private SelectModel rangeCategoryOption;

        public String validate(PropertyModel propertyModel) {
            //TODO
//            if (rangeCategoryOption != null && rangeCategoryOption.getId() == null) {
//                rangeCategoryOption = null;
//            }
//            if (type == null) {
//                return "Ownership type cannot be null.";
//            } else if (type == OwnershipType.KOOPWONING && ) {
//
//            } else if (rangeCategoryOption != null && value != null) {
//                return "Only one type of value can be entered.";
//            } else if (value != null && !value.isValid(true)) {
//                return "Invalid numeric value or numeric range.";
//            } else if (rangeCategoryOption != null) {
//                SelectModel knownRangeOption = propertyModel.getRanges().stream()
//                    .filter(r -> r.getDisabled() == Boolean.FALSE && r.getId().equals(rangeCategoryOption.getId())).findFirst().orElse(null);
//                if (knownRangeOption == null) {
//                    return "Unknown ownership range category option.";
//                }
//            }

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
    }

    @Getter
    @Setter
    @NoArgsConstructor
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
