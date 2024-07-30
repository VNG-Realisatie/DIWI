package nl.vng.diwi.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nl.vng.diwi.dal.entities.enums.GoalDirection;
import nl.vng.diwi.dal.entities.enums.GoalType;
import nl.vng.diwi.dal.entities.enums.OwnershipType;
import nl.vng.diwi.dal.entities.enums.PropertyKind;
import nl.vng.diwi.dal.entities.enums.PropertyType;
import nl.vng.diwi.models.superclasses.DatedDataModelSuperClass;

import java.math.BigDecimal;
import java.util.ArrayList;
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

    public String validate() {
        //TODO

        return null;

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
        private List<String> listOptions;
        private PlanOwnershipModel ownershipOption;

    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class PlanOwnershipModel {

        private OwnershipType type;
        private SingleValueOrRangeModel<Integer> value = new SingleValueOrRangeModel<>();
        private SelectModel rangeCategoryOption;

    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class PlanGeographyModel {

        private UUID conditionId;
        private List<GeographyOptionModel> options;

    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class GeographyOptionModel {

        private String brkGemeenteCode;
        private String brkSectie;
        private Long brkPerceelNummer;

    }

}
