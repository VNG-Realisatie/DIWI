package nl.vng.diwi.dal;

import nl.vng.diwi.dal.entities.DataExchangeState;
import org.hibernate.Session;

import java.util.List;
import java.util.UUID;

public class DataExchangeDAO extends AbstractRepository {

    public DataExchangeDAO(Session session) {
        super(session);
    }

    public DataExchangeState getActiveDataExchangeStateByDataExchangeUuid(UUID dataExchangeUuid) {
        return session.createQuery("FROM DataExchangeState des WHERE des.dataExchange.id = :dataExchangeUuid AND des.changeEndDate IS NULL", DataExchangeState.class)
            .setParameter("dataExchangeUuid", dataExchangeUuid)
            .getSingleResultOrNull();
    }

    public List<DataExchangeState> getActiveDataExchangeStates() {
        return session.createQuery("FROM DataExchangeState des WHERE des.changeEndDate IS NULL ORDER BY des.name", DataExchangeState.class)
            .list();
    }
}
