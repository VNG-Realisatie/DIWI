package nl.vng.diwi.dal.entities;

import org.hibernate.annotations.Filter;

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
@Table(name = "data_exchange_price_category_mapping_state", schema = GenericRepository.VNG_SCHEMA_NAME)
@Getter
@Setter
@NoArgsConstructor
@Filter(name = GenericRepository.CURRENT_DATA_FILTER)
public class DataExchangePriceCategoryMappingState extends ChangeDataSuperclass {

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "data_exchange_price_category_mapping_id")
    private DataExchangePriceCategoryMapping dataExchangePriceCategoryMapping;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "price_range_id")
    private PropertyRangeCategoryValue priceRange;
}
