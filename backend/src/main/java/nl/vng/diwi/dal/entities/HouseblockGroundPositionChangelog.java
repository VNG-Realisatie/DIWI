package nl.vng.diwi.dal.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nl.vng.diwi.dal.GenericRepository;
import nl.vng.diwi.dal.entities.superclasses.HouseblockMilestoneChangeDataSuperclass;

import java.util.List;

import org.hibernate.Session;

@Entity
@Table(name = "woningblok_grondpositie_changelog", schema = GenericRepository.VNG_SCHEMA_NAME)
@Getter
@Setter
@NoArgsConstructor
public class HouseblockGroundPositionChangelog extends HouseblockMilestoneChangeDataSuperclass {

    @OneToMany(mappedBy="groundPositionChangelog", fetch = FetchType.EAGER)
    private List<HouseblockGroundPositionChangelogValue> values;

    @Override
    public Object getCopyWithoutMilestones(Session session) {
        var newChangelog = new HouseblockGroundPositionChangelog();
        newChangelog.setHouseblock(getHouseblock());
        newChangelog.setStartMilestone(getStartMilestone());
        newChangelog.setEndMilestone(getEndMilestone());
        return newChangelog;
    }
}
