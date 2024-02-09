package com.vng.dal;

import java.util.UUID;

import org.hibernate.Session;
import org.hibernate.query.SelectionQuery;

import com.vng.dal.entities.MilestoneState;

import jakarta.annotation.Nullable;
import lombok.NonNull;

public class MilestoneRepository extends AbstractRepository {

    public MilestoneRepository(Session session) {
        super(session);
    }

    @Nullable
    public MilestoneState getCurrentState(@NonNull UUID milestoneUuid) {
        String statement = "FROM MilestoneState M WHERE M.changeEndDate IS NULL AND M.milestone.id = :uuid";
        SelectionQuery<MilestoneState> query = session
                .createSelectionQuery(statement, MilestoneState.class)
                .setParameter("uuid", milestoneUuid)
                ;
        return query.getSingleResultOrNull();
    }
}
