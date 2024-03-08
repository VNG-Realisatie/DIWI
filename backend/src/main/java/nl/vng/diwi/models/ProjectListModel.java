package nl.vng.diwi.models;

import lombok.Data;

import lombok.EqualsAndHashCode;
import nl.vng.diwi.models.superclasses.ProjectSnapshotModelSuperclass;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class ProjectListModel extends ProjectSnapshotModelSuperclass {

    public static final List<String> SORTABLE_COLUMNS = List.of("projectName", "projectOwners", "projectLeaders", "confidentialityLevel", "organizationName",
        "planType", "startDate", "endDate", "priority", "projectPhase", "municipalityRole", "planningPlanStatus", "totalValue", "municipality", "wijk", "buurt");
    public static final String DEFAULT_SORT_COLUMN = "startDate";

    public ProjectListModel(ProjectListSqlModel sqlModel) {
        super(sqlModel);
    }

}
