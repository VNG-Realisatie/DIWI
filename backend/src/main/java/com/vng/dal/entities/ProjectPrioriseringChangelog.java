package com.vng.dal.entities;

import com.vng.dal.entities.enums.ValueType;
import com.vng.dal.entities.superclasses.MilestoneChangeDataSuperclass;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;

import static com.vng.dal.GenericRepository.VNG_SCHEMA_NAME;

@Entity
@Table(name = "project_priorisering_changelog", schema = VNG_SCHEMA_NAME)
@Data
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
