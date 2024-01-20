package com.vng.dal.entities;

import com.vng.dal.entities.superclasses.ChangeDataSuperclass;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import static com.vng.dal.GenericRepository.VNG_SCHEMA_NAME;

@Entity
@Table(name = "project_priorisering_value_state", schema = VNG_SCHEMA_NAME)
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class ProjectPrioriseringValueState extends ChangeDataSuperclass {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_priorisering_value_id")
    private ProjectPrioriseringValue value;

    @Column(name = "value_label")
    private String valueLabel;

    @Column(name = "ordinal_level")
    private String ordinalLevel;
}
