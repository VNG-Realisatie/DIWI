package nl.vng.diwi.models;

import lombok.NoArgsConstructor;
import nl.vng.diwi.dal.entities.ProjectListSqlModel;
import nl.vng.diwi.models.superclasses.ProjectSnapshotModelSuperclass;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class ProjectSnapshotModel extends ProjectSnapshotModelSuperclass {


    private List<ProjectHouseblockCustomPropertyModel> customProperties = new ArrayList<>();

    public ProjectSnapshotModel(ProjectListSqlModel sqlProject) {
        super(sqlProject);

    }

}
