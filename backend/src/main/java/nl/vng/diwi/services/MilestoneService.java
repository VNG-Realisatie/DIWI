package nl.vng.diwi.services;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.poi.ss.formula.functions.T;

import nl.vng.diwi.dal.VngRepository;
import nl.vng.diwi.dal.entities.Milestone;
import nl.vng.diwi.dal.entities.User;
import nl.vng.diwi.dal.entities.superclasses.MilestoneChangeDataSuperclass;
import nl.vng.diwi.models.MilestoneModel;

public class MilestoneService {

    public MilestoneService() {
    }

    public Milestone getCurrentMilestone(VngRepository repo, UUID milestoneUuid) {
        return repo.getMilestoneDAO().getCurrentMilestone(milestoneUuid);
    }

    public <T extends MilestoneChangeDataSuperclass> void replaceChangelogsWithSingleChangelog(
            VngRepository repo,
            ZonedDateTime zdtNow,
            User loggedUser,
            Milestone startMilestone,
            Milestone endMilestone,
            List<T> changelogs) {
        if (changelogs.isEmpty()){
            return;
        }

        var orderedChangelogs = changelogs
                .stream()
                .map(n -> Pair.of(n, new MilestoneModel(n.getStartMilestone()).getDate()))
                .sorted((a, b) -> a.getRight().compareTo(b.getRight()))
                .toList();

        var lastName = orderedChangelogs.get(orderedChangelogs.size() - 1).getLeft();
        for (var nameChangelog : orderedChangelogs) {
            T changelog = nameChangelog.getLeft();
            changelog.setChangeEndDate(zdtNow);
            changelog.setChangeUser(loggedUser);
            repo.persist(changelog);
        }
        var newChangelog = (T) lastName.getCopyWithoutMilestones(repo.getSession());
        newChangelog.setStartMilestone(startMilestone);
        newChangelog.setEndMilestone(endMilestone);
        newChangelog.setCreateUser(loggedUser);
        newChangelog.setChangeStartDate(zdtNow);

        repo.persist(newChangelog);
        newChangelog.persistValues(repo.getSession());

    }
}
