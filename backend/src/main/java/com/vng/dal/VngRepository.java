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
        Query q = session.createNativeQuery("SELECT * FROM get_active_and_future_projects_list(:now, :offset, :limit) ",
                ProjectListModel.class)
            .setTupleTransformer(new BeanTransformer<>(ProjectListModel.class))
            .setParameter("now", LocalDate.now())
            .setParameter("offset", filtering.getFirstResultIndex())
            .setParameter("limit", filtering.getPageSize());
        return q.getResultList();
    }

    public Integer getProjectsTableCount(FilterPaginationSorting filtering) {
        return session.createNativeQuery("SELECT COUNT(*) FROM get_active_and_future_projects_list(:now, :offset, :limit) ",
                Integer.class)
            .setParameter("now", LocalDate.now())
            .setParameter("offset", 0)
            .setParameter("limit", Integer.MAX_VALUE)
            .uniqueResult();
    }

}
