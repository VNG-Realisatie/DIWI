package nl.vng.diwi.dal.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nl.vng.diwi.dal.JsonListType;
import nl.vng.diwi.dal.entities.enums.ObjectType;
import nl.vng.diwi.dal.entities.enums.PropertyType;
import org.hibernate.annotations.JdbcType;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.Type;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;
import org.hibernate.type.SqlTypes;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class DataExchangePropertySqlModel {

    @Id
    private UUID id;

    private String name;

    private UUID customPropertyId;

    @Enumerated(EnumType.STRING)
    @JdbcType(PostgreSQLEnumJdbcType.class)
    private ObjectType objectType;

    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(columnDefinition = "text[]")
    @Getter(AccessLevel.NONE)
    private List<PropertyType> propertyTypes;

    private Boolean mandatory;

    private Boolean singleSelect;

    @Type(value = JsonListType.class)
    @Getter(AccessLevel.NONE)
    private List<DataExchangeOptionSqlModel> options;

    public List<DataExchangeOptionSqlModel> getOptions() {
        if (options == null) {
            return new ArrayList<>();
        }
        return options;
    }

    public List<PropertyType> getPropertyTypes() {
        if (propertyTypes == null) {
            return new ArrayList<>();
        }
        return propertyTypes;
    }
}
