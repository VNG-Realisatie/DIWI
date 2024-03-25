package nl.vng.diwi.dal.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nl.vng.diwi.dal.GenericRepository;
import nl.vng.diwi.dal.entities.superclasses.HouseblockMilestoneChangeDataSuperclass;

@Entity
@Table(name = "woningblok_duration_changelog", schema = GenericRepository.VNG_SCHEMA_NAME)
@Getter
@Setter
@NoArgsConstructor
public class HouseblockDurationChangelog extends HouseblockMilestoneChangeDataSuperclass {

    @Override
    public Object getShallowCopy() {
        var newChangelog = new HouseblockDurationChangelog();
        newChangelog.setHouseblock(getHouseblock());
        newChangelog.setStartMilestone(getStartMilestone());
        newChangelog.setEndMilestone(getEndMilestone());
        return newChangelog;
    }
}
