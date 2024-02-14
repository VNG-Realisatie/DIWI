package nl.vng.diwi.dal;

import java.util.UUID;

import nl.vng.diwi.dal.entities.Milestone;
import org.hibernate.Session;
import org.hibernate.query.SelectionQuery;

import jakarta.annotation.Nullable;
import lombok.NonNull;

public class MilestoneDAO extends AbstractRepository {

    public MilestoneDAO(Session session) {
        super(session);
    }

    @Nullable
    public Milestone getCurrentData(@NonNull UUID milestoneUuid) {
        session.enableFilter("current");
        String statement = "FROM Milestone M WHERE M.id = :uuid";
        SelectionQuery<Milestone> query = session
            .createSelectionQuery(statement, Milestone.class)
            .setParameter("uuid", milestoneUuid);
        return query.getSingleResultOrNull();
    }
}
