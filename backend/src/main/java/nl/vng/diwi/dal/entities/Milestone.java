package nl.vng.diwi.dal.entities;

import java.util.List;

import nl.vng.diwi.dal.GenericRepository;
import nl.vng.diwi.dal.entities.superclasses.IdSuperclass;
import org.hibernate.annotations.Filter;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "milestone", schema = GenericRepository.VNG_SCHEMA_NAME)
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class Milestone extends IdSuperclass {

    @JsonIgnoreProperties("milestone")
    @OneToMany(mappedBy="milestone", fetch = FetchType.LAZY)
    @Filter(name = GenericRepository.CURRENT_DATA_FILTER, condition = "change_end_date IS NULL")
    private List<MilestoneState> state;

}
