package nl.vng.diwi.dal.entities;

import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import nl.vng.diwi.dal.GenericRepository;
import nl.vng.diwi.dal.entities.superclasses.IdSuperclass;

import org.hibernate.annotations.Filter;

import lombok.NoArgsConstructor;

@Entity
@Table(name = "woningblok", schema = GenericRepository.VNG_SCHEMA_NAME)
@Getter
@Setter
@NoArgsConstructor
public class Houseblock extends IdSuperclass {

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "project_id")
    private Project project;

    @OneToMany(mappedBy= "houseblock", fetch = FetchType.LAZY)
    @Filter(name = GenericRepository.CURRENT_DATA_FILTER, condition = "change_end_date IS NULL")
    private List<HouseblockDurationChangelog> duration;

    @OneToMany(mappedBy= "houseblock", fetch = FetchType.LAZY)
    @Filter(name = GenericRepository.CURRENT_DATA_FILTER, condition = "change_end_date IS NULL")
    private List<HouseblockNameChangelog> names;

    @OneToMany(mappedBy= "houseblock", fetch = FetchType.LAZY)
    @Filter(name = GenericRepository.CURRENT_DATA_FILTER, condition = "change_end_date IS NULL")
    private List<HouseblockSizeChangelog> sizes;

    @OneToMany(mappedBy= "houseblock", fetch = FetchType.LAZY)
    @Filter(name = GenericRepository.CURRENT_DATA_FILTER, condition = "change_end_date IS NULL")
    private List<HouseblockProgrammingChangelog> programmings;

    @OneToMany(mappedBy= "houseblock", fetch = FetchType.LAZY)
    @Filter(name = GenericRepository.CURRENT_DATA_FILTER, condition = "change_end_date IS NULL")
    private List<HouseblockMutatieChangelog> mutaties;

    @OneToMany(mappedBy= "houseblock", fetch = FetchType.LAZY)
    @Filter(name = GenericRepository.CURRENT_DATA_FILTER, condition = "change_end_date IS NULL")
    private List<HouseblockGroundPositionChangelog> groundPositions;

    @OneToMany(mappedBy= "houseblock", fetch = FetchType.LAZY)
    @Filter(name = GenericRepository.CURRENT_DATA_FILTER, condition = "change_end_date IS NULL")
    private List<HouseblockOwnershipValueChangelog> ownershipValues;

    @OneToMany(mappedBy= "houseblock", fetch = FetchType.LAZY)
    @Filter(name = GenericRepository.CURRENT_DATA_FILTER, condition = "change_end_date IS NULL")
    private List<HouseblockAppearanceAndTypeChangelog> appearanceAndTypes;

    @OneToMany(mappedBy= "houseblock", fetch = FetchType.LAZY)
    @Filter(name = GenericRepository.CURRENT_DATA_FILTER, condition = "change_end_date IS NULL")
    private List<HouseblockPurposeChangelog> purposes;

}
