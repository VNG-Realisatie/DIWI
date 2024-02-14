package com.vng.dal;

import java.util.UUID;

import org.hibernate.Session;
import org.hibernate.query.SelectionQuery;

import com.vng.dal.entities.Milestone;

import jakarta.annotation.Nullable;
import lombok.NonNull;

public class MilestoneRepository extends AbstractRepository {

    public MilestoneRepository(Session session) {
        super(session);
    }

    @Nullable
    public Milestone getCurrentData(@NonNull UUID milestoneUuid) {
        session.enableFilter("current");
        String statement = "FROM Milestone M WHERE M.id = :uuid";
        SelectionQuery<Milestone> query = session
                .createSelectionQuery(statement, Milestone.class)
                .setParameter("uuid", milestoneUuid)
                ;
        return query.getSingleResultOrNull();
    }
}
