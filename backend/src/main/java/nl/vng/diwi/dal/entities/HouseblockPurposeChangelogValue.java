package nl.vng.diwi.dal.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nl.vng.diwi.dal.GenericRepository;
import nl.vng.diwi.dal.entities.superclasses.IdSuperclass;

@Entity
@Table(name = "woningblok_doelgroep_changelog_value", schema = GenericRepository.VNG_SCHEMA_NAME)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HouseblockPurposeChangelogValue extends IdSuperclass {

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "woningblok_doelgroep_changelog_id")
    HouseblockPurposeChangelog purposeChangelog;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "property_value_id")
    private PropertyCategoryValue categoryValue;

    private Integer amount;

}
