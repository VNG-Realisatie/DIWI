package nl.vng.diwi.dal;

import nl.vng.diwi.dal.entities.HouseblockSnapshotSqlModel;
import org.hibernate.Session;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class HouseblockDAO extends AbstractRepository {

    public HouseblockDAO(Session session) {
        super(session);
    }

    public HouseblockSnapshotSqlModel getHouseblockByUuid(UUID houseblockUuid) {
        return session.createNativeQuery(
                "SELECT * FROM get_active_or_future_houseblock_snapshots(null, :houseblockUuid, :now) " , HouseblockSnapshotSqlModel.class)
            .setParameter("now", LocalDate.now())
            .setParameter("houseblockUuid", houseblockUuid)
            .getSingleResultOrNull();
    }

    public List<HouseblockSnapshotSqlModel> getHouseblocksByProjectUuid(UUID projectUuid) {
        return session.createNativeQuery(
                "SELECT * FROM get_active_or_future_houseblock_snapshots(:projectUuid, null, :now) " , HouseblockSnapshotSqlModel.class)
            .setParameter("now", LocalDate.now())
            .setParameter("projectUuid", projectUuid)
            .list();
    }
}
