package nl.vng.diwi.dal.entities;

import org.hibernate.Session;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nl.vng.diwi.dal.GenericRepository;
import nl.vng.diwi.dal.entities.superclasses.HouseblockMilestoneChangeDataSuperclass;

@Entity
@Table(name = "woningblok_naam_changelog", schema = GenericRepository.VNG_SCHEMA_NAME)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HouseblockNameChangelog extends HouseblockMilestoneChangeDataSuperclass {

    @Column(name = "naam")
    private String name;

    @Override
    public Object getCopyWithoutMilestones(Session session) {
        var newChangelog = HouseblockNameChangelog.builder()
            .name(name).build();
        newChangelog.setHouseblock(getHouseblock());
        newChangelog.setStartMilestone(getStartMilestone());
        newChangelog.setEndMilestone(getEndMilestone());
        return newChangelog;
    }
}
