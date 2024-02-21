package nl.vng.diwi.dal;

import nl.vng.diwi.models.OrganizationUserModel;
import org.hibernate.Session;

import java.util.List;

public class OrganizationsDAO extends AbstractRepository {

    public OrganizationsDAO(Session session) {
        super(session);
    }

    public List<OrganizationUserModel> getOrganizationUsersList() {

        return session.createNativeQuery("""
                SELECT o.id AS organizationUuid,
                    os.naam AS organizationName,
                    us.id AS uuid,
                    us.last_name AS lastName,
                    us.first_name AS firstName,
                    LEFT(us.last_name, 1) || LEFT(us.first_name,1) AS initials
                FROM diwi_testset.organization o
                    JOIN diwi_testset.organization_state os ON o.id = os.organization_id AND os.change_end_date IS NULL
                    JOIN diwi_testset.user_to_organization uo ON uo.organization_id = os.organization_id AND uo.change_end_date IS NULL
                    JOIN diwi_testset.user_state us ON us.user_id = uo.user_id AND us.change_end_date IS NULL
                ORDER BY organizationName, initials, lastName, firstName """, Object[].class)
            .setTupleTransformer(new BeanTransformer<>(OrganizationUserModel.class))
            .list();

    }
}
