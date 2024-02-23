package nl.vng.diwi.dal.entities;

import nl.vng.diwi.dal.entities.superclasses.IdSuperclass;

import org.hibernate.annotations.Filter;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

import nl.vng.diwi.dal.GenericRepository;

@Entity
@Table(name = "project_gemeenterol_value", schema = GenericRepository.VNG_SCHEMA_NAME)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class ProjectGemeenteRolValue extends IdSuperclass {

    @JsonIgnoreProperties("value")
    @OneToMany(mappedBy="value", fetch = FetchType.LAZY)
    @Filter(name = GenericRepository.CURRENT_DATA_FILTER, condition = "change_end_date IS NULL")
    private List<ProjectGemeenteRolValueState> state;

    public List<ProjectGemeenteRolValueState> getState() {
        return state;
    }

    public void setState(List<ProjectGemeenteRolValueState> state) {
        this.state = state;
    }
}
