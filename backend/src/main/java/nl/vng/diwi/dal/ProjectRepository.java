package nl.vng.diwi.dal;

import java.util.UUID;

import org.hibernate.Session;
import org.hibernate.query.SelectionQuery;

import nl.vng.diwi.dal.entities.Project;

import lombok.NonNull;

public class ProjectRepository  extends AbstractRepository {
    public ProjectRepository(Session session) {
        super(session);
    }
    
    public Project getCurrentState(@NonNull UUID projectUuid) {
        session.enableFilter("current");
        String statement = "FROM Project P WHERE P.id = :uuid";
        SelectionQuery<Project> query = session
                .createSelectionQuery(statement, Project.class)
                .setParameter("uuid", projectUuid)
                ;
        return query.getSingleResultOrNull();
    }
}
