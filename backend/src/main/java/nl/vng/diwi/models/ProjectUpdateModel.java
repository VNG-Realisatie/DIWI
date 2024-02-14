package nl.vng.diwi.models;

import lombok.Data;
import nl.vng.diwi.dal.entities.enums.Confidentiality;
import org.apache.commons.lang3.EnumUtils;

@Data
public class ProjectUpdateModel {

    private static final String COLOR_REGEX = "^#[0-9a-fA-F]{6}$";

    public enum ProjectProperty {
        projectColor,
        confidentialityLevel,
        name;
    }

    private ProjectProperty property;

    private String value;

    public String validate() {
        if (property == null) {
            return "Property is missing";
        }

        return switch (property) {
            case projectColor -> (value == null || !value.matches(COLOR_REGEX)) ?
                "New color is not valid." : null;
            case confidentialityLevel -> (value == null || !EnumUtils.isValidEnum(Confidentiality.class, value)) ?
                "New confidentiality level value is not valid." : null;
            case name -> null; //TODO
        };
    }
}
