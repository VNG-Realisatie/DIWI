package nl.vng.diwi.dal.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nl.vng.diwi.dal.GenericRepository;
import nl.vng.diwi.dal.entities.superclasses.MilestoneChangeDataSuperclass;

import java.util.List;

@Entity
@Table(name = "woningblok_type_en_fysiek_changelog", schema = GenericRepository.VNG_SCHEMA_NAME)
@Getter
@Setter
@NoArgsConstructor
public class HouseblockAppearanceAndTypeChangelog extends MilestoneChangeDataSuperclass {

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "woningblok_id")
    private Houseblock houseblock;

    @OneToMany(mappedBy="appearanceAndTypeChangelog", fetch = FetchType.EAGER)
    private List<HouseblockPhysicalAppearanceChangelogValue> physicalAppearanceValues;

    @OneToMany(mappedBy="appearanceAndTypeChangelog", fetch = FetchType.EAGER)
    private List<HouseblockHouseTypeChangelogValue> houseblockHouseTypeValues;

}
