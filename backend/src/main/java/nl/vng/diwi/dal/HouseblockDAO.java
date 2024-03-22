package nl.vng.diwi.dal;

import nl.vng.diwi.dal.entities.Houseblock;
import nl.vng.diwi.dal.entities.HouseblockSnapshotSqlModel;
import nl.vng.diwi.dal.entities.ProjectHouseblockCustomPropertySqlModel;
import org.hibernate.Session;
import org.hibernate.query.SelectionQuery;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class HouseblockDAO extends AbstractRepository {

    public HouseblockDAO(Session session) {
        super(session);
    }

    public HouseblockSnapshotSqlModel getHouseblockByUuid(UUID houseblockUuid) {
        return session.createNativeQuery(
                "SELECT * FROM get_houseblock_snapshots(null, :houseblockUuid, :now) " , HouseblockSnapshotSqlModel.class)
            .setParameter("now", LocalDate.now())
            .setParameter("houseblockUuid", houseblockUuid)
            .getSingleResultOrNull();
    }

    public List<HouseblockSnapshotSqlModel> getHouseblocksByProjectUuid(UUID projectUuid) {
        return session.createNativeQuery(
                "SELECT * FROM get_houseblock_snapshots(:projectUuid, null, :now) " , HouseblockSnapshotSqlModel.class)
            .setParameter("now", LocalDate.now())
            .setParameter("projectUuid", projectUuid)
            .list();
    }

    public Houseblock getCurrentHouseblock(UUID houseblockId) {
        session.enableFilter(GenericRepository.CURRENT_DATA_FILTER);
        String statement = "FROM Houseblock H WHERE H.id = :uuid";
        SelectionQuery<Houseblock> query = session
            .createSelectionQuery(statement, Houseblock.class)
            .setParameter("uuid", houseblockId);
        return query.getSingleResultOrNull();
    }

    public List<ProjectHouseblockCustomPropertySqlModel> getHouseblockCustomProperties(UUID houseblockUuid) {
        List<ProjectHouseblockCustomPropertySqlModel> result = session.createNativeQuery(
                "SELECT * FROM get_houseblock_custom_properties(:houseblockUuid, :now) " , ProjectHouseblockCustomPropertySqlModel.class)
            .setParameter("now", LocalDate.now())
            .setParameter("houseblockUuid", houseblockUuid)
            .list();

        return result;
    }
}
