package nl.vng.diwi.dal.entities;

import java.util.List;

import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nl.vng.diwi.dal.GenericRepository;
import nl.vng.diwi.dal.entities.enums.OwnershipCategory;
import nl.vng.diwi.dal.entities.superclasses.IdSuperclass;

@Entity
@Table(name = "data_exchange_price_category_mapping", schema = GenericRepository.VNG_SCHEMA_NAME)
@Getter
@Setter
@NoArgsConstructor
public class DataExchangePriceCategoryMapping extends IdSuperclass {

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "data_exchange_id")
    private DataExchange dataExchange;

    @Column(name = "ownership_category")
    @Enumerated(EnumType.STRING)
    @JdbcType(PostgreSQLEnumJdbcType.class)
    private OwnershipCategory ownershipCategory;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "data_exchange_price_category_mapping_state")
    private List<DataExchangePriceCategoryMappingState> mappings;
}
