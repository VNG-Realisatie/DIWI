package com.vng.dal.entities;

import com.vng.dal.entities.superclasses.ChangeDataSuperclass;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import static com.vng.dal.GenericRepository.VNG_SCHEMA_NAME;

@Entity
@Table(name = "project_gemeenterol_value_state", schema = VNG_SCHEMA_NAME)
@Data
@NoArgsConstructor
public class ProjectGemeenteRolValueState extends ChangeDataSuperclass {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_gemeenterol_value_id")
    private ProjectGemeenteRolValue value;

    @Column(name = "value_label")
    private String valueLabel;
}
