package nl.vng.diwi.dal.entities;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import nl.vng.diwi.dal.GenericRepository;
import nl.vng.diwi.dal.entities.superclasses.HouseblockMilestoneChangeDataSuperclass;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "woningblok_mutatie_changelog", schema = GenericRepository.VNG_SCHEMA_NAME)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HouseblockMutatieChangelog extends HouseblockMilestoneChangeDataSuperclass {

    @Column(name = "bruto_plancapaciteit")
    private Integer grossPlanCapacity;

    @Column(name = "sloop")
    private Integer demolition;

    @Column(name = "netto_plancapaciteit")
    private Integer netPlanCapacity;

    @JsonIgnoreProperties("mutatieChangelog")
    @OneToMany(mappedBy = "mutatieChangelog", fetch = FetchType.EAGER)
    private List<HouseblockMutatieChangelogTypeValue> type;

    @Override
    public Object getShallowCopy() {
        var newChangelog = HouseblockMutatieChangelog.builder()
            .grossPlanCapacity(grossPlanCapacity).demolition(demolition).netPlanCapacity(netPlanCapacity)
            .build();
        newChangelog.setHouseblock(getHouseblock());
        newChangelog.setStartMilestone(getStartMilestone());
        newChangelog.setEndMilestone(getEndMilestone());
        return newChangelog;
    }
}
