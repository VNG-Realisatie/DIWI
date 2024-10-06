package nl.vng.diwi.dal.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nl.vng.diwi.dal.GenericRepository;
import nl.vng.diwi.dal.entities.superclasses.ChangeDataSuperclass;

@Entity
@Table(name = "property_range_category_value_state", schema = GenericRepository.VNG_SCHEMA_NAME)
@Getter
@Setter
@NoArgsConstructor
public class PropertyRangeCategoryValueState extends ChangeDataSuperclass {

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "range_category_value_id")
    private PropertyRangeCategoryValue rangeCategoryValue;

    @Column(name = "name")
    private String name;

    @Column(name = "min")
    private Long min;

    @Column(name = "max")
    private Long max;

}
