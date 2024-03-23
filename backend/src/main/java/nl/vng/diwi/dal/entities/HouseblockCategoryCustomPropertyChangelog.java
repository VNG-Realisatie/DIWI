package nl.vng.diwi.dal.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nl.vng.diwi.dal.GenericRepository;
import nl.vng.diwi.dal.entities.superclasses.HouseblockMilestoneChangeDataSuperclass;

import java.util.List;

@Entity
@Table(name = "woningblok_maatwerk_categorie_changelog", schema = GenericRepository.VNG_SCHEMA_NAME)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HouseblockCategoryCustomPropertyChangelog extends HouseblockMilestoneChangeDataSuperclass {

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "eigenschap_id")
    private CustomProperty customProperty;

    @OneToMany(mappedBy="categoryChangelog", fetch = FetchType.LAZY)
    private List<HouseblockCategoryCustomPropertyChangelogValue> changelogCategoryValues;

    @Override
    public Object getShallowCopy() {
        var newChangelog = HouseblockCategoryCustomPropertyChangelog.builder().customProperty(customProperty).build();
        newChangelog.setHouseblock(getHouseblock());
        newChangelog.setStartMilestone(getStartMilestone());
        newChangelog.setEndMilestone(getEndMilestone());
        return newChangelog;
    }
}
