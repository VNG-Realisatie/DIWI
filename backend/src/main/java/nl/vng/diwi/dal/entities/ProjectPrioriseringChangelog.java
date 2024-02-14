package nl.vng.diwi.dal.entities;

import nl.vng.diwi.dal.entities.enums.ValueType;
import nl.vng.diwi.dal.entities.superclasses.MilestoneChangeDataSuperclass;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import nl.vng.diwi.dal.GenericRepository;
import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;

@Entity
@Table(name = "project_priorisering_changelog", schema = GenericRepository.VNG_SCHEMA_NAME)
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class ProjectPrioriseringChangelog extends MilestoneChangeDataSuperclass {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;

    @Column(name = "value_type")
    @Enumerated(EnumType.STRING)
    @JdbcType(PostgreSQLEnumJdbcType.class)
    private ValueType valueType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_priorisering_value_id")
    private ProjectPrioriseringValue value;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_priorisering_min_value_id")
    private ProjectPrioriseringValue minValue;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_priorisering_max_value_id")
    private ProjectPrioriseringValue maxValue;
}
