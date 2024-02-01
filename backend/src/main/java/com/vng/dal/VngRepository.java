package com.vng.dal;

import com.vng.models.ProjectListModel;
import jakarta.persistence.Query;
import org.hibernate.Session;

import java.time.LocalDate;
import java.util.List;

public class VngRepository extends AbstractRepository {

    public VngRepository(Session session) {
        super(session);
    }

    public List<ProjectListModel> getProjectsTable(FilterPaginationSorting filtering) {
        Query q = session.createNativeQuery("""
                SELECT * FROM get_active_and_future_projects_list(:now, :offset, :limit, :sortColumn, :sortDirection,
                    :filterColumn, CAST(:filterValue AS text[]), :filterCondition) """ , ProjectListModel.class)
            .setTupleTransformer(new BeanTransformer<>(ProjectListModel.class))
            .setParameter("now", LocalDate.now())
            .setParameter("offset", filtering.getFirstResultIndex())
            .setParameter("limit", filtering.getPageSize())
            .setParameter("sortColumn", filtering.getSortColumn())
            .setParameter("sortDirection", filtering.getSortDirection().name())
            .setParameter("filterColumn", filtering.getFilterColumn())
            .setParameter("filterValue", fromJavaListToSqlArrayLiteral(filtering.getFilterValue()))
            .setParameter("filterCondition", filtering.getFilterCondition().name());

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
            .setParameter("filterValue", fromJavaListToSqlArrayLiteral(filtering.getFilterValue()))
            .setParameter("filterCondition", filtering.getFilterCondition().name())
            .uniqueResult();
    }

}
