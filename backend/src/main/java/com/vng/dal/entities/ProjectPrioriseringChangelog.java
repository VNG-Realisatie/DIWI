package com.vng.dal.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.vng.dal.entities.enums.ValueType;
import com.vng.dal.entities.superclasses.MilestoneChangeDataSuperclass;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import static com.vng.dal.GenericRepository.VNG_SCHEMA_NAME;

@Entity
@Table(name = "project_priorisering_changelog", schema = VNG_SCHEMA_NAME)
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class ProjectPrioriseringChangelog extends MilestoneChangeDataSuperclass {

    @JsonIgnoreProperties("priority")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "project_id")
    private Project project;

    @Column(name = "value_type")
    @Enumerated(EnumType.STRING)
    private ValueType valueType;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "project_priorisering_value_id")
    private ProjectPrioriseringValue value;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "project_priorisering_min_value_id")
    private ProjectPrioriseringValue minValue;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "project_priorisering_max_value_id")
    private ProjectPrioriseringValue maxValue;
}
