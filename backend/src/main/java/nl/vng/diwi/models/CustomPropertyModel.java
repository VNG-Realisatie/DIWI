package nl.vng.diwi.models;

import lombok.Data;
import nl.vng.diwi.dal.entities.enums.ObjectType;
import nl.vng.diwi.dal.entities.enums.PropertyType;

import java.util.List;
import java.util.UUID;

@Data
public class CustomPropertyModel {

    private UUID id;
    private String name;
    private ObjectType objectType;
    private PropertyType propertyType;
    private Boolean disabled;
    private List<SelectModel> categories;

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
