package nl.vng.diwi.dal;

import nl.vng.diwi.models.OrganizationUserModel;
import org.hibernate.Session;

import java.util.List;
import java.util.UUID;

public class OrganizationsDAO extends AbstractRepository {

    public OrganizationsDAO(Session session) {
        super(session);
    }

    public List<OrganizationUserModel> getOrganizationUsersList() {

        return session.createNativeQuery(String.format("""
                SELECT o.id AS organizationUuid,
                    os.naam AS organizationName,
                    us.id AS uuid,
                    us.last_name AS lastName,
                    us.first_name AS firstName,
                    LEFT(us.last_name, 1) || LEFT(us.first_name,1) AS initials
                FROM %1$s.organization o
                    JOIN %1$s.organization_state os ON o.id = os.organization_id AND os.change_end_date IS NULL
                    JOIN %1$s.user_to_organization uo ON uo.organization_id = os.organization_id AND uo.change_end_date IS NULL
                    JOIN %1$s.user_state us ON us.user_id = uo.user_id AND us.change_end_date IS NULL
                ORDER BY organizationName, initials, lastName, firstName """, GenericRepository.VNG_SCHEMA_NAME), Object[].class)
            .setTupleTransformer(new BeanTransformer<>(OrganizationUserModel.class))
            .list();

    }

    public UUID findOrganizationForProject(UUID projectUuid, UUID organizationUuid) {
        return session.createNativeQuery(String.format("""
                SELECT id
                    FROM %1$s.organization_to_project
                WHERE project_id = :projectId
                    AND organization_id = :organizationId
                    AND change_end_date IS NULL""", GenericRepository.VNG_SCHEMA_NAME), UUID.class)
            .setParameter("projectId", projectUuid)
            .setParameter("organizationId", organizationUuid)
            .getSingleResultOrNull();
    }

    public void addOrganizationToProject(UUID projectUuid, UUID organizationToAdd, UUID loggedInUserUuid) {
        session.createNativeMutationQuery(String.format("""
                INSERT INTO %1$s.organization_to_project (id, organization_id, project_id, change_start_date, create_user_id)
                    VALUES (:id, :organizationId, :projectId, NOW(), :createUserId) """, GenericRepository.VNG_SCHEMA_NAME))
            .setParameter("id", CustomUuidGenerator.generateUUIDv7())
            .setParameter("organizationId", organizationToAdd)
            .setParameter("projectId", projectUuid)
            .setParameter("createUserId", loggedInUserUuid)
            .executeUpdate();
    }

    public void removeOrganizationFromProject(UUID projectUuid, UUID organizationToRemove, UUID loggedInUserUuid) {
        session.createNativeMutationQuery(String.format("""
                UPDATE %1$s.organization_to_project
                    SET change_end_date = NOW(),
                    change_user_id = :changeUserId
                WHERE organization_id = :organizationId
                    AND project_id = :projectId
                    AND change_end_date IS NULL """, GenericRepository.VNG_SCHEMA_NAME))
            .setParameter("organizationId", organizationToRemove)
            .setParameter("projectId", projectUuid)
            .setParameter("changeUserId", loggedInUserUuid)
            .executeUpdate();
    }
}
