package nl.vng.diwi.dal.entities;

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
import nl.vng.diwi.dal.entities.enums.ValueType;
import nl.vng.diwi.dal.entities.superclasses.HouseblockMilestoneChangeDataSuperclass;

import org.hibernate.Session;
import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;

@Entity
@Table(name = "woningblok_maatwerk_ordinaal_changelog", schema = GenericRepository.VNG_SCHEMA_NAME)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HouseblockOrdinalCustomPropertyChangelog extends HouseblockMilestoneChangeDataSuperclass {

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "eigenschap_id")
    private Property property;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "value_id")
    private PropertyOrdinalValue value;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "min_value_id")
    private PropertyOrdinalValue minValue;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "max_value_id")
    private PropertyOrdinalValue maxValue;

    @Column(name = "value_type")
    @Enumerated(EnumType.STRING)
    @JdbcType(PostgreSQLEnumJdbcType.class)
    private ValueType valueType;

    @Override
    public Object getCopyWithoutMilestones(Session session) {
        var newChangelog = HouseblockOrdinalCustomPropertyChangelog.builder()
            .property(property).value(value).minValue(minValue).maxValue(maxValue).valueType(valueType).build();
        newChangelog.setHouseblock(getHouseblock());
        newChangelog.setStartMilestone(getStartMilestone());
        newChangelog.setEndMilestone(getEndMilestone());
        return newChangelog;
    }
}
