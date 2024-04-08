package nl.vng.diwi.dal.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nl.vng.diwi.dal.GenericRepository;
import nl.vng.diwi.dal.entities.superclasses.IdSuperclass;
import org.hibernate.annotations.Filter;

import java.util.List;

@Entity
@Table(name = "property_ordinal_value", schema = GenericRepository.VNG_SCHEMA_NAME)
@Getter
@Setter
@NoArgsConstructor
public class PropertyOrdinalValue extends IdSuperclass {

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "property_id")
    private Property property;

    @OneToMany(mappedBy= "propertyOrdinalValue", fetch = FetchType.LAZY)
    @Filter(name = GenericRepository.CURRENT_DATA_FILTER, condition = "change_end_date IS NULL")
    private List<PropertyOrdinalValueState> states;

}
