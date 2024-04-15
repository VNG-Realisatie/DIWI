package nl.vng.diwi.models;

import lombok.Data;
import lombok.NoArgsConstructor;
import nl.vng.diwi.dal.VngRepository;
import nl.vng.diwi.dal.entities.Property;
import nl.vng.diwi.dal.entities.enums.Confidentiality;
import nl.vng.diwi.dal.entities.enums.PlanStatus;
import nl.vng.diwi.dal.entities.enums.PlanType;
import nl.vng.diwi.dal.entities.enums.ProjectPhase;
import nl.vng.diwi.dal.entities.superclasses.IdSuperclass;
import nl.vng.diwi.generic.Constants;
import nl.vng.diwi.models.validation.Validation;

import org.apache.commons.lang3.EnumUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
public class ProjectUpdateModel {

    public enum ProjectProperty {
        confidentialityLevel,
        location,
        municipalityRole,
        name,
        planningPlanStatus,
        planType,
        priority,
        projectColor,
        projectLeaders,
        projectOwners,
        projectPhase,
        region,
        district,
        neighbourhood,
        // Do not change the order - startDate and endDate must be the last ones!
        // It will cause problems in future problems and extra milestones to be created.
        // See ProjectsResourceTest.updateProjectTest_futureProject
        startDate, // Do not change the order - startDate and endDate must be the last ones
        endDate; // Do not change the order - startDate and endDate must be the last ones
    }

    private ProjectProperty property;
    private String value;
    private List<String> values;
    private UUID add;
    private UUID remove;
    private UUID min;
    private UUID max;

    public ProjectUpdateModel(ProjectProperty property, UUID add, UUID remove) {
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

    public ProjectUpdateModel(ProjectProperty property, String value, UUID min, UUID max) {
        this.property = property;
        this.value = value;
        this.min = min;
        this.max = max;
    }

    public String validate(VngRepository repo) {
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
            case priority -> {
                if (value == null && min != null && max != null) {
                    yield null;
                }
                if (value != null && min == null && max == null) {
                    try {
                        UUID.fromString(value);
                    } catch (IllegalArgumentException ex) {
                        yield "UUID provided for priority 'value' field is not valid.";
                    }
                    yield null;
                }
                yield "Invalid values for new priority. Provide either the 'value' field, or the 'min' and 'max' fields.";
            }
            case projectColor -> (value == null || Validation.validateColor(value)) ? "New color is not valid." : null;
            case location -> {
                if (values == null || values.size() != 2) {
                    yield "Invalid values for new location. 2 values must be provided in the 'values' list.";
                }
                try {
                    if (values.get(0) != null) {
                        Double.parseDouble(values.get(0));
                    }
                    if (values.get(1) != null) {
                        Double.parseDouble(values.get(1));
                    }
                } catch (NumberFormatException ex) {
                    yield "Latitude and/or longitude values do not have the correct format.";
                }
                yield null;
            }
            case projectPhase -> (value == null || !EnumUtils.isValidEnum(ProjectPhase.class, value)) ? "New project phase value is not valid." : null;
            case municipalityRole, projectLeaders, projectOwners -> null;
            case region -> {
                Property regionProperty = repo.getPropertyDAO().getActivePropertyStateByName(Constants.FIXED_PROPERTY_REGION).getProperty();
                if (regionProperty == null) {
                    yield "Missing region property";
                }
                List<UUID> regionCatUuids = regionProperty.getCategoryValues().stream().map(IdSuperclass::getId).toList();
                if (!regionCatUuids.containsAll(getValuesAsUuids())) {
                    yield "Invalid region property";
                }
                yield null;
            }
            case district -> {
                Property districtProperty = repo.getPropertyDAO().getActivePropertyStateByName(Constants.FIXED_PROPERTY_DISTRICT).getProperty();
                if (districtProperty == null) {
                    yield "Missing district property";
                }
                List<UUID> districtCatUuids = districtProperty.getCategoryValues().stream().map(IdSuperclass::getId).toList();
                if (!districtCatUuids.containsAll(getValuesAsUuids())) {
                    yield "Invalid district property";
                }
                yield null;
            }
            case neighbourhood -> {
                Property neighbourhoodProperty = repo.getPropertyDAO().getActivePropertyStateByName(Constants.FIXED_PROPERTY_NEIGHBOURHOOD).getProperty();
                if (neighbourhoodProperty == null) {
                    yield "Missing neighbourhood property";
                }
                List<UUID> neighbourhoodCatUuids = neighbourhoodProperty.getCategoryValues().stream().map(IdSuperclass::getId).toList();
                if (!neighbourhoodCatUuids.containsAll(getValuesAsUuids())) {
                    yield "Invalid neighbourhood property";
                }
                yield null;
            }
        };
    }

    public List<UUID> getValuesAsUuids() {
        return values.stream().map(UUID::fromString).toList();
    }
}
