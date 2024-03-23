package nl.vng.diwi.dal.entities;

import io.hypersistence.utils.hibernate.type.range.PostgreSQLRangeType;
import io.hypersistence.utils.hibernate.type.range.Range;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nl.vng.diwi.dal.GenericRepository;
import nl.vng.diwi.dal.entities.enums.OwnershipType;
import nl.vng.diwi.dal.entities.enums.ValueType;
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

    @Column(name = "waarde_value_type")
    @Enumerated(EnumType.STRING)
    @JdbcType(PostgreSQLEnumJdbcType.class)
    private ValueType valueType;

    @Column(name = "waarde_value")
    private Integer value;

    @Type(PostgreSQLRangeType.class)
    @Column(name = "waarde_value_range", columnDefinition = "int4range")
    private Range<Integer> valueRange;

    @Column(name = "huurbedrag_value_type")
    @Enumerated(EnumType.STRING)
    @JdbcType(PostgreSQLEnumJdbcType.class)
    private ValueType rentalValueType;

    @Column(name = "huurbedrag_value")
    private Integer rentalValue;

    @Type(PostgreSQLRangeType.class)
    @Column(name = "huurbedrag_value_range", columnDefinition = "int4range")
    private Range<Integer> rentalValueRange;

    private Integer amount;

    @Column(name = "eigendom_soort")
    @Enumerated(EnumType.STRING)
    @JdbcType(PostgreSQLEnumJdbcType.class)
    private OwnershipType ownershipType;

    @Override
    public Object getShallowCopy() {
        var newChangelog = HouseblockOwnershipValueChangelog.builder()
            .valueType(valueType).value(value).valueRange(valueRange)
            .rentalValueType(rentalValueType).rentalValue(rentalValue).rentalValueRange(rentalValueRange)
            .amount(amount).ownershipType(ownershipType).build();
        newChangelog.setHouseblock(getHouseblock());
        newChangelog.setStartMilestone(getStartMilestone());
        newChangelog.setEndMilestone(getEndMilestone());
        return newChangelog;
    }
}
