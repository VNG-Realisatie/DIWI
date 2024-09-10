package nl.vng.diwi.dal;

import nl.vng.diwi.dal.entities.PlanState;
import nl.vng.diwi.models.PlanSqlModel;
import nl.vng.diwi.models.SelectModel;
import org.hibernate.Session;

import java.util.List;
import java.util.UUID;

public class GoalDAO extends AbstractRepository {

    public GoalDAO(Session session) {
        super(session);
    }


    public List<SelectModel> getAllGoalCategories() {
        return session.createNativeQuery(String.format("""
            SELECT cs.plan_category_id AS id, value_label AS name
                FROM %1$s.plan_category_state cs
                    WHERE cs.change_end_date IS NULL
                    ORDER BY cs.value_label """, GenericRepository.VNG_SCHEMA_NAME), Object[].class)
            .setTupleTransformer(new BeanTransformer<>(SelectModel.class))
            .list();
    }

    public List<PlanState> getActivePlanStatesByCategoryId(UUID categoryId) {
        return session.createQuery("FROM PlanState ps WHERE ps.category.id = :categoryId AND ps.changeEndDate IS NULL", PlanState.class)
            .setParameter("categoryId", categoryId)
            .list();
    }

    public List<PlanSqlModel> getGoals() {
        return session.createNativeQuery(String.format(
                    "SELECT * FROM %s.get_active_plans(null) ",
                    GenericRepository.VNG_SCHEMA_NAME),
                PlanSqlModel.class)
            .list();
    }

    public PlanSqlModel getGoalById(UUID planUuid) {
        return session.createNativeQuery(String.format(
                    "SELECT * FROM %s.get_active_plans(:planUuid) ",
                    GenericRepository.VNG_SCHEMA_NAME),
                PlanSqlModel.class)
            .setParameter("planUuid", planUuid)
            .getSingleResultOrNull();
    }
}
