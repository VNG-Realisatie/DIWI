package nl.vng.diwi.dal;

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

    public PropertyModel getPropertyById(UUID customPropertyUuid) {

        return session.createNativeQuery("SELECT * FROM get_property_definitions(:customPropertyUuid, null, null, null) ", PropertyModel.class)
            .setParameter("customPropertyUuid", customPropertyUuid)
            .getSingleResultOrNull();

    }

    public List<PropertyState> getActivePropertyStateByName(String name) {
        return session.createQuery("FROM PropertyState cps WHERE cps.propertyName = :name AND cps.changeEndDate IS NULL", PropertyState.class)
            .setParameter("name", name)
            .list();
    }
}
