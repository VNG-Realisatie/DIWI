package nl.vng.diwi.dal;

import nl.vng.diwi.dal.entities.UserGroupState;
import nl.vng.diwi.models.UserGroupUserModel;
import org.hibernate.Session;

import java.util.List;
import java.util.UUID;

public class UserGroupDAO extends AbstractRepository {

    public UserGroupDAO(Session session) {
        super(session);
    }

    public List<UserGroupUserModel> getUserGroupUsersList(boolean includeSingleUser) {

        String querySql = String.format("""
                SELECT ug.id AS userGroupUuid,
                    ugs.naam AS userGroupName,
                    us.id AS uuid,
                    us.last_name AS lastName,
                    us.first_name AS firstName,
                    LEFT(us.last_name, 1) || LEFT(us.first_name,1) AS initials
                FROM %1$s.userGroup ug
                    JOIN %1$s.usergroup_state ugs ON ug.id = ugs.usergroup_id AND ugs.change_end_date IS NULL
                    JOIN %1$s.user_to_usergroup utug ON utug.usergroup_id = ugs.usergroup_id AND utug.change_end_date IS NULL
                    JOIN %1$s.user_state us ON us.user_id = utug.user_id AND us.change_end_date IS NULL """, GenericRepository.VNG_SCHEMA_NAME) +
                (includeSingleUser ? "" : " WHERE ug.single_user = false ") +
                " ORDER BY userGroupName, initials, lastName, firstName ";

        return session.createNativeQuery(querySql, Object[].class)
            .setTupleTransformer(new BeanTransformer<>(UserGroupUserModel.class))
            .list();

    }

    public List<UserGroupUserModel> getUserGroupUsers(UUID groupId) {

        return session.createNativeQuery(String.format("""
                SELECT ug.id AS userGroupUuid,
                    ugs.naam AS userGroupName,
                    us.id AS uuid,
                    us.last_name AS lastName,
                    us.first_name AS firstName,
                    LEFT(us.last_name, 1) || LEFT(us.first_name,1) AS initials
                FROM %1$s.userGroup ug
                    JOIN %1$s.usergroup_state ugs ON ug.id = ugs.usergroup_id AND ugs.change_end_date IS NULL
                    JOIN %1$s.user_to_usergroup utug ON utug.usergroup_id = ugs.usergroup_id AND utug.change_end_date IS NULL
                    JOIN %1$s.user_state us ON us.user_id = utug.user_id AND us.change_end_date IS NULL
                WHERE ug.id = :groupId """, GenericRepository.VNG_SCHEMA_NAME), Object[].class)
            .setTupleTransformer(new BeanTransformer<>(UserGroupUserModel.class))
            .setParameter("groupId", groupId)
            .list();

    }

    public UUID findUserGroupForProject(UUID projectUuid, UUID groupUuid) {
        return session.createNativeQuery(String.format("""
                SELECT id
                    FROM %1$s.usergroup_to_project
                WHERE project_id = :projectId
                    AND usergroup_id = :groupId
                    AND change_end_date IS NULL""", GenericRepository.VNG_SCHEMA_NAME), UUID.class)
            .setParameter("projectId", projectUuid)
            .setParameter("groupId", groupUuid)
            .getSingleResultOrNull();
    }

    public void addUserGroupToProject(UUID projectUuid, UUID groupToAdd, UUID loggedInUserUuid) {
        session.createNativeMutationQuery(String.format("""
                INSERT INTO %1$s.usergroup_to_project (id, usergroup_id, project_id, change_start_date, create_user_id)
                    VALUES (:id, :groupId, :projectId, NOW(), :createUserId) """, GenericRepository.VNG_SCHEMA_NAME))
            .setParameter("id", CustomUuidGenerator.generateUUIDv7())
            .setParameter("groupId", groupToAdd)
            .setParameter("projectId", projectUuid)
            .setParameter("createUserId", loggedInUserUuid)
            .executeUpdate();
    }

    public void removeUserGroupFromProject(UUID projectUuid, UUID groupToRemove, UUID loggedInUserUuid) {
        session.createNativeMutationQuery(String.format("""
                UPDATE %1$s.usergroup_to_project
                    SET change_end_date = NOW(),
                    change_user_id = :changeUserId
                WHERE usergroup_id = :groupId
                    AND project_id = :projectId
                    AND change_end_date IS NULL """, GenericRepository.VNG_SCHEMA_NAME))
            .setParameter("groupId", groupToRemove)
            .setParameter("projectId", projectUuid)
            .setParameter("changeUserId", loggedInUserUuid)
            .executeUpdate();
    }

    public List<UserGroupState> findActiveUserGroupStateByName(String userGroupName) {
        return session.createQuery("FROM UserGroupState ugs WHERE ugs.name = :userGroupName AND ugs.changeEndDate IS NULL", UserGroupState.class)
            .setParameter("userGroupName", userGroupName)
            .list();
    }
}
