package nl.vng.diwi.dal.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nl.vng.diwi.dal.GenericRepository;
import nl.vng.diwi.dal.entities.enums.ValueType;
import nl.vng.diwi.dal.entities.superclasses.MilestoneChangeDataSuperclass;
import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;

@Entity
@Table(name = "project_maatwerk_ordinaal_changelog", schema = GenericRepository.VNG_SCHEMA_NAME)
@Getter
@Setter
@NoArgsConstructor
public class ProjectOrdinalCustomPropertyChangelog extends MilestoneChangeDataSuperclass {

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "project_id")
    private Project project;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "eigenschap_id")
    private CustomProperty customProperty;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "value_id")
    private CustomOrdinalValue value;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "min_value_id")
    private CustomOrdinalValue minValue;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "max_value_id")
    private CustomOrdinalValue maxValue;

    @Column(name = "value_type")
    @Enumerated(EnumType.STRING)
    @JdbcType(PostgreSQLEnumJdbcType.class)
    private ValueType valueType;

}
