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
import nl.vng.diwi.models.AmountModel;
import org.hibernate.annotations.JdbcType;
import org.hibernate.annotations.Type;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;

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
public class HouseblockExportSqlModel {

    private UUID projectId;

    @Id
    private UUID houseblockId;

    private LocalDate startDate;
    private LocalDate endDate;


    private Integer mutationAmount;

    @Enumerated(EnumType.STRING)
    @JdbcType(PostgreSQLEnumJdbcType.class)
    private MutationType mutationKind;

    @Type(value = JsonListType.class)
    @Getter(AccessLevel.NONE)
    private List<OwnershipValueSqlModel> ownershipValueList;

    private Integer noPermissionOwner;
    private Integer intentionPermissionOwner;
    private Integer formalPermissionOwner;

    @Type(value = JsonListType.class)
    @Getter(AccessLevel.NONE)
    private List<AmountModel> physicalAppearanceList;

    private Integer meergezinswoning;
    private Integer eengezinswoning;

    @Type(value = JsonListType.class)
    @Getter(AccessLevel.NONE)
    private List<AmountModel> targetGroupList;

    public List<AmountModel> getPhysicalAppearanceList() {
        if (physicalAppearanceList == null) {
            return new ArrayList<>();
        }
        return physicalAppearanceList;
    }

    public List<AmountModel> getTargetGroupList() {
        if (targetGroupList == null) {
            return new ArrayList<>();
        }
        return targetGroupList;
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
