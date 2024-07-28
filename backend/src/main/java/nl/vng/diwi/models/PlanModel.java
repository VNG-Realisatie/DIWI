package nl.vng.diwi.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nl.vng.diwi.dal.entities.enums.GoalDirection;
import nl.vng.diwi.dal.entities.enums.GoalType;
import nl.vng.diwi.dal.entities.enums.PropertyKind;
import nl.vng.diwi.dal.entities.enums.PropertyType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class PlanModel {

    @Id
    private UUID id;

    @JsonProperty(required = true)
    private String name;

    @JsonProperty(required = true)
    private LocalDate startDate;

    @JsonProperty(required = true)
    private LocalDate endDate;

    @JsonProperty(required = true)
    private GoalType goalType;

    @JsonProperty(required = true)
    private GoalDirection goalDirection;

    @JsonProperty(required = true)
    private BigDecimal goalValue;

    @JsonProperty(required = true)
    private SelectModel category;

    @JsonProperty(required = false)
    private List<PlanConditionModel> conditions;

    //TODO: geography

    public String validate() {
        //TODO

        return null;

    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class PlanConditionModel {

        private UUID conditionId;
        private ConditionFieldType conditionFieldType;

        private UUID propertyId;
        private String propertyName;
        private PropertyKind type;
        private PropertyType propertyType;

        private Boolean booleanValue;

        private List<SelectModel> categoryOptions;
        private List<String> listOptions;

        //TODO ownership

    }

    public enum ConditionFieldType {
        PROPERTY,
        GROUND_POSITION,
        PROGRAMMING,
        HOUSE_TYPE,
        OWNERSHIP
    }

}
