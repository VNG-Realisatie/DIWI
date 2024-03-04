package nl.vng.diwi.models;

import lombok.Data;
import lombok.NoArgsConstructor;
import nl.vng.diwi.dal.entities.enums.Confidentiality;
import nl.vng.diwi.dal.entities.enums.PlanStatus;
import nl.vng.diwi.dal.entities.enums.PlanType;
import nl.vng.diwi.dal.entities.enums.ProjectPhase;
import org.apache.commons.lang3.EnumUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
public class ProjectUpdateModel {

    private static final String COLOR_REGEX = "^#[0-9a-fA-F]{6}$";

    public enum ProjectProperty {
        confidentialityLevel,
        municipalityRole,
        name,
        planningPlanStatus,
        planType,
        projectColor,
        projectLeaders,
        projectOwners,
        projectPhase,
        startDate, //do not change the order - startDate and endDate must be the last ones
        endDate;  //do not change the order - startDate and endDate must be the last ones
    }

    private ProjectProperty property;

    private String value;

    private List<String> values;

    private String add;

    private String remove;

    public ProjectUpdateModel(ProjectProperty property, String add, String remove) {
        this.property = property;
        this.add = add;
        this.remove = remove;
    }

    public ProjectUpdateModel(ProjectProperty property, String value) {
        this.property = property;
        this.value = value;
    }

    public ProjectUpdateModel(ProjectProperty property, List<String> values) {
        this.property = property;
        this.values = values;
    }

    public String validate() {
        if (property == null) {
            return "Property is missing";
        }

        return switch (property) {
            case startDate -> {
                try {
                    LocalDate.parse(value);
                } catch (Exception ex) {
                    yield "Date provided is not valid.";
                }
                yield null;
            }
            case endDate -> {
                try {
                    LocalDate endDate = LocalDate.parse(value);
                    if (!endDate.isAfter(LocalDate.now())) {
                        yield "Project end date must be in the future.";
                    }
                } catch (Exception ex) {
                    yield "Date provided is not valid.";
                }
                yield null;
            }
            case confidentialityLevel ->
                (value == null || !EnumUtils.isValidEnum(Confidentiality.class, value)) ? "New confidentiality level value is not valid." : null;
            case name -> (value == null || value.isBlank()) ? "New project name value is not valid." : null; // TODO
            case planningPlanStatus -> {
                if (values != null) {
                    for (String planningPlanStatusValue : values) {
                        if (!EnumUtils.isValidEnum(PlanStatus.class, planningPlanStatusValue)) {
                            yield "New planning plan status value is not valid.";
                        }
                    }
                }
                yield null;
            }
            case planType -> {
                if (values != null) {
                    for (String planType : values) {
                        if (!EnumUtils.isValidEnum(PlanType.class, planType)) {
                            yield "New plan type value is not valid.";
                        }
                    }
                }
                yield null;
            }
            case projectColor -> (value == null || !value.matches(COLOR_REGEX)) ? "New color is not valid." : null;
            case projectPhase -> (value == null || !EnumUtils.isValidEnum(ProjectPhase.class, value)) ? "New project phase value is not valid." : null;
            case municipalityRole, projectLeaders, projectOwners -> {
                if (add != null) {
                    try {
                        UUID.fromString(add);
                    } catch (IllegalArgumentException ex) {
                        yield "UUID provided for 'add' field is not valid.";
                    }
                }
                if (remove != null) {
                    try {
                        UUID.fromString(remove);
                    } catch (IllegalArgumentException ex) {
                        yield "UUID provided for 'remove' field is not valid.";
                    }
                }
                yield null;
            }
        };

    }
}
