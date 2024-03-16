package nl.vng.diwi.dal;

import nl.vng.diwi.dal.entities.enums.ObjectType;
import nl.vng.diwi.models.CustomPropertyModel;
import org.hibernate.Session;

import java.util.List;
import java.util.UUID;

public class CustomPropertiesDAO extends AbstractRepository {

    public CustomPropertiesDAO(Session session) {
        super(session);
    }

    public List<CustomPropertyModel> getCustomProperiesList(ObjectType objectType) {

        //TODO: include categories... only active ones? or also disabled with a flag.. ? Also, ordinalValues
        return session.createNativeQuery(String.format("""
                    SELECT cp.id AS id,
                           cpState.eigenschap_naam AS name,
                           cpState.eigenschap_object_soort AS objectType,
                           cpState.eigenschap_type AS propertyType,
                           CASE WHEN cpState.change_end_date IS NULL THEN false ELSE TRUE END AS disabled
                            FROM %1$s.maatwerk_eigenschap cp
                            LEFT JOIN LATERAL (
                                    SELECT * FROM %1$s.maatwerk_eigenschap_state cps
                                            WHERE cps.eigenschap_id = cp.id
                                    ORDER BY cps.change_start_date DESC
                                    LIMIT 1) cpState ON TRUE;
                """, GenericRepository.VNG_SCHEMA_NAME), Object[].class)
            .setTupleTransformer(new BeanTransformer<>(CustomPropertyModel.class))
            .list();
        //TODO: use objectType in where clause, if present

    }

    public CustomPropertyModel getCustomProperyById(UUID customPropertyUuid) {

        //TODO: include categories... only active ones? or also disabled with a flag.. ? Also, ordinalValues
        return session.createNativeQuery(String.format("""
                    SELECT cp.id AS id,
                           cpState.eigenschap_naam AS name,
                           cpState.eigenschap_object_soort AS objectType,
                           cpState.eigenschap_type AS propertyType,
                           CASE WHEN cpState.change_end_date IS NULL THEN false ELSE TRUE END AS disabled
                            FROM %1$s.maatwerk_eigenschap cp
                            LEFT JOIN LATERAL (
                                    SELECT * FROM %1$s.maatwerk_eigenschap_state cps
                                            WHERE cps.eigenschap_id = cp.id
                                    ORDER BY cps.change_start_date DESC
                                    LIMIT 1) cpState ON TRUE
                     WHERE cp.id = :customPropertyUuid ;
                """, GenericRepository.VNG_SCHEMA_NAME), Object[].class)
            .setTupleTransformer(new BeanTransformer<>(CustomPropertyModel.class))
            .setParameter("customPropertyUuid", customPropertyUuid)
            .getSingleResultOrNull();

    }

}
