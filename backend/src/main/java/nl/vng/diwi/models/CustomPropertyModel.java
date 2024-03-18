package nl.vng.diwi.models;

import com.fasterxml.jackson.annotation.JsonProperty;
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
import org.hibernate.annotations.JdbcType;
import org.hibernate.annotations.Type;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;

import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class CustomPropertyModel {

    @Id
    private UUID id;

    @JsonProperty(required = true)
    private String name;

    @JsonProperty(required = true)
    @Enumerated(EnumType.STRING)
    @JdbcType(PostgreSQLEnumJdbcType.class)
    private ObjectType objectType;

    @JsonProperty(required = true)
    @Enumerated(EnumType.STRING)
    @JdbcType(PostgreSQLEnumJdbcType.class)
    private PropertyType propertyType;

    @JsonProperty(required = true)
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
        } else if (this.getCategoryValues() != null) {
            for (SelectDisabledModel category : this.getCategoryValues()) {
                if (category.getName() == null || category.getName().isEmpty()) {
                    return "Custom property category value name can not be null.";
                }
            }
        } else if (this.getOrdinalValues() != null) {
            for (OrdinalSelectDisabledModel ordinal : this.getOrdinalValues()) {
                if (ordinal.getName() == null || ordinal.getName().isEmpty()) {
                    return "Custom property ordinal value name can not be null.";
                }
                if (ordinal.getLevel() == null) {
                    return "Custom property ordinal value level can not be null.";
                }
            }
        }

        return null;

    }
}
