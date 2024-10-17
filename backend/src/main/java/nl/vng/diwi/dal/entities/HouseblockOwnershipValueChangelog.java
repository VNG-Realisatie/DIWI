package nl.vng.diwi.dal.entities;

import io.hypersistence.utils.hibernate.type.range.PostgreSQLRangeType;
import io.hypersistence.utils.hibernate.type.range.Range;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nl.vng.diwi.dal.GenericRepository;
import nl.vng.diwi.dal.entities.enums.OwnershipType;
import nl.vng.diwi.dal.entities.superclasses.HouseblockMilestoneChangeDataSuperclass;
import org.hibernate.annotations.JdbcType;
import org.hibernate.annotations.Type;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;

@Entity
@Table(name = "woningblok_eigendom_en_waarde_changelog", schema = GenericRepository.VNG_SCHEMA_NAME)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HouseblockOwnershipValueChangelog extends HouseblockMilestoneChangeDataSuperclass {

    @Column(name = "waarde_value")
    private Long value;

    @Type(PostgreSQLRangeType.class)
    @Column(name = "waarde_value_range", columnDefinition = "int8range")
    private Range<Long> valueRange;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ownership_property_value_id")
    private PropertyRangeCategoryValue ownershipRangeCategoryValue;

    @Column(name = "huurbedrag_value")
    private Long rentalValue;

    @Type(PostgreSQLRangeType.class)
    @Column(name = "huurbedrag_value_range", columnDefinition = "int8range")
    private Range<Long> rentalValueRange;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "rental_property_value_id")
    private PropertyRangeCategoryValue rentalRangeCategoryValue;

    private Integer amount;

    @Column(name = "eigendom_soort")
    @Enumerated(EnumType.STRING)
    @JdbcType(PostgreSQLEnumJdbcType.class)
    private OwnershipType ownershipType;

    @Override
    public Object getShallowCopy() {
        var newChangelog = HouseblockOwnershipValueChangelog.builder()
            .value(value).valueRange(valueRange).ownershipRangeCategoryValue(ownershipRangeCategoryValue)
            .rentalValue(rentalValue).rentalValueRange(rentalValueRange).rentalRangeCategoryValue(rentalRangeCategoryValue)
            .amount(amount).ownershipType(ownershipType).build();
        newChangelog.setHouseblock(getHouseblock());
        newChangelog.setStartMilestone(getStartMilestone());
        newChangelog.setEndMilestone(getEndMilestone());
        return newChangelog;
    }
}
