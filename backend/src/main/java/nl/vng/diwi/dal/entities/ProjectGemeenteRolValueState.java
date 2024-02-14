package nl.vng.diwi.dal.entities;

import nl.vng.diwi.dal.entities.superclasses.ChangeDataSuperclass;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import nl.vng.diwi.dal.GenericRepository;

@Entity
@Table(name = "project_gemeenterol_value_state", schema = GenericRepository.VNG_SCHEMA_NAME)
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class ProjectGemeenteRolValueState extends ChangeDataSuperclass {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_gemeenterol_value_id")
    private ProjectGemeenteRolValue value;

    @Column(name = "value_label")
    private String valueLabel;
}
