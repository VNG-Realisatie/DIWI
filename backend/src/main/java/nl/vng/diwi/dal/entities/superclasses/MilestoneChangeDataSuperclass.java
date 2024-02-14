package nl.vng.diwi.dal.entities.superclasses;

import nl.vng.diwi.dal.entities.Milestone;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@MappedSuperclass
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class MilestoneChangeDataSuperclass extends ChangeDataSuperclass {

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "start_milestone_id")
    private Milestone startMilestone;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "end_milestone_id")
    private Milestone endMilestone;
}
