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

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "woningblok_doelgroep_changelog", schema = GenericRepository.VNG_SCHEMA_NAME)
@Getter
@Setter
@NoArgsConstructor
public class HouseblockPurposeChangelog extends HouseblockMilestoneChangeDataSuperclass {

    @OneToMany(mappedBy="purposeChangelog", fetch = FetchType.EAGER)
    private List<HouseblockPurposeChangelogValue> purposeValues = new ArrayList<>();

    @Override
    public Object getShallowCopy() {
        var newChangelog = new HouseblockPurposeChangelog();
        newChangelog.setHouseblock(getHouseblock());
        newChangelog.setStartMilestone(getStartMilestone());
        newChangelog.setEndMilestone(getEndMilestone());
        return newChangelog;
    }
}
