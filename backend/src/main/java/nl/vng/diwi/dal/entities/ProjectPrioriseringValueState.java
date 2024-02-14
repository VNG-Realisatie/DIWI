package nl.vng.diwi.dal.entities;

import nl.vng.diwi.dal.entities.superclasses.ChangeDataSuperclass;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import nl.vng.diwi.dal.GenericRepository;

@Entity
@Table(name = "project_priorisering_value_state", schema = GenericRepository.VNG_SCHEMA_NAME)
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class ProjectPrioriseringValueState extends ChangeDataSuperclass {

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "project_priorisering_value_id")
    private ProjectPrioriseringValue value;

    @Column(name = "value_label")
    private String valueLabel;

    @Column(name = "ordinal_level")
    private Integer ordinalLevel;
}
