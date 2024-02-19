package nl.vng.diwi.models;

import lombok.Data;
import nl.vng.diwi.dal.entities.enums.Confidentiality;
import nl.vng.diwi.dal.entities.enums.PlanStatus;
import nl.vng.diwi.dal.entities.enums.PlanType;
import nl.vng.diwi.dal.entities.enums.ProjectPhase;
import org.apache.commons.lang3.EnumUtils;

import java.util.List;

@Data
public class ProjectUpdateModel {

    private static final String COLOR_REGEX = "^#[0-9a-fA-F]{6}$";

    public enum ProjectProperty {
        confidentialityLevel,
        name,
        planningPlanStatus,
        planType,
        projectColor,
        projectPhase;
    }

    private ProjectProperty property;

    private String value;

    private List<String> values;

    public String validate() {
        if (property == null) {
            return "Property is missing";
        }

        String errorMessage = null;

        switch (property) {
            case confidentialityLevel -> errorMessage = (value == null || !EnumUtils.isValidEnum(Confidentiality.class, value)) ?
                "New confidentiality level value is not valid." : null;
            case name -> errorMessage = (value == null || value.isBlank()) ?
                "New project name value is not valid." : null; //TODO
            case planningPlanStatus -> {
                if (values != null)
                    for (String planningPlanStatusValue : values) {
                        if (!EnumUtils.isValidEnum(PlanStatus.class, planningPlanStatusValue)) {
                            errorMessage = "New planning plan status value is not valid.";
                        }
                    }
            }
            case planType -> {
                if (values != null)
                    for (String planType : values) {
                        if (!EnumUtils.isValidEnum(PlanType.class, planType)) {
                            errorMessage = "New plan type value is not valid.";
                        }
                    }
            }
            case projectColor -> errorMessage = (value == null || !value.matches(COLOR_REGEX)) ?
                "New color is not valid." : null;
            case projectPhase -> errorMessage = (value == null || !EnumUtils.isValidEnum(ProjectPhase.class, value)) ?
                "New project phase value is not valid." : null;
        }

        return errorMessage;
    }
}
