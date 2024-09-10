package nl.vng.diwi.dal.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nl.vng.diwi.dal.GenericRepository;
import nl.vng.diwi.dal.entities.superclasses.IdSuperclass;

@Entity
@Table(name = "plan_conditie_property_category_value", schema = GenericRepository.VNG_SCHEMA_NAME)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PlanConditionCategoryCustomPropertyValue extends IdSuperclass {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_conditie_property_category_id")
    private PlanConditionCategoryCustomProperty categoryCondition;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "property_value_id")
    private PropertyCategoryValue categoryValue;

}
