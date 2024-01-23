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

    public List<ProjectListModel> getProjectsTable() {
        Query q = session.createNativeQuery("SELECT * FROM get_active_and_future_projects_list(:now) ",
                ProjectListModel.class)
            .setTupleTransformer(new BeanTransformer<>(ProjectListModel.class))
            .setParameter("now", LocalDate.now());
        return q.getResultList();
    }

}
