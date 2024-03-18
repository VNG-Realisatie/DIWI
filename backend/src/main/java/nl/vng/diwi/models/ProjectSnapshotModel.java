package nl.vng.diwi.models;

import lombok.NoArgsConstructor;
import nl.vng.diwi.dal.entities.ProjectListSqlModel;
import nl.vng.diwi.models.superclasses.ProjectSnapshotModelSuperclass;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class ProjectSnapshotModel extends ProjectSnapshotModelSuperclass {

    private LocationModel location;

    public ProjectSnapshotModel(ProjectListSqlModel sqlProject) {
        super(sqlProject);

        this.location = new LocationModel(sqlProject.getLatitude(), sqlProject.getLongitude());
    }

}
