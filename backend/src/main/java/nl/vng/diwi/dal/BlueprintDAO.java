package nl.vng.diwi.dal;

import nl.vng.diwi.dal.entities.BlueprintSqlModel;
import nl.vng.diwi.dal.entities.BlueprintState;
import org.hibernate.Session;

import java.util.List;
import java.util.UUID;

public class BlueprintDAO extends AbstractRepository {

    public BlueprintDAO(Session session) {
        super(session);
    }

    public List<BlueprintSqlModel> getBlueprintsList(UUID userId) {

        return session.createNativeQuery(String.format("SELECT * FROM %s.get_dashboard_blueprints(null, :userId) ", GenericRepository.VNG_SCHEMA_NAME),
                BlueprintSqlModel.class)
            .setParameter("userId", userId)
            .list();
    }

    public BlueprintSqlModel getBlueprintById(UUID blueprintUuid) {

        return session.createNativeQuery(String.format("SELECT * FROM %s.get_dashboard_blueprints(:blueprintUuid, null) ", GenericRepository.VNG_SCHEMA_NAME),
                BlueprintSqlModel.class)
            .setParameter("blueprintUuid", blueprintUuid)
            .getSingleResultOrNull();
    }

    public BlueprintState getActiveBlueprintStateByName(String name) {
        return session.createQuery("FROM BlueprintState bs WHERE bs.name = :name AND bs.changeEndDate IS NULL", BlueprintState.class)
            .setParameter("name", name)
            .getSingleResultOrNull();
    }

}
