package nl.vng.diwi.dal.entities;

import nl.vng.diwi.dal.entities.superclasses.IdSuperclass;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import nl.vng.diwi.dal.GenericRepository;

import java.util.List;

import org.hibernate.annotations.Filter;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "project_priorisering_value", schema = GenericRepository.VNG_SCHEMA_NAME)
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class ProjectPrioriseringValue extends IdSuperclass {

    @JsonIgnoreProperties("value")
    @OneToMany(mappedBy="value", fetch = FetchType.LAZY)
    @Filter(name = "current", condition = "change_end_date IS NULL")
    private List<ProjectPrioriseringValueState> state;

}
