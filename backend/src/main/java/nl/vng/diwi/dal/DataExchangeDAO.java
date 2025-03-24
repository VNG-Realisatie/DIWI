package nl.vng.diwi.dal;

import nl.vng.diwi.dal.entities.DataExchangeOptionState;
import nl.vng.diwi.dal.entities.DataExchangePriceCategoryMapping;
import nl.vng.diwi.dal.entities.DataExchangePriceCategoryMappingState;
import nl.vng.diwi.dal.entities.DataExchangePropertySqlModel;
import nl.vng.diwi.dal.entities.DataExchangePropertyState;
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

    public DataExchangePropertyState getActiveDataExchangePropertyStateByDataExchangePropertyUuid(UUID dataExchangePropertyUuid) {
        return session.createQuery("FROM DataExchangePropertyState deps WHERE deps.dataExchangeProperty.id = :dataExchangePropertyUuid AND deps.changeEndDate IS NULL",
                DataExchangePropertyState.class)
            .setParameter("dataExchangePropertyUuid", dataExchangePropertyUuid)
            .getSingleResultOrNull();
    }

    public List<DataExchangeOptionState> getActiveDataExchangeOptionsStatesByDataExchangeOptionUuid(UUID dataExchangeOptionUuid) {
        return session.createQuery("FROM DataExchangeOptionState deos WHERE deos.dataExchangeOption.id = :dataExchangeOptionUuid AND deos.changeEndDate IS NULL",
                DataExchangeOptionState.class)
            .setParameter("dataExchangeOptionUuid", dataExchangeOptionUuid)
            .list();
    }

    public List<DataExchangePropertySqlModel> getDataExchangeProperties(UUID dataExchangeId) {
        return session.createNativeQuery(String.format("""
            SELECT
                dep.id                          AS id,
                dep.data_exchange_property_name AS name,
                dep.object_type                 AS objectType,
                dep.property_type               AS propertyTypes,
                dep.mandatory                   AS mandatory,
                dep.single_select               AS singleSelect,
                deps.property_id                AS customPropertyId,
                to_jsonb(array_agg(jsonb_build_object('id', deo.id, 'name', deo.data_exchange_option_name,
                        'propertyCategoryValueId', deos.property_category_value_id, 'propertyOrdinalValueId', deos.property_ordinal_value_id))
                    FILTER (WHERE deo.id IS NOT NULL)) AS options
            FROM diwi.data_exchange_property dep
                LEFT JOIN diwi.data_exchange_property_state deps ON dep.id = deps.data_exchange_property_id AND deps.change_end_date IS NULL
                LEFT JOIN diwi.data_exchange_option deo ON dep.id = deo.data_exchange_property_id
                LEFT JOIN diwi.data_exchange_option_state deos ON deo.id = deos.data_exchange_option_id AND deos.change_end_date IS NULL
            WHERE data_exchange_id = :dataExchangeId
                    GROUP BY dep.id, dep.data_exchange_property_name, dep.mandatory, deps.property_id
            ORDER BY dep.data_exchange_property_name """,
                GenericRepository.VNG_SCHEMA_NAME), DataExchangePropertySqlModel.class)
            .setParameter("dataExchangeId", dataExchangeId)
            .getResultList();
    }

    public List<DataExchangePriceCategoryMappingState> getDataExchangePriceMappings(UUID dataExchangeId) {
        return session.createQuery("""
                FROM DataExchangePriceCategoryMappingState
                WHERE changeEndDate IS NULL
                  AND dataExchangePriceCategoryMapping.dataExchange.id = :dataExchangeId
                """, DataExchangePriceCategoryMappingState.class)
                .setParameter("dataExchangeId", dataExchangeId)
                .list();
    }
}
