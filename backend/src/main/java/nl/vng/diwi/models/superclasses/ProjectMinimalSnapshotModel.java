package nl.vng.diwi.models.superclasses;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * These are the mandatory fields for a project.
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ProjectMinimalSnapshotModel extends ProjectCreateSnapshotModel {
    @JsonProperty(required = true)
    private UUID projectId;

}
