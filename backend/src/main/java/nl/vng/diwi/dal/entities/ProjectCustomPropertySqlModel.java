package nl.vng.diwi.dal.entities;

import io.hypersistence.utils.hibernate.type.range.PostgreSQLRangeType;
import io.hypersistence.utils.hibernate.type.range.Range;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nl.vng.diwi.dal.entities.enums.PropertyType;
import nl.vng.diwi.dal.entities.enums.ValueType;
import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@NoArgsConstructor
@Getter
@Setter
@Convert(
    attributeName = "numrange",
    converter = PostgreSQLRangeType.class
)
public class ProjectCustomPropertySqlModel {

    @Id
    private UUID customPropertyId;

    private String textValue;

    private Boolean booleanValue;

    private Double numericValue;

    @Enumerated(EnumType.STRING)
    @JdbcType(PostgreSQLEnumJdbcType.class)
    private ValueType numericValueType;

    @Column(columnDefinition = "numrange")
    private Range<BigDecimal> numericValueRange;

    @Enumerated(EnumType.STRING)
    @JdbcType(PostgreSQLEnumJdbcType.class)
    private PropertyType propertyType;

}
