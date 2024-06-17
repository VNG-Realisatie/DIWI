package nl.vng.diwi.models.superclasses;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import nl.vng.diwi.dal.entities.enums.Confidentiality;
import nl.vng.diwi.dal.entities.enums.ProjectPhase;
import nl.vng.diwi.models.UserGroupModel;
import nl.vng.diwi.models.validation.Validation;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ProjectCreateSnapshotModel extends DatedDataModelSuperClass {

    @JsonProperty(required = true)
    private String projectName;

    @JsonProperty(required = true)
    private String projectColor;

    @JsonProperty(required = true)
    private Confidentiality confidentialityLevel;

    @JsonProperty(required = true)
    private ProjectPhase projectPhase;

    @JsonProperty(required = true)
    private List<UserGroupModel> projectOwners = new ArrayList<>();

    public String validate() {
        if (getStartDate() == null) {
            return "startDate can not be null";
        } else if (getEndDate() == null) {
            return "endDate can not be null";
        } else if (!getEndDate().isAfter(getStartDate())) {
            return "endDate must be after startDate";
        } else if (projectName == null) {
            return "projectName can not be null";
        } else if (projectColor == null) {
            return "projectColor can not be null";
        } else if (Validation.validateColor(projectColor)) {
            return "projectColor is invalid. It should have the format '#abc123'.";
        } else if (confidentialityLevel == null) {
            return "confidentialityLevel can not be null";
        } else if (projectPhase == null) {
            return "projectPhase can not be null";
        } else if (projectOwners == null || projectOwners.isEmpty()) {
            return "projectOwners can not be empty";
        }

        return null;
    }
}
