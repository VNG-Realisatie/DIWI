package nl.vng.diwi.dal.entities;

import io.hypersistence.utils.hibernate.type.range.PostgreSQLRangeType;
import io.hypersistence.utils.hibernate.type.range.Range;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nl.vng.diwi.dal.JsonListType;
import nl.vng.diwi.dal.entities.enums.MutationType;
import nl.vng.diwi.dal.entities.enums.OwnershipType;
import nl.vng.diwi.dal.entities.enums.ValueType;
import org.hibernate.annotations.JdbcType;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.Type;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;
import org.hibernate.type.SqlTypes;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Convert(
    attributeName = "numrange",
    converter = PostgreSQLRangeType.class
)
@Convert(
    attributeName = "int4range",
    converter = PostgreSQLRangeType.class
)
public class HouseblockSnapshotSqlModel {

    private UUID projectId;

    @Id
    private UUID houseblockId;
    private String houseblockName;

    private LocalDate startDate;
    private LocalDate endDate;

    private BigDecimal sizeValue;

    @Column(columnDefinition = "numrange")
    private Range<BigDecimal> sizeValueRange;

    @Enumerated(EnumType.STRING)
    @JdbcType(PostgreSQLEnumJdbcType.class)
    private ValueType sizeValueType;

    private Boolean programming;

    private Integer grossPlanCapacity;
    private Integer netPlanCapacity;
    private Integer demolition;

    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(columnDefinition = "text[]")
    @Getter(AccessLevel.NONE)
    private List<MutationType> mutationKind;

    @Type(value = JsonListType.class)
    @Getter(AccessLevel.NONE)
    private List<OwnershipValueSqlModel> ownershipValueList;

    private Integer noPermissionOwner;
    private Integer intentionPermissionOwner;
    private Integer formalPermissionOwner;

    private Integer tussenwoning;
    private Integer tweeondereenkap;
    private Integer portiekflat;
    private Integer hoekwoning;
    private Integer vrijstaand;
    private Integer gallerijflat;

    private Integer meergezinswoning;
    private Integer eengezinswoning;

    private Integer regular;
    private Integer youth;
    private Integer student;
    private Integer elderly;
    private Integer GHZ;
    private Integer largeFamilies;

    public List<MutationType> getMutationKind() {
        if (mutationKind == null) {
            return new ArrayList<>();
        }
        return mutationKind;
    }

    public List<OwnershipValueSqlModel> getOwnershipValueList() {
        if (ownershipValueList == null) {
            return new ArrayList<>();
        }
        return ownershipValueList;
    }

    @Getter
    @Setter
    public static class OwnershipValueSqlModel implements Serializable {
        private static final long serialVersionUID = 1L;
        private UUID ownershipId;
        private OwnershipType ownershipType;
        private Integer ownershipAmount;
        private Integer ownershipValue;
        private Integer ownershipRentalValue;
        private Integer ownershipValueRangeMin;
        private Integer ownershipValueRangeMax;
        private Integer ownershipRentalValueRangeMin;
        private Integer ownershipRentalValueRangeMax;
    }

}
