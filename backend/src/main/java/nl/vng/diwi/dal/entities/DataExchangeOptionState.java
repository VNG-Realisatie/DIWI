package nl.vng.diwi.dal.entities;

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
import org.hibernate.annotations.Filter;

@Entity
@Table(name = "data_exchange_option_state", schema = GenericRepository.VNG_SCHEMA_NAME)
@Getter
@Setter
@NoArgsConstructor
@Filter(name = GenericRepository.CURRENT_DATA_FILTER)
public class DataExchangeOptionState extends ChangeDataSuperclass {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "data_exchange_option_id")
    private DataExchangeOption dataExchangeOption;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "property_category_value_id")
    private PropertyCategoryValue propertyCategoryValue;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "property_ordinal_value_id")
    private PropertyOrdinalValue propertyOrdinalValue;


}
