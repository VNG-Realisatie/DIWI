package nl.vng.diwi.dal;

import nl.vng.diwi.dal.entities.enums.ProjectRole;
import nl.vng.diwi.models.OrganizationUserModel;
import org.hibernate.Session;

import java.util.List;
import java.util.UUID;

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

    public UUID findOrganizationForProject(UUID projectUuid, UUID organizationUuid, ProjectRole projectRole) {
        return session.createNativeQuery("""
                SELECT id
                    FROM diwi_testset.organization_to_project
                WHERE project_id = :projectId
                    AND organization_id = :organizationId
                    AND project_rol = CAST(:projectRole AS diwi_testset.project_rol)
                    AND change_end_date IS NULL""", UUID.class)
            .setParameter("projectId", projectUuid)
            .setParameter("organizationId", organizationUuid)
            .setParameter("projectRole", projectRole.name())
            .getSingleResultOrNull();
    }

    public void addOrganizationToProject(UUID projectUuid, UUID organizationToAdd, ProjectRole projectRole, UUID loggedInUserUuid) {
        session.createNativeMutationQuery("""
                INSERT INTO diwi_testset.organization_to_project (id, organization_id, project_id, project_rol, change_start_date, change_user_id)
                    VALUES (:id, :organizationId, :projectId, CAST(:projectRole AS diwi_testset.project_rol), NOW(), :changeUserId) """)
            .setParameter("id", CustomUuidGenerator.generateUUIDv7())
            .setParameter("organizationId", organizationToAdd)
            .setParameter("projectId", projectUuid)
            .setParameter("projectRole", projectRole.name())
            .setParameter("changeUserId", loggedInUserUuid)
            .executeUpdate();
    }

    public void removeOrganizationFromProject(UUID projectUuid, UUID organizationToRemove, ProjectRole projectRole) {
        session.createNativeMutationQuery("""
                UPDATE diwi_testset.organization_to_project
                    SET change_end_date = NOW()
                WHERE organization_id = :organizationId
                    AND project_id = :projectId
                    AND project_rol = CAST(:projectRole AS diwi_testset.project_rol)
                    AND change_end_date IS NULL """)
            .setParameter("organizationId", organizationToRemove)
            .setParameter("projectId", projectUuid)
            .setParameter("projectRole", projectRole.name())
            .executeUpdate();
    }
}
