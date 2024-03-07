package nl.vng.diwi.models;

import lombok.NoArgsConstructor;
import nl.vng.diwi.models.superclasses.ProjectSnapshotModelSuperclass;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class ProjectSnapshotModel extends ProjectSnapshotModelSuperclass {

    public ProjectSnapshotModel(ProjectListSqlModel sqlProject) {
        super(sqlProject);
    }

}
