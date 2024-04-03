package nl.vng.diwi.dal;

import nl.vng.diwi.dal.entities.CustomPropertyState;
import nl.vng.diwi.dal.entities.enums.ObjectType;
import nl.vng.diwi.models.CustomPropertyModel;
import org.hibernate.Session;

import java.util.List;
import java.util.UUID;

public class CustomPropertiesDAO extends AbstractRepository {

    public CustomPropertiesDAO(Session session) {
        super(session);
    }

    public List<CustomPropertyModel> getCustomPropertiesList(ObjectType objectType, Boolean disabled) {

        return session.createNativeQuery("SELECT * FROM get_customproperty_definitions(null, :objectType, :disabled) ", CustomPropertyModel.class)
            .setParameter("objectType", objectType == null ? null : objectType.name())
            .setParameter("disabled", disabled)
            .list();
    }

    public CustomPropertyModel getCustomPropertyById(UUID customPropertyUuid) {

        return session.createNativeQuery("SELECT * FROM get_customproperty_definitions(:customPropertyUuid, null, null) ", CustomPropertyModel.class)
            .setParameter("customPropertyUuid", customPropertyUuid)
            .getSingleResultOrNull();

    }

    public List<CustomPropertyState> getActiveCustomPropertyStateByName(String name) {
        return session.createQuery("FROM CustomPropertyState cps WHERE cps.propertyName = :name AND cps.changeEndDate IS NULL", CustomPropertyState.class)
            .setParameter("name", name)
            .list();
    }
}
