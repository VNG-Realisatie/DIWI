package nl.vng.diwi.models;

import lombok.Data;

@Data
public class ProjectUpdateModel {

    public enum ProjectProperty {
        projectColor,
        confidentialityLevel,
        name;
    }

    private ProjectProperty property;

    private String value;
}
