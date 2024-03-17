package nl.vng.diwi.models;

import java.util.List;
import java.util.UUID;

import org.hibernate.annotations.JdbcType;
import org.hibernate.annotations.Type;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nl.vng.diwi.dal.JsonListType;
import nl.vng.diwi.dal.entities.enums.ObjectType;
import nl.vng.diwi.dal.entities.enums.PropertyType;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class CustomPropertyModel {

    @Id
    private UUID id;
    private String name;

    @Enumerated(EnumType.STRING)
    @JdbcType(PostgreSQLEnumJdbcType.class)
    private ObjectType objectType;

    @Enumerated(EnumType.STRING)
    @JdbcType(PostgreSQLEnumJdbcType.class)
    private PropertyType propertyType;
    private Boolean disabled;

    @Type(value = JsonListType.class)
    private List<SelectDisabledModel> categoryValues;

    @Type(value = JsonListType.class)
    private List<OrdinalSelectDisabledModel> ordinalValues;

    public String validate() {

        if (this.name == null || this.name.isBlank()) {
            return "Custom property name can not be null.";
        } else if (this.objectType == null) {
            return "Custom property object-type can not be null.";
        } else if (this.propertyType == null) {
            return "Custom property property-type can not be null.";
        }

        return null;

    }
}
