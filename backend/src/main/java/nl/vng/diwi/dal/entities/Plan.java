package nl.vng.diwi.dal.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nl.vng.diwi.dal.GenericRepository;
import nl.vng.diwi.dal.entities.superclasses.IdSuperclass;

import java.util.List;

@Entity
@Table(name = "plan", schema = GenericRepository.VNG_SCHEMA_NAME)
@Getter
@Setter
@NoArgsConstructor
public class Plan extends IdSuperclass {

    @OneToMany(mappedBy="plan", fetch = FetchType.LAZY)
    private List<PlanState> states;

    @OneToMany(mappedBy="plan", fetch = FetchType.LAZY)
    private List<PlanCondition> conditions;

}
