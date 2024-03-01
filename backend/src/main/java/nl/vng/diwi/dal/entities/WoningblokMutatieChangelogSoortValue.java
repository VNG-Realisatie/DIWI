package nl.vng.diwi.dal.entities;

import nl.vng.diwi.dal.GenericRepository;
import nl.vng.diwi.dal.entities.enums.MutatieSoort;
import nl.vng.diwi.dal.entities.superclasses.IdSuperclass;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "woningblok_mutatie_changelog", schema = GenericRepository.VNG_SCHEMA_NAME)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class WoningblokMutatieChangelogSoortValue extends IdSuperclass {

    @JsonIgnoreProperties("type")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "woningblok_mutatie_changelog_id")
    WoningblokMutatieChangelog mutatieChangelog;

    @Column(name = "mutatie_soort")
    @Enumerated(EnumType.STRING)
    private MutatieSoort value;

    public WoningblokMutatieChangelog getMutatieChangelog() {
        return mutatieChangelog;
    }

    public void setMutatieChangelog(WoningblokMutatieChangelog mutatieChangelog) {
        this.mutatieChangelog = mutatieChangelog;
    }

    public MutatieSoort getValue() {
        return value;
    }

    public void setValue(MutatieSoort value) {
        this.value = value;
    }

}
