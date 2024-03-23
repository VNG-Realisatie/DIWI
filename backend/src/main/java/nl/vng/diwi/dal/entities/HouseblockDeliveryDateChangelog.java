package nl.vng.diwi.dal.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nl.vng.diwi.dal.GenericRepository;
import nl.vng.diwi.dal.entities.superclasses.HouseblockMilestoneChangeDataSuperclass;

import java.time.LocalDate;

@Entity
@Table(name = "woningblok_opleverdatum_changelog", schema = GenericRepository.VNG_SCHEMA_NAME)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HouseblockDeliveryDateChangelog extends HouseblockMilestoneChangeDataSuperclass {

    @Column(name = "verwachte_opleverdatum")
    private LocalDate expectedDeliveryDate;

    @Override
    public Object getShallowCopy() {
        var newChangelog = HouseblockDeliveryDateChangelog.builder()
            .expectedDeliveryDate(expectedDeliveryDate).build();
        newChangelog.setHouseblock(getHouseblock());
        newChangelog.setStartMilestone(getStartMilestone());
        newChangelog.setEndMilestone(getEndMilestone());
        return newChangelog;
    }
}
