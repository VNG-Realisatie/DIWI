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

import org.hibernate.Session;

@Entity
@Table(name = "woningblok_deliverydate_changelog", schema = GenericRepository.VNG_SCHEMA_NAME)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HouseblockDeliveryDateChangelog extends HouseblockMilestoneChangeDataSuperclass {

    @Column(name = "latest_deliverydate")
    private LocalDate latestDeliveryDate;

    @Column(name = "earliest_deliverydate")
    private LocalDate earliestDeliveryDate;

    @Override
    public Object getCopyWithoutMilestones(Session session) {
        var newChangelog = HouseblockDeliveryDateChangelog.builder()
            .latestDeliveryDate(latestDeliveryDate)
            .earliestDeliveryDate(earliestDeliveryDate).build();
        newChangelog.setHouseblock(getHouseblock());
        newChangelog.setStartMilestone(getStartMilestone());
        newChangelog.setEndMilestone(getEndMilestone());
        return newChangelog;
    }
}
