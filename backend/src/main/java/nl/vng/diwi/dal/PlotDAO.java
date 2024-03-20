package nl.vng.diwi.dal;

import java.util.List;
import java.util.UUID;

import org.hibernate.Session;

import nl.vng.diwi.dal.entities.ProjectRegistryLinkChangelogValue;

public class PlotDAO extends AbstractRepository {

    public PlotDAO(Session session) {
        super(session);
    }

    public List<ProjectRegistryLinkChangelogValue> getPlots(UUID projectUuid) {
        return session
                .createQuery("""
                FROM ProjectRegistryLinkChangelogValue v
                WHERE
                        v.projectRegistryLinkChangelog.project.id = :projectUuid
                        AND v.projectRegistryLinkChangelog.changeEndDate is null""",
                        ProjectRegistryLinkChangelogValue.class)
                .setParameter("projectUuid", projectUuid)
                .list();
    }
}
