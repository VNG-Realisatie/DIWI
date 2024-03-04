package nl.vng.diwi.dal.entities;

import lombok.Getter;
import lombok.Setter;
import nl.vng.diwi.dal.GenericRepository;
import nl.vng.diwi.dal.entities.enums.MutatieSoort;
import nl.vng.diwi.dal.entities.superclasses.IdSuperclass;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "woningblok_mutatie_changelog", schema = GenericRepository.VNG_SCHEMA_NAME)
@Getter
@Setter
@NoArgsConstructor
public class WoningblokMutatieChangelogSoortValue extends IdSuperclass {

    @JsonIgnoreProperties("type")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "woningblok_mutatie_changelog_id")
    WoningblokMutatieChangelog mutatieChangelog;

    @Column(name = "mutatie_soort")
    @Enumerated(EnumType.STRING)
    private MutatieSoort value;

}
