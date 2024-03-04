package nl.vng.diwi.dal.entities;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import nl.vng.diwi.dal.GenericRepository;
import nl.vng.diwi.dal.entities.superclasses.MilestoneChangeDataSuperclass;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "woningblok_mutatie_changelog", schema = GenericRepository.VNG_SCHEMA_NAME)
@Getter
@Setter
@NoArgsConstructor
public class WoningblokMutatieChangelog extends MilestoneChangeDataSuperclass {

    @JsonIgnoreProperties("mutaties")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "woningblok_id")
    private Woningblok woningblok;

    @Column(name = "bruto_plancapaciteit")
    private Integer amountCreated;

    @Column(name = "sloop")
    private Integer amountRemoved;

    @Column(name = "netto_plancapaciteit")
    private Integer totalAmount;

    @JsonIgnoreProperties("mutatieChangelog")
    @OneToMany(mappedBy="mutatieChangelog", fetch = FetchType.EAGER)
    private List<WoningblokMutatieChangelogSoortValue> type;

}
