package nl.vng.diwi.dal;

import nl.vng.diwi.dal.entities.PropertyCategoryValueState;
import nl.vng.diwi.dal.entities.PropertyState;
import nl.vng.diwi.dal.entities.enums.ObjectType;
import nl.vng.diwi.dal.entities.enums.PropertyKind;
import nl.vng.diwi.models.PropertyModel;
import org.hibernate.Session;

import java.util.List;
import java.util.UUID;

public class PropertyDAO extends AbstractRepository {

    public PropertyDAO(Session session) {
        super(session);
    }

    public List<PropertyModel> getPropertiesList(ObjectType objectType, Boolean disabled, PropertyKind type) {

        return session.createNativeQuery("SELECT * FROM get_property_definitions(null, :objectType, :disabled, :type) ", PropertyModel.class)
            .setParameter("objectType", objectType == null ? null : objectType.name())
            .setParameter("disabled", disabled)
            .setParameter("type", type == null ? null : type.name())
            .list();
    }

    public PropertyModel getPropertyById(UUID propertyUuid) {

        return session.createNativeQuery("SELECT * FROM get_property_definitions(:propertyUuid, null, null, null) ", PropertyModel.class)
            .setParameter("propertyUuid", propertyUuid)
            .getSingleResultOrNull();

    }

    public PropertyState getActivePropertyStateByName(String name) {
        return session.createQuery("FROM PropertyState cps WHERE cps.propertyName = :name AND cps.changeEndDate IS NULL", PropertyState.class)
            .setParameter("name", name)
            .getSingleResult();
    }

    public List<PropertyCategoryValueState> getCategoryStatesByPropertyName(String propertyName) {
        return session.createNativeQuery(String.format("""
            SELECT cs.* FROM %1$s.property_category_value_state cs
                JOIN %1$s.property_category_value c ON cs.category_value_id = c.id
                JOIN %1$s.property_state ps ON c.property_id = ps.property_id AND ps.change_end_date IS NULL
                WHERE cs.change_end_date IS NULL AND ps.property_name = :propertyName""", GenericRepository.VNG_SCHEMA_NAME), PropertyCategoryValueState.class)
            .setParameter("propertyName", propertyName)
            .list();
    }
}
