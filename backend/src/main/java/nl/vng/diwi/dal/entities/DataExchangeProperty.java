package nl.vng.diwi.dal.entities;

import io.hypersistence.utils.hibernate.type.array.EnumArrayType;
import io.hypersistence.utils.hibernate.type.array.internal.AbstractArrayType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nl.vng.diwi.dal.GenericRepository;
import nl.vng.diwi.dal.entities.enums.ObjectType;
import nl.vng.diwi.dal.entities.enums.PropertyType;
import nl.vng.diwi.dal.entities.superclasses.IdSuperclass;
import org.hibernate.annotations.JdbcType;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;


@Entity
@Table(name = "data_exchange_property", schema = GenericRepository.VNG_SCHEMA_NAME)
@Getter
@Setter
@NoArgsConstructor
public class DataExchangeProperty extends IdSuperclass {

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "data_exchange_id")
    private DataExchange dataExchange;

    @Column(name = "data_exchange_property_name")
    private String dxPropertyName;

    @Column(name = "object_type")
    @Enumerated(EnumType.STRING)
    @JdbcType(PostgreSQLEnumJdbcType.class)
    private ObjectType objectType;

    @Type(
        value = EnumArrayType.class,
        parameters = @Parameter(name = AbstractArrayType.SQL_ARRAY_TYPE, value = "diwi.maatwerk_eigenschap_type")
    )
    @Column(
        name = "property_type",
        columnDefinition = "diwi.maatwerk_eigenschap_type[]"
    )
    @Enumerated(EnumType.STRING)
    private PropertyType[] propertyTypes;

    @Column(name = "mandatory")
    private Boolean mandatory;

    @Column(name = "single_select")
    private Boolean singleSelect;

}
