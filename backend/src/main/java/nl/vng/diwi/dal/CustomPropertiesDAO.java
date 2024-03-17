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

        return session.createNativeQuery("SELECT * FROM get_customproperty_definitions(null, :objectType) ", CustomPropertyModel.class)
            .setParameter("objectType", objectType == null ? null : objectType.name())
            .list();
    }

    public CustomPropertyModel getCustomProperyById(UUID customPropertyUuid) {

        return session.createNativeQuery("SELECT * FROM get_customproperty_definitions(:customPropertyUuid, null) ", CustomPropertyModel.class)
            .setParameter("customPropertyUuid", customPropertyUuid)
            .getSingleResultOrNull();

    }

}
