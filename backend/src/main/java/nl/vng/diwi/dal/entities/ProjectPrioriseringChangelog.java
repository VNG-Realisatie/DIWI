package nl.vng.diwi.dal.entities;

import lombok.Getter;
import lombok.Setter;
import nl.vng.diwi.dal.GenericRepository;
import nl.vng.diwi.dal.entities.enums.ValueType;
import nl.vng.diwi.dal.entities.superclasses.MilestoneChangeDataSuperclass;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "project_priorisering_changelog", schema = GenericRepository.VNG_SCHEMA_NAME)
@Getter
@Setter
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
