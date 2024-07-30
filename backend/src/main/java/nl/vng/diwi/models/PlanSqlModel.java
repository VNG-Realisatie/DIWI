package nl.vng.diwi.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nl.vng.diwi.dal.JsonListType;
import nl.vng.diwi.dal.entities.enums.GoalDirection;
import nl.vng.diwi.dal.entities.enums.GoalType;
import nl.vng.diwi.dal.entities.enums.GroundPosition;
import nl.vng.diwi.dal.entities.enums.HouseType;
import nl.vng.diwi.dal.entities.enums.OwnershipType;
import org.hibernate.annotations.JdbcType;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.Type;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class PlanSqlModel {

    @Id
    private UUID id;

    private String name;

    private LocalDate startDate;

    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    @JdbcType(PostgreSQLEnumJdbcType.class)
    private GoalType goalType;

    @Enumerated(EnumType.STRING)
    @JdbcType(PostgreSQLEnumJdbcType.class)
    private GoalDirection goalDirection;

    private BigDecimal goalValue;

    @JdbcTypeCode(SqlTypes.JSON)
    private SelectModel planCategory;

    private UUID geographyConditionId;
    @Type(value = JsonListType.class)
    @Getter(AccessLevel.NONE)
    private List<PlanModel.GeographyOptionModel> geographyOptions;

    private UUID programmingConditionId;
    private Boolean programmingValue;

    private UUID groundPositionConditionId;
    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(columnDefinition = "text[]")
    @Getter(AccessLevel.NONE)
    private List<GroundPosition> groundPositionOptions;

    private UUID houseTypeConditionId;
    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(columnDefinition = "text[]")
    @Getter(AccessLevel.NONE)
    private List<HouseType> houseTypeOptions;

    @JdbcTypeCode(SqlTypes.JSON)
    private PlanSqlOwnershipConditionModel ownershipCondition;

    public List<PlanModel.GeographyOptionModel> getGeographyOptions() {
        if (geographyOptions == null) {
            return new ArrayList<>();
        }
        return geographyOptions;
    }

    public List<GroundPosition> getGroundPositionOptions() {
        if (groundPositionOptions == null) {
            return new ArrayList<>();
        }
        return groundPositionOptions;
    }

    public List<HouseType> getHouseTypeOptions() {
        if (houseTypeOptions == null) {
            return new ArrayList<>();
        }
        return houseTypeOptions;
    }

    @Getter
    @Setter
    public static class PlanSqlOwnershipConditionModel {
        private UUID ownershipConditionId;
        private OwnershipType ownershipType;
        private Integer ownershipValue;
        private UUID ownershipRangeCategoryId;
        private String ownershipRangeCategoryName;
        private Integer ownershipValueRangeMin;
        private Integer ownershipValueRangeMax;
    }
}
