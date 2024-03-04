package nl.vng.diwi.dal;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import nl.vng.diwi.dal.entities.Project;
import nl.vng.diwi.dal.entities.ProjectState;

import nl.vng.diwi.models.ProjectListSqlModel;
import org.hibernate.Session;
import org.hibernate.query.SelectionQuery;

import jakarta.annotation.Nullable;

import lombok.NonNull;

public class ProjectsDAO extends AbstractRepository {

    public ProjectsDAO (Session session) {
        super(session);
    }

    @Nullable
    public Project getCurrentProject(@NonNull UUID projectUuid) {
        session.enableFilter(GenericRepository.CURRENT_DATA_FILTER);
        String statement = "FROM Project P WHERE P.id = :uuid";
        SelectionQuery<Project> query = session
                .createSelectionQuery(statement, Project.class)
                .setParameter("uuid", projectUuid);
        return query.getSingleResultOrNull();
    }

    public List<ProjectListSqlModel> getProjectsTable(FilterPaginationSorting filtering) {
        SelectionQuery<ProjectListSqlModel> q = session.createNativeQuery("""
                SELECT * FROM get_active_and_future_projects_list(:now, :offset, :limit, :sortColumn, :sortDirection,
                    :filterColumn, CAST(:filterValue AS text[]), :filterCondition) """ , ProjectListSqlModel.class)
            .setParameter("now", LocalDate.now())
            .setParameter("offset", filtering.getFirstResultIndex())
            .setParameter("limit", filtering.getPageSize())
            .setParameter("sortColumn", filtering.getSortColumn())
            .setParameter("sortDirection", filtering.getSortDirection().name())
            .setParameter("filterColumn", filtering.getFilterColumn())
            .setParameter("filterValue", filtering.getFilterColumn() == null ? null :fromJavaListToSqlArrayLiteral(filtering.getFilterValue()))
            .setParameter("filterCondition", filtering.getFilterColumn() == null ? null : filtering.getFilterCondition().name());

        return q.getResultList();
    }

    public Integer getProjectsTableCount(FilterPaginationSorting filtering) {
        return session.createNativeQuery("""
                SELECT COUNT(*) FROM get_active_and_future_projects_list(:now, :offset, :limit, :sortColumn, :sortDirection,
                :filterColumn, CAST(:filterValue AS text[]), :filterCondition) """, Integer.class)
            .setParameter("now", LocalDate.now())
            .setParameter("offset", 0)
            .setParameter("limit", Integer.MAX_VALUE)
            .setParameter("sortColumn", null)
            .setParameter("sortDirection", null)
            .setParameter("filterColumn", filtering.getFilterColumn())
            .setParameter("filterValue", filtering.getFilterColumn() == null ? null : fromJavaListToSqlArrayLiteral(filtering.getFilterValue()))
            .setParameter("filterCondition", filtering.getFilterColumn() == null ? null :filtering.getFilterCondition().name())
            .uniqueResult();
    }

    public ProjectState getCurrentProjectState(UUID projectUuid) {
        session.enableFilter(GenericRepository.CURRENT_DATA_FILTER);
        String statement = "FROM ProjectState ps WHERE ps.project.id = :projectUuid";
        SelectionQuery<ProjectState> query = session
            .createSelectionQuery(statement, ProjectState.class)
            .setParameter("projectUuid", projectUuid);
        return query.getSingleResultOrNull();
    }
}
