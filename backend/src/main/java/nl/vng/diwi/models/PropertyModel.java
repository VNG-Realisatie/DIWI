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
import nl.vng.diwi.dal.entities.enums.PropertyKind;
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
public class PropertyModel {

    @Id
    private UUID id;

    @JsonProperty(required = true)
    private String name;

    @JsonProperty(required = true)
    @Enumerated(EnumType.STRING)
    @JdbcType(PostgreSQLEnumJdbcType.class)
    private PropertyKind type;

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
    private List<SelectDisabledModel> categories;

    @Type(value = JsonListType.class)
    private List<OrdinalSelectDisabledModel> ordinals;

    public SelectDisabledModel getActiveCategoryValue(String categoryValue) {
        if (categories != null) {
            return categories.stream().filter(c -> c.getName().equals(categoryValue) && c.getDisabled() == Boolean.FALSE).findFirst().orElse(null);
        }
        return null;
    }

    public String validate() {

        if (this.name == null || this.name.isBlank()) {
            return "Property name can not be null.";
        } else if (this.objectType == null) {
            return "Property object-type can not be null.";
        } else if (this.propertyType == null) {
            return "Property property-type can not be null.";
        } else if (this.getCategories() != null) {
            for (SelectDisabledModel category : this.getCategories()) {
                if (category.getName() == null || category.getName().isEmpty()) {
                    return "Property category value name can not be null.";
                }
            }
        } else if (this.getOrdinals() != null) {
            for (OrdinalSelectDisabledModel ordinal : this.getOrdinals()) {
                if (ordinal.getName() == null || ordinal.getName().isEmpty()) {
                    return "Property ordinal value name can not be null.";
                }
                if (ordinal.getLevel() == null) {
                    return "Property ordinal value level can not be null.";
                }
            }
        }

        return null;

    }
}
