package nl.vng.diwi.dal.entities;

import nl.vng.diwi.dal.GenericRepository;
import nl.vng.diwi.dal.entities.superclasses.ChangeDataSuperclass;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "project_gemeenterol_value_state", schema = GenericRepository.VNG_SCHEMA_NAME)
@ToString
@RequiredArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ProjectGemeenteRolValueState extends ChangeDataSuperclass {

    @JsonIgnoreProperties("state")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_gemeenterol_value_id")
    private ProjectGemeenteRolValue value;

    @Column(name = "value_label")
    private String valueLabel;

    public ProjectGemeenteRolValue getValue() {
        return value;
    }

    public void setValue(ProjectGemeenteRolValue value) {
        this.value = value;
    }

    public String getValueLabel() {
        return valueLabel;
    }

    public void setValueLabel(String valueLabel) {
        this.valueLabel = valueLabel;
    }
}
