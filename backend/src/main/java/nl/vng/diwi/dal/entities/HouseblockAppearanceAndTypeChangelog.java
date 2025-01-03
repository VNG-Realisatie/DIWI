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
@Table(name = "woningblok_type_en_fysiek_changelog", schema = GenericRepository.VNG_SCHEMA_NAME)
@Getter
@Setter
@NoArgsConstructor
public class HouseblockAppearanceAndTypeChangelog extends HouseblockMilestoneChangeDataSuperclass {

    @OneToMany(mappedBy="appearanceAndTypeChangelog", fetch = FetchType.EAGER)
    private List<HouseblockPhysicalAppearanceChangelogValue> physicalAppearanceValues;

    @OneToMany(mappedBy="appearanceAndTypeChangelog", fetch = FetchType.EAGER)
    private List<HouseblockHouseTypeChangelogValue> houseblockHouseTypeValues;

    @Override
    public Object getCopyWithoutMilestones(Session session) {
        var newChangelog = new HouseblockAppearanceAndTypeChangelog();
        newChangelog.setHouseblock(getHouseblock());
        newChangelog.setStartMilestone(getStartMilestone());
        newChangelog.setEndMilestone(getEndMilestone());
        return newChangelog;
    }
}
