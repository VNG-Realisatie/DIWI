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
import nl.vng.diwi.dal.entities.superclasses.IdSuperclass;
import org.hibernate.annotations.Filter;

@Entity
@Table(name = "data_exchange_option", schema = GenericRepository.VNG_SCHEMA_NAME)
@Getter
@Setter
@NoArgsConstructor
@Filter(name = GenericRepository.CURRENT_DATA_FILTER)
public class DataExchangeOption extends IdSuperclass {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "data_exchange_property_id")
    private DataExchangeProperty dataExchangeProperty;

    @Column(name = "data_exchange_option_name")
    private String dxOptionName;

}
