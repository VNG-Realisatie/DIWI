package nl.vng.diwi.models.superclasses;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import nl.vng.diwi.dal.entities.enums.Confidentiality;
import nl.vng.diwi.dal.entities.enums.ProjectPhase;
import nl.vng.diwi.models.validation.Validation;

/**
 * These are the mandatory fields for a project.
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ProjectMinimalSnapshotModel extends DatedDataModelSuperClass {

    @JsonProperty(required = true)
    private UUID projectId;

    @JsonProperty(required = true)
    private UUID projectStateId;

    @JsonProperty(required = true)
    private String projectName;

    @JsonProperty(required = true)
    private String projectColor;

    @JsonProperty(required = true)
    private Confidentiality confidentialityLevel;

    @JsonProperty(required = true)
    private ProjectPhase projectPhase;

    public String validate() {
        if (projectId == null) {
            return "projectId can not be null";
        } else if (projectStateId == null) {
            return "projectStateId can not be null";
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
        }

        return null;
    }
}
