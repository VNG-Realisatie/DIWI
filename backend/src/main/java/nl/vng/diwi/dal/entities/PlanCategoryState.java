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
@Table(name = "plan_category_state", schema = GenericRepository.VNG_SCHEMA_NAME)
@Getter
@Setter
@NoArgsConstructor
public class PlanCategoryState extends ChangeDataSuperclass {

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "plan_category_id")
    private PlanCategory planCategory;

    @Column(name = "value_label")
    private String label;

}
