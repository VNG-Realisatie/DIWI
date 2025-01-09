package nl.vng.diwi.dal.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nl.vng.diwi.dal.JsonListType;
import nl.vng.diwi.dal.entities.enums.Confidentiality;
import nl.vng.diwi.dal.entities.enums.MutationType;
import nl.vng.diwi.dal.entities.enums.OwnershipType;
import nl.vng.diwi.dal.entities.enums.PlanStatus;
import nl.vng.diwi.dal.entities.enums.PlanType;
import nl.vng.diwi.dal.entities.enums.ProjectPhase;
import nl.vng.diwi.dal.entities.enums.ProjectStatus;
import nl.vng.diwi.models.SingleValueOrRangeModel;
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
@NoArgsConstructor
// @AllArgsConstructor
@Getter
@Setter
public class ProjectExportSqlModelPlus {

    @Id
    private UUID projectId;

    private String name;

    @Enumerated(EnumType.STRING)
    @JdbcType(PostgreSQLEnumJdbcType.class)
    private Confidentiality confidentiality;

    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(columnDefinition = "text[]")
    @Getter(AccessLevel.NONE)
    private List<PlanType> planType;

    private LocalDate startDate;

    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    @JdbcType(PostgreSQLEnumJdbcType.class)
    private ProjectPhase projectPhase;

    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(columnDefinition = "text[]")
    @Getter(AccessLevel.NONE)
    private List<PlanStatus> planningPlanStatus;

    private LocalDate realizationPhaseDate;

    private LocalDate planStatusPhase1Date;

    @Type(value = JsonListType.class)
    @Getter(AccessLevel.NONE)
    private List<TextPropertyModel> textProperties;

    @Type(value = JsonListType.class)
    @Getter(AccessLevel.NONE)
    private List<NumericPropertyModel> numericProperties;

    @Type(value = JsonListType.class)
    @Getter(AccessLevel.NONE)
    private List<BooleanPropertyModel> booleanProperties;

    @Type(value = JsonListType.class)
    @Getter(AccessLevel.NONE)
    private List<CategoryPropertyModel> categoryProperties;

    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(columnDefinition = "text[]")
    @Getter(AccessLevel.NONE)
    private List<String> geometries;

    @Type(value = JsonListType.class)
    @Getter(AccessLevel.NONE)
    private List<HouseblockExportSqlModel> houseblocks;

    @Enumerated(EnumType.STRING)
    @JdbcType(PostgreSQLEnumJdbcType.class)
    private ProjectStatus status;

    public List<PlanType> getPlanType() {
        return planType == null ? new ArrayList<>() : planType;
    }

    public List<PlanStatus> getPlanningPlanStatus() {
        return planningPlanStatus == null ? new ArrayList<>() : planningPlanStatus;
    }

    public List<TextPropertyModel> getTextProperties() {
        return textProperties == null ? new ArrayList<>() : textProperties;
    }

    public List<NumericPropertyModel> getNumericProperties() {
        return numericProperties == null ? new ArrayList<>() : numericProperties;
    }

    public List<BooleanPropertyModel> getBooleanProperties() {
        return booleanProperties == null ? new ArrayList<>() : booleanProperties;
    }

    public List<CategoryPropertyModel> getCategoryProperties() {
        return categoryProperties == null ? new ArrayList<>() : categoryProperties;
    }

    public List<String> getGeometries() {
        return geometries == null ? new ArrayList<>() : geometries;
    }

    public List<HouseblockExportSqlModel> getHouseblocks() {
        return houseblocks == null ? new ArrayList<>() : houseblocks;
    }

    @Data
    @NoArgsConstructor
    public static class TextPropertyModel {
        private UUID propertyId;
        private String textValue;
    }

    @Data
    @NoArgsConstructor
    public static class NumericPropertyModel {
        private UUID propertyId;
        private BigDecimal value;
        private BigDecimal min;
        private BigDecimal max;

        public SingleValueOrRangeModel<BigDecimal> getSingleValueOrRangeModel() {
            return new SingleValueOrRangeModel<>(value, min, max);
        }
    }

    @Data
    @NoArgsConstructor
    public static class BooleanPropertyModel {
        private UUID propertyId;
        private Boolean booleanValue;
    }

    @Data
    @NoArgsConstructor
    public static class CategoryPropertyModel {
        private UUID propertyId;
        private List<UUID> optionValues;
    }

    @Data
    @NoArgsConstructor
    public static class HouseblockExportSqlModel {

        private UUID houseblockId;
        private String name;
        private LocalDate endDate;
        private Integer deliveryYear;
        private Integer mutationAmount;

        @Enumerated(EnumType.STRING)
        @JdbcType(PostgreSQLEnumJdbcType.class)
        private MutationType mutationKind;

        @Type(value = JsonListType.class)
        @Getter(AccessLevel.NONE)
        private List<OwnershipValueSqlModel> ownershipValueList;

        private Integer meergezinswoning;
        private Integer eengezinswoning;


        public List<OwnershipValueSqlModel> getOwnershipValueList() {
            return ownershipValueList == null ? new ArrayList<>() : ownershipValueList;
        }

    }

    @Data
    @NoArgsConstructor
    public static class OwnershipValueSqlModel {
        private UUID ownershipId;
        private OwnershipType ownershipType;
        private Integer ownershipAmount;
        private Long ownershipValue;
        private Long ownershipRentalValue;
        private UUID ownershipRangeCategoryId;
        private UUID ownershipRentalRangeCategoryId;
        private Long ownershipValueRangeMin;
        private Long ownershipValueRangeMax;
        private Long ownershipRentalValueRangeMin;
        private Long ownershipRentalValueRangeMax;
    }
}
