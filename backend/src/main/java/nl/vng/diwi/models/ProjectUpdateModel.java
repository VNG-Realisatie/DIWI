package nl.vng.diwi.models;

import lombok.Data;
import nl.vng.diwi.dal.entities.enums.Confidentiality;
import nl.vng.diwi.dal.entities.enums.PlanStatus;
import nl.vng.diwi.dal.entities.enums.PlanType;
import nl.vng.diwi.dal.entities.enums.ProjectPhase;
import org.apache.commons.lang3.EnumUtils;

import java.util.List;
import java.util.UUID;

@Data
public class ProjectUpdateModel {

    private static final String COLOR_REGEX = "^#[0-9a-fA-F]{6}$";

    public enum ProjectProperty {
        confidentialityLevel,
        name,
        planningPlanStatus,
        planType,
        projectColor,
        projectLeaders,
        projectOwners,
        projectPhase;
    }

    private ProjectProperty property;

    private String value;

    private List<String> values;

    private String add;

    private String remove;

    public String validate() {
        if (property == null) {
            return "Property is missing";
        }

        switch (property) {
            case confidentialityLevel:
                return (value == null || !EnumUtils.isValidEnum(Confidentiality.class, value)) ? "New confidentiality level value is not valid." : null;
            case name:
                return (value == null || value.isBlank()) ? "New project name value is not valid." : null; //TODO
            case planningPlanStatus:
                if (values != null) {
                    for (String planningPlanStatusValue : values) {
                        if (!EnumUtils.isValidEnum(PlanStatus.class, planningPlanStatusValue)) {
                            return "New planning plan status value is not valid.";
                        }
                    }
                }
                return null;
            case planType:
                if (values != null) {
                    for (String planType : values) {
                        if (!EnumUtils.isValidEnum(PlanType.class, planType)) {
                            return "New plan type value is not valid.";
                        }
                    }
                }
                return null;
            case projectColor:
                return (value == null || !value.matches(COLOR_REGEX)) ? "New color is not valid." : null;
            case projectPhase:
                return (value == null || !EnumUtils.isValidEnum(ProjectPhase.class, value)) ? "New project phase value is not valid." : null;
            case projectLeaders:
            case projectOwners:
                if (add != null) {
                    try {
                        UUID.fromString(add);
                    } catch (IllegalArgumentException ex) {
                        return "UUID provided for 'add' field is not valid.";
                    }
                }
                if (remove != null) {
                    try {
                        UUID.fromString(remove);
                    } catch (IllegalArgumentException ex) {
                        return "UUID provided for 'remove' field is not valid.";
                    }
                }
                return null;
            default:
                return "Unknown project property";
        }
    }
}
